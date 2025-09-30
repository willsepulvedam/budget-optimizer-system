package com.budgetoptimizer.budget_optimizer_backend.service;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import com.budgetoptimizer.budget_optimizer_backend.model.Coordenada;
import com.fasterxml.jackson.databind.JsonNode;


// Servicio para interactuar con una API de geolocalización externa para poder obtener las coordenadas del usuario a partir de su ciudad y país

@Service
@RequiredArgsConstructor
@Slf4j
public class ServicioGeolocalizacion {

    private final RestTemplate restTemplate;

    @Value("${geolocalizacion.api.url}")
    private String API_URL;

    @Value("${geolocalizacion.api.key}")
    private String API_KEY;

    public Coordenada obtenerCoordenadas(String ciudad, String pais) {
        try {
            log.info("Obteniendo coordenadas para: {}, {}", ciudad, pais);
            
            // Construir la consulta
            String query = ciudad + ", " + pais;
            String url = String.format("%s?q=%s&key=%s&limit=1", 
                API_URL, query, API_KEY);

            // Realizar petición HTTP
            JsonNode response = restTemplate.getForObject(url, JsonNode.class);

            // Extraer coordenadas del resultado
            if (response != null && response.has("results") && 
                response.get("results").size() > 0) {
                
                JsonNode geometry = response.get("results").get(0).get("geometry");
                double latitud = geometry.get("lat").asDouble();
                double longitud = geometry.get("lng").asDouble();

                log.info("Coordenadas obtenidas: lat={}, lng={}", latitud, longitud);
                return new Coordenada(latitud, longitud);
            } else {
                log.warn("No se encontraron coordenadas para: {}", query);
                throw new RuntimeException("No se encontraron coordenadas para: " + query);
            }

        } catch (RestClientException e) {
            log.error("Error en la petición HTTP: {}", e.getMessage());
            throw new RuntimeException("Error al conectar con el servicio de geolocalización", e);
        } catch (Exception e) {
            log.error("Error al obtener coordenadas: {}", e.getMessage());
            throw new RuntimeException("Error al obtener coordenadas: " + e.getMessage(), e);
        }
    }
}
