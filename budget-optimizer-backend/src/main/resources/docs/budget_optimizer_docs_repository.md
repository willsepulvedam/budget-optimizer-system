# Budget Optimizer Backend - Documentación Completa
## Parte 3: Repositorios (Capa de Acceso a Datos)

---

## 📋 ¿Qué es un Repository en Spring Data JPA?

Los **Repositories** son interfaces que gestionan el acceso a la base de datos sin escribir SQL manualmente:

✅ **Query Methods**: Spring genera consultas automáticamente por el nombre del método  
✅ **@Query**: Para consultas complejas con JPQL o SQL nativo  
✅ **Type-safe**: Errores en tiempo de compilación, no ejecución  
✅ **DRY**: No repetir código de acceso a datos

---

## 🎯 Filosofía de Diseño del Proyecto

### ✅ Preferir Query Methods
```java
// Spring genera automáticamente la query
List<Usuario> findByEmail(String email);
List<Presupuesto> findByUsuarioIdAndStatus(Long id, BudgetStatus status);
```

### ⚠️ Usar @Query Solo Cuando Sea Necesario
```java
// Casos válidos para @Query:
// 1. Agregaciones (SUM, AVG, COUNT)
// 2. Cálculos geoespaciales
// 3. Lógica OR con valores específicos
// 4. GROUP BY
// 5. Joins complejos
@Query("SELECT SUM(e.monto) FROM Expense e WHERE e.presupuesto.id = :id")
Double sumMontoByPresupuestoId(@Param("id") Long id);
```

---

## 1️⃣ UsuarioRepository

### 📝 Entidad: Usuario
Representa un usuario del sistema con su ubicación, tipo de cuenta y presupuesto base.

### 🔍 Query Methods Automáticos

#### Búsqueda Básica
```java
// Buscar por email (único)
Optional<Usuario> findByEmail(String email);

// Verificar existencia
boolean existsByEmail(String email);

// Búsqueda parcial case-insensitive
List<Usuario> findByNombreContainingIgnoreCase(String texto);
```

**Ejemplo de uso:**
```java
@Service
public class UsuarioService {
    
    public Usuario buscarPorEmail(String email) {
        return usuarioRepo.findByEmail(email)
            .orElseThrow(() -> new UsuarioNoEncontradoException(email));
    }
    
    public boolean emailYaRegistrado(String email) {
        return usuarioRepo.existsByEmail(email);
    }
}
```

#### Por Tipo de Cuenta
```java
// Todos de un tipo
List<Usuario> findByAccountType(AccountType accountType);

// Contar por tipo
long countByAccountType(AccountType accountType);

// Múltiples tipos (IN clause)
List<Usuario> findByAccountTypeIn(List<AccountType> types);
```

**Ejemplo:**
```java
// Obtener todos los usuarios premium
List<Usuario> premiums = usuarioRepo.findByAccountType(AccountType.PREMIUM);

// Usuarios premium o business
List<Usuario> especiales = usuarioRepo.findByAccountTypeIn(
    Arrays.asList(AccountType.PREMIUM, AccountType.BUSINESS)
);
```

#### Por Estado
```java
// Solo activos
List<Usuario> findByActivoTrue();

// Solo inactivos
List<Usuario> findByActivoFalse();

// Combinado: tipo Y activo
List<Usuario> findByAccountTypeAndActivoTrue(AccountType type);
```

#### Por Presupuesto
```java
// Mayor que
List<Usuario> findByPresupuestoMensualBaseGreaterThan(Double monto);

// Rango (BETWEEN)
List<Usuario> findByPresupuestoMensualBaseBetween(Double min, Double max);

// Menor que
List<Usuario> findByPresupuestoMensualBaseLessThan(Double monto);
```

**Ejemplo:**
```java
// Usuarios con presupuesto alto (>$5000)
List<Usuario> altosGastadores = usuarioRepo
    .findByPresupuestoMensualBaseGreaterThan(5000.0);

// Rango medio ($1000-$3000)
List<Usuario> medios = usuarioRepo
    .findByPresupuestoMensualBaseBetween(1000.0, 3000.0);
```

#### Por Fechas
```java
// Registrados después de una fecha
List<Usuario> findByFechaCreacionAfter(LocalDateTime fecha);

// Rango de fechas
List<Usuario> findByFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin);
```

#### Ordenamiento
```java
// Top 10 con mayor presupuesto
List<Usuario> findTop10ByActivoTrueOrderByPresupuestoMensualBaseDesc();

// Más recientes
List<Usuario> findByActivoTrueOrderByFechaCreacionDesc();
```

### 🔍 @Query para Casos Complejos

