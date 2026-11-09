/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cineplus.entidades;
import com.cineplus.entidades.enums.Clasificacion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
/**
 *
 * @author luisf
 */
@Entity
@Table(name = "peliculas")
public class Pelicula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pelicula")
    private Integer idPelicula;

    // Id de la pelicula en TMDB, para no importarla dos veces
    @Column(name = "id_externo", unique = true, length = 20)
    private String idExterno;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(length = 200)
    private String director;

    @Column(length = 100)
    private String genero;

    @Column(name = "duracion_minutos")
    private Integer duracionMinutos;

    @Column(name = "fecha_estreno")
    private LocalDate fechaEstreno;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Clasificacion clasificacion;

    @Column(length = 2000)
    private String sinopsis;

    // Direccion (URL) de la imagen del poster
    @Column(length = 500)
    private String imagen;

    @Column(nullable = false)
    private Boolean disponible = Boolean.TRUE;

    @Column(name = "inicio_cartelera")
    private LocalDate inicioCartelera;

    @Column(name = "fin_cartelera")
    private LocalDate finCartelera;

    public Pelicula() {
    }

    public Integer getIdPelicula() {
        return idPelicula;
    }

    public void setIdPelicula(Integer idPelicula) {
        this.idPelicula = idPelicula;
    }

    public String getIdExterno() {
        return idExterno;
    }

    public void setIdExterno(String idExterno) {
        this.idExterno = idExterno;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public Integer getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(Integer duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    public LocalDate getFechaEstreno() {
        return fechaEstreno;
    }

    public void setFechaEstreno(LocalDate fechaEstreno) {
        this.fechaEstreno = fechaEstreno;
    }

    public Clasificacion getClasificacion() {
        return clasificacion;
    }

    public void setClasificacion(Clasificacion clasificacion) {
        this.clasificacion = clasificacion;
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public void setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Boolean getDisponible() {
        return disponible;
    }

    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }

    public LocalDate getInicioCartelera() {
        return inicioCartelera;
    }

    public void setInicioCartelera(LocalDate inicioCartelera) {
        this.inicioCartelera = inicioCartelera;
    }

    public LocalDate getFinCartelera() {
        return finCartelera;
    }

    public void setFinCartelera(LocalDate finCartelera) {
        this.finCartelera = finCartelera;
    }
}
