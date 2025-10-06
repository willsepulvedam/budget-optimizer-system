package com.budgetoptimizer.budget_optimizer_backend.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import com.budgetoptimizer.budget_optimizer_backend.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "expenses")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "presupuesto_id", nullable = false)
    private Presupuesto presupuesto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id")
    private Empresa empresa; // Si el gasto fue en una empresa específica

    @Column(nullable = false, length = 200)
    private String descripcion;

    // 💰 Tipo BigDecimal con precisión y escala (compatible con Hibernate)
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal monto;

    @Column(nullable = false)
    private LocalDateTime fechaGasto;

    @Enumerated(EnumType.STRING)
    private PaymentMethod metodoPago = PaymentMethod.CASH;

    @Column(length = 500)
    private String notas;

    @CreationTimestamp
    private LocalDateTime fechaCreacion;

    
}
