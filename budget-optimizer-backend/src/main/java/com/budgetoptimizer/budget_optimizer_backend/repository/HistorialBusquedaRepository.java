package com.budgetoptimizer.budget_optimizer_backend.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.budgetoptimizer.budget_optimizer_backend.model.HistorialBusqueda;
import java.util.List;

@Repository
public interface HistorialBusquedaRepository extends JpaRepository<HistorialBusqueda, Long> {
    List<HistorialBusqueda> findByUsuarioId(Long usuarioId);
    List<HistorialBusqueda> findByUsuarioIdOrderByFechaDesc(Long usuarioId);
    List<HistorialBusqueda> findTop10ByUsuarioIdOrderByFechaDesc(Long usuarioId);
}
