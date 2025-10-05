package com.budgetoptimizer.budget_optimizer_backend.model;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
    
    @Column(nullable = false, precision = 10, scale = 2)
    private Double montoTotal;
    
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
    
    // Relaciones
    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CategoryLimit> limitesCategorias;
    
    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL)
    private List<Expense> expenses;
    
    // Métodos helper
    public Double calcularGastoTotal() {
    return expenses.stream()
        .mapToDouble(expense -> expense.getMonto())
        .sum();
}


    
    public Double calcularPresupuestoRestante() {
        return montoTotal - calcularGastoTotal();
    }
    
    public Boolean estaSobrePresupuesto() {
        return calcularGastoTotal() > montoTotal;
    }
}
