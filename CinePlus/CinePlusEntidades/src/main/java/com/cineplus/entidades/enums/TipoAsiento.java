/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cineplus.entidades.enums;
/**
 *
 * @author luisf
 */

/** Tipo de asiento. El precio de cada tipo se define después, en Negocio. */
public enum TipoAsiento {
    CLASICO("Clásico"),
    VIP("VIP");

    private final String etiqueta;

    TipoAsiento(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}