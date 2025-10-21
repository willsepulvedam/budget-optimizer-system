from flask import Flask, jsonify, request
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

app = Flask(__name__)

# Configurar Redis con error handling
try:
    redis_client = redis.Redis(
        host=Config.REDIS_HOST,
        port=Config.REDIS_PORT,
        password=Config.REDIS_PASSWORD,
        decode_responses=True,
        socket_connect_timeout=5  # timeout para conexión
    )
    redis_client.ping()  # Verificar conexión
    logger.info("Conexión a Redis exitosa")
except Exception as e:
    logger.error(f"Error conectando a Redis: {e}")
    redis_client = None

# Configuramos Gemini
try:
    genai.configure(api_key=Config.GOOGLE_IA_API_KEY) # type: ignore
    model = genai.GenerativeModel('gemini-2.5-flash') # type: ignore
    logger.info("Gemini configurado correctamente")
except Exception as e:
    logger.error(f"Error configurando Gemini: {e}")
    model = None


@app.route('/analizar', methods=['POST'])
def analizar():
    try:
        data = request.get_json()
        
        if not data:
            logger.warning("Petición sin datos JSON")
            return jsonify({"error": "No se enviaron datos"}), 400
        
        nombre = data.get('nombre')
        prompt = data.get('prompt')
        
        if not nombre or not prompt:
            logger.warning(f"Datos incompletos - nombre: {nombre}, prompt: {prompt}")
            return jsonify({"error": "Falta nombre o prompt"}), 400
        
        logger.info(f"Analizando presupuesto para: {nombre}")
        
        cache_key = f"analisis:{nombre}:{hash(prompt)}"
        
        # The line `if redis_client and redis_client.exists(cache_key):` is checking if the
        # `redis_client` object exists and if the key `cache_key` exists in the Redis database.
        if redis_client and redis_client.exists(cache_key):
            logger.info(f"Usando datos de cache para {nombre}")
            cached_data = redis_client.get(cache_key)
            return jsonify(json.loads(cached_data)), 200 # type: ignore
        
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
            return jsonify({"error": "Servicio de IA no disponible"}), 500
        
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
        return jsonify(resultado), 200
    
    except json.JSONDecodeError as e:
        logger.error(f"Error parseando JSON de Gemini: {e}")
        return jsonify({"error": "Error procesando respuesta de IA"}), 500
    
    except Exception as e:
        logger.error(f"Error en analizar: {e}", exc_info=True)
        return jsonify({"error": "Error interno del servidor"}), 500


@app.route('/health', methods=['GET'])
def health():
    return jsonify({
        "status": "ok",
        "gemini": "connected" if model else "disconnected",
        "redis": "connected" if redis_client else "disconnected"
    }), 200


if __name__ == '__main__':
    app.run(
        host=Config.ML_SERVICE_HOST,
        port=Config.ML_SERVICE_PORT,
        debug=False  # NUNCA en producción
    )