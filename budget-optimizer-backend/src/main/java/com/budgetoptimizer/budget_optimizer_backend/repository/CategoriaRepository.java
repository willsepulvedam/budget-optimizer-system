package com.budgetoptimizer.budget_optimizer_backend.repository;

import com.budgetoptimizer.budget_optimizer_backend.model.Categoria;
import com.budgetoptimizer.budget_optimizer_backend.enums.CategoryType;
import com.budgetoptimizer.budget_optimizer_backend.enums.TipoEmpresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    
    // ==========================================
    // QUERY METHODS AUTOMÁTICOS
    // ==========================================
    
    // Búsqueda básica
    Optional<Categoria> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
    List<Categoria> findByNombreContainingIgnoreCase(String texto);
    
    // Por estado
    List<Categoria> findByActivaTrue();
    List<Categoria> findByActivaFalse();
    
    // Por tipo
    List<Categoria> findByTipo(CategoryType tipo);
    List<Categoria> findByTipoAndActivaTrue(CategoryType tipo);
    
    // Por tipo de empresa
    List<Categoria> findByTipoEmpresaAsociada(TipoEmpresa tipoEmpresa);
    List<Categoria> findByTipoEmpresaAsociadaIsNull();
    List<Categoria> findByTipoEmpresaAsociadaIsNullAndActivaTrue();
    
    // ==========================================
    // @Query SOLO PARA LÓGICA OR
    // ==========================================
    
    /**
     * Categorías para gastos (EXPENSE o BOTH)
     * Usa @Query porque necesita OR con valores específicos del enum
     */
    @Query("SELECT c FROM Categoria c WHERE " +
           "(c.tipo = 'EXPENSE' OR c.tipo = 'BOTH') AND c.activa = true")
    List<Categoria> findCategoriasParaGastos();
    
    /**
     * Categorías para empresas (BUSINESS o BOTH)
     */
    @Query("SELECT c FROM Categoria c WHERE " +
           "(c.tipo = 'BUSINESS' OR c.tipo = 'BOTH') AND c.activa = true")
    List<Categoria> findCategoriasParaEmpresas();
}