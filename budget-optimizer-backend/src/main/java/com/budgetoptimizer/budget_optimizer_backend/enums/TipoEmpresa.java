package com.budgetoptimizer.budget_optimizer_backend.enums;
import lombok.Getter;
import java.util.Arrays;
import java.util.List;

/**
 * Enum para los diferentes tipos de empresa
 * Incluye metaprogramación con categorías, horarios típicos y comportamientos
 */
@Getter
public enum TipoEmpresa {
    
    RESTAURANTE(
        "Restaurante",
        "Alimentación",
        Arrays.asList("10:00-23:00"),
        true,
        50.0,
        "Establecimiento de comida y bebidas"
    ),
    
    GIMNASIO(
        "Gimnasio",
        "Deporte y Bienestar",
        Arrays.asList("06:00-22:00"),
        true,
        30.0,
        "Centro de acondicionamiento físico"
    ),
    
    ABASTO(
        "Abasto",
        "Comercio",
        Arrays.asList("07:00-21:00"),
        true,
        20.0,
        "Tienda de abarrotes y productos básicos"
    ),
    
    PANADERIA(
        "Panadería",
        "Alimentación",
        Arrays.asList("06:00-20:00"),
        true,
        15.0,
        "Establecimiento de productos de panadería"
    ),
    
    TIENDA(
        "Tienda",
        "Comercio",
        Arrays.asList("09:00-20:00"),
        true,
        25.0,
        "Tienda de productos varios"
    ),
    
    VENDEDOR_AMBULANTE(
        "Vendedor Ambulante",
        "Comercio Informal",
        Arrays.asList("Variable"),
        false,
        10.0,
        "Vendedor sin establecimiento físico fijo"
    );
    
    // Atributos del enum
    private final String displayName;
    private final String categoria;
    private final List<String> horariosComunes;
    private final Boolean requiereUbicacionFija;
    private final Double gastoPromedioCliente;
    private final String descripcion;
    
    // Constructor
    TipoEmpresa(String displayName, String categoria, 
                List<String> horariosComunes, Boolean requiereUbicacionFija,
                Double gastoPromedioCliente, String descripcion) {
        this.displayName = displayName;
        this.categoria = categoria;
        this.horariosComunes = horariosComunes;
        this.requiereUbicacionFija = requiereUbicacionFija;
        this.gastoPromedioCliente = gastoPromedioCliente;
        this.descripcion = descripcion;
    }
    
    // Métodos de negocio
    
    /**
     * Calcula el presupuesto estimado mensual para este tipo de empresa
     */
    public Double calcularPresupuestoEstimado(Integer clientesPorDia) {
        if (clientesPorDia == null || clientesPorDia <= 0) {
            return 0.0;
        }
        return gastoPromedioCliente * clientesPorDia * 30; // 30 días
    }
    
    /**
     * Verifica si el tipo de empresa pertenece a la categoría de alimentación
     */
    public Boolean esAlimentacion() {
        return "Alimentación".equals(this.categoria);
    }
    
    /**
     * Verifica si el tipo de empresa es de comercio
     */
    public Boolean esComercio() {
        return this.categoria.contains("Comercio");
    }
    
    /**
     * Obtiene el factor de ajuste de presupuesto según el tipo de empresa
     * (usado para optimización de presupuestos)
     */
    public Double getFactorAjustePresupuesto() {
        switch (this) {
            case RESTAURANTE:
                return 1.5; // Alto costo operativo
            case GIMNASIO:
                return 1.3;
            case PANADERIA:
                return 1.2;
            case ABASTO:
            case TIENDA:
                return 1.1;
            case VENDEDOR_AMBULANTE:
                return 0.8; // Bajo costo operativo
            default:
                return 1.0;
        }
    }
    
    /**
     * Verifica si necesita radio de cobertura para ubicación
     */
    public Boolean necesitaRadioCobertura() {
        return !requiereUbicacionFija;
    }
    
    /**
     * Obtiene todos los tipos de empresa de una categoría específica
     */
    public static List<TipoEmpresa> getPorCategoria(String categoria) {
        return Arrays.stream(TipoEmpresa.values())
                .filter(tipo -> tipo.getCategoria().equalsIgnoreCase(categoria))
                .toList();
    }
    
    /**
     * Busca un TipoEmpresa por su nombre (case-insensitive)
     */
    public static TipoEmpresa fromString(String nombre) {
        for (TipoEmpresa tipo : TipoEmpresa.values()) {
            if (tipo.name().equalsIgnoreCase(nombre) || 
                tipo.displayName.equalsIgnoreCase(nombre)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de empresa no válido: " + nombre);
    }
    
    /**
     * Obtiene descripción completa del tipo de empresa
     */
    public String getDescripcionCompleta() {
        return String.format("%s - %s: %s (Gasto promedio: $%.2f)", 
            displayName, categoria, descripcion, gastoPromedioCliente);
    }
}