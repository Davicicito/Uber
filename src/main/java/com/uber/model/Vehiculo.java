package com.uber.model;

import com.uber.enums.EstadoVehiculo;
import com.uber.enums.TipoVehiculo;

public class Vehiculo {

    private int idVehiculo;
    private TipoVehiculo tipo;
    private String marca;
    private String modelo;
    private EstadoVehiculo estadoVehiculo;
    private Estacion estacion;
    private double nivelBateria;
    private double kilometraje;

    public Vehiculo() {}

    public Vehiculo(int idVehiculo, TipoVehiculo tipo, String marca, String modelo, EstadoVehiculo estadoVehiculo, Estacion estacion, double nivelBateria, double kilometraje) {
        this.idVehiculo = idVehiculo;
        this.tipo = tipo;
        this.marca = marca;
        this.modelo = modelo;
        this.estadoVehiculo = estadoVehiculo;
        this.estacion = estacion;
        this.nivelBateria = nivelBateria;
        this.kilometraje = kilometraje;
    }

    // getters y setters

    public int getIdVehiculo() { return idVehiculo; }
    public void setIdVehiculo(int idVehiculo) { this.idVehiculo = idVehiculo; }

    public TipoVehiculo getTipo() { return tipo; }
    public void setTipo(TipoVehiculo tipo) { this.tipo = tipo; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public EstadoVehiculo getEstadoVehiculo() { return estadoVehiculo; }
    public void setEstadoVehiculo(EstadoVehiculo estadoVehiculo) { this.estadoVehiculo = estadoVehiculo; }

    public Estacion getEstacion() {
        return estacion;
    }

    public void setEstacion(Estacion estacion) {
        this.estacion = estacion;
    }

    public double getNivelBateria() { return nivelBateria; }
    public void setNivelBateria(double nivelBateria) { this.nivelBateria = nivelBateria; }

    public double getKilometraje() { return kilometraje; }
    public void setKilometraje(double kilometraje) { this.kilometraje = kilometraje; }

    @Override
    public String toString() {
        return tipo + " " + marca + " " + modelo + " (ID " + idVehiculo + ")";
    }
}