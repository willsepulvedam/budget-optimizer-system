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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "historial_busquedas")
@NoArgsConstructor
@AllArgsConstructor
public class HistorialBusqueda {


    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario; 

    @Column(updatable = false, nullable = false)
    private LocalDateTime fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id")
    private Empresa empresaSeleccionada;

    @Column(length = 1000)
    private String filtrosUsados;

    private Integer resultadosEncontrados;
    
    // metodos relevantes 
    public Boolean esReciente(LocalDateTime fechaComparacion) { // verifica si la busqueda es posterior a una fecha dada
        return this.fecha.isAfter(fechaComparacion);
    }
}
