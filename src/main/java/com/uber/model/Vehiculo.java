package com.uber.model;

import com.uber.enums.EstadoVehiculo;
import com.uber.enums.TipoVehiculo;

/**
 * Clase que representa un vehículo de la flota.
 * Contiene información sobre su tipo, marca, modelo, estado, ubicación y métricas de uso.
 */
public class Vehiculo {

    private int idVehiculo;
    private TipoVehiculo tipo;
    private String marca;
    private String modelo;
    private EstadoVehiculo estadoVehiculo;
    private Estacion estacion;
    private double nivelBateria;
    private double kilometraje;

    /**
     * Constructor vacío por defecto.
     */
    public Vehiculo() {}

    /**
     * Constructor completo para inicializar un vehículo con todos sus datos.
     * @param idVehiculo Identificador único.
     * @param tipo Tipo de vehículo (Coche, Moto, Patinete).
     * @param marca Marca del fabricante.
     * @param modelo Modelo del vehículo.
     * @param estadoVehiculo Estado actual (Disponible, En Uso, Mantenimiento, Inactivo).
     * @param estacion Estación donde se encuentra o a la que pertenece.
     * @param nivelBateria Nivel de carga de la batería (0-100).
     * @param kilometraje Distancia total recorrida.
     */
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

    /**
     * Getters y Setters
     */
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