package com.cineplus.presentacion;

import com.cineplus.dto.SesionDTO;

import java.awt.Cursor;
import java.awt.FlowLayout;
import java.util.EnumMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Barra inferior de las pantallas del cliente: Inicio, Cartelera, Boletos, Historial,
 * Comunidad, CinePLUS y el avatar (Perfil). Uso en una pantalla nueva:
 * <pre>pnlPie.add(new PanelNavegacion(coordinador, Seccion.BOLETOS));</pre>
 *
 * @author luisf
 */
public final class PanelNavegacion extends JPanel {

    private final CoordinadorAplicacion coordinador;
    private final Seccion actual;
    private final Map<Seccion, JButton> botones = new EnumMap<>(Seccion.class);

    public PanelNavegacion(CoordinadorAplicacion coordinador, Seccion actual) {
        super(new FlowLayout(FlowLayout.CENTER, 12, 5));
        this.coordinador = coordinador;
        this.actual = actual;
        setOpaque(false);
        for (Seccion seccion : Seccion.values()) {
            JButton boton = new JButton(seccion.texto());
            boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            boton.setFocusable(false);
            boton.addActionListener(e -> coordinador.navegar(seccion));
            botones.put(seccion, boton);
            add(boton);
        }
        actualizar();
    }

    /** Vuelve a leer la sesion: iniciales en el avatar y botones habilitados o no. */
    public void actualizar() {
        SesionDTO sesion = coordinador.sesion();
        for (Map.Entry<Seccion, JButton> entrada : botones.entrySet()) {
            Seccion seccion = entrada.getKey();
            JButton boton = entrada.getValue();
            boton.setEnabled(sesion != null || !seccion.requiereSesion());
            if (seccion == actual) {
                Tema.botonPrimario(boton);
            } else {
                Tema.botonSecundario(boton);
            }
        }
        JButton avatar = botones.get(Seccion.PERFIL);
        avatar.setText(sesion == null ? "Perfil" : iniciales(sesion.usuario().nombre()));
        avatar.setToolTipText(sesion == null ? "Inicia sesión" : sesion.usuario().nombre());
        avatar.putClientProperty("JButton.buttonType", "roundRect");
    }

    /** "Ana Lopez" da "AL". */
    static String iniciales(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return "?";
        }
        StringBuilder sb = new StringBuilder();
        for (String parte : nombre.trim().split("\\s+")) {
            sb.append(Character.toUpperCase(parte.charAt(0)));
            if (sb.length() == 2) {
                break;
            }
        }
        return sb.toString();
    }
}
