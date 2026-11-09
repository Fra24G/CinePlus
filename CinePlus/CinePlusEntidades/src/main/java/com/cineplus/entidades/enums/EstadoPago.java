/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cineplus.entidades.enums;

/**
 *
 * @author luisf
 */
/** Estado de una compra. */
public enum EstadoPago {
    PENDIENTE("Pendiente"),
    APROBADO("Aprobado"),
    DENEGADO("Denegado"),
    CANCELADO("Cancelado");

    private final String etiqueta;

    EstadoPago(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}