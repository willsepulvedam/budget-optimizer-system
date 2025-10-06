# Budget Optimizer Backend - Documentación Completa
## Parte 5: Models/Entidades JPA

---

## 📋 ¿Qué es una Entidad JPA?

Las **Entidades** son clases Java que se mapean a tablas en la base de datos:

✅ **@Entity**: Marca la clase como entidad persistente  
✅ **@Table**: Define nombre de tabla (opcional)  
✅ **@Id**: Campo clave primaria  
✅ **@Column**: Configura columnas individuales  
✅ **Relaciones**: @OneToMany, @ManyToOne, @ManyToMany

---

## 🎯 Anotaciones Comunes de Lombok

```java
@Getter                    // Genera getters automáticamente
@Setter                    // Genera setters automáticamente
@NoArgsConstructor         // Constructor sin parámetros
@AllArgsConstructor        // Constructor con todos los parámetros
@Builder                   // Patrón Builder para crear objetos
@ToString                  // Método toString()
@EqualsAndHashCode        // equals() y hashCode()
```

---

## 1️⃣ Usuario (Entidad Principal)

### 📝 Descripción
Representa un usuario del sistema con información personal, ubicación y configuración de cuenta.

### 🏗️ Estructura Completa

```java
@Entity
@Table(name = "usuarios")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {
    
    // ==========================================
    // CLAVE PRIMARIA
    // ==========================================
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // ==========================================
    // INFORMACIÓN BÁSICA
    // ==========================================
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(unique = true, nullable = false, length = 150)
    private String email;
    
    @Column(nullable = false)
    private String password;  // Hash, nunca texto plano
    
    @Column(length = 20)
    private String telefono;
    
    // ==========================================
    // TIPO DE CUENTA Y PERMISOS
    // ==========================================
    
    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    @Builder.Default
    private AccountType accountType = AccountType.USER;
    
    // ==========================================
    // ESTADO Y CONFIGURACIÓN
    // ==========================================
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;
    
    @Column(name = "presupuesto_mensual_base")
    private Double presupuestoMensualBase;
    
    @Column(name = "transacciones_mes_actual")
    @Builder.Default
    private Integer transaccionesMesActual = 0;
    
    // ==========================================
    // UBICACIÓN (EMBEBIDA)
    // ==========================================
    
    @Embedded
    private Coordenada ubicacion;
    
    @Column(length = 100)
    private String ciudad;
    
    @Column(length = 100)
    private String pais;
    
    // ==========================================
    // FECHAS DE AUDITORÍA
    // ==========================================
    
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;
    
    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;
    
    // ==========================================
    // RELACIONES
    // ==========================================
    
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Presupuesto> presupuestos = new ArrayList<>();
    
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Expense> gastos = new ArrayList<>();
    
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Cuenta cuenta;
    
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();
    
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<HistorialBusqueda> historialBusquedas = new ArrayList<>();
    
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MLOptimization> optimizaciones = new ArrayList<>();
    
    // ==========================================
    // MÉTODOS DE NEGOCIO
    // ==========================================
    
    /**
     * Verifica si el usuario puede realizar otra transacción este mes
     */
    public Boolean puedeRealizarTransaccion() {
        return accountType.puedeRealizarTransaccion(transaccionesMesActual);
    }
    
    /**
     * Incrementa el contador de transacciones del mes
     */
    public void registrarTransaccion() {
        if (!puedeRealizarTransaccion()) {
            throw new LimiteTransaccionesException("Límite de transacciones alcanzado");
        }
        this.transaccionesMesActual++;
    }
    
    /**
     * Reinicia el contador mensual de transacciones
     */
    public void reiniciarContadorMensual() {
        this.transaccionesMesActual = 0;
    }
    
    /**
     * Actualiza la fecha de último acceso
     */
    public void actualizarUltimoAcceso() {
        this.ultimoAcceso = LocalDateTime.now();
    }
    
    /**
     * Upgrade de cuenta
     */
    public void upgradeCuenta(AccountType nuevoTipo) {
        if (nuevoTipo.esSuperiorA(this.accountType)) {
            this.accountType = nuevoTipo;
            this.fechaModificacion = LocalDateTime.now();
        } else {
            throw new IllegalArgumentException("El nuevo tipo debe ser superior al actual");
        }
    }
    
    // Hook de JPA: ejecutar antes de actualizar
    @PreUpdate
    protected void onUpdate() {
        this.fechaModificacion = LocalDateTime.now();
    }
}
```

