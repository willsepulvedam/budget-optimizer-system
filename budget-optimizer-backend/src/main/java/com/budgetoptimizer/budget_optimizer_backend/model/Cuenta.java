package com.budgetoptimizer.budget_optimizer_backend.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import com.budgetoptimizer.budget_optimizer_backend.enums.TipoCuenta;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "cuentas")
@Data
public class Cuenta {
   @Column(nullable = false, unique = true, length = 36)
   private String id;
   @Column(nullable = false, length = 100)
   private Double saldo;
   @Enumerated(EnumType.STRING)
   private TipoCuenta tipoCuenta;
   @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
   @JoinColumn(name = "usuario_id", nullable = false) 
   private Usuario usuario;
   @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
   @JoinColumn(name = "cuenta_categoria", nullable = false)
   private Set<Categoria> preferencias; 
   @OneToMany(mappedBy = "cuenta", cascade = CascadeType.ALL, orphanRemoval = true)
   private List<HistorialBusqueda> historialBusquedas;
   @Column(updatable = false, nullable = false)
   private LocalDate fechaCreacion;

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

   // metodo para calcular el presupuesto disponible basado en el saldo y las preferencias
   public Double calcularPresupuestoDisponible() {
        double factorPreferencias = 1 + (this.preferencias.size() * 0.05); // 5% extra por cada categoria preferida
        return this.saldo * factorPreferencias;
   }
}
