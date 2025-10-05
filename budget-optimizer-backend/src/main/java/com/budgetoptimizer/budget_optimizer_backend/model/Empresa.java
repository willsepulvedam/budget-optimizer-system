package com.budgetoptimizer.budget_optimizer_backend.model;


import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.budgetoptimizer.budget_optimizer_backend.enums.TipoEmpresa;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "empresas")
@AllArgsConstructor
@NoArgsConstructor
public class Empresa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false, length = 36)
    private String id;
    
    @Column(nullable = false, length = 200)
    private String nombre;
    
    @Enumerated(EnumType.STRING)
    private TipoEmpresa tipoEmpresa;
    
    // ⭐ AGREGAR: Relación ManyToMany con categorías
    @ManyToMany
    @JoinTable(
        name = "empresa_categorias",
        joinColumns = @JoinColumn(name = "empresa_id"),
        inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    private List<Categoria> categorias;
    
    @Embedded
    private Coordenada ubicacion;
    
    @Embedded
    private RangoPrecios rangoPrecios;
    
    @Column(precision = 3, scale = 2)
    private Double calificacionPromedio;
    
    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;
    
    // ⚠️ AGREGAR: Relación con gastos
    @OneToMany(mappedBy = "empresa")
    private List<Expense> expenses;
    
    @Column(nullable = false)
    private Boolean activa = true;
    
    @CreationTimestamp
    private LocalDateTime fechaCreacion;

    // metodos relevantes 


    // 1. metodo para calcular la distnaica entre la empresa y una coordenada dada pero tomando el metodo dado en coordenada que lo calcula 
    public Double calcularDistanciaA(Coordenada otraUbicacion) {
        return this.ubicacion.distanciaA(otraUbicacion);
    }

    // 2. metodo para verificar si cumple con el presupuesto dado un monto 
    public Boolean cumpleConPresupuesto(Double presupuesto) {
        return this.rangoPrecios.esAccesible(presupuesto);
    }

    // 3. metodo para obtener la calicacion promedio actualizada basado en las reviews
    public void actualizarCalificacionPromedio() {
        if (reviews == null || reviews.isEmpty()) {
            this.calificacionPromedio = null;
            return;
        }
        double suma = 0.0;
        for (Review review : reviews) {
            suma += review.getCalificacion();
        }
        this.calificacionPromedio = suma / reviews.size();
    }


    
}
