package com.budgetoptimizer.budget_optimizer_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.budgetoptimizer.budget_optimizer_backend.enums.TipoEmpresa;
import com.budgetoptimizer.budget_optimizer_backend.model.Empresa;
import java.util.List;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, String> {
    List<Empresa> findByActivaTrue();
    List<Empresa> findByTipoEmpresa(TipoEmpresa tipo);
    List<Empresa> findByNombreContainingIgnoreCase(String nombre);
    List<Empresa> findByCalificacionPromedioGreaterThanEqual(Double calificacion);
    
    // Búsqueda geoespacial requiere @Query
    @Query("SELECT e FROM Empresa e WHERE " +
           "6371 * acos(cos(radians(:lat)) * cos(radians(e.ubicacion.latitud)) * " +
           "cos(radians(e.ubicacion.longitud) - radians(:lng)) + " +
           "sin(radians(:lat)) * sin(radians(e.ubicacion.latitud))) <= :radioKm " +
           "AND e.activa = true")
    List<Empresa> findEmpresasCercanas(@Param("lat") double lat, 
                                       @Param("lng") double lng, 
                                       @Param("radioKm") double radioKm);
}