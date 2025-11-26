package com.uber.utils;

import com.uber.model.Usuario;

public class Sesion {

    private static Sesion instancia;
    private Usuario usuarioLogueado;

    private Sesion() {}

    public static Sesion getInstancia() {
        if (instancia == null) {
            instancia = new Sesion();
        }
        return instancia;
    }

    public void logIn(Usuario usuario) {
        this.usuarioLogueado = usuario;
    }

    public void logOut() {
        this.usuarioLogueado = null;
    }

    public Usuario getUsuarioLogueado() {
        return usuarioLogueado;
    }
}