package com.budgetoptimizer.budget_optimizer_backend.model;

import java.math.BigDecimal;
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
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal minimo;
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal maximo;
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal promedio;

    // metodo para verificar si un precio es accesible dentro del rango
    public boolean esAccesible(BigDecimal precio) {
        return precio.compareTo(minimo) >= 0 && precio.compareTo(maximo) <= 0;
    }
}
