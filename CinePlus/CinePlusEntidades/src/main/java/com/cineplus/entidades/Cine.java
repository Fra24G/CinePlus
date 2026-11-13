package com.cineplus.entidades;

import jakarta.persistence.*;

/**
 * Sucursal de cine.
 *
 * @author luisf
 */
@Entity
@Table(name = "cines")
public class Cine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cine")
    private Integer idCine;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String ciudad;

    public Cine() {
    }

    public Cine(String nombre, String ciudad) {
        this.nombre = nombre;
        this.ciudad = ciudad;
    }

    /**
     * Ejemplo: "CinePLUS Centro - Ciudad Obregon".
     */
    public String getNombreCompleto() {
        return nombre + " - " + ciudad;
    }

    public Integer getIdCine() {
        return idCine;
    }

    public void setIdCine(Integer idCine) {
        this.idCine = idCine;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }
}
