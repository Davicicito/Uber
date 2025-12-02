package com.uber;

import org.h2.tools.Server;

/**
 * Clase de utilidad para iniciar el servidor web de la base de datos H2.
 * Permite acceder a la consola de administración de H2 desde el navegador
 * para inspeccionar las tablas y los datos durante el desarrollo.
 */
public class TestH2 {

    /**
     * Método principal para arrancar el servidor de H2.
     * @param args Argumentos de consola.
     * @throws Exception Si hay error al iniciar el servidor.
     */
    public static void main(String[] args) throws Exception {
        // Inicia el servidor web de H2
        // -web: Habilita la interfaz web
        // -webAllowOthers: Permite conexiones externas
        // -browser: Intenta abrir el navegador automáticamente
        Server.createWebServer("-web", "-webAllowOthers", "-browser").start();

        System.out.println("Servidor H2 iniciado. Accede a la consola web.");
    }
}

