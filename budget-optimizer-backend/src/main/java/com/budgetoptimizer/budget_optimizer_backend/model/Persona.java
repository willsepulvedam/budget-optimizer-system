package com.budgetoptimizer.budget_optimizer_backend.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "personas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Persona {
   
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
   
    @Column(nullable = false, length = 100)
    private String nombre;
   
    @Column(nullable = false, unique = true, length = 100)
    private String email;
   
    @Column(nullable = false)
    private Boolean activo = true;
   
    @Column(nullable = false)
    private LocalDate fechaNacimiento;
   
    @Embedded
    private Coordenada ubicacion;
   
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
   
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
   
    // Métodos de negocio
    public Integer calcularEdad() {
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }
   
    public void actualizarUbicacion(Coordenada coordenada) {
        this.ubicacion = coordenada;
    }
   
    public Boolean validarEmail() {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}