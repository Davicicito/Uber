package com.uber.utils;

import com.uber.model.Usuario;

/**
 * Clase Singleton para gestionar la sesión del usuario actual.
 * Permite mantener el usuario logueado accesible desde cualquier parte de la aplicación.
 */
public class Sesion {

    private static Sesion instancia;
    private Usuario usuarioLogueado;

    /**
     * Constructor privado para evitar instanciación externa.
     */
    private Sesion() {}

    /**
     * Obtiene la instancia única de la sesión.
     * Si no existe, la crea.
     * @return La instancia de Sesion.
     */
    public static Sesion getInstancia() {
        if (instancia == null) {
            instancia = new Sesion();
        }
        return instancia;
    }

    /**
     * Inicia sesión almacenando el usuario.
     * @param usuario El usuario que ha iniciado sesión.
     */
    public void logIn(Usuario usuario) {
        this.usuarioLogueado = usuario;
    }

    /**
     * Cierra la sesión actual eliminando el usuario almacenado.
     */
    public void logOut() {
        this.usuarioLogueado = null;
    }

    /**
     * Obtiene el usuario logueado actualmente.
     * @return El objeto Usuario o null si no hay sesión iniciada.
     */
    public Usuario getUsuarioLogueado() {
        return usuarioLogueado;
    }
}