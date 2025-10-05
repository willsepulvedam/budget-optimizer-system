package com.budgetoptimizer.budget_optimizer_backend.enums;

import lombok.Getter;
/**
 * Enum para los diferentes métodos de pago de gastos
 * Incluye información sobre comisiones y características
 */
@Getter
public enum PaymentMethod {
    
    CASH(
        "Efectivo",
        "💵",
        0.0,
        true,
        false,
        "Pago en efectivo"
    ),
    
    DEBIT_CARD(
        "Tarjeta de Débito",
        "💳",
        0.0,
        true,
        true,
        "Pago con tarjeta de débito"
    ),
    
    CREDIT_CARD(
        "Tarjeta de Crédito",
        "💳",
        2.5,
        false,
        true,
        "Pago con tarjeta de crédito"
    ),
    
    BANK_TRANSFER(
        "Transferencia Bancaria",
        "🏦",
        0.0,
        true,
        true,
        "Transferencia entre cuentas"
    ),
    
    MOBILE_PAYMENT(
        "Pago Móvil",
        "📱",
        1.0,
        true,
        true,
        "Pago mediante aplicación móvil"
    ),
    
    QR_CODE(
        "Código QR",
        "📲",
        0.5,
        true,
        true,
        "Pago mediante código QR"
    ),
    
    DIGITAL_WALLET(
        "Billetera Digital",
        "👛",
        1.5,
        true,
        true,
        "Pago con billetera digital (PayPal, etc.)"
    ),
    
    CHECK(
        "Cheque",
        "📝",
        0.0,
        true,
        true,
        "Pago con cheque"
    ),
    
    CRYPTOCURRENCY(
        "Criptomoneda",
        "₿",
        3.0,
        false,
        true,
        "Pago con criptomonedas"
    ),
    
    OTHER(
        "Otro",
        "❓",
        0.0,
        true,
        false,
        "Otro método de pago"
    );
    
    private final String displayName;
    private final String icono;
    private final Double comisionPorcentaje;
    private final Boolean esInstantaneo;
    private final Boolean tieneTrazabilidad;
    private final String descripcion;
    
    PaymentMethod(String displayName, String icono, Double comisionPorcentaje,
                  Boolean esInstantaneo, Boolean tieneTrazabilidad, String descripcion) {
        this.displayName = displayName;
        this.icono = icono;
        this.comisionPorcentaje = comisionPorcentaje;
        this.esInstantaneo = esInstantaneo;
        this.tieneTrazabilidad = tieneTrazabilidad;
        this.descripcion = descripcion;
    }
    
    /**
     * Calcula la comisión aplicable a un monto
     */
    public Double calcularComision(Double monto) {
        if (monto == null || monto <= 0) {
            return 0.0;
        }
        return monto * (comisionPorcentaje / 100);
    }
    
    /**
     * Calcula el monto total incluyendo comisión
     */
    public Double calcularMontoConComision(Double monto) {
        if (monto == null || monto <= 0) {
            return monto;
        }
        return monto + calcularComision(monto);
    }
    
    /**
     * Verifica si es un método digital
     */
    public Boolean esMetodoDigital() {
        return this != CASH && this != CHECK;
    }
    
    /**
     * Verifica si tiene costos adicionales
     */
    public Boolean tieneComision() {
        return comisionPorcentaje > 0;
    }
    
    /**
     * Busca un PaymentMethod por nombre
     */
    public static PaymentMethod fromString(String nombre) {
        for (PaymentMethod metodo : PaymentMethod.values()) {
            if (metodo.name().equalsIgnoreCase(nombre) || 
                metodo.displayName.equalsIgnoreCase(nombre)) {
                return metodo;
            }
        }
        throw new IllegalArgumentException("Método de pago no válido: " + nombre);
    }
}

