import os
import json
import google.generativeai as genai
from app.core.config import GeminiConfig
from app.core.exceptions import BudgetServiceError


class GeminiProvider:
    def __init__(self):
        self.config = GeminiConfig()
        self.api_key = self.config.api_key
        self.enabled = bool(self.api_key)
        if self.enabled:
            genai.configure(api_key=self.api_key)
        # model name from config, e.g. 'gemini-1.5-flash'
        self.model_name = self._normalize_model_name(self.config.model_name)

    def _normalize_model_name(self, name: str) -> str:
        if not name:
            return name
        name = name.strip()
        if name.startswith("models/") or name.startswith("tunedModels/"):
            return name
        # prepend models/ to match google.generativeai expected format
        return f"models/{name}"

    def _try_parse_json(self, text: str) -> dict:
        text = text.strip()
        if text.startswith("{") or text.startswith("["):
            try:
                return json.loads(text)
            except Exception:
                return {"raw": text}
        return {"raw": text}

    def _extract_text(self, resp) -> str:
        if resp is None:
            return ""
        if hasattr(resp, 'candidates') and getattr(resp, 'candidates'):
            cand = resp.candidates[0]
            if hasattr(cand, 'content') and cand.content is not None:
                if hasattr(cand.content, 'parts') and cand.content.parts:
                    parts = getattr(cand.content, 'parts')
                    if len(parts) > 0 and hasattr(parts[0], 'text'):
                        return parts[0].text
                return str(cand.content)
            if isinstance(cand, dict):
                return cand.get('content', '')
            return str(cand)
        if hasattr(resp, 'output') and getattr(resp, 'output') is not None:
            return str(resp.output)
        if hasattr(resp, 'text'):
            return str(resp.text)
        return str(resp)

    def _parse_response(self, resp, prompt: str, details: dict = None) -> dict:
        raw_text = self._extract_text(resp)
        parsed = self._try_parse_json(raw_text)
        parsed['_sent_prompt'] = prompt
        parsed['_raw_text'] = raw_text
        parsed['details'] = parsed.get('details', {})
        if details:
            parsed['details'].update(details)
        try:
            parsed['_raw_resp'] = json.loads(json.dumps(resp, default=str))
        except Exception:
            parsed['_raw_resp'] = str(resp)
        if 'summary' not in parsed:
            parsed['summary'] = raw_text
        return parsed

    def call_model(self, prompt: str) -> dict:
        """Call Gemini model using chat completions when available, fallback to text generation.

        Returns a dict with at least `summary` key and optional structured fields.
        """
        # If provider not enabled, return structured error without raising
        if not self.enabled:
            return {
                "summary": "error calling model",
                "details": {"error": "GEMINI_API_KEY not configured"},
                "_sent_prompt": prompt,
            }

        # First, try the newer GenerativeModel interface if available
        GenModel = getattr(genai, 'GenerativeModel', None)
        raw_name = self.model_name
        if raw_name and raw_name.startswith('models/'):
            raw_name = raw_name.split('/', 1)[1]

        if GenModel is not None:
            fallback_raw_names = [
                raw_name,
                self.config.model_name,
                'gemini-flash-latest',
                'gemini-3-flash-preview',
                'gemini-2.5-flash',
                'gemini-2.0-flash',
            ]
            tried = []
            for candidate in fallback_raw_names:
                if not candidate or candidate in tried:
                    continue
                tried.append(candidate)
                try:
                    gm = GenModel(candidate)
                except Exception:
                    continue

                if gm is None:
                    continue

                resp = None
                if hasattr(gm, 'generate_content') and callable(getattr(gm, 'generate_content')):
                    try:
                        resp = gm.generate_content(prompt)
                    except Exception:
                        resp = None
                if resp is None and hasattr(gm, 'start_chat') and callable(getattr(gm, 'start_chat')):
                    try:
                        chat = gm.start_chat()
                        if hasattr(chat, 'send_message') and callable(getattr(chat, 'send_message')):
                            resp = chat.send_message(prompt)
                        elif hasattr(chat, 'send_message_async') and callable(getattr(chat, 'send_message_async')):
                            resp = chat.send_message_async(prompt)
                        else:
                            resp = str(chat)
                    except Exception:
                        resp = None

                if resp is not None:
                    return self._parse_response(
                        resp,
                        prompt,
                        details={
                            '_used_model_api': 'GenerativeModel',
                            '_used_model_name': candidate,
                            '_tried_models': tried,
                        },
                    )
        try:
            # Use text generation API (compatible with installed google-generativeai)
            resp = genai.generate_text(model=self.model_name, prompt=prompt, temperature=0.2)
            return self._parse_response(
                resp,
                prompt,
                details={
                    '_used_model_api': 'generate_text',
                    '_used_model_name': self.model_name,
                },
            )
        except Exception as e:
            # Don't raise to avoid an unhandled 500; attempt fallbacks on NOT_FOUND
            import traceback
            tb = traceback.format_exc()
            err = str(e)
            # If the error indicates model not found, try a few common fallbacks
            if "Requested entity was not found" in err or "Requested entity was not found" in tb:
                fallback_models = [
                    self._normalize_model_name("gemini-1.5"),
                    self._normalize_model_name("gemini-1.0"),
                    self._normalize_model_name("text-bison-001"),
                    self._normalize_model_name("chat-bison-001"),
                ]
                tried = [self.model_name]
                last_exc = None
                for alt in fallback_models:
                    if not alt or alt in tried:
                        continue
                    tried.append(alt)
                    try:
                        resp = genai.generate_text(model=alt, prompt=prompt, temperature=0.2)
                        return self._parse_response(
                            resp,
                            prompt,
                            details={
                                '_used_model_api': 'generate_text',
                                '_used_model_name': alt,
                            },
                        )
                    except Exception as e2:
                        last_exc = e2
                        continue

                # none of the fallbacks worked
                return {
                    "summary": "error calling model",
                    "details": {
                        "error": err,
                        "trace": tb,
                        "attempted_models": tried,
                        "last_error": str(last_exc) if last_exc is not None else None,
                    }
                }

            return {
                "summary": "error calling model",
                "details": {
                    "error": err,
                    "trace": tb
                }
            }
        # As a last resort (different client surface), try genai.GenerativeModel if available
        try:
            GenModel = getattr(genai, 'GenerativeModel', None)
            if GenModel is not None:
                # derive name without 'models/' prefix
                raw_name = self.model_name
                if raw_name and raw_name.startswith('models/'):
                    raw_name = raw_name.split('/', 1)[1]
                try:
                    gm = GenModel(raw_name)
                except Exception:
                    # some versions expect the model id in a different form
                    gm = GenModel(self.config.model_name) if getattr(self.config, 'model_name', None) else None

                if gm is not None:
                    # try common method names
                    for method_name in ('generate', 'generate_text', 'predict'):
                        method = getattr(gm, method_name, None)
                        if callable(method):
                            try:
                                resp = method(prompt)
                                text = None
                                if hasattr(resp, 'candidates') and getattr(resp, 'candidates'):
                                    cand = resp.candidates[0]
                                    text = getattr(cand, 'content', None) or (cand.get('content') if isinstance(cand, dict) else None) or str(cand)
                                else:
                                    text = getattr(resp, 'output', None) or str(resp)

                                raw_text = text if text is not None else ""
                                parsed = self._try_parse_json(raw_text)
                                parsed['_sent_prompt'] = prompt
                                parsed['_raw_text'] = raw_text
                                try:
                                    parsed['_raw_resp'] = json.loads(json.dumps(resp, default=str))
                                except Exception:
                                    parsed['_raw_resp'] = str(resp)
                                if 'summary' not in parsed:
                                    parsed['summary'] = raw_text
                                parsed['details'] = parsed.get('details', {})
                                parsed['details']['_used_model_api'] = 'GenerativeModel'
                                parsed['details']['_used_model_name'] = raw_name
                                return parsed
                            except Exception:
                                continue
        except Exception:
            pass


# Cached provider instance to avoid re-instantiation per request
_provider_instance = None

def get_provider():
    global _provider_instance
    if _provider_instance is None:
        _provider_instance = GeminiProvider()
    return _provider_instance
