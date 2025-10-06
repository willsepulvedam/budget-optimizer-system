# Budget Optimizer Backend - Documentación Completa
## Parte 1: Configuración General y Dependencias

---

## 📋 Índice General
1. **Parte 1: Configuración General** (este documento)
2. Parte 2: Enums de Negocio
3. Parte 3: Repositorios
4. Parte 4: Servicios

---

## 🎯 Resumen del Proyecto

**Budget Optimizer** es un sistema backend para optimización de presupuestos con Machine Learning. Permite a los usuarios:
- Crear y gestionar presupuestos personales
- Registrar gastos y categorizarlos
- Recibir recomendaciones de ML
- Buscar empresas cercanas
- Obtener análisis de patrones de gasto

---

## 🛠️ Stack Tecnológico

### Framework Principal
- **Spring Boot 3.5.6**
- **Java 21** (LTS)
- **Maven** como gestor de dependencias

### Base de Datos
- **PostgreSQL** (JDBC)
- **Spring Data JPA** para persistencia
- **Hibernate** como ORM

### Librerías Clave
- **Lombok**: Reduce código boilerplate (@Getter, @Setter, etc.)
- **Jackson**: Serialización/deserialización JSON
- **Caffeine**: Cache en memoria
- **Hypersistence Utils**: Tipos avanzados de Hibernate

---

## 📦 Estructura del POM.xml

### Configuración Base
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.5.6</version>
</parent>

<properties>
    <java.version>21</java.version>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>
```

### Dependencias Principales

#### 1. Spring Boot Starters
```xml
<!-- Web MVC + REST APIs -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- JPA + Hibernate -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- Monitoreo (health checks, métricas) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

#### 2. Base de Datos
```xml
<!-- Driver PostgreSQL -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

#### 3. Cache y Utilidades
```xml
<!-- Cache de alto rendimiento -->
<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
</dependency>

<!-- Tipos JSON para Hibernate -->
<dependency>
    <groupId>io.hypersistence</groupId>
    <artifactId>hypersistence-utils-hibernate-63</artifactId>
    <version>3.7.3</version>
</dependency>
```

#### 4. Desarrollo
```xml
<!-- Reduce código boilerplate -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>

<!-- Testing -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

---

## ⚙️ Configuración de Application.properties

### Configuración de la Aplicación
```properties
spring.application.name=budget-optimizer-backend
server.port=${BACKEND_PORT:8080}  # Puerto configurable
```

### Base de Datos
```properties
# URL con valores por defecto
spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/budget_optimizer}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME:admin}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:adminpass}
spring.datasource.driver-class-name=org.postgresql.Driver
```

**💡 Tip**: Usa variables de entorno (`${VAR:default}`) para diferentes ambientes (dev, prod).

### JPA/Hibernate
```properties
# Actualiza automáticamente el esquema (¡no uses en producción!)
spring.jpa.hibernate.ddl-auto=update

# Muestra SQL en consola (solo desarrollo)
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Dialecto PostgreSQL
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

**⚠️ Importante**: 
- `ddl-auto=update` es para desarrollo
- En producción usa `validate` o `none` + scripts de migración (Flyway/Liquibase)

### Logging
```properties
logging.level.root=${LOG_LEVEL:INFO}
logging.level.com.budgetoptimizer=DEBUG  # Tu código en DEBUG
logging.level.org.hibernate=ERROR        # Hibernate silencioso
```

**Niveles**: TRACE < DEBUG < INFO < WARN < ERROR < FATAL

### Cache con Caffeine
```properties
spring.cache.type=caffeine
# 500 elementos max, expira después de 10 min sin uso
spring.cache.caffeine.spec=maximumSize=500,expireAfterAccess=600s
```

### Actuator (Monitoreo)
```properties
# Expone endpoints /actuator/health, /actuator/info
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=when_authorized
```

**Endpoints útiles**:
- `GET /actuator/health` - Estado del sistema
- `GET /actuator/info` - Info de la app
- `GET /actuator/metrics` - Métricas (CPU, memoria, etc.)

### Subida de Archivos
```properties
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

### Compresión HTTP
```properties
server.compression.enabled=true
server.compression.mime-types=application/json,application/xml,text/html
server.compression.min-response-size=1024  # Solo si > 1KB
```

### API de Geolocalización
```properties
app.geolocation.api-key=${GEOLOCATION_API_KEY:n8TCvvBOQNBWzn3R5wuMVcyXVE5zuGzD1dEpfWYaPLkvrQUZsPUjkefxlxfnC96I}
app.geolocation.api-url=${GEOLOCATION_API_URL:https://api.distancematrix.ai/maps/api/geocode/json}
```

---

## 🔧 Configuración de Beans (AppConfig.java)

```java
@Configuration
public class AppConfig {
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

### ¿Qué hace?
- `@Configuration`: Marca la clase como fuente de beans
- `@Bean`: Registra `RestTemplate` en el contenedor de Spring
- **RestTemplate**: Cliente HTTP para llamar APIs externas (geolocalización, ML, etc.)

### Uso posterior
```java
@Service
public class MiServicio {
    private final RestTemplate restTemplate;
    
    public MiServicio(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;  // Inyección automática
    }
}
```

---

## 📁 Estructura de Carpetas Recomendada

```
budget-optimizer-backend/
├── src/main/java/com/budgetoptimizer/
│   ├── config/           # Configuración de beans
│   ├── controller/       # Endpoints REST
│   ├── dto/              # Data Transfer Objects
│   ├── enums/            # Enumeraciones de negocio ⭐
│   ├── exception/        # Manejo de errores
│   ├── model/            # Entidades JPA
│   ├── repository/       # Acceso a datos ⭐
│   ├── service/          # Lógica de negocio ⭐
│   └── util/             # Utilidades
├── src/main/resources/
│   ├── application.properties
│   └── application-prod.properties
└── pom.xml
```

**⭐ = Documentado en las siguientes partes**

---

## 🚀 Cómo Levantar el Proyecto

### 1. Requisitos Previos
- Java 21 instalado
- PostgreSQL corriendo
- Maven instalado (o usa el wrapper `mvnw`)

### 2. Configurar Base de Datos
```sql
CREATE DATABASE budget_optimizer;
CREATE USER admin WITH PASSWORD 'adminpass';
GRANT ALL PRIVILEGES ON DATABASE budget_optimizer TO admin;
```

### 3. Variables de Entorno (opcional)
```bash
export BACKEND_PORT=8080
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/budget_optimizer
export SPRING_DATASOURCE_USERNAME=admin
export SPRING_DATASOURCE_PASSWORD=adminpass
```

### 4. Compilar y Ejecutar
```bash
# Compilar
mvn clean install

# Ejecutar
mvn spring-boot:run

# O usando el JAR
java -jar target/budget-optimizer-backend-0.0.1-SNAPSHOT.jar
```

### 5. Verificar
- **API**: http://localhost:8080
- **Health**: http://localhost:8080/actuator/health
- **Info**: http://localhost:8080/actuator/info

---

## 🔍 Próximos Pasos

Continúa con:
- **Parte 2**: Enums de Negocio (AccountType, BudgetPeriod, etc.)
- **Parte 3**: Repositorios (acceso a datos)
- **Parte 4**: Servicios (lógica de negocio)

---

## 📚 Referencias Útiles

- [Spring Boot Docs](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Lombok](https://projectlombok.org/features/)
- [PostgreSQL](https://www.postgresql.org/docs/)