#### Búsqueda Geoespacial
```java
@Query("SELECT u FROM Usuario u WHERE " +
       "6371 * acos(cos(radians(:lat)) * cos(radians(u.ubicacion.latitud)) * " +
       "cos(radians(u.ubicacion.longitud) - radians(:lng)) + " +
       "sin(radians(:lat)) * sin(radians(u.ubicacion.latitud))) <= :radioKm " +
       "AND u.activo = true")
List<Usuario> findUsuariosCercanos(
    @Param("lat") double latitud,
    @Param("lng") double longitud,
    @Param("radioKm") double radioKm
);
```

**¿Por qué @Query?** Fórmula Haversine para calcular distancia en esfera.

**Ejemplo de uso:**
```java
// Usuarios en un radio de 10km
List<Usuario> cercanos = usuarioRepo.findUsuariosCercanos(
    10.4236,  // Latitud Cartagena
    -75.5223, // Longitud Cartagena
    10.0      // 10 km de radio
);
```

#### Usuarios Sin Presupuestos
```java
@Query("SELECT u FROM Usuario u WHERE " +
       "u.activo = true AND " +
       "SIZE(u.presupuestos) = 0")
List<Usuario> findUsuariosSinPresupuestos();
```

**¿Por qué @Query?** Necesita verificar tamaño de colección relacionada.

---

## 2️⃣ PresupuestoRepository

### 📝 Entidad: Presupuesto
Representa un presupuesto con período, monto y estado.

### 🔍 Query Methods Automáticos

#### Por Usuario
```java
List<Presupuesto> findByUsuarioId(Long usuarioId);
List<Presupuesto> findByUsuarioIdAndStatus(Long usuarioId, BudgetStatus status);
long countByUsuarioId(Long usuarioId);
```

#### Por Estado
```java
List<Presupuesto> findByStatus(BudgetStatus status);
List<Presupuesto> findByStatusAndFechaFinAfter(BudgetStatus status, LocalDateTime fecha);
```

#### Por Período
```java
List<Presupuesto> findByPeriodo(BudgetPeriod periodo);
List<Presupuesto> findByPeriodoAndStatus(BudgetPeriod periodo, BudgetStatus status);
```

#### Por Fechas
```java
List<Presupuesto> findByFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin);
List<Presupuesto> findByFechaFinBetween(LocalDateTime inicio, LocalDateTime fin);
```

#### Por Monto
```java
List<Presupuesto> findByMontoTotalGreaterThan(Double monto);
List<Presupuesto> findByMontoTotalBetween(Double min, Double max);
```

#### Ordenamiento
```java
List<Presupuesto> findTop10ByStatusOrderByMontoTotalDesc(BudgetStatus status);
List<Presupuesto> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);
```

### 🔍 @Query para Casos Complejos

#### Presupuestos Vigentes
```java
@Query("SELECT p FROM Presupuesto p WHERE " +
       ":fecha BETWEEN p.fechaInicio AND p.fechaFin")
List<Presupuesto> findPresupuestosVigentes(@Param("fecha") LocalDateTime fecha);
```

**Ejemplo:**
```java
// Presupuestos activos ahora
List<Presupuesto> vigentes = presupuestoRepo
    .findPresupuestosVigentes(LocalDateTime.now());
```

#### Presupuesto Activo Actual
```java
@Query("SELECT p FROM Presupuesto p WHERE " +
       "p.usuario.id = :usuarioId AND " +
       "p.status = 'ACTIVE' AND " +
       ":ahora BETWEEN p.fechaInicio AND p.fechaFin " +
       "ORDER BY p.fechaCreacion DESC")
Optional<Presupuesto> findPresupuestoActivoActual(
    @Param("usuarioId") Long usuarioId,
    @Param("ahora") LocalDateTime ahora
);
```

**¿Por qué @Query?** Combina múltiples condiciones con BETWEEN.

#### Próximos a Vencer
```java
@Query("SELECT p FROM Presupuesto p WHERE " +
       "p.status = 'ACTIVE' AND " +
       "p.fechaFin BETWEEN :ahora AND :fechaLimite")
List<Presupuesto> findPresupuestosProximosAVencer(
    @Param("ahora") LocalDateTime ahora,
    @Param("fechaLimite") LocalDateTime fechaLimite
);
```

**Ejemplo:**
```java
// Presupuestos que vencen en 7 días
List<Presupuesto> proximos = presupuestoRepo.findPresupuestosProximosAVencer(
    LocalDateTime.now(),
    LocalDateTime.now().plusDays(7)
);
```

---

## 3️⃣ ExpenseRepository

### 📝 Entidad: Expense (Gasto)
Representa un gasto registrado por un usuario.

### 🔍 Query Methods Automáticos

#### Por Usuario
```java
List<Expense> findByUsuarioId(Long usuarioId);
List<Expense> findByUsuarioIdOrderByFechaGastoDesc(Long usuarioId);
```

#### Por Presupuesto
```java
List<Expense> findByPresupuestoId(Long presupuestoId);
```

#### Por Categoría
```java
List<Expense> findByCategoriaId(Long categoriaId);
List<Expense> findByUsuarioIdAndCategoriaId(Long usuarioId, Long categoriaId);
```

#### Por Empresa
```java
List<Expense> findByEmpresaId(String empresaId);
List<Expense> findByUsuarioIdAndEmpresaId(Long usuarioId, String empresaId);
```

#### Por Método de Pago
```java
List<Expense> findByMetodoPago(PaymentMethod metodoPago);
List<Expense> findByUsuarioIdAndMetodoPago(Long usuarioId, PaymentMethod metodoPago);
```

#### Por Fechas
```java
List<Expense> findByFechaGastoBetween(LocalDateTime inicio, LocalDateTime fin);
List<Expense> findByUsuarioIdAndFechaGastoBetween(Long usuarioId, LocalDateTime inicio, LocalDateTime fin);
```

#### Por Monto
```java
List<Expense> findByMontoGreaterThan(Double monto);
List<Expense> findByMontoBetween(Double min, Double max);
List<Expense> findTop10ByUsuarioIdOrderByMontoDesc(Long usuarioId);
```

#### Por Descripción
```java
List<Expense> findByDescripcionContainingIgnoreCase(String keyword);
List<Expense> findByUsuarioIdAndDescripcionContainingIgnoreCase(Long usuarioId, String keyword);
```

### 🔍 @Query para Agregaciones

#### Suma Total
```java
@Query("SELECT COALESCE(SUM(e.monto), 0) FROM Expense e WHERE e.presupuesto.id = :presupuestoId")
Double sumMontoByPresupuestoId(@Param("presupuestoId") Long presupuestoId);

@Query("SELECT COALESCE(SUM(e.monto), 0) FROM Expense e WHERE e.categoria.id = :categoriaId")
Double sumMontoByCategoriaId(@Param("categoriaId") Long categoriaId);
```

**¿Por qué @Query?** Necesita SUM() y COALESCE (para evitar null).

#### Gastos por Categoría (GROUP BY)
```java
@Query("SELECT e.categoria.nombre, SUM(e.monto) " +
       "FROM Expense e " +
       "WHERE e.usuario.id = :usuarioId " +
       "GROUP BY e.categoria.nombre " +
       "ORDER BY SUM(e.monto) DESC")
List<Object[]> findGastosPorCategoria(@Param("usuarioId") Long usuarioId);
```

**Ejemplo de uso:**
```java
List<Object[]> gastos = expenseRepo.findGastosPorCategoria(userId);
for (Object[] row : gastos) {
    String categoria = (String) row[0];
    Double total = (Double) row[1];
    System.out.println(categoria + ": $" + total);
}
```

#### Promedio por Categoría
```java
@Query("SELECT e.categoria.nombre, AVG(e.monto) " +
       "FROM Expense e " +
       "WHERE e.usuario.id = :usuarioId " +
       "GROUP BY e.categoria.nombre")
List<Object[]> findPromedioGastoPorCategoria(@Param("usuarioId") Long usuarioId);
```

---

## 4️⃣ CategoriaRepository

### 📝 Entidad: Categoria
Representa categorías para gastos y/o empresas.

### 🔍 Query Methods Automáticos

#### Búsqueda Básica
```java
Optional<Categoria> findByNombre(String nombre);
boolean existsByNombre(String nombre);
List<Categoria> findByNombreContainingIgnoreCase(String texto);
```

#### Por Estado
```java
List<Categoria> findByActivaTrue();
List<Categoria> findByActivaFalse();
```

#### Por Tipo
```java
List<Categoria> findByTipo(CategoryType tipo);
List<Categoria> findByTipoAndActivaTrue(CategoryType tipo);
```

#### Por Tipo de Empresa
```java
List<Categoria> findByTipoEmpresaAsociada(TipoEmpresa tipoEmpresa);
List<Categoria> findByTipoEmpresaAsociadaIsNull();
List<Categoria> findByTipoEmpresaAsociadaIsNullAndActivaTrue();
```

### 🔍 @Query para Lógica OR

#### Categorías para Gastos (EXPENSE o BOTH)
```java
@Query("SELECT c FROM Categoria c WHERE " +
       "(c.tipo = 'EXPENSE' OR c.tipo = 'BOTH') AND c.activa = true")
List<Categoria> findCategoriasParaGastos();
```

**¿Por qué @Query?** Necesita OR con valores específicos del enum.

#### Categorías para Empresas (BUSINESS o BOTH)
```java
@Query("SELECT c FROM Categoria c WHERE " +
       "(c.tipo = 'BUSINESS' OR c.tipo = 'BOTH') AND c.activa = true")
List<Categoria> findCategoriasParaEmpresas();
```

