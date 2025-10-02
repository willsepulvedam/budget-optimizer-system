package com.budgetoptimizer.budget_optimizer_backend.model;


import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "historial_busquedas")
public class HistorialBusqueda {


    private Integer resultadosEncontrados;
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "cuenta_id", nullable = false)
    private Cuenta cuenta; 
    @Column(updatable = false, nullable = false)
    private LocalDateTime fecha;
    @Column(length = 1000)
    private String filtrosUsados;
    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "empresa_id")
    private Empresa empresaSeleccionada; 
    
    // metodos relevantes 
    public Boolean esReciente(LocalDateTime fechaComparacion) { // verifica si la busqueda es posterior a una fecha dada
        return this.fecha.isAfter(fechaComparacion);
    }
}
