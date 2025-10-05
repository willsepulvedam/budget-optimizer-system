package com.budgetoptimizer.budget_optimizer_backend.model;

import java.time.LocalDateTime;
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
import jakarta.persistence.Table;
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
    @JoinColumn(name = "budget_id", nullable = false)
    private Presupuesto presupuesto;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;
    
    // ⭐ NUEVA: Relación con empresa (opcional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id")
    private Empresa empresa; // Si el gasto fue en una empresa específica
    
    @Column(nullable = false, length = 200)
    private String descripcion;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private Double monto;
    
    @Column(nullable = false)
    private LocalDateTime fechaGasto;
    
    @Enumerated(EnumType.STRING)
    private PaymentMethod metodoPago = PaymentMethod.CASH;
    
    @Column(length = 500)
    private String notas;
    
    @CreationTimestamp
    private LocalDateTime fechaCreacion;
}
