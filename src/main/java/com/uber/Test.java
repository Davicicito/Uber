package com.uber;

import com.uber.dao.UsuarioDAO;
import com.uber.model.Usuario;

import java.util.List;

public class Test {
    public static void main(String[] args) {

        UsuarioDAO dao = new UsuarioDAO();

        List<Usuario> usuarios = dao.getAllUsuarios();

        System.out.println("Usuarios encontrados en la BD:");
        for (Usuario u : usuarios) {
            System.out.println("- " + u.getNombre() + " " + u.getApellidos());
        }
    }
}

