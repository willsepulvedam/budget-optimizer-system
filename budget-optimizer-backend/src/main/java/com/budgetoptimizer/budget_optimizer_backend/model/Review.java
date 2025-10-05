package com.budgetoptimizer.budget_optimizer_backend.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "reviews")
@NoArgsConstructor
@AllArgsConstructor
public class Review {
     
    @Id
    @Column(length = 36, nullable = false, unique = true)
    private String id; 
    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;
    @Column(nullable = false)
    private Integer calificacion; // 1 a 5
    @Column(length = 1000)
    private String comentario;
    @Column(updatable = false, nullable = false)
    private LocalDateTime fecha; 
    @Column(nullable = false)
    private Boolean verificada;


    // metodos relevantes 

    // 1. metodo para verificar si es reciente la review 
    public Boolean esReciente(LocalDateTime fechaComparacion) {
        return this.fecha.isAfter(fechaComparacion);
    }

    // 2. metodo para verificar si es positiva la review 
    public Boolean esPositiva() {
        return this.calificacion >= 4; // considera positiva si la calificacion es 4 o 5
    }
}
