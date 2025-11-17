package com.uber.model;

import java.time.LocalDateTime;

public class Tiene {

    private int idVehiculo;
    private int idMantenimiento;
    private LocalDateTime fechaHora;
    private double coste;
    private String notas;

    public Tiene() {}

    public Tiene(int idVehiculo, int idMantenimiento,
                 LocalDateTime fechaHora, double coste, String notas) {
        this.idVehiculo = idVehiculo;
        this.idMantenimiento = idMantenimiento;
        this.fechaHora = fechaHora;
        this.coste = coste;
        this.notas = notas;
    }

    // Getters y setters
    public int getIdVehiculo() {
        return idVehiculo;
    }

    public void setIdVehiculo(int idVehiculo) {
        this.idVehiculo = idVehiculo;
    }

    public int getIdMantenimiento() {
        return idMantenimiento;
    }

    public void setIdMantenimiento(int idMantenimiento) {
        this.idMantenimiento = idMantenimiento;
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
        return "Mantenimiento aplicado al vehículo " + idVehiculo +
                " el " + fechaHora + " (tipo " + idMantenimiento + ")";
    }
}
