package com.uber.model;

import com.uber.enums.TipoMantenimiento;

public class Mantenimiento {

    private int idMantenimiento;
    private TipoMantenimiento nombreMantenimiento;
    private String descripcion;

    public Mantenimiento() {}

    public Mantenimiento(int idMantenimiento, TipoMantenimiento nombreMantenimiento, String descripcion) {
        this.idMantenimiento = idMantenimiento;
        this.nombreMantenimiento = nombreMantenimiento;
        this.descripcion = descripcion;
    }

    public int getIdMantenimiento() { return idMantenimiento; }
    public void setIdMantenimiento(int idMantenimiento) { this.idMantenimiento = idMantenimiento; }

    public TipoMantenimiento getNombreMantenimiento() { return nombreMantenimiento; }
    public void setNombreMantenimiento(TipoMantenimiento nombreMantenimiento) { this.nombreMantenimiento = nombreMantenimiento; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    @Override
    public String toString() {
        return nombreMantenimiento.toString();
    }
}
