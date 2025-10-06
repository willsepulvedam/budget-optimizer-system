package com.budgetoptimizer.budget_optimizer_backend.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import com.budgetoptimizer.budget_optimizer_backend.enums.BudgetPeriod;
import com.budgetoptimizer.budget_optimizer_backend.enums.BudgetStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "presupuestos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Presupuesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 100)
    private String nombre; // "Presupuesto Octubre 2025"

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal montoTotal;

    @Column(nullable = false)
    private LocalDateTime fechaInicio;

    @Column(nullable = false)
    private LocalDateTime fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BudgetPeriod periodo = BudgetPeriod.MONTHLY;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BudgetStatus status = BudgetStatus.ACTIVE;

    @CreationTimestamp
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "presupuesto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CategoryLimit> limitesCategorias;

    @OneToMany(mappedBy = "presupuesto", cascade = CascadeType.ALL)
    private List<Expense> expenses;

    // Métodos helper
    public BigDecimal calcularGastoTotal() {
        return expenses.stream()
            .map(Expense::getMonto)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calcularPresupuestoRestante() {
        return montoTotal.subtract(calcularGastoTotal());
    }

    public Boolean estaSobrePresupuesto() {
        return calcularGastoTotal().compareTo(montoTotal) > 0;
    }
}
