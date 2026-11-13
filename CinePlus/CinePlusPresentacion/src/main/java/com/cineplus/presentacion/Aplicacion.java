package com.cineplus.presentacion;

import com.cineplus.negocio.FachadaNegocio;

import javax.swing.SwingUtilities;

public final class Aplicacion {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(
                () -> {
                    Tema.instalar();
                    new CoordinadorAplicacion(new FachadaNegocio()).inicio();
                });
    }
}
