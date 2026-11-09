/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cineplus.entidades.enums;
/**
 *
 * @author luisf
 */

/** Idioma de la función. */
public enum TipoFuncion {
    ESPANOL("Español"),
    SUBTITULADA("Subtitulada");

    private final String etiqueta;

    TipoFuncion(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}