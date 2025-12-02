package com.uber.model;

import com.uber.enums.EstadoCuenta;
import com.uber.enums.Rol;

/**
 * Clase que representa a un usuario del sistema.
 * Almacena la información personal, credenciales de acceso y estado de la cuenta.
 */
public class Usuario {

    /**
     *  Atributos del usuario
     */
    private int idUsuario;
    private String nombre;
    private String apellidos;
    private String email;
    private String contrasena;
    private String telefono;
    private String metodoPago;
    private double saldo;
    private Rol rol;
    private EstadoCuenta estadoCuenta;

    /**
     * Constructor vacío
     */
    public Usuario() {}

    /**
     * Constructor completo para inicializar todos los atributos.
     * @param idUsuario Identificador único del usuario
     * @param nombre Nombre de pila
     * @param apellidos Apellidos del usuario
     * @param email Correo electrónico (usado para el login)
     * @param contrasena Contraseña de acceso
     * @param telefono Número de teléfono de contacto
     * @param metodoPago Forma de pago preferida (Tarjeta, PayPal, etc.)
     * @param saldo Dinero disponible en la cuenta
     * @param estadoCuenta Estado actual (ACTIVO, BLOQUEADO...)
     * @param rol Rol del usuario en la aplicación
     */
    public Usuario(int idUsuario, String nombre, String apellidos, String email, String contrasena,
                   String telefono, String metodoPago, double saldo, EstadoCuenta estadoCuenta, Rol rol) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.contrasena = contrasena;
        this.telefono = telefono;
        this.metodoPago = metodoPago;
        this.saldo = saldo;
        this.rol = rol;
        this.estadoCuenta = estadoCuenta;
    }

    /**
     * Getters y Setters
     */
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public double getSaldo() { return saldo; }
    public void setSaldo(double saldo) { this.saldo = saldo; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    public EstadoCuenta getEstadoCuenta() { return estadoCuenta; }
    public void setEstadoCuenta(EstadoCuenta estadoCuenta) { this.estadoCuenta = estadoCuenta; }

    @Override
    public String toString() {
        return nombre + " " + apellidos + " (" + email + ")";
    }
}

