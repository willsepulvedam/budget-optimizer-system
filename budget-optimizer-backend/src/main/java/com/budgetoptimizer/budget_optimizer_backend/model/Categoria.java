package com.budgetoptimizer.budget_optimizer_backend.model;

import com.budgetoptimizer.budget_optimizer_backend.enums.TipoEmpresa;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "categorias") 
public class Categoria {
     
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id; 

    @Column(unique = true, nullable = false, length = 100)
    private String nombre; 

    @Column(length = 500)
    private String descripcion;
    
    @Enumerated(jakarta.persistence.EnumType.STRING)
    private TipoEmpresa tipoEmpresaAsociada;

    @Column(nullable = false)
    private Boolean activa; 

    // Metodos relevantes
    public Boolean validarNombre(String nombre){
        return this.nombre.equalsIgnoreCase(nombre);
    }
}
