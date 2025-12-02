package com.uber.model;

/**
 * Representa una estación donde se encuentran los vehículos.
 * Incluye información básica como la ciudad, nombre, dirección y capacidad.
 */
public class Estacion {

    /*** Atributos de la estación. */
    private int idEstacion;
    private String ciudad;
    private String nombreEstacion;
    private String direccion;
    private int capacidad;

    /** Constructor vacío */
    public Estacion() {}


    /**
     * Constructor completo.
     * @param idEstacion id de la estación
     * @param ciudad ciudad donde está ubicada
     * @param nombreEstacion nombre de la estación
     * @param direccion dirección física
     * @param capacidad número máximo de vehículos
     */
    public Estacion(int idEstacion, String ciudad, String nombreEstacion,
                    String direccion, int capacidad) {
        this.idEstacion = idEstacion;
        this.ciudad = ciudad;
        this.nombreEstacion = nombreEstacion;
        this.direccion = direccion;
        this.capacidad = capacidad;
    }

    /** Getters y Setters */
    public int getIdEstacion() { return idEstacion; }
    public void setIdEstacion(int idEstacion) { this.idEstacion = idEstacion; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getNombreEstacion() { return nombreEstacion; }
    public void setNombreEstacion(String nombreEstacion) { this.nombreEstacion = nombreEstacion; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }

    @Override
    public String toString() {
        return nombreEstacion + " (" + ciudad + ")";
    }
}
