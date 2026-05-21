import google.generativeai as genai
from app.core.config import GeminiConfig

cfg = GeminiConfig()
print('API_KEY_SET=', bool(cfg.api_key))

genai.configure(api_key=cfg.api_key)
print('genai module type:', type(genai))
try:
    r = genai.generate_text(model='models/gemini-1.5-flash', prompt='hola', temperature=0.2)
    print('RESULT:', r)
except Exception as e:
    import traceback
    traceback.print_exc()
    print('EXCEPTION:', e)
