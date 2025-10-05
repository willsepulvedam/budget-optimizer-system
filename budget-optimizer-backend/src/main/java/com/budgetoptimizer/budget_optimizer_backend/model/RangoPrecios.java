package com.budgetoptimizer.budget_optimizer_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RangoPrecios {
    @Column(nullable = false)
    private Double minimo;
    @Column(nullable = false)
    private Double maximo;
    @Column(nullable = false)
    private Double promedio;

    // metodo para verificar si un precio es accesible dentro del rango
    public boolean esAccesible(double precio) {
        return precio >= minimo && precio <= maximo;
    }
}
