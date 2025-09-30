package com.budgetoptimizer.budget_optimizer_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "usuarios")
@Data
@EqualsAndHashCode(callSuper = true)
public class Usuario extends Persona {
    
    @Column(nullable = false, unique = true, length = 100)
    private String username;
    
    @Column(nullable = false, length = 255)
    private String passwordHash;
    
    @Column(nullable = false)
    private Double presupuestoMensual;
    
    @Embedded
    private Coordenada ubicacion;
    
    // Método de validar presupuesto
    public boolean validarPresupuesto(Double monto) {
        return monto <= this.presupuestoMensual;
    }
    
    // Método para establecer ubicación
    public void establecerUbicacion(Coordenada coordenada) {
        this.ubicacion = coordenada;
    }
}