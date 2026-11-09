/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cineplus.entidades.enums;
/**
 *
 * @author luisf
 */
/** Clasificación de la película, con la edad mínima para comprar boleto. */
public enum Clasificacion {
    G("G", 0),
    PG("PG", 0),
    PG_13("PG-13", 13),
    R("R", 17),
    NC_17("NC-17", 18);

    private final String etiqueta;
    private final int edadMinima;

    Clasificacion(String etiqueta, int edadMinima) {
        this.etiqueta = etiqueta;
        this.edadMinima = edadMinima;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public int getEdadMinima() {
        return edadMinima;
    }

    /** Busca por etiqueta ("PG-13") o por nombre ("PG_13"). Devuelve null si no existe. */
    public static Clasificacion porEtiqueta(String texto) {
        if (texto == null) {
            return null;
        }
        String buscado = texto.trim();
        for (Clasificacion c : values()) {
            if (c.etiqueta.equalsIgnoreCase(buscado) || c.name().equalsIgnoreCase(buscado)) {
                return c;
            }
        }
        return null;
    }
}