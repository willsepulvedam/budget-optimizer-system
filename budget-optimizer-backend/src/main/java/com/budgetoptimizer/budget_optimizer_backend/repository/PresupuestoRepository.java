package com.budgetoptimizer.budget_optimizer_backend.repository;

import com.budgetoptimizer.budget_optimizer_backend.model.Presupuesto;
import com.budgetoptimizer.budget_optimizer_backend.enums.BudgetStatus;
import com.budgetoptimizer.budget_optimizer_backend.enums.BudgetPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PresupuestoRepository extends JpaRepository<Presupuesto, Long> {
    
    // ==========================================
    // QUERY METHODS AUTOMÁTICOS
    // ==========================================
    
    // Por usuario
    List<Presupuesto> findByUsuarioId(Long usuarioId);
    List<Presupuesto> findByUsuarioIdAndStatus(Long usuarioId, BudgetStatus status);
    long countByUsuarioId(Long usuarioId);
    
    // Por estado
    List<Presupuesto> findByStatus(BudgetStatus status);
    List<Presupuesto> findByStatusAndFechaFinAfter(BudgetStatus status, LocalDateTime fecha);
    
    // Por período
    List<Presupuesto> findByPeriodo(BudgetPeriod periodo);
    List<Presupuesto> findByPeriodoAndStatus(BudgetPeriod periodo, BudgetStatus status);
    
    // Por fechas
    List<Presupuesto> findByFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin);
    List<Presupuesto> findByFechaFinBetween(LocalDateTime inicio, LocalDateTime fin);
    
    // Por monto
    List<Presupuesto> findByMontoTotalGreaterThan(Double monto);
    List<Presupuesto> findByMontoTotalBetween(Double min, Double max);
    
    // Ordenamiento
    List<Presupuesto> findTop10ByStatusOrderByMontoTotalDesc(BudgetStatus status);
    List<Presupuesto> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);
    
    // ==========================================
    // @Query SOLO PARA LÓGICA COMPLEJA
    // ==========================================
    
    /**
     * Presupuestos vigentes en una fecha
     * Usa @Query porque necesita BETWEEN en ambas fechas
     */
    @Query("SELECT p FROM Presupuesto p WHERE " +
           ":fecha BETWEEN p.fechaInicio AND p.fechaFin")
    List<Presupuesto> findPresupuestosVigentes(@Param("fecha") LocalDateTime fecha);
    
    /**
     * Presupuesto activo actual de un usuario
     * Usa @Query porque combina múltiples condiciones y BETWEEN
     */
    @Query("SELECT p FROM Presupuesto p WHERE " +
           "p.usuario.id = :usuarioId AND " +
           "p.status = 'ACTIVE' AND " +
           ":ahora BETWEEN p.fechaInicio AND p.fechaFin " +
           "ORDER BY p.fechaCreacion DESC")
    Optional<Presupuesto> findPresupuestoActivoActual(
        @Param("usuarioId") Long usuarioId,
        @Param("ahora") LocalDateTime ahora
    );
    
    /**
     * Presupuestos próximos a vencer
     * Usa @Query porque necesita rango específico de fechas futuras
     */
    @Query("SELECT p FROM Presupuesto p WHERE " +
           "p.status = 'ACTIVE' AND " +
           "p.fechaFin BETWEEN :ahora AND :fechaLimite")
    List<Presupuesto> findPresupuestosProximosAVencer(
        @Param("ahora") LocalDateTime ahora,
        @Param("fechaLimite") LocalDateTime fechaLimite
    );
}