package com.budgetoptimizer.budget_optimizer_backend.repository;

import com.budgetoptimizer.budget_optimizer_backend.model.Usuario;
import com.budgetoptimizer.budget_optimizer_backend.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    // ==========================================
    // QUERY METHODS - SPRING LOS GENERA AUTOMÁTICAMENTE
    // ==========================================
    
    // Búsqueda básica
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Usuario> findByNombreContainingIgnoreCase(String texto);
    
    // Por tipo de cuenta
    List<Usuario> findByAccountType(AccountType accountType);
    long countByAccountType(AccountType accountType);
    List<Usuario> findByAccountTypeIn(List<AccountType> types);
    
    // Por estado
    List<Usuario> findByActivoTrue();
    List<Usuario> findByActivoFalse();
    List<Usuario> findByAccountTypeAndActivoTrue(AccountType type);
    
    // Por presupuesto
    List<Usuario> findByPresupuestoMensualBaseGreaterThan(Double monto);
    List<Usuario> findByPresupuestoMensualBaseBetween(Double min, Double max);
    List<Usuario> findByPresupuestoMensualBaseLessThan(Double monto);
    
    // Por fechas
    List<Usuario> findByFechaCreacionAfter(LocalDateTime fecha);
    List<Usuario> findByFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin);
    
    // Ordenamiento
    List<Usuario> findTop10ByActivoTrueOrderByPresupuestoMensualBaseDesc();
    List<Usuario> findByActivoTrueOrderByFechaCreacionDesc();
    
    // ==========================================
    // @Query SOLO PARA CASOS COMPLEJOS
    // ==========================================
    
    /**
     * Busca usuarios cerca de una ubicación
     * Usa @Query porque involucra cálculo geoespacial complejo
     */
    @Query("SELECT u FROM Usuario u WHERE " +
           "6371 * acos(cos(radians(:lat)) * cos(radians(u.ubicacion.latitud)) * " +
           "cos(radians(u.ubicacion.longitud) - radians(:lng)) + " +
           "sin(radians(:lat)) * sin(radians(u.ubicacion.latitud))) <= :radioKm " +
           "AND u.activo = true")
    List<Usuario> findUsuariosCercanos(
        @Param("lat") double latitud,
        @Param("lng") double longitud,
        @Param("radioKm") double radioKm
    );
    
    /**
     * Usuarios sin presupuestos (requiere LEFT JOIN)
     * Usa @Query porque necesita comprobar relación NULL
     */
    @Query("SELECT u FROM Usuario u WHERE " +
           "u.activo = true AND " +
           "SIZE(u.presupuestos) = 0")
    List<Usuario> findUsuariosSinPresupuestos();
}