### 🔍 Análisis de Anotaciones

#### @Entity y @Table
```java
@Entity                      // Marca como entidad JPA
@Table(name = "usuarios")   // Nombre de tabla (opcional)
```
- Sin `@Table`, usa nombre de clase en snake_case: `usuario`
- Con `@Table(name = "usuarios")`, usa `usuarios`

#### @Id y @GeneratedValue
```java
@Id                                            // Clave primaria
@GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-incremento
private Long id;
```
**Estrategias:**
- `IDENTITY`: Auto-incremento de BD (PostgreSQL: SERIAL)
- `SEQUENCE`: Usa secuencia de BD
- `AUTO`: JPA decide
- `TABLE`: Tabla auxiliar (no recomendado)

#### @Column
```java
@Column(nullable = false, length = 100)
private String nombre;

@Column(unique = true, nullable = false, length = 150)
private String email;
```
**Atributos:**
- `nullable = false`: NOT NULL
- `unique = true`: Constraint UNIQUE
- `length`: VARCHAR(n)
- `name`: Nombre personalizado de columna

#### @Enumerated
```java
@Enumerated(EnumType.STRING)
@Column(name = "account_type", nullable = false)
private AccountType accountType;
```
**Tipos:**
- `EnumType.STRING`: Guarda "USER", "PREMIUM", etc. (recomendado)
- `EnumType.ORDINAL`: Guarda 0, 1, 2... (peligroso si reordenas enum)

#### @Embedded
```java
@Embedded
private Coordenada ubicacion;
```
Inserta campos de `Coordenada` directamente en tabla `usuarios`:
```sql
CREATE TABLE usuarios (
    id BIGINT PRIMARY KEY,
    latitud DOUBLE,
    longitud DOUBLE,
    -- otros campos...
);
```

#### @OneToMany
```java
@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Presupuesto> presupuestos = new ArrayList<>();
```
**Atributos:**
- `mappedBy`: Campo en la otra entidad (no crea FK aquí)
- `cascade = CascadeType.ALL`: Opera en hijos (guardar, eliminar, etc.)
- `orphanRemoval = true`: Elimina hijos sin padre
- **Lado NO dueño**: No tiene `@JoinColumn`

#### @OneToOne
```java
@OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
private Cuenta cuenta;
```
Similar a @OneToMany pero 1:1.

#### @PreUpdate
```java
@PreUpdate
protected void onUpdate() {
    this.fechaModificacion = LocalDateTime.now();
}
```
**Lifecycle callbacks:**
- `@PrePersist`: Antes de INSERT
- `@PostPersist`: Después de INSERT
- `@PreUpdate`: Antes de UPDATE
- `@PostUpdate`: Después de UPDATE
- `@PreRemove`: Antes de DELETE

---

## 2️⃣ Coordenada (Embebible)

### 📝 Descripción
Representa coordenadas geográficas (latitud/longitud). No es una entidad, se embebe en otras.

```java
@Embeddable
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Coordenada {
    
    @Column(name = "latitud")
    private Double latitud;
    
    @Column(name = "longitud")
    private Double longitud;
    
    /**
     * Calcula distancia en kilómetros a otra coordenada (Haversine)
     */
    public Double distanciaA(Coordenada otra) {
        if (otra == null) return null;
        
        final int R = 6371; // Radio de la Tierra en km
        
        double latDistance = Math.toRadians(otra.latitud - this.latitud);
        double lonDistance = Math.toRadians(otra.longitud - this.longitud);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(this.latitud)) 
                * Math.cos(Math.toRadians(otra.latitud))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
}
```

**@Embeddable**: Indica que esta clase se puede embeber en entidades.

