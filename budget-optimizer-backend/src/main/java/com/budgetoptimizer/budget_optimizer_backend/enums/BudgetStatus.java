package com.budgetoptimizer.budget_optimizer_backend.enums;

import lombok.Getter;
/**
 * Enum para los diferentes estados de un presupuesto
 * Incluye lógica de transición de estados
 */
@Getter
public enum BudgetStatus {
    
    DRAFT(
        "Borrador",
        "Presupuesto en edición, no activo aún",
        true,
        false
    ),
    
    ACTIVE(
        "Activo",
        "Presupuesto en uso actualmente",
        false,
        true
    ),
    
    PAUSED(
        "Pausado",
        "Presupuesto temporalmente suspendido",
        true,
        false
    ),
    
    COMPLETED(
        "Completado",
        "Presupuesto finalizado exitosamente",
        false,
        false
    ),
    
    EXCEEDED(
        "Excedido",
        "Presupuesto sobrepasado",
        false,
        true
    ),
    
    CANCELLED(
        "Cancelado",
        "Presupuesto cancelado por el usuario",
        false,
        false
    ),
    
    ARCHIVED(
        "Archivado",
        "Presupuesto archivado para historial",
        false,
        false
    );
    
    private final String displayName;
    private final String descripcion;
    private final Boolean puedeEditar;
    private final Boolean puedeRegistrarGastos;
    
    BudgetStatus(String displayName, String descripcion, 
                 Boolean puedeEditar, Boolean puedeRegistrarGastos) {
        this.displayName = displayName;
        this.descripcion = descripcion;
        this.puedeEditar = puedeEditar;
        this.puedeRegistrarGastos = puedeRegistrarGastos;
    }
    
    /**
     * Verifica si el presupuesto está finalizado
     */
    public Boolean esFinal() {
        return this == COMPLETED || this == CANCELLED || this == ARCHIVED;
    }
    
    /**
     * Verifica si se puede activar desde este estado
     */
    public Boolean puedeActivarse() {
        return this == DRAFT || this == PAUSED;
    }
    
    /**
     * Verifica si se puede pausar desde este estado
     */
    public Boolean puedePausarse() {
        return this == ACTIVE || this == EXCEEDED;
    }
    
    /**
     * Obtiene el color para visualización del estado
     */
    public String getColorHex() {
        switch (this) {
            case DRAFT: return "#9E9E9E";        // Gris
            case ACTIVE: return "#4CAF50";       // Verde
            case PAUSED: return "#FF9800";       // Naranja
            case COMPLETED: return "#2196F3";    // Azul
            case EXCEEDED: return "#F44336";     // Rojo
            case CANCELLED: return "#757575";    // Gris oscuro
            case ARCHIVED: return "#607D8B";     // Gris azulado
            default: return "#000000";
        }
    }
    
    /**
     * Busca un BudgetStatus por nombre
     */
    public static BudgetStatus fromString(String nombre) {
        for (BudgetStatus status : BudgetStatus.values()) {
            if (status.name().equalsIgnoreCase(nombre) || 
                status.displayName.equalsIgnoreCase(nombre)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Estado de presupuesto no válido: " + nombre);
    }
}

