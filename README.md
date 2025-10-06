# DeepWiki 

[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/willsepulvedam/budget-optimizer-system)



# Budget Optimizer

Sistema Multimodal de Optimización de Presupuestos con Machine Learning

## Requisitos Previos

Solo necesitas tener instalado:

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (para Windows o Mac)
- [Docker Engine](https://docs.docker.com/engine/install/) (para Linux)
- [Docker Compose](https://docs.docker.com/compose/install/) (viene incluido en Docker Desktop)

## Estructura del Proyecto

El proyecto está dividido en tres componentes principales que se ejecutan en contenedores Docker independientes:

```
budget-optimizer-backend/    # Servicio backend en Spring Boot
bubget-optimizer-frontend/   # Aplicación frontend en Vite
budgte-optimizer-ml-service/ # Servicio de Machine Learning
```

## Cómo Ejecutar el Proyecto

### 1. Clonar el Repositorio

```bash
git clone <URL_DEL_REPOSITORIO>
cd Proyecto-De-Aula
```

### 2. Configuración Inicial

No se requiere ninguna configuración adicional. Todos los servicios están pre-configurados en el archivo `docker-compose.yml`.

### 3. Iniciar los Servicios

En la carpeta raíz del proyecto, ejecuta:

```bash
docker-compose up -d
```

Este comando:
- Construirá todas las imágenes necesarias
- Creará los contenedores
- Iniciará todos los servicios
- Configurará la red entre los servicios
- Iniciará la base de datos PostgreSQL

### 4. Verificar que todo esté funcionando

Una vez que los contenedores estén en ejecución, podrás acceder a:

- Frontend: http://localhost:5173
- Backend: http://localhost:8080
- Servicio ML: http://localhost:8000
- Base de datos: localhost:5432

Para ver los logs de los servicios:

```bash
# Ver todos los logs
docker-compose logs -f

# Ver logs de un servicio específico
docker-compose logs -f frontend
docker-compose logs -f backend
docker-compose logs -f ml-service
```

### 5. Detener los Servicios

Para detener todos los servicios:

```bash
docker-compose down
```

Para detener y eliminar también los volúmenes (esto borrará los datos de la base de datos):

```bash
docker-compose down -v
```

## Comandos Docker Útiles

### Ver el Estado de los Contenedores
```bash
docker-compose ps
```

### Reiniciar un Servicio Específico
```bash
docker-compose restart frontend
docker-compose restart backend
docker-compose restart ml-service
```

### Reconstruir un Servicio (después de cambios)
```bash
docker-compose up -d --build frontend
docker-compose up -d --build backend
docker-compose up -d --build ml-service
```

## Solución de Problemas

### Los contenedores no inician
1. Verifica que Docker Desktop esté corriendo
2. Comprueba los puertos:
```bash
# Windows
netstat -ano | findstr "5173"
netstat -ano | findstr "8080"
netstat -ano | findstr "8000"
```

3. Si algún puerto está en uso, debes detener el servicio que lo está usando o modificar el puerto en el `docker-compose.yml`

### Errores comunes y soluciones

#### Error: "port is already allocated"
```bash
# Detener todos los contenedores
docker-compose down
# Limpiar recursos no utilizados
docker system prune
```

#### Error: "connection refused" al backend
1. Verifica que todos los contenedores estén corriendo:
```bash
docker-compose ps
```
2. Revisa los logs del backend:
```bash
docker-compose logs backend
```

#### Error: No se pueden guardar datos
1. Verifica que el contenedor de PostgreSQL esté corriendo:
```bash
docker-compose ps db
```
2. Revisa los logs de la base de datos:
```bash
docker-compose logs db
```

## Mantenimiento

### Actualizar los Contenedores
```bash
# Detener los contenedores
docker-compose down

# Obtener las últimas imágenes
docker-compose pull

# Reconstruir y reiniciar
docker-compose up -d --build
```

### Limpiar Recursos
```bash
# Eliminar contenedores parados
docker container prune

# Eliminar imágenes no utilizadas
docker image prune

# Eliminar todo lo no utilizado (¡usar con precaución!)
docker system prune -a
```