from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import google.generativeai as genai
from dotenv import load_dotenv
import redis
from configuracion import Config
import logging
import json


# Cargamos el archivo env
load_dotenv()

# Configuramos logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

app = FastAPI()

# Configurar Redis con error handling
try:
    redis_client = redis.Redis(
        host=Config.REDIS_HOST,
        port=Config.REDIS_PORT,
        password=Config.REDIS_PASSWORD,
        decode_responses=True,
        socket_connect_timeout=5
    )
    redis_client.ping()
    logger.info("Conexión a Redis exitosa")
except Exception as e:
    logger.error(f"Error conectando a Redis: {e}")
    redis_client = None

# Configuramos Gemini
try:
    genai.configure(api_key=Config.GOOGLE_IA_API_KEY)
    model = genai.GenerativeModel('gemini-2.5-flash')
    logger.info("Gemini configurado correctamente")
except Exception as e:
    logger.error(f"Error configurando Gemini: {e}")
    model = None


# Definir modelo Pydantic para validación
class AnalisisRequest(BaseModel):
    nombre: str
    prompt: str


@app.post('/analizar')
def analizar(request_data: AnalisisRequest):
    try:
        nombre = request_data.nombre
        prompt = request_data.prompt
        
        if not nombre or not prompt:
            logger.warning(f"Datos incompletos - nombre: {nombre}, prompt: {prompt}")
            raise HTTPException(
                status_code=400,
                detail="Falta nombre o prompt"
            )
        
        logger.info(f"Analizando presupuesto para: {nombre}")
        
        cache_key = f"analisis:{nombre}:{hash(prompt)}"
        
        if redis_client and redis_client.exists(cache_key):
            logger.info(f"Usando datos de cache para {nombre}")
            cached_data = redis_client.get(cache_key)
            return json.loads(cached_data)
        
        prompt_final = f"""
Eres un analista de presupuestos experto. Basándote en la siguiente información del usuario, 
haz un análisis presupuestal completo y detallado.

Usuario: {nombre}
Información: {prompt}

Retorna SOLO un JSON válido (sin markdown) con esta estructura exacta:
{{
  "usuario": "{nombre}",
  "analisis": "string con análisis detallado del presupuesto",
  "recomendaciones": ["recomendación 1", "recomendación 2", "recomendación 3"],
  "ahorro_potencial": "string estimado de ahorro mensual/anual"
}}
"""
        
        if not model:
            logger.error("Modelo Gemini no disponible")
            raise HTTPException(
                status_code=500,
                detail="Servicio de IA no disponible"
            )
        
        response = model.generate_content(prompt_final)
        
        response_text = response.text.strip()
        if response_text.startswith('```'):
            response_text = response_text.split('```')[1]
            if response_text.startswith('json'):
                response_text = response_text[4:]
            response_text = response_text.strip()
        
        resultado = json.loads(response_text)
        
        if redis_client:
            try:
                redis_client.setex(cache_key, 3600, json.dumps(resultado))
                logger.info(f"Resultado guardado en cache para {nombre}")
            except Exception as e:
                logger.warning(f"Error guardando en cache: {e}")
        
        logger.info(f"Análisis completado exitosamente para {nombre}")
        return resultado
    
    except json.JSONDecodeError as e:
        logger.error(f"Error parseando JSON de Gemini: {e}")
        raise HTTPException(
            status_code=500,
            detail="Error procesando respuesta de IA"
        )
    
    except HTTPException:
        raise
    
    except Exception as e:
        logger.error(f"Error en analizar: {e}", exc_info=True)
        raise HTTPException(
            status_code=500,
            detail="Error interno del servidor"
        )


@app.get('/health')
def health():
    return {
        "status": "ok",
        "gemini": "connected" if model else "disconnected",
        "redis": "connected" if redis_client else "disconnected"
    }


if __name__ == '__main__':
    import uvicorn
    uvicorn.run(
        app,
        host=Config.ML_SERVICE_HOST,
        port=Config.ML_SERVICE_PORT,
        reload=False
    )