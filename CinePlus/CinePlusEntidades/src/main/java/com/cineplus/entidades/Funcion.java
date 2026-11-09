/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cineplus.entidades;

import com.cineplus.entidades.enums.FormatoFuncion;
import com.cineplus.entidades.enums.TipoFuncion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;/**
 *
 * @author luisf
 */
/** Una proyeccion de una pelicula en una sala, a una hora. */
@Entity
@Table(name = "funciones")
public class Funcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_funcion")
    private Integer idFuncion;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_pelicula", nullable = false)
    private Pelicula pelicula;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_sala", nullable = false)
    private Sala sala;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    // Idioma: Espanol o Subtitulada
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoFuncion tipo;

    // Formato: 2D o 3D
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private FormatoFuncion formato;

    public Funcion() {
    }

    public Integer getIdFuncion() {
        return idFuncion;
    }

    public void setIdFuncion(Integer idFuncion) {
        this.idFuncion = idFuncion;
    }

    public Pelicula getPelicula() {
        return pelicula;
    }

    public void setPelicula(Pelicula pelicula) {
        this.pelicula = pelicula;
    }

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public TipoFuncion getTipo() {
        return tipo;
    }

    public void setTipo(TipoFuncion tipo) {
        this.tipo = tipo;
    }

    public FormatoFuncion getFormato() {
        return formato;
    }

    public void setFormato(FormatoFuncion formato) {
        this.formato = formato;
    }
}