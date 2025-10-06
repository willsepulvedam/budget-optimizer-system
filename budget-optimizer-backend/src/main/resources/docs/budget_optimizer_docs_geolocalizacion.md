# Budget Optimizer Backend - Documentación Completa
## Parte 4: Servicios (Lógica de Negocio)

---

## 📋 ¿Qué es un Service en Spring?

Los **Services** contienen la lógica de negocio de la aplicación:

✅ **Orquestan** múltiples repositorios  
✅ **Validan** reglas de negocio  
✅ **Transforman** datos entre capas  
✅ **Gestionan** transacciones  
✅ **Interactúan** con servicios externos (APIs)

---

## 🏗️ Arquitectura de Capas

```
┌─────────────────────────────────┐
│   Controller (REST API)         │  ← Recibe peticiones HTTP
├─────────────────────────────────┤
│   Service (Lógica de Negocio)   │  ← Valida, calcula, orquesta
├─────────────────────────────────┤
│   Repository (Acceso a Datos)   │  ← Consultas JPA
├─────────────────────────────────┤
│   Database (PostgreSQL)         │  ← Persistencia
└─────────────────────────────────┘
```

---

## 🌍 ServicioGeolocalizacion

### 📝 Propósito
Obtener coordenadas geográficas (latitud/longitud) a partir de ciudad y país usando una API externa.

### 🔧 Configuración
```properties
# application.properties
app.geolocation.api-key=${GEOLOCATION_API_KEY:tu_api_key}
app.geolocation.api-url=${GEOLOCATION_API_URL:https://api.distancematrix.ai/maps/api/geocode/json}
```

### 📦 Dependencias
```java
@Service
@RequiredArgsConstructor  // Lombok: Constructor con dependencias final
@Slf4j                    // Lombok: Logger automático
public class ServicioGeolocalizacion {
    
    private final RestTemplate restTemplate;  // Cliente HTTP
    
    @Value("${app.geolocation.api-url}")
    private String API_URL;
    
    @Value("${app.geolocation.api-key}")
    private String API_KEY;
}
```

**💡 @RequiredArgsConstructor**: Lombok genera constructor con campos `final`:
```java
// Equivalente a:
public ServicioGeolocalizacion(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
}
```

**💡 @Slf4j**: Lombok añade automáticamente:
```java
private static final Logger log = LoggerFactory.getLogger(ServicioGeolocalizacion.class);
```

### 🎯 Método Principal

#### obtenerCoordenadas()
```java
public Coordenada obtenerCoordenadas(String ciudad, String pais) {
    try {
        log.info("Obteniendo coordenadas para: {}, {}", ciudad, pais);
        
        // 1. Construir URL con parámetros
        String query = ciudad + ", " + pais;
        String url = String.format("%s?q=%s&key=%s&limit=1", 
            API_URL, query, API_KEY);

        // 2. Realizar petición GET a la API
        JsonNode response = restTemplate.getForObject(url, JsonNode.class);

        // 3. Extraer coordenadas del JSON
        if (response != null && response.has("results") && 
            response.get("results").size() > 0) {
            
            JsonNode geometry = response.get("results").get(0).get("geometry");
            double latitud = geometry.get("lat").asDouble();
            double longitud = geometry.get("lng").asDouble();

            log.info("Coordenadas obtenidas: lat={}, lng={}", latitud, longitud);
            return new Coordenada(latitud, longitud);
        } else {
            log.warn("No se encontraron coordenadas para: {}", query);
            throw new RuntimeException("No se encontraron coordenadas para: " + query);
        }

    } catch (RestClientException e) {
        log.error("Error en la petición HTTP: {}", e.getMessage());
        throw new RuntimeException("Error al conectar con el servicio de geolocalización", e);
    } catch (Exception e) {
        log.error("Error al obtener coordenadas: {}", e.getMessage());
        throw new RuntimeException("Error al obtener coordenadas: " + e.getMessage(), e);
    }
}
```

