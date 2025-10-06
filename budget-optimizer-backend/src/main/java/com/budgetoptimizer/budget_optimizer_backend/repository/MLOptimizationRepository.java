package com.budgetoptimizer.budget_optimizer_backend.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.budgetoptimizer.budget_optimizer_backend.model.MLOptimization;
import com.budgetoptimizer.budget_optimizer_backend.enums.OptimizationType;
import java.util.List;

@Repository
public interface MLOptimizationRepository extends JpaRepository<MLOptimization, Long> {
    List<MLOptimization> findByUsuarioId(Long usuarioId);
    List<MLOptimization> findByUsuarioIdAndTipo(Long usuarioId, OptimizationType tipo);
    List<MLOptimization> findByUsuarioIdAndAplicadaFalse(Long usuarioId);
    List<MLOptimization> findByPresupuestoId(Long presupuestoId);
}
