import os
from pathlib import Path


def load_env(env_path: str = None):
    if env_path is None:
        env_path = Path(__file__).resolve().parents[2] / ".env"

    env_file = Path(env_path)
    if not env_file.exists():
        return

    for line in env_file.read_text(encoding="utf-8").splitlines():
        line = line.strip()
        if not line or line.startswith("#") or "=" not in line:
            continue

        key, value = line.split("=", 1)
        key = key.strip()
        value = value.strip().strip('"').strip("'")
        if key and key not in os.environ:
            os.environ[key] = value


load_env()


class GeminiConfig:
    def __init__(self):
        # Support both GEMINI_* and GOOGLE_AI_* env var names
        self.api_key = os.getenv("GEMINI_API_KEY", os.getenv("GOOGLE_AI_API_KEY", ""))
        self.model_name = os.getenv("GEMINI_MODEL_NAME", os.getenv("GOOGLE_AI_MODEL_NAME", "gemini-1.5-flash"))