### 🔍 Análisis Paso a Paso

#### 1. Construcción de URL
```java
String query = ciudad + ", " + pais;
String url = String.format("%s?q=%s&key=%s&limit=1", 
    API_URL, query, API_KEY);

// Ejemplo resultado:
// https://api.distancematrix.ai/maps/api/geocode/json?q=Cartagena, Colombia&key=ABC123&limit=1
```

**Parámetros:**
- `q`: Consulta (ciudad, país)
- `key`: API key de autenticación
- `limit`: Número máximo de resultados (1)

#### 2. Petición HTTP GET
```java
JsonNode response = restTemplate.getForObject(url, JsonNode.class);
```

**RestTemplate.getForObject():**
- Hace petición HTTP GET
- Deserializa respuesta JSON a `JsonNode` (árbol JSON de Jackson)
- Maneja headers, encoding, etc. automáticamente

**Respuesta esperada (JSON):**
```json
{
  "results": [
    {
      "geometry": {
        "lat": 10.4236,
        "lng": -75.5223
      }
    }
  ]
}
```

#### 3. Extracción de Datos
```java
JsonNode geometry = response.get("results").get(0).get("geometry");
double latitud = geometry.get("lat").asDouble();
double longitud = geometry.get("lng").asDouble();
```

**Navegación del JSON:**
1. `response.get("results")` → Array de resultados
2. `.get(0)` → Primer resultado
3. `.get("geometry")` → Objeto geometry
4. `.get("lat").asDouble()` → Valor latitud como double

#### 4. Retorno
```java
return new Coordenada(latitud, longitud);
```

**Coordenada**: Clase embebida (no entidad) con lat/lng.

### 🚨 Manejo de Errores

#### Error de Conexión
```java
catch (RestClientException e) {
    log.error("Error en la petición HTTP: {}", e.getMessage());
    throw new RuntimeException("Error al conectar con el servicio de geolocalización", e);
}
```

**Causas comunes:**
- API no disponible
- Timeout de conexión
- DNS no resuelve

#### Error de Procesamiento
```java
catch (Exception e) {
    log.error("Error al obtener coordenadas: {}", e.getMessage());
    throw new RuntimeException("Error al obtener coordenadas: " + e.getMessage(), e);
}
```

**Causas comunes:**
- JSON mal formado
- Campo faltante
- Conversión de tipo fallida

#### No se Encontraron Resultados
```java
if (response != null && response.has("results") && 
    response.get("results").size() > 0) {
    // Procesar...
} else {
    log.warn("No se encontraron coordenadas para: {}", query);
    throw new RuntimeException("No se encontraron coordenadas para: " + query);
}
```

### 💡 Ejemplo de Uso

#### En un Controller
```java
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    
    private final ServicioGeolocalizacion geoService;
    
    @PostMapping("/registrar")
    public ResponseEntity<Usuario> registrar(@RequestBody RegistroDTO dto) {
        // Obtener coordenadas antes de guardar
        Coordenada coords = geoService.obtenerCoordenadas(
            dto.getCiudad(), 
            dto.getPais()
        );
        
        Usuario usuario = new Usuario();
        usuario.setUbicacion(coords);
        // ... más configuración
        
        return ResponseEntity.ok(usuario);
    }
}
```

#### En un Service
```java
@Service
@RequiredArgsConstructor
public class UsuarioService {
    
    private final UsuarioRepository usuarioRepo;
    private final ServicioGeolocalizacion geoService;
    
    @Transactional
    public Usuario crearUsuario(String email, String ciudad, String pais) {
        // 1. Validar email único
        if (usuarioRepo.existsByEmail(email)) {
            throw new EmailYaRegistradoException(email);
        }
        
        // 2. Obtener coordenadas
        Coordenada ubicacion = geoService.obtenerCoordenadas(ciudad, pais);
        
        // 3. Crear usuario
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setUb