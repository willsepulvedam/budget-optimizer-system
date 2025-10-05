package com.budgetoptimizer.budget_optimizer_backend.model;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Entity
@Table(name = "category_limits")
@AllArgsConstructor
@NoArgsConstructor
public class CategoryLimit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "presupuesto_id", nullable = false)
    private Presupuesto presupuesto;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;
    
    @Column(nullable = false)
    private Double limiteAsignado;
    
    @Column(nullable = false)
    private Double gastoActual = 0.0;
    
    @CreationTimestamp
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    private LocalDateTime fechaActualizacion;
    
    
    // Métodos helper

     /**
     * Calcula el monto restante del límite
     */
    public Double calcularRestante() {
        return limiteAsignado - gastoActual;
    }
    
    /**
     * Calcula el porcentaje usado del límite
     */
    public Double calcularPorcentajeUsado() {
        if (limiteAsignado == null || limiteAsignado == 0) {
            return 0.0;
        }
        return (gastoActual / limiteAsignado) * 100;
    }
    
    /**
     * Verifica si está cerca del límite (>= 80%)
     */
    public Boolean estaCercaDelLimite() {
        return calcularPorcentajeUsado() >= 80.0;
    }
    
    /**
     * Verifica si ha excedido el límite
     */
    public Boolean haExcedidoLimite() {
        return gastoActual > limiteAsignado;
    }
    
    /**
     * Agrega un gasto al total actual
     */
    public void agregarGasto(Double monto) {
        if (monto != null && monto > 0) {
            this.gastoActual += monto;
        }
    }
    
    /**
     * Resta un gasto del total actual (para cuando se elimina un gasto)
     */
    public void restarGasto(Double monto) {
        if (monto != null && monto > 0) {
            this.gastoActual = Math.max(0, this.gastoActual - monto);
        }
    }
    
    /**
     * Verifica si puede agregar un gasto sin exceder el límite
     */
    public Boolean puedeAgregarGasto(Double monto) {
        if (monto == null || monto <= 0) {
            return false;
        }
        return (gastoActual + monto) <= limiteAsignado;
    }
    
    /**
     * Calcula cuánto falta para alcanzar el límite
     */
    public Double cuantoFaltaParaLimite() {
        return Math.max(0, limiteAsignado - gastoActual);
    }
}