---

## 3️⃣ Presupuesto

### 📝 Descripción
Representa un presupuesto con período, monto y estado.

```java
@Entity
@Table(name = "presupuestos")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Presupuesto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // ==========================================
    // RELACIÓN CON USUARIO (MUCHOS A UNO)
    // ==========================================
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    // ==========================================
    // INFORMACIÓN BÁSICA
    // ==========================================
    
    @Column(nullable = false, length = 150)
    private String nombre;
    
    @Column(length = 500)
    private String descripcion;
    
    @Column(name = "monto_total", nullable = false)
    private Double montoTotal;
    
    @Column(name = "monto_gastado")
    @Builder.Default
    private Double montoGastado = 0.0;
    
    // ==========================================
    // PERÍODO Y ESTADO
    // ==========================================
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BudgetPeriod periodo = BudgetPeriod.MONTHLY;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BudgetStatus status = BudgetStatus.DRAFT;
    
    // ==========================================
    // FECHAS
    // ==========================================
    
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;
    
    @Column(name = "fecha_fin", nullable = false)
    private LocalDateTime fechaFin;
    
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    
    // ==========================================
    // RELACIONES (UNO A MUCHOS)
    // ==========================================
    
    @OneToMany(mappedBy = "presupuesto", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Expense> gastos = new ArrayList<>();
    
    @OneToMany(mappedBy = "presupuesto", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CategoryLimit> limitesCategorias = new ArrayList<>();
    
    // ==========================================
    // MÉTODOS DE NEGOCIO
    // ==========================================
    
    /**
     * Calcula el porcentaje gastado del presupuesto
     */
    public Double getPorcentajeGastado() {
        if (montoTotal == null || montoTotal == 0) return 0.0;
        return (montoGastado / montoTotal) * 100;
    }
    
    /**
     * Calcula el monto restante
     */
    public Double getMontoRestante() {
        return montoTotal - montoGastado;
    }
    
    /**
     * Verifica si está cerca del límite (>80%)
     */
    public Boolean estaCercaDelLimite() {
        return getPorcentajeGastado() >= 80.0;
    }
    
    /**
     * Verifica si el presupuesto está excedido
     */
    public Boolean estaExcedido() {
        return montoGastado > montoTotal;
    }
    
    /**
     * Verifica si está vigente en este momento
     */
    public Boolean estaVigente() {
        LocalDateTime ahora = LocalDateTime.now();
        return ahora.isAfter(fechaInicio) && ahora.isBefore(fechaFin);
    }
    
    /**
     * Registra un gasto en el presupuesto
     */
    public void registrarGasto(Double monto) {
        if (!status.getPuedeRegistrarGastos()) {
            throw new IllegalStateException("El presupuesto no permite registrar gastos");
        }
        
        this.montoGastado += monto;
        
        // Auto-cambiar estado si se excede
        if (estaExcedido() && status == BudgetStatus.ACTIVE) {
            this.status = BudgetStatus.EXCEEDED;
        }
    }
    
    /**
     * Activa el presupuesto
     */
    public void activar() {
        if (!status.puedeActivarse()) {
            throw new IllegalStateException("No se puede activar desde el estado: " + status);
        }
        this.status = BudgetStatus.ACTIVE;
    }
    
    /**
     * Pausa el presupuesto
     */
    public void pausar() {
        if (!status.puedePausarse()) {
            throw new IllegalStateException("No se puede pausar desde el estado: " + status);
        }
        this.status = BudgetStatus.PAUSED;
    }
}
```

### 🔍 Análisis de Relaciones

#### @ManyToOne (Lado Dueño)
```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "usuario_id", nullable = false)
private Usuario usuario;
```
- **Lado dueño**: Tiene `@JoinColumn` (crea FK en BD)
- `fetch = LAZY`: No carga usuario automáticamente (performance)
- `name = "usuario_id"`: Columna FK en tabla `presupuestos`

**SQL generado:**
```sql
CREATE TABLE presupuestos (
    id BIGINT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);
```

---

## 4️⃣ Expense (Gasto)

