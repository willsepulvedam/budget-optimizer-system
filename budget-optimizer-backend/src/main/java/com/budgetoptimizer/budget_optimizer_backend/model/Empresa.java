package com.budgetoptimizer.budget_optimizer_backend.model;


import java.time.LocalDateTime;
import java.util.List;

import com.budgetoptimizer.budget_optimizer_backend.enums.TipoEmpresa;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "empresas")
public class Empresa {

    @Embedded
    private Coordenada ubicacion;
    @Embedded
    private RangoPrecios rangoPrecios;
    @Column(unique = true, nullable = false, length = 36)
    private String id; 
    @Column(nullable = false, length = 200)
    private String nombre;
    @Enumerated(EnumType.STRING)
    private TipoEmpresa tipoEmpresa;
    @Column(precision = 3, scale = 2) // Ejemplo: 4.75
    private Double calificacionPromedio;
    @OneToMany(mappedBy = "empresa", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;
    @Column(nullable = false)
    private Boolean activa;
    @Column(updatable = false, nullable = false)
    private LocalDateTime fechaCreacion;

    // metodos relevantes 


    // 1. metodo para calcular la distnaica entre la empresa y una coordenada dada pero tomando el metodo dado en coordenada que lo calcula 
    public Double calcularDistanciaA(Coordenada otraUbicacion) {
        return this.ubicacion.distanciaA(otraUbicacion);
    }

    // 2. metodo para verificar si cumple con el presupuesto dado un monto 
    public Boolean cumpleConPresupuesto(Double presupuesto) {
        return this.rangoPrecios.esAccesible(presupuesto);
    }

    // 3. metodo para obtener la calicacion promedio actualizada basado en las reviews
    public void actualizarCalificacionPromedio() {
        if (reviews == null || reviews.isEmpty()) {
            this.calificacionPromedio = null;
            return;
        }
        double suma = 0.0;
        for (Review review : reviews) {
            suma += review.getCalificacion();
        }
        this.calificacionPromedio = suma / reviews.size();
    }


    
}
