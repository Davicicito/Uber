package com.uber.model;

import com.uber.enums.EstadoReserva;

import java.time.LocalDateTime;

public class Reserva {

    private int idReserva;
    private int idUsuario;
    private int idVehiculo;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private double coste;
    private EstadoReserva estado;

    public Reserva() {}

    public Reserva(int idReserva, int idUsuario, int idVehiculo,
                   LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin,
                   double coste, EstadoReserva estado) {
        this.idReserva = idReserva;
        this.idUsuario = idUsuario;
        this.idVehiculo = idVehiculo;
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
        this.coste = coste;
        this.estado = estado;
    }

    // getters y setters

    public int getIdReserva() { return idReserva; }
    public void setIdReserva(int idReserva) { this.idReserva = idReserva; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public int getIdVehiculo() { return idVehiculo; }
    public void setIdVehiculo(int idVehiculo) { this.idVehiculo = idVehiculo; }

    public LocalDateTime getFechaHoraInicio() { return fechaHoraInicio; }
    public void setFechaHoraInicio(LocalDateTime fechaHoraInicio) { this.fechaHoraInicio = fechaHoraInicio; }

    public LocalDateTime getFechaHoraFin() { return fechaHoraFin; }
    public void setFechaHoraFin(LocalDateTime fechaHoraFin) { this.fechaHoraFin = fechaHoraFin; }

    public double getCoste() { return coste; }
    public void setCoste(double coste) { this.coste = coste; }

    public EstadoReserva getEstado() { return estado; }
    public void setEstado(EstadoReserva estado) { this.estado = estado; }

    @Override
    public String toString() {
        return "Reserva " + idReserva + " - Usuario " + idUsuario;
    }
}
