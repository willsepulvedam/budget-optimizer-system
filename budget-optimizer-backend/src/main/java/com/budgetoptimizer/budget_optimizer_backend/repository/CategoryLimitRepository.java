package com.budgetoptimizer.budget_optimizer_backend.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.budgetoptimizer.budget_optimizer_backend.model.CategoryLimit;
import java.util.List;
import java.util.Optional;


@Repository
public interface CategoryLimitRepository extends JpaRepository<CategoryLimit, Long> {
    List<CategoryLimit> findByPresupuestoId(Long presupuestoId);
    List<CategoryLimit> findByCategoriaId(Long categoriaId);
    Optional<CategoryLimit> findByPresupuestoIdAndCategoriaId(Long presupuestoId, Long categoriaId);
    
    // Solo si necesitas verificar si está cerca del límite
    @Query("SELECT cl FROM CategoryLimit cl WHERE " +
           "cl.presupuesto.id = :presupuestoId AND " +
           "(cl.gastoActual / cl.limiteAsignado) >= 0.8")
    List<CategoryLimit> findLimitesCercaDelTope(@Param("presupuestoId") Long presupuestoId);
}