---

## 5️⃣ EmpresaRepository

### 📝 Entidad: Empresa
Representa empresas/negocios registrados.

### 🔍 Query Methods Automáticos
```java
List<Empresa> findByActivaTrue();
List<Empresa> findByTipoEmpresa(TipoEmpresa tipo);
List<Empresa> findByNombreContainingIgnoreCase(String nombre);
List<Empresa> findByCalificacionPromedioGreaterThanEqual(Double calificacion);
```

### 🔍 @Query para Búsqueda Geoespacial
```java
@Query("SELECT e FROM Empresa e WHERE " +
       "6371 * acos(cos(radians(:lat)) * cos(radians(e.ubicacion.latitud)) * " +
       "cos(radians(e.ubicacion.longitud) - radians(:lng)) + " +
       "sin(radians(:lat)) * sin(radians(e.ubicacion.latitud))) <= :radioKm " +
       "AND e.activa = true")
List<Empresa> findEmpresasCercanas(
    @Param("lat") double lat, 
    @Param("lng") double lng, 
    @Param("radioKm") double radioKm
);
```

**Ejemplo:**
```java
// Restaurantes en un radio de 5km
List<Empresa> restaurantes = empresaRepo.findEmpresasCercanas(
    10.4236, -75.5223, 5.0
);
```

---

## 6️⃣ CategoryLimitRepository

### 📝 Entidad: CategoryLimit
Límites de gasto por categoría dentro de un presupuesto.

### 🔍 Query Methods Automáticos
```java
List<CategoryLimit> findByPresupuestoId(Long presupuestoId);
List<CategoryLimit> findByCategoriaId(Long categoriaId);
Optional<CategoryLimit> findByPresupuestoIdAndCategoriaId(Long presupuestoId, Long categoriaId);
```

### 🔍 @Query para Alertas
```java
@Query("SELECT cl FROM CategoryLimit cl WHERE " +
       "cl.presupuesto.id = :presupuestoId AND " +
       "(cl.gastoActual / cl.limiteAsignado) >= 0.8")
List<CategoryLimit> findLimitesCercaDelTope(@Param("presupuestoId") Long presupuestoId);
```

**¿Por qué @Query?** Calcula porcentaje en la query (80% del límite).

---

## 7️⃣ ReviewRepository

### 📝 Entidad: Review
Reseñas de usuarios sobre empresas.

### 🔍 Query Methods Automáticos
```java
List<Review> findByEmpresaId(String empresaId);
List<Review> findByUsuarioId(Long usuarioId);
List<Review> findByEmpresaIdAndVerificadaTrue(String empresaId);
List<Review> findByCalificacionGreaterThanEqual(Integer calificacion);
long countByEmpresaId(String empresaId);
```

### 🔍 @Query para Promedio
```java
@Query("SELECT AVG(r.calificacion) FROM Review r WHERE r.empresa.id = :empresaId")
Double calcularPromedioCalificacion(@Param("empresaId") String empresaId);
```

---

## 8️⃣ Repositorios Simples

### CuentaRepository
```java
Optional<Cuenta> findByUsuarioId(Long usuarioId);
List<Cuenta> findByTipoCuenta(AccountType tipoCuenta);
List<Cuenta> findBySaldoGreaterThan(Double saldo);
```

### MLOptimizationRepository
```java
List<MLOptimization> findByUsuarioId(Long usuarioId);
List<MLOptimization> findByUsuarioIdAndTipo(Long usuarioId, OptimizationType tipo);
List<MLOptimization> findByUsuarioIdAndAplicadaFalse(Long usuarioId);
List<MLOptimization> findByPresupuestoId(Long presupuestoId);
```

### HistorialBusquedaRepository
```java
List<HistorialBusqueda> findByUsuarioId(Long usuarioId);
List<HistorialBusqueda> findByUsuarioIdOrderByFechaDesc(Long usuarioId);
List<HistorialBusqueda> findTop10ByUsuarioIdOrderByFechaDesc(Long usuarioId);
```

---

## 🎯 Guía Rápida: ¿Query Method o @Query?

### ✅ Usa Query Method Si:
- Búsqueda por campos simples
- Comparaciones básicas (>, <, BETWEEN)
- AND/OR con pocos campos
- Ordenamiento simple
- EXISTS, COUNT simples

### ⚠️ Usa @Query Si:
- **Agregaciones**: SUM, AVG, COUNT con GROUP BY
- **Cálculos**: Fórmulas matemáticas (geoespacial)
- **OR con valores específicos**: `WHERE (campo = 'A' OR campo = 'B')`
- **Subqueries o Joins complejos**
- **SIZE() de colecciones**

---

## 📚 Siguiente Parte

Continúa con **Parte 4: Servicios** para ver cómo se usan estos repositorios en la lógica de negocio.