### 📝 Descripción
Representa un gasto individual realizado por un usuario.

```java
@Entity
@Table(name = "expenses")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Expense {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // ==========================================
    // RELACIONES (MUCHOS A UNO)
    // ==========================================
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "presupuesto_id")
    private Presupuesto presupuesto;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;
    
    // ==========================================
    // INFORMACIÓN DEL GASTO
    // ==========================================
    
    @Column(nullable = false)
    private Double monto;
    
    @Column(length = 500)
    private String descripcion;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false)
    private PaymentMethod metodoPago;
    
    @Column(name = "fecha_gasto", nullable = false)
    @Builder.Default
    private LocalDateTime fechaGasto = LocalDateTime.now();
    
    @Column(name = "fecha_registro", updatable = false)
    @Builder.Default
    private LocalDateTime fechaRegistro = LocalDateTime.now();
    
    // ==========================================
    // MÉTODOS DE NEGOCIO
    // ==========================================
    
    /**
     * Calcula el monto con comisión del método de pago
     */
    public Double getMontoConComision() {
        return metodoPago.calcularMontoConComision(monto);
    }
    
    /**
     * Obtiene la comisión aplicada
     */
    public Double getComision() {
        return metodoPago.calcularComision(monto);
    }
}
```

---

## 5️⃣ Categoria

### 📝 Descripción
Categorías para clasificar gastos y empresas.

```java
@Entity
@Table(name = "categorias")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categoria {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 100)
    private String nombre;
    
    @Column(length = 500)
    private String descripcion;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CategoryType tipo = CategoryType.EXPENSE;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_empresa_asociada")
    private TipoEmpresa tipoEmpresaAsociada;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean activa = true;
    
    // ==========================================
    // RELACIONES
    // ==========================================
    
    @OneToMany(mappedBy = "categoria")
    private List<Expense> gastos = new ArrayList<>();
    
    @OneToMany(mappedBy = "categoria")
    private List<Empresa> empresas = new ArrayList<>();
}
```

---

## 6️⃣ Empresa

### 📝 Descripción
Representa un negocio o empresa donde se realizan gastos.

```java
@Entity
@Table(name = "empresas")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Empresa {
    
    @Id
    @Column(length = 50)
    private String id;  // ID personalizado (ej: "REST-001")
    
    @Column(nullable = false, length = 200)
    private String nombre;
    
    @Column(length = 1000)
    private String descripcion;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_empresa", nullable = false)
    private TipoEmpresa tipoEmpresa;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;
    
    @Embedded
    private Coordenada ubicacion;
    
    @Column(length = 200)
    private String direccion;
    
    @Column(length = 20)
    private String telefono;
    
    @Column(name = "calificacion_promedio")
    @Builder.Default
    private Double calificacionPromedio = 0.0;
    
    @Column(name = "total_reviews")
    @Builder.Default
    private Integer totalReviews = 0;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean activa = true;
    
    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();
    
    @OneToMany(mappedBy = "empresa")
    private List<Expense> gastos = new ArrayList<>();
    
    /**
     * Actualiza la calificación promedio
     */
    public void actualizarCalificacion() {
        if (reviews.isEmpty()) {
            this.calificacionPromedio = 0.0;
            this.totalReviews = 0;
        } else {
            this.calificacionPromedio = reviews.stream()
                .mapToInt(Review::getCalificacion)
                .average()
                .orElse(0.0);
            this.totalReviews = reviews.size();
        }
    }
}
```

---

## 7️⃣ Otras Entidades

### CategoryLimit
```java
@Entity
@Table(name = "category_limits")
@Getter @Setter
public class CategoryLimit {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "presupuesto_id")
    private Presupuesto presupuesto;
    
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;
    
    @Column(name = "limite_asignado")
    private Double limiteAsignado;
    
    @Column(name = "gasto_actual")
    @Builder.Default
    private Double gastoActual = 0.0;
    
    public Double getPorcentajeUtilizado() {
        return (gastoActual / limiteAsignado) * 100;
    }
}
```

