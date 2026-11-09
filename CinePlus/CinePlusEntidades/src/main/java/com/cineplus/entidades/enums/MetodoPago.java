/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cineplus.entidades.enums;

/**
 *
 * @author luisf
 */

/** Cómo se pagó una compra. PUNTOS se usa cuando los puntos cubrieron el total. */
public enum MetodoPago {
    TARJETA("Tarjeta"),
    APPLE_GOOGLE_PAY("Apple / Google Pay"),
    PUNTOS("Puntos CinePLUS");

    private final String etiqueta;

    MetodoPago(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
