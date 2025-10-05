package com.budgetoptimizer.budget_optimizer_backend.enums;

import lombok.Getter;
/**
 * Enum para los diferentes tipos de optimización que genera el ML
 * Incluye prioridad y comportamientos específicos
 */
@Getter
public enum OptimizationType {
    
    PREDICTION(
        "Predicción",
        "Predicción de gastos futuros basada en patrones históricos",
        3,
        "📊",
        false
    ),
    
    SUGGESTION(
        "Sugerencia",
        "Sugerencia de optimización de presupuesto",
        2,
        "💡",
        true
    ),
    
    RECOMMENDATION(
        "Recomendación",
        "Recomendación de empresas según presupuesto disponible",
        2,
        "🏪",
        true
    ),
    
    ANALYSIS(
        "Análisis",
        "Análisis detallado de patrones de gasto",
        4,
        "📈",
        false
    ),
    
    ALERT(
        "Alerta",
        "Alerta de sobregasto o anomalía detectada",
        1,
        "⚠️",
        true
    ),
    
    WARNING(
        "Advertencia",
        "Advertencia de aproximación al límite de categoría",
        2,
        "⚡",
        true
    ),
    
    INSIGHT(
        "Perspectiva",
        "Perspectiva o patrón interesante identificado",
        3,
        "🔍",
        false
    ),
    
    GOAL_TRACKING(
        "Seguimiento de Meta",
        "Seguimiento de progreso hacia metas financieras",
        3,
        "🎯",
        false
    ),
    
    SAVINGS_OPPORTUNITY(
        "Oportunidad de Ahorro",
        "Oportunidad identificada para ahorrar dinero",
        1,
        "💰",
        true
    );
    
    private final String displayName;
    private final String descripcion;
    private final Integer prioridad; // 1 = alta, 5 = baja
    private final String icono;
    private final Boolean requiereAccion;
    
    OptimizationType(String displayName, String descripcion, Integer prioridad,
                     String icono, Boolean requiereAccion) {
        this.displayName = displayName;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.icono = icono;
        this.requiereAccion = requiereAccion;
    }
    
    /**
     * Verifica si es de alta prioridad
     */
    public Boolean esAltaPrioridad() {
        return prioridad <= 2;
    }
    
    /**
     * Verifica si es una alerta crítica
     */
    public Boolean esCritico() {
        return this == ALERT || this == WARNING;
    }
    
    /**
     * Verifica si es informativo (no requiere acción)
     */
    public Boolean esInformativo() {
        return this == ANALYSIS || this == INSIGHT || this == GOAL_TRACKING;
    }
    
    /**
     * Obtiene el color según prioridad
     */
    public String getColorByPriority() {
        switch (prioridad) {
            case 1: return "#F44336";  // Rojo - Alta prioridad
            case 2: return "#FF9800";  // Naranja - Media-alta
            case 3: return "#2196F3";  // Azul - Media
            case 4: return "#4CAF50";  // Verde - Baja
            default: return "#9E9E9E"; // Gris - Muy baja
        }
    }
    
    /**
     * Busca un OptimizationType por nombre
     */
    public static OptimizationType fromString(String nombre) {
        for (OptimizationType tipo : OptimizationType.values()) {
            if (tipo.name().equalsIgnoreCase(nombre) || 
                tipo.displayName.equalsIgnoreCase(nombre)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de optimización no válido: " + nombre);
    }
}

