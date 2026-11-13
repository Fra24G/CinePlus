package com.cineplus.entidades;

import jakarta.persistence.*;

/**
 * Boleto de una compra: un asiento en una funcion, con su codigo unico para el
 * QR.
 *
 * @author luisf
 */
/**
 * Un boleto: un asiento de una funcion, dentro de una compra.
 */
@Entity
@Table(name = "boletos")
public class Boleto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_boleto")
    private Integer idBoleto;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_compra", nullable = false)
    private Compra compra;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_funcion", nullable = false)
    private Funcion funcion;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_asiento", nullable = false)
    private Asiento asiento;

    // Codigo unico que lleva el QR
    @Column(nullable = false, unique = true, length = 60)
    private String codigo;

    @Column(nullable = false)
    private Double precio;

    public Boleto() {
    }

    public Integer getIdBoleto() {
        return idBoleto;
    }

    public void setIdBoleto(Integer idBoleto) {
        this.idBoleto = idBoleto;
    }

    public Compra getCompra() {
        return compra;
    }

    public void setCompra(Compra compra) {
        this.compra = compra;
    }

    public Funcion getFuncion() {
        return funcion;
    }

    public void setFuncion(Funcion funcion) {
        this.funcion = funcion;
    }

    public Asiento getAsiento() {
        return asiento;
    }

    public void setAsiento(Asiento asiento) {
        this.asiento = asiento;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }
}
