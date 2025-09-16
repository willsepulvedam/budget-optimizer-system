# Etapa de desarrollo
FROM node:18-alpine

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar package.json y package-lock.json
COPY package*.json ./

# Instalar dependencias
RUN npm install

# Copiar el resto de los archivos del proyecto
COPY . .

# Exponer el puerto 5173
EXPOSE 5173

# Iniciar en modo desarrollo
CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0"]