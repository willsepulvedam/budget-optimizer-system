import os
from dotenv import load_dotenv

load_dotenv()

class Config:
    ML_SERVICE_PORT = int(os.getenv('ML_SERVICE_PORT', '8000'))
    ML_SERVICE_HOST = os.getenv('ML_SERVICE_HOST', '0.0.0.0')
    GOOGLE_IA_API_KEY = os.getenv('GOOGLE_IA_API_KEY')
    REDIS_HOST = os.getenv('REDIS_HOST', 'localhost')
    REDIS_PORT = int(os.getenv('REDIS_PORT', '6379'))
    REDIS_PASSWORD = os.getenv('REDIS_PASSWORD')
    REDIS_TIMEOUT = int(os.getenv('REDIS_TIMEOUT', '5'))
    REDIS_CACHE_EXPIRATION = int(os.getenv('REDIS_CACHE_EXPIRATION', '3600'))
    LOG_LEVEL = os.getenv('LOG_LEVEL', 'INFO')
    
    @classmethod
    def validate(cls):
        if not cls.GOOGLE_IA_API_KEY:
            raise ValueError("GOOGLE_IA_API_KEY no configurada en .env")
        return True

Config.validate()