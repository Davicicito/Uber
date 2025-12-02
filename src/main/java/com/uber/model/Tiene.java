package com.uber.model;

import java.time.LocalDateTime;

/**
 * Relación entre un vehículo y un mantenimiento realizado.
 * Guarda información del tipo de mantenimiento, fecha, coste y notas adicionales.
 */
public class Tiene {

    /*** Atributos de la relación. */
    private Vehiculo vehiculo;
    private Mantenimiento mantenimiento;
    private LocalDateTime fechaHora;
    private double coste;
    private String notas;

    /** Constructor vacío */
    public Tiene() {}

    /**
     * Constructor completo.
     * @param vehiculo vehículo al que se le aplica el mantenimiento
     * @param mantenimiento mantenimiento realizado
     * @param fechaHora fecha y hora del mantenimiento
     * @param coste coste total del mantenimiento
     * @param notas notas adicionales
     */
    public Tiene(Vehiculo vehiculo, Mantenimiento mantenimiento, LocalDateTime fechaHora, double coste, String notas) {
        this.vehiculo = vehiculo;
        this.mantenimiento = mantenimiento;
        this.fechaHora = fechaHora;
        this.coste = coste;
        this.notas = notas;
    }



    /** Getters y Setters */
    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public Mantenimiento getMantenimiento() {
        return mantenimiento;
    }

    public void setMantenimiento(Mantenimiento mantenimiento) {
        this.mantenimiento = mantenimiento;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public double getCoste() {
        return coste;
    }

    public void setCoste(double coste) {
        this.coste = coste;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    @Override
    public String toString() {
        return "Mantenimiento aplicado al vehículo " + vehiculo +
                " el " + fechaHora + " (tipo " + mantenimiento + ")";
    }
}
