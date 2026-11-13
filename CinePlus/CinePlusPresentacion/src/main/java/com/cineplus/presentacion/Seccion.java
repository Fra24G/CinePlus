package com.cineplus.presentacion;

/**
 * Secciones de la barra de navegacion. Las usa PanelNavegacion para marcar el boton
 * actual y CoordinadorAplicacion.navegar para saber a donde ir.
 *
 * @author luisf
 */
public enum Seccion {
    INICIO("Inicio", false),
    CARTELERA("Cartelera", false),
    BOLETOS("Boletos", true),
    HISTORIAL("Historial", true),
    COMUNIDAD("Comunidad", true),
    CINEPLUS("CinePLUS", true),
    PERFIL("Perfil", true);

    private final String texto;
    private final boolean requiereSesion;

    Seccion(String texto, boolean requiereSesion) {
        this.texto = texto;
        this.requiereSesion = requiereSesion;
    }

    /** Texto del boton. */
    public String texto() {
        return texto;
    }

    /** Si la seccion solo se puede abrir con la sesion iniciada. */
    public boolean requiereSesion() {
        return requiereSesion;
    }
}
