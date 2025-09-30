package com.budgetoptimizer.budget_optimizer_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Coordenada {
    @Column(nullable = false)
     private double latitud;
    @Column(nullable = false)
     private double longitud;
     
     // metodos para la funcionalidad de las coordenadas
     public double distanciaA(Coordenada otra) {
          double radioTierra = 6371e3; // en metros
          double lat1Rad = Math.toRadians(this.latitud);
          double lat2Rad = Math.toRadians(otra.latitud);
          double deltaLatRad = Math.toRadians(otra.latitud - this.latitud);
          double deltaLonRad = Math.toRadians(otra.longitud - this.longitud);

          double a = Math.sin(deltaLatRad / 2) * Math.sin(deltaLatRad / 2) +
                     Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                     Math.sin(deltaLonRad / 2) * Math.sin(deltaLonRad / 2);
          double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

          return radioTierra * c; // distancia en metros
     }
}
