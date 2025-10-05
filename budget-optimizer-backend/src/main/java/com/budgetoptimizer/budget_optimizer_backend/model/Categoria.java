package com.budgetoptimizer.budget_optimizer_backend.model;

import com.budgetoptimizer.budget_optimizer_backend.enums.CategoryType;
import com.budgetoptimizer.budget_optimizer_backend.enums.TipoEmpresa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.EnumType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.ManyToMany;
import java.util.List;




@Data
@Entity
@Table(name = "categorias")
@AllArgsConstructor
@NoArgsConstructor
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 100)
    private String nombre; // "food", "transport", "gym", "entertainment"
    
    @Column(length = 500)
    private String descripcion;
    
    @Column(length = 20)
    private String icono; // "🍔", "🚗", "🏋️"
    
    @Column(length = 7)
    private String color; // "#FF5733" para visualización
    
    // ⚠️ CAMBIAR: Separar tipo de empresa (puede ser null)
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_empresa_asociada")
    private TipoEmpresa tipoEmpresaAsociada; // Puede ser null
    
    @Column(nullable = false)
    private Boolean activa = true;
    
    // ⭐ NUEVO: Indicar si es para gastos, empresas o ambos
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryType tipo = CategoryType.BOTH;
    
    // Relaciones
    @OneToMany(mappedBy = "categoria")
    private List<CategoryLimit> limitesCategorias;
    
    @OneToMany(mappedBy = "categoria")
    private List<Expense> expenses;
    
    @ManyToMany(mappedBy = "categorias")
    private List<Empresa> empresas;
}
