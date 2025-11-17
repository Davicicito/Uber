package com.uber.model;

public class Estacion {

    private int idEstacion;
    private String ciudad;
    private String nombreEstacion;
    private String direccion;
    private int capacidad;

    public Estacion() {}

    public Estacion(int idEstacion, String ciudad, String nombreEstacion,
                    String direccion, int capacidad) {
        this.idEstacion = idEstacion;
        this.ciudad = ciudad;
        this.nombreEstacion = nombreEstacion;
        this.direccion = direccion;
        this.capacidad = capacidad;
    }

    public int getIdEstacion() { return idEstacion; }
    public void setIdEstacion(int idEstacion) { this.idEstacion = idEstacion; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getNombreEstacion() { return nombreEstacion; }
    public void setNombreEstacion(String nombreEstacion) { this.nombreEstacion = nombreEstacion; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }

    @Override
    public String toString() {
        return nombreEstacion + " (" + ciudad + ")";
    }
}
