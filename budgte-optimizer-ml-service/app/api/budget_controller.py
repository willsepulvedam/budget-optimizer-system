from fastapi import APIRouter
from app.schemas.request.budget_request import BudgetRequest
from app.schemas.response.budget_response import BudgetResponse
from app.services.budget_service import BudgetService
from app.providers.gemini_provider import get_provider
from app.core.config import GeminiConfig
import os

router = APIRouter()


@router.post("/analizar", response_model=BudgetResponse)
def analyze_budget(request: BudgetRequest):
    service = BudgetService()
    return service.analyze(request)


@router.post("/predict", response_model=BudgetResponse)
def predict_budget(request: BudgetRequest):
    service = BudgetService()
    return service.predict(request)


@router.post("/optimize", response_model=BudgetResponse)
def optimize_budget(request: BudgetRequest):
    service = BudgetService()
    return service.optimize(request)


@router.post("/anomalias", response_model=BudgetResponse)
def detect_anomalies(request: BudgetRequest):
    service = BudgetService()
    return service.detect_anomalies(request)


@router.get('/diagnostics')
def diagnostics():
    """Return diagnostic info about provider, config and available models (masked)."""
    cfg = GeminiConfig()
    provider = get_provider()

    def mask(s: str | None):
        if not s:
            return None
        if len(s) <= 8:
            return s[0] + '***' + s[-1]
        return s[:4] + '...' + s[-4:]

    info = {
        'provider_enabled': provider.enabled,
        'configured_model': cfg.model_name,
        'api_key_masked': mask(cfg.api_key),
    }

    # try to list models (may be large) — return short list and details for configured model
    try:
        import google.generativeai as genai
        genai.configure(api_key=cfg.api_key)
        list_models = getattr(genai, 'list_models', None)
        if callable(list_models):
            models = list(list_models())
            # return first 30 names
            names = []
            for m in models[:30]:
                try:
                    names.append(getattr(m, 'name', str(m)))
                except Exception:
                    names.append(str(m))
            info['models_preview'] = names

            # find configured model info
            target = cfg.model_name
            entry = None
            for m in models:
                try:
                    if getattr(m, 'name', None) == target:
                        entry = m
                        break
                except Exception:
                    continue
            if entry is not None:
                info['configured_model_info'] = {
                    'display_name': getattr(entry, 'display_name', None),
                    'description': getattr(entry, 'description', None),
                    'input_token_limit': getattr(entry, 'input_token_limit', None),
                    'output_token_limit': getattr(entry, 'output_token_limit', None),
                    'supported_generation_methods': getattr(entry, 'supported_generation_methods', None),
                }
    except Exception as e:
        info['models_error'] = str(e)

    return info
