package com.uber;

/**
 * Clase de entrada principal para la aplicación.
 * Esta clase actúa como un lanzador (launcher) para evitar problemas de carga
 * con las librerías de JavaFX al ejecutar la aplicación desde un archivo JAR.
 * Simplemente redirige la ejecución a la clase {@link App}.
 */
public class Main {
    public static void main(String[] args) {
        App.main(args);
    }
}
