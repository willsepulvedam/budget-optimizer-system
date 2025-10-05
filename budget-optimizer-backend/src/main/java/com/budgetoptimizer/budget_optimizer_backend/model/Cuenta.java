package com.budgetoptimizer.budget_optimizer_backend.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

import com.budgetoptimizer.budget_optimizer_backend.enums.AccountType;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "cuentas")
@Data
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

     @OneToMany(mappedBy = "cuenta", cascade = CascadeType.ALL, orphanRemoval = true)
     private List<HistorialBusqueda> historialBusquedas;

     // metodos relevantes

     // metodo para suscribirse a una categoria
     public void suscribirseACategoria(Categoria categoria) {
          this.preferencias.add(categoria);
     }

     // metodo para agregar una busqueda al historial
     public void agregarBusquedaAlHistorial(HistorialBusqueda busqueda) {
          this.historialBusquedas.add(busqueda);
          busqueda.setCuenta(this);
     }

     // metodo para calcular el presupuesto disponible basado en el saldo y las
     // preferencias
     public Double calcularPresupuestoDisponible() {
          double factorPreferencias = 1 + (this.preferencias.size() * 0.05); // 5% extra por cada categoria preferida
          return this.saldo * factorPreferencias;
     }
}
