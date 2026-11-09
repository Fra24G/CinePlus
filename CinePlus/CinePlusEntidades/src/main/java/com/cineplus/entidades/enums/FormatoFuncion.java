/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cineplus.entidades.enums;
/**
 *
 * @author luisf
 */
/** Formato de proyección de la función. */
public enum FormatoFuncion {
    DOS_D("2D"),
    TRES_D("3D");

    private final String etiqueta;

    FormatoFuncion(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}