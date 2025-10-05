package com.budgetoptimizer.budget_optimizer_backend.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.budgetoptimizer.budget_optimizer_backend.enums.OptimizationType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "ml_optimizations")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class MLOptimization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id")
    private Presupuesto presupuesto; // Puede ser null si es análisis general
    
    // ⭐⭐⭐ COLUMNA JSONB - Respuesta completa de FastAPI
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private String mlResponse;
    
    /* Ejemplo de JSON guardado:
    {
      "optimized_budget": 450,
      "recommended_categories": ["food", "transport"],
      "suggested_category_limits": {
        "food": 300,
        "transport": 150
      },
      "recommended_businesses": [
        {"id": "123", "name": "Restaurante A", "estimated_cost": 50},
        {"id": "456", "name": "Gym B", "estimated_cost": 30}
      ],
      "predicted_savings": 50,
      "alerts": ["Estás gastando 20% más en comida"],
      "confidence": 0.85,
      "model_version": "v1.2.3"
    }
    */
    
    @Column
    private Double confidence; // 0.0 - 1.0
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OptimizationType tipo = OptimizationType.PREDICTION;
    
    @Column(nullable = false)
    private Boolean aplicada = false; // Si el usuario aplicó la sugerencia
    
    @CreationTimestamp
    private LocalDateTime fechaCreacion;
    
    // Método helper para extraer empresas sugeridas del JSON
    public List<String> getEmpresasSugeridas() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(mlResponse);
        List<String> empresaIds = new ArrayList<>();
        
        if (node.has("recommended_businesses")) {
            JsonNode businesses = node.get("recommended_businesses");
            for (JsonNode business : businesses) {
                empresaIds.add(business.get("id").asText());
            }
        }
        
        return empresaIds;
    }

    /**
     * Extrae el presupuesto optimizado del JSON
     */
    public Double getPresupuestoOptimizado() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(mlResponse);
        
        if (node.has("optimized_budget")) {
            return node.get("optimized_budget").asDouble();
        }
        
        return null;
    }
    
    /**
     * Extrae las alertas del JSON
     */
    public List<String> getAlertas() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(mlResponse);
        List<String> alertas = new ArrayList<>();
        
        if (node.has("alerts")) {
            JsonNode alertsNode = node.get("alerts");
            for (JsonNode alert : alertsNode) {
                alertas.add(alert.asText());
            }
        }
        
        return alertas;
    }
    
    /**
     * Verifica si la optimización tiene alta confianza (>= 0.7)
     */
    public Boolean tieneAltaConfianza() {
        return confidence != null && confidence >= 0.7;
    }
    
    /**
     * Verifica si ya fue aplicada por el usuario
     */
    public Boolean fueAplicada() {
        return aplicada != null && aplicada;
    }
}




