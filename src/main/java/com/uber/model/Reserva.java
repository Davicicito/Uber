package com.uber.model;

import com.uber.enums.EstadoReserva;

import java.time.LocalDateTime;

/**
 * Representa una reserva realizada por un usuario sobre un vehículo.
 * Incluye información de fechas, coste y estado de la reserva.
 */
public class Reserva {

    /*** Atributos de la reserva. */
    private int idReserva;
    private Usuario usuario;
    private Vehiculo vehiculo;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private double coste;
    private EstadoReserva estado;

    /** Constructor vacío */
    public Reserva() {
    }

    /**
     * Constructor completo.
     * @param idReserva id de la reserva
     * @param usuario usuario asociado
     * @param vehiculo vehículo reservado
     * @param fechaHoraInicio inicio de la reserva
     * @param fechaHoraFin fin de la reserva
     * @param coste coste total
     * @param estado estado de la reserva
     */
    public Reserva(int idReserva, Usuario usuario, Vehiculo vehiculo, LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin, double coste, EstadoReserva estado) {
        this.idReserva = idReserva;
        this.usuario = usuario;
        this.vehiculo = vehiculo;
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
        this.coste = coste;
        this.estado = estado;
    }


    /** Getters y Setters */
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
