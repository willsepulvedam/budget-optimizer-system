# Usar una imagen base de Python 3.11
FROM python:3.11-slim

# Establecer el directorio de trabajo
WORKDIR /app

# Instalar dependencias del sistema necesarias para numpy y scipy
RUN apt-get update && apt-get install -y \
    build-essential \
    python3-venv \
    && rm -rf /var/lib/apt/lists/*

# Crear y activar el entorno virtual
ENV VIRTUAL_ENV=/app/venv
RUN python -m venv $VIRTUAL_ENV
ENV PATH="$VIRTUAL_ENV/bin:$PATH"

# Actualizar pip en el entorno virtual
RUN pip install --no-cache-dir --upgrade pip

# Copiar los requisitos e instalar dependencias en el entorno virtual
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copiar el resto del código
COPY . .

# Puerto expuesto para FastAPI
EXPOSE 8000

# Crear el script de inicio
RUN echo '#!/bin/bash' > /app/start.sh && \
    echo 'source /app/venv/bin/activate' >> /app/start.sh && \
    echo 'exec uvicorn main:app --host 0.0.0.0 --port 8000 --reload' >> /app/start.sh && \
    chmod +x /app/start.sh

# Usar el script como punto de entrada
CMD ["/bin/bash", "/app/start.sh"]