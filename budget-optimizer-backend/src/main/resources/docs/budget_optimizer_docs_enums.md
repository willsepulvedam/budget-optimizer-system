# Budget Optimizer Backend - Documentación Completa
## Parte 2: Enums de Negocio (Metaprogramación)

---

## 📋 ¿Qué son los Enums en este Proyecto?

Los **Enums** son tipos especiales de Java que definen conjuntos cerrados de valores constantes. En este proyecto están **potenciados con metaprogramación**:

✅ No solo definen valores, sino también **comportamientos**  
✅ Encapsulan lógica de negocio relacionada  
✅ Evitan código duplicado y hardcoding  
✅ Facilitan mantenimiento y extensión

---

## 1️⃣ AccountType (Tipo de Cuenta)

### 📝 Descripción
Unifica **tipos de usuario** y **beneficios de cuenta** en un solo enum. Reemplaza a UserRole + TipoCuenta.

### 🎯 Valores Disponibles

| Valor | Display Name | Descuento | Transacciones/mes | Nivel | Admin Empresas |
|-------|--------------|-----------|-------------------|-------|----------------|
| `USER` | Usuario Básico | 0% | 100 | 1 | ❌ |
| `PREMIUM` | Usuario Premium | 10% | 1000 | 2 | ❌ |
| `BUSINESS` | Empresa/Vendedor | 5% | 500 | 2 | ✅ |
| `ADMIN` | Administrador | 15% | 999999 | 3 | ✅ |

### 🔧 Métodos Principales

#### Permisos y Roles
```java
// Verificar si es administrador
accountType.esAdmin()  // true solo para ADMIN

// Verificar acceso premium
accountType.tienePremium()  // true para PREMIUM y ADMIN

// Comparar niveles
PREMIUM.esSuperiorA(USER)  // true

// Verificar cuenta de negocio
accountType.esCuentaNegocio()  // true para BUSINESS y ADMIN
```

#### Cálculos de Descuento
```java
AccountType tipo = AccountType.PREMIUM;

// Aplicar descuento (10%)
Double precioFinal = tipo.aplicarDescuento(100.0);  // 90.0

// Calcular ahorro
Double ahorro = tipo.calcularAhorro(100.0);  // 10.0

// Alias más semántico
Double total = tipo.calcularMontoFinal(100.0);  // 90.0
```

#### Gestión de Transacciones
```java
AccountType tipo = AccountType.USER;  // Límite: 100

// Verificar si puede hacer otra transacción
Boolean puede = tipo.puedeRealizarTransaccion(95);  // true
Boolean noPuede = tipo.puedeRealizarTransaccion(100);  // false

// Calcular porcentaje usado
Double porcentaje = tipo.calcularPorcentajeTransaccionesUsadas(80);  // 80.0%

// Transacciones restantes
Integer restantes = tipo.calcularTransaccionesRestantes(95);  // 5

// Alerta cerca del límite (>80%)
Boolean alerta = tipo.estaCercaDelLimite(85);  // true
```

#### Upgrade de Cuenta
```java
AccountType actual = AccountType.USER;

// Obtener siguiente nivel
AccountType siguiente = actual.obtenerSiguienteNivel();  // PREMIUM

// Verificar si puede hacer upgrade
Boolean puede = actual.puedeHacerUpgrade();  // true (ADMIN devuelve false)

// Calcular beneficio de upgrade
Double beneficio = actual.calcularBeneficioUpgrade(500.0);  // 50.0 ($50 más de ahorro)
```

#### Utilidades
```java
// Icono para UI
String icono = AccountType.PREMIUM.getIcono();  // "⭐"

// Color hexadecimal
String color = AccountType.PREMIUM.getColorHex();  // "#FFD700"

// Descripción completa
String desc = AccountType.PREMIUM.getDescripcionCompleta();
// "⭐ Usuario Premium - Cuenta premium con máximos beneficios..."

// Buscar por nombre
AccountType tipo = AccountType.fromString("premium");  // PREMIUM

// Recomendar según gasto
AccountType recomendado = AccountType.recomendarSegunGasto(1000.0);  // PREMIUM
```

### 💡 Ejemplo de Uso Completo
```java
// En un servicio de facturación
@Service
public class ServicioFacturacion {
    
    public Double calcularPrecioFinal(Usuario usuario, Double precioBase) {
        AccountType tipo = usuario.getAccountType();
        
        // Aplicar descuento según tipo de cuenta
        Double precioConDescuento = tipo.aplicarDescuento(precioBase);
        
        // Verificar límite de transacciones
        if (!tipo.puedeRealizarTransaccion(usuario.getTransaccionesMes())) {
            throw new LimiteExcedidoException(
                "Has alcanzado tu límite de " + tipo.getLimiteTransaccionesMes() + " transacciones"
            );
        }
        
        // Sugerir upgrade si es conveniente
        if (tipo.puedeHacerUpgrade()) {
            Double ahorroPotencial = tipo.calcularBeneficioUpgrade(usuario.getGastoMensual());
            if (ahorroPotencial > 20) {
                enviarNotificacionUpgrade(usuario, ahorroPotencial);
            }
        }
        
        return precioConDescuento;
    }
}
```

---

## 2️⃣ BudgetPeriod (Período de Presupuesto)

### 📝 Descripción
Define los períodos de tiempo para presupuestos con cálculos automáticos.

### 🎯 Valores Disponibles

| Valor | Display Name | Duración (días) | Períodos/año |
|-------|--------------|-----------------|--------------|
| `DAILY` | Diario | 1 | 365 |
| `WEEKLY` | Semanal | 7 | 52 |
| `BIWEEKLY` | Quincenal | 15 | 24 |
| `MONTHLY` | Mensual | 30 | 12 |
| `QUARTERLY` | Trimestral | 90 | 4 |
| `BIANNUAL` | Semestral | 180 | 2 |
| `YEARLY` | Anual | 365 | 1 |
| `CUSTOM` | Personalizado | 0 | 0 |

### 🔧 Métodos Principales

```java
BudgetPeriod periodo = BudgetPeriod.MONTHLY;

// Presupuesto diario promedio
Double diario = periodo.calcularPresupuestoDiario(3000.0);  // 100.0

// Proyección anual
Double anual = periodo.calcularPresupuestoAnual(3000.0);  // 36000.0

// Verificar si es corto plazo (<30 días)
Boolean corto = periodo.esCortoPlazo();  // false

// Siguiente período
BudgetPeriod siguiente = periodo.obtenerSiguientePeriodo();  // QUARTERLY

// Buscar por nombre
BudgetPeriod p = BudgetPeriod.fromString("Mensual");  // MONTHLY
```

---

## 3️⃣ BudgetStatus (Estado de Presupuesto)

### 📝 Descripción
Estados del ciclo de vida de un presupuesto con lógica de transición.

### 🎯 Valores Disponibles

| Valor | Display Name | ¿Puede Editar? | ¿Puede Gastar? | Color |
|-------|--------------|----------------|----------------|-------|
| `DRAFT` | Borrador | ✅ | ❌ | Gris |
| `ACTIVE` | Activo | ❌ | ✅ | Verde |
| `PAUSED` | Pausado | ✅ | ❌ | Naranja |
| `COMPLETED` | Completado | ❌ | ❌ | Azul |
| `EXCEEDED` | Excedido | ❌ | ✅ | Rojo |
| `CANCELLED` | Cancelado | ❌ | ❌ | Gris oscuro |
| `ARCHIVED` | Archivado | ❌ | ❌ | Gris azulado |

### 🔧 Métodos Principales

```java
BudgetStatus status = BudgetStatus.ACTIVE;

// Verificar si está finalizado
Boolean finalizado = status.esFinal();  // false (true para COMPLETED, CANCELLED, ARCHIVED)

// Verificar transiciones
Boolean puedeActivar = status.puedeActivarse();  // false (true para DRAFT, PAUSED)
Boolean puedePausar = status.puedePausarse();  // true (true para ACTIVE, EXCEEDED)

// Color para UI
String color = status.getColorHex();  // "#4CAF50"
```

### 💡 Diagrama de Transiciones
```
DRAFT → ACTIVE → EXCEEDED → PAUSED → ACTIVE
  ↓       ↓         ↓
CANCELLED  ↓      COMPLETED
      ARCHIVED
```

---

## 4️⃣ CategoryType (Tipo de Categoría)

### 📝 Descripción
Define el uso de categorías (gastos, empresas o ambos).

### 🎯 Valores Disponibles

| Valor | Display Name | Para Gastos | Para Empresas | Icono |
|-------|--------------|-------------|---------------|-------|
| `EXPENSE` | Gasto | ✅ | ❌ | 📝 |
| `BUSINESS` | Empresa | ❌ | ✅ | 🏢 |
| `BOTH` | Ambos | ✅ | ✅ | 🔄 |

### 🔧 Métodos Principales

```java
CategoryType tipo = CategoryType.BOTH;

// Verificar usos
Boolean paraGastos = tipo.puedeUsarseParaGastos();  // true
Boolean paraEmpresas = tipo.puedeUsarseParaEmpresas();  // true
Boolean versatil = tipo.esVersatil();  // true
```

---

## 5️⃣ OptimizationType (Tipo de Optimización ML)

### 📝 Descripción
Tipos de recomendaciones que genera el sistema de ML.

### 🎯 Valores Disponibles