### Cuenta
```java
@Entity
@Table(name = "cuentas")
@Getter @Setter
public class Cuenta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cuenta")
    private AccountType tipoCuenta;
    
    @Column(nullable = false)
    @Builder.Default
    private Double saldo = 0.0;
    
    public void depositar(Double monto) {
        this.saldo += monto;
    }
    
    public void retirar(Double monto) {
        if (monto > saldo) {
            throw new SaldoInsuficienteException();
        }
        this.saldo -= monto;
    }
}
```

### Review
```java
@Entity
@Table(name = "reviews")
@Getter @Setter
public class Review {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    
    @ManyToOne
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;
    
    @Column(nullable = false)
    private Integer calificacion;  // 1-5
    
    @Column(length = 1000)
    private String comentario;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean verificada = false;
    
    @Column(name = "fecha_creacion")
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}
```

### MLOptimization
```java
@Entity
@Table(name = "ml_optimizations")
@Getter @Setter
public class MLOptimization {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    
    @ManyToOne
    @JoinColumn(name = "presupuesto_id")
    private Presupuesto presupuesto;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OptimizationType tipo;
    
    @Column(length = 1000)
    private String mensaje;
    
    @Column(name = "aplicada")
    @Builder.Default
    private Boolean aplicada = false;
    
    @Column(name = "fecha_generacion")
    @Builder.Default
    private LocalDateTime fechaGeneracion = LocalDateTime.now();
}
```

### HistorialBusqueda
```java
@Entity
@Table(name = "historial_busquedas")
@Getter @Setter
public class HistorialBusqueda {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    
    @Column(nullable = false)
    private String consulta;
    
    @Column(name = "fecha")
    @Builder.Default
    private LocalDateTime fecha = LocalDateTime.now();
}
```

---

## 🎯 Diagrama de Relaciones

```
Usuario (1) ──────< (N) Presupuesto
   │                       │
   │                       │
   │                       └──< (N) Expense
   │                       │
   │                       └──< (N) CategoryLimit ──> (1) Categoria
   │
   ├──< (N) Expense ──────> (1) Categoria
   │         │
   │         └──────────> (1) Empresa ──> (1) Categoria
   │
   ├──< (N) Review ───────> (1) Empresa
   │
   ├──< (N) HistorialBusqueda
   │
   ├──< (N) MLOptimization
   │
   └──< (1:1) Cuenta
```

**Leyenda:**
- `(1)`: Uno
- `(N)`: Muchos
- `──<`: Lado no dueño (@OneToMany)
- `>──`: Lado dueño (@ManyToOne)

---

## 🎯 Buenas Prácticas

### ✅ DO (Hacer)
```java
// Usar @Builder para crear objetos
Usuario usuario = Usuario.builder()
    .nombre("Juan")
    .email("juan@example.com")
    .accountType(AccountType.PREMIUM)
    .build();

// Inicializar colecciones
@Builder.Default
private List<Presupuesto> presupuestos = new ArrayList<>();

// Usar LAZY para relaciones
@ManyToOne(fetch = FetchType.LAZY)

// Métodos de negocio en entidades
public Double getPorcentajeGastado() {
    return (montoGastado / montoTotal) * 100;
}
```

### ❌ DON'T (Evitar)
```java
// NO olvidar inicializar colecciones
private List<Presupuesto> presupuestos;  // ❌ NullPointerException

// NO usar EAGER innecesariamente
@ManyToOne(fetch = FetchType.EAGER)  // ❌ Performance

// NO lógica compleja en entidades
// (mover a Services)

// NO exponer setters de fechas de auditoría
public void setFechaCreacion(LocalDateTime fecha) {}  // ❌
```

---

## 📚 Resumen

✅ **Entidades** mapean tablas en la BD  
✅ **Lombok** reduce código boilerplate  
✅ **Relaciones**: @OneToMany, @ManyToOne, @OneToOne  
✅ **Enums**: Usar `EnumType.STRING`  
✅ **Lazy Loading**: Para mejor performance  
✅ **Métodos de Negocio**: Lógica simple en entidades  

---
