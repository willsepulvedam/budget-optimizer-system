package com.budgetoptimizer.budget_optimizer_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.budgetoptimizer.budget_optimizer_backend.model.Review;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByEmpresaId(String empresaId);
    List<Review> findByUsuarioId(Long usuarioId);
    List<Review> findByEmpresaIdAndVerificadaTrue(String empresaId);
    List<Review> findByCalificacionGreaterThanEqual(Integer calificacion);
    long countByEmpresaId(String empresaId);
    
    @Query("SELECT AVG(r.calificacion) FROM Review r WHERE r.empresa.id = :empresaId")
    Double calcularPromedioCalificacion(@Param("empresaId") String empresaId);
}
