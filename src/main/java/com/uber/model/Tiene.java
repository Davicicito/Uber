package com.uber.model;

import java.time.LocalDateTime;

public class Tiene {

    private Vehiculo vehiculo;
    private Mantenimiento mantenimiento;
    private LocalDateTime fechaHora;
    private double coste;
    private String notas;

    public Tiene() {}

    public Tiene(Vehiculo vehiculo, Mantenimiento mantenimiento, LocalDateTime fechaHora, double coste, String notas) {
        this.vehiculo = vehiculo;
        this.mantenimiento = mantenimiento;
        this.fechaHora = fechaHora;
        this.coste = coste;
        this.notas = notas;
    }

    // Getters y setters


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
