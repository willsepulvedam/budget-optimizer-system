package com.budgetoptimizer.budget_optimizer_backend.enums;


import lombok.Getter;

/**
 * Enum para los diferentes tipos de cuenta de usuario
 * Incluye metaprogramación con atributos y comportamientos específicos
 */
@Getter
public enum TipoCuenta {
    
    NORMAL(
        "Normal", 
        0.0, 
        false, 
        100,
        "Cuenta estándar sin beneficios adicionales"
    ),
    
    VENDEDOR(
        "Vendedor", 
        5.0, 
        true, 
        500,
        "Cuenta para vendedores con comisiones reducidas"
    ),
    
    PREMIUM(
        "Premium", 
        10.0, 
        true, 
        1000,
        "Cuenta premium con máximos beneficios y descuentos"
    );
    
    // Atributos del enum
    private final String displayName;
    private final Double descuentoPorcentaje;
    private final Boolean tieneBeneficios;
    private final Integer limiteTransaccionesMes;
    private final String descripcion;
    
    // Constructor
    TipoCuenta(String displayName, Double descuentoPorcentaje, 
               Boolean tieneBeneficios, Integer limiteTransaccionesMes,
               String descripcion) {
        this.displayName = displayName;
        this.descuentoPorcentaje = descuentoPorcentaje;
        this.tieneBeneficios = tieneBeneficios;
        this.limiteTransaccionesMes = limiteTransaccionesMes;
        this.descripcion = descripcion;
    }
    
    // Métodos de negocio
    
    /**
     * Calcula el monto con descuento aplicado según el tipo de cuenta
     */
    public Double aplicarDescuento(Double montoOriginal) {
        if (montoOriginal == null || montoOriginal <= 0) {
            return montoOriginal;
        }
        return montoOriginal * (1 - descuentoPorcentaje / 100);
    }
    
    /**
     * Verifica si el tipo de cuenta puede realizar una transacción
     */
    public Boolean puedeRealizarTransaccion(Integer transaccionesRealizadas) {
        return transaccionesRealizadas < limiteTransaccionesMes;
    }
    
    /**
     * Calcula el ahorro obtenido con el descuento
     */
    public Double calcularAhorro(Double montoOriginal) {
        if (montoOriginal == null || montoOriginal <= 0) {
            return 0.0;
        }
        return montoOriginal * (descuentoPorcentaje / 100);
    }
    
    /**
     * Verifica si este tipo de cuenta es superior a otro
     */
    public Boolean esSuperiorA(TipoCuenta otraCuenta) {
        return this.ordinal() > otraCuenta.ordinal();
    }
    
    /**
     * Obtiene el siguiente nivel de cuenta (upgrade)
     */
    public TipoCuenta obtenerSiguienteNivel() {
        TipoCuenta[] valores = TipoCuenta.values();
        int indiceActual = this.ordinal();
        if (indiceActual < valores.length - 1) {
            return valores[indiceActual + 1];
        }
        return this; // Ya está en el nivel máximo
    }
    
    /**
     * Busca un TipoCuenta por su nombre (case-insensitive)
     */
    public static TipoCuenta fromString(String nombre) {
        for (TipoCuenta tipo : TipoCuenta.values()) {
            if (tipo.name().equalsIgnoreCase(nombre) || 
                tipo.displayName.equalsIgnoreCase(nombre)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de cuenta no válido: " + nombre);
    }
}