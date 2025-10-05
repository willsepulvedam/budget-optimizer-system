package com.budgetoptimizer.budget_optimizer_backend.model;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.budgetoptimizer.budget_optimizer_backend.enums.AccountType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 
    
    @Column(unique = true, nullable = false)
    private String email;  
    
    @Column(nullable = false)
    private String password; // Hashed password
    
    @Column(nullable = false)
    private String nombre;   
    
    @Embedded
    private Coordenada ubicacion;  // Coordenadas geográficas

    @Column(nullable = false)
    private Double presupuestoMensualBase; // Presupuesto base sin ajustes
    
    @Enumerated(EnumType.STRING)
    private AccountType role = AccountType.USER;
    
    @Column(nullable = false)
    private Boolean activo = true;
    
    @CreationTimestamp
    private LocalDateTime fechaCreacion;
    
    // ⚠️ AGREGAR: Relaciones con presupuestos
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Presupuesto> presupuestos;
    
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Expense> expenses;
    
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<MLOptimization> mlOptimizations;
    
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Review> reviews;
    
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<HistorialBusqueda> historialBusquedas;
    
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    private Cuenta cuenta;

    
    // Método de validar presupuesto
    public boolean validarPresupuesto(Double monto) {
        return monto <= this.presupuestoMensualBase;
    }
    
    // Método para establecer ubicación
    public void establecerUbicacion(Coordenada coordenada) {
        this.ubicacion = coordenada;
    }
}
