package com.uber.model;

import com.uber.enums.TipoMantenimiento;

public class Mantenimiento {

    private int idMantenimiento;
    private TipoMantenimiento tipo;
    private String descripcion;

    public Mantenimiento() {}

    public Mantenimiento(int idMantenimiento, TipoMantenimiento tipo, String descripcion) {
        this.idMantenimiento = idMantenimiento;
        this.tipo = tipo;
        this.descripcion = descripcion;
    }

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
        return tipo.toString();
    }
}
