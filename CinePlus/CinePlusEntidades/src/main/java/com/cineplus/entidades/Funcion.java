package com.cineplus.entidades;

import com.cineplus.entidades.enums.FormatoFuncion;
import com.cineplus.entidades.enums.TipoFuncion;
import java.time.LocalDateTime;
import jakarta.persistence.*;

/**
 * Una proyeccion de una pelicula en una sala, en una fecha y hora.
 * tipo es el idioma (Espanol o Subtitulada) y formato es 2D o 3D.
 *
 * @author luisf
 */
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoFuncion tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
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