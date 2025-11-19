package com.uber.model;

import com.uber.enums.EstadoReserva;

import java.time.LocalDateTime;

public class Reserva {

    private int idReserva;
    private Usuario usuario;
    private Vehiculo vehiculo;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private double coste;
    private EstadoReserva estado;

    public Reserva() {
    }

    public Reserva(int idReserva, Usuario usuario, Vehiculo vehiculo, LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin, double coste, EstadoReserva estado) {
        this.idReserva = idReserva;
        this.usuario = usuario;
        this.vehiculo = vehiculo;
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
        this.coste = coste;
        this.estado = estado;
    }


// getters y setters

    public int getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(int idReserva) {
        this.idReserva = idReserva;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public LocalDateTime getFechaHoraInicio() {
        return fechaHoraInicio;
    }

    public void setFechaHoraInicio(LocalDateTime fechaHoraInicio) {
        this.fechaHoraInicio = fechaHoraInicio;
    }

    public LocalDateTime getFechaHoraFin() {
        return fechaHoraFin;
    }

    public void setFechaHoraFin(LocalDateTime fechaHoraFin) {
        this.fechaHoraFin = fechaHoraFin;
    }

    public double getCoste() {
        return coste;
    }

    public void setCoste(double coste) {
        this.coste = coste;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Reserva{" +
                "idReserva=" + idReserva +
                ", usuario=" + usuario +
                ", vehiculo=" + vehiculo +
                ", fechaHoraInicio=" + fechaHoraInicio +
                ", fechaHoraFin=" + fechaHoraFin +
                ", coste=" + coste +
                ", estado=" + estado +
                '}';
    }
}
