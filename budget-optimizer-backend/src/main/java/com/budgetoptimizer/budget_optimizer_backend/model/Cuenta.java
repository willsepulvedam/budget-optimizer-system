package com.budgetoptimizer.budget_optimizer_backend.model;

import java.time.LocalDateTime;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

import com.budgetoptimizer.budget_optimizer_backend.enums.AccountType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cuentas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cuenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @Column(nullable = false)
    private Double saldo;
    
    @Enumerated(EnumType.STRING)
    private AccountType tipoCuenta;
    
    @ManyToMany
    @JoinTable(
        name = "cuenta_preferencias",
        joinColumns = @JoinColumn(name = "cuenta_id"),
        inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    private Set<Categoria> preferencias;
    
     @CreationTimestamp
     private LocalDateTime fechaCreacion;

     // metodos relevantes

     // metodo para suscribirse a una categoria
     public void suscribirseACategoria(Categoria categoria) {
          this.preferencias.add(categoria);
     }

     // metodo para calcular el presupuesto disponible basado en el saldo y las
     // preferencias
     public Double calcularPresupuestoDisponible() {
          double factorPreferencias = 1 + (this.preferencias.size() * 0.05); // 5% extra por cada categoria preferida
          return this.saldo * factorPreferencias;
     }
}
