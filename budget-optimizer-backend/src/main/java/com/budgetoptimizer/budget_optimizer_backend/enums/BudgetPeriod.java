package com.budgetoptimizer.budget_optimizer_backend.enums;
import lombok.Getter;

/**
 * Enum para los diferentes períodos de tiempo de un presupuesto
 * Incluye metaprogramación con duración en días y comportamientos
 */
@Getter
public enum BudgetPeriod {
    
    DAILY(
        "Diario",
        1,
        "Presupuesto para un día",
        365
    ),
    
    WEEKLY(
        "Semanal",
        7,
        "Presupuesto para una semana",
        52
    ),
    
    BIWEEKLY(
        "Quincenal",
        15,
        "Presupuesto para quince días",
        24
    ),
    
    MONTHLY(
        "Mensual",
        30,
        "Presupuesto para un mes",
        12
    ),
    
    QUARTERLY(
        "Trimestral",
        90,
        "Presupuesto para tres meses",
        4
    ),
    
    BIANNUAL(
        "Semestral",
        180,
        "Presupuesto para seis meses",
        2
    ),
    
    YEARLY(
        "Anual",
        365,
        "Presupuesto para un año",
        1
    ),
    
    CUSTOM(
        "Personalizado",
        0,
        "Período personalizado por el usuario",
        0
    );
    
    private final String displayName;
    private final Integer duracionDias;
    private final String descripcion;
    private final Integer periodosEnAnio;
    
    BudgetPeriod(String displayName, Integer duracionDias, 
                 String descripcion, Integer periodosEnAnio) {
        this.displayName = displayName;
        this.duracionDias = duracionDias;
        this.descripcion = descripcion;
        this.periodosEnAnio = periodosEnAnio;
    }
    
    /**
     * Calcula el presupuesto diario promedio
     */
    public Double calcularPresupuestoDiario(Double montoTotal) {
        if (montoTotal == null || montoTotal <= 0 || duracionDias == 0) {
            return 0.0;
        }
        return montoTotal / duracionDias;
    }
    
    /**
     * Calcula el presupuesto anual basado en este período
     */
    public Double calcularPresupuestoAnual(Double montoTotal) {
        if (montoTotal == null || montoTotal <= 0 || periodosEnAnio == 0) {
            return 0.0;
        }
        return montoTotal * periodosEnAnio;
    }
    
    /**
     * Verifica si es un período de corto plazo (menos de 30 días)
     */
    public Boolean esCortoPlazo() {
        return duracionDias > 0 && duracionDias < 30;
    }
    
    /**
     * Obtiene el siguiente período (para sugerencias de upgrade)
     */
    public BudgetPeriod obtenerSiguientePeriodo() {
        BudgetPeriod[] valores = BudgetPeriod.values();
        int indiceActual = this.ordinal();
        if (indiceActual < valores.length - 2) { // -2 para evitar CUSTOM
            return valores[indiceActual + 1];
        }
        return this;
    }
    
    /**
     * Busca un BudgetPeriod por nombre (case-insensitive)
     */
    public static BudgetPeriod fromString(String nombre) {
        for (BudgetPeriod periodo : BudgetPeriod.values()) {
            if (periodo.name().equalsIgnoreCase(nombre) || 
                periodo.displayName.equalsIgnoreCase(nombre)) {
                return periodo;
            }
        }
        throw new IllegalArgumentException("Período de presupuesto no válido: " + nombre);
    }
}