| Valor | Prioridad | Requiere Acción | Icono |
|-------|-----------|-----------------|-------|
| `ALERT` | 1 (Alta) | ✅ | ⚠️ |
| `WARNING` | 2 | ✅ | ⚡ |
| `SUGGESTION` | 2 | ✅ | 💡 |
| `RECOMMENDATION` | 2 | ✅ | 🏪 |
| `SAVINGS_OPPORTUNITY` | 1 | ✅ | 💰 |
| `PREDICTION` | 3 | ❌ | 📊 |
| `INSIGHT` | 3 | ❌ | 🔍 |
| `GOAL_TRACKING` | 3 | ❌ | 🎯 |
| `ANALYSIS` | 4 | ❌ | 📈 |

### 🔧 Métodos Principales

```java
OptimizationType tipo = OptimizationType.ALERT;

// Verificar prioridad
Boolean altaPrioridad = tipo.esAltaPrioridad();  // true (prioridad <= 2)
Boolean critico = tipo.esCritico();  // true (ALERT o WARNING)
Boolean informativo = tipo.esInformativo();  // false

// Color según prioridad
String color = tipo.getColorByPriority();  // "#F44336" (rojo)
```

---

## 6️⃣ PaymentMethod (Método de Pago)

### 📝 Descripción
Métodos de pago con comisiones y características.

### 🎯 Valores Destacados

| Valor | Display Name | Comisión | Instantáneo | Trazable |
|-------|--------------|----------|-------------|----------|
| `CASH` | Efectivo | 0% | ✅ | ❌ |
| `DEBIT_CARD` | Tarjeta de Débito | 0% | ✅ | ✅ |
| `CREDIT_CARD` | Tarjeta de Crédito | 2.5% | ❌ | ✅ |
| `MOBILE_PAYMENT` | Pago Móvil | 1% | ✅ | ✅ |
| `QR_CODE` | Código QR | 0.5% | ✅ | ✅ |
| `CRYPTOCURRENCY` | Criptomoneda | 3% | ❌ | ✅ |

### 🔧 Métodos Principales

```java
PaymentMethod metodo = PaymentMethod.CREDIT_CARD;

// Calcular comisión
Double comision = metodo.calcularComision(100.0);  // 2.5

// Monto total con comisión
Double total = metodo.calcularMontoConComision(100.0);  // 102.5

// Verificaciones
Boolean digital = metodo.esMetodoDigital();  // true
Boolean tieneComision = metodo.tieneComision();  // true
```

---

## 7️⃣ TipoEmpresa (Tipo de Empresa)

### 📝 Descripción
Clasificación de empresas con datos de negocio.

### 🎯 Valores Disponibles

| Valor | Categoría | Gasto Promedio | Ubicación Fija |
|-------|-----------|----------------|----------------|
| `RESTAURANTE` | Alimentación | $50 | ✅ |
| `GIMNASIO` | Deporte y Bienestar | $30 | ✅ |
| `ABASTO` | Comercio | $20 | ✅ |
| `PANADERIA` | Alimentación | $15 | ✅ |
| `TIENDA` | Comercio | $25 | ✅ |
| `VENDEDOR_AMBULANTE` | Comercio Informal | $10 | ❌ |

### 🔧 Métodos Principales

```java
TipoEmpresa tipo = TipoEmpresa.RESTAURANTE;

// Estimación de presupuesto
Double presupuesto = tipo.calcularPresupuestoEstimado(50);  // 75000 (50 clientes * $50 * 30 días)

// Verificaciones de categoría
Boolean esAlimentacion = tipo.esAlimentacion();  // true
Boolean esComercio = tipo.esComercio();  // false

// Factor de ajuste para ML
Double factor = tipo.getFactorAjustePresupuesto();  // 1.5

// Radio de cobertura
Boolean necesitaRadio = tipo.necesitaRadioCobertura();  // false

// Filtrar por categoría
List<TipoEmpresa> alimentacion = TipoEmpresa.getPorCategoria("Alimentación");
```

---

## 🎯 Buenas Prácticas con Enums

### ✅ DO (Hacer)
```java
// Usar los métodos del enum
Double descuento = accountType.aplicarDescuento(precio);

// Comparar con ==
if (status == BudgetStatus.ACTIVE) { ... }

// Usar switch expressions
String mensaje = switch (periodo) {
    case DAILY -> "Presupuesto diario";
    case MONTHLY -> "Presupuesto mensual";
    default -> "Otro período";
};
```

### ❌ DON'T (Evitar)
```java
// NO hardcodear strings
if (cuenta.getTipo().equals("PREMIUM")) { ... }  // ❌

// NO calcular manualmente
double descuento = monto * 0.10;  // ❌
// Usa: accountType.aplicarDescuento(monto) ✅

// NO comparar con .equals() innecesariamente
if (status.equals(BudgetStatus.ACTIVE)) { ... }  // ❌
// Usa: if (status == BudgetStatus.ACTIVE) ✅
```

---

## 📚 Siguiente Parte

Continúa con **Parte 3: Repositorios** para ver cómo estos enums se usan en consultas a la base de datos.
