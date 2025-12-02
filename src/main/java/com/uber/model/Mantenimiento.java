package com.uber.model;

import com.uber.enums.TipoMantenimiento;

/**
 * Representa un mantenimiento que puede realizarse a un vehículo.
 * Incluye el tipo de mantenimiento y una descripción opcional.
 */
public class Mantenimiento {

    /*** Atributos del mantenimiento. */
    private int idMantenimiento;
    private TipoMantenimiento tipo;
    private String descripcion;

    /** Constructor vacío */
    public Mantenimiento() {}

    /**
     * Constructor completo.
     * @param idMantenimiento id del mantenimiento
     * @param tipo tipo de mantenimiento
     * @param descripcion descripción del trabajo realizado
     */
    public Mantenimiento(int idMantenimiento, TipoMantenimiento tipo, String descripcion) {
        this.idMantenimiento = idMantenimiento;
        this.tipo = tipo;
        this.descripcion = descripcion;
    }

    /** Getters y Setters */
    public int getIdMantenimiento() { return idMantenimiento; }
    public void setIdMantenimiento(int idMantenimiento) { this.idMantenimiento = idMantenimiento; }

    public TipoMantenimiento getTipo() {
        return tipo;
    }

    public void setTipo(TipoMantenimiento tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    @Override
    public String toString() {
        return "Mantenimiento{" +
                "id=" + idMantenimiento +
                ", tipo=" + (tipo != null ? tipo : "DESCONOCIDO") +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}
