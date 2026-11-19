package com.cineplus.presentacion.formularios;

import com.cineplus.presentacion.CarruselPeliculas;
import com.cineplus.presentacion.CoordinadorAplicacion;
import com.cineplus.presentacion.PanelEncabezado;
import com.cineplus.presentacion.PanelNavegacion;
import com.cineplus.presentacion.PresentadorCartelera;
import com.cineplus.presentacion.Seccion;
import com.cineplus.presentacion.Tema;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JFrame;

/**
 * Pantalla de inicio sin sesion (invitado): encabezado con logo CinePLUS, pildora de
 * busqueda y botones de campana y mas opciones; carrusel de peliculas; y barra inferior
 * tipo capsula con "Iniciar sesion" y "Registrar cuenta" a la derecha del separador.
 *
 * @author luisf
 */
public class FrmInicio extends JFrame {

    private CoordinadorAplicacion coordinador;
    private PresentadorCartelera cartelera;
    private CarruselPeliculas carrusel;
    private PanelEncabezado encabezado;
    private PanelNavegacion navegacion;

    public FrmInicio() {
        initComponents();
        configurarVista();
    }

    public FrmInicio(CoordinadorAplicacion coordinador) {
        this();
        this.coordinador = coordinador;
        cargar();
    }

    private void configurarVista() {
        Tema.preparar(this);
        setTitle("CinePlus · Inicio");
        encabezado = new PanelEncabezado();
        pnlCabecera.add(encabezado, BorderLayout.CENTER);
        lblPeliculasRecientes.setFont(Tema.fuente(Font.BOLD, 24));
        lblPeliculasRecientes.setForeground(Tema.TEXTO);
        lblEstado.setForeground(Tema.MUTED);
        lblSinResultados.setForeground(Tema.MUTED);
    }

    private void cargar() {
        navegacion = new PanelNavegacion(coordinador, Seccion.INICIO);
        pnlPie.add(navegacion, BorderLayout.CENTER);

        carrusel = new CarruselPeliculas(coordinador, coordinador::seleccionarPelicula);
        pnlPeliculas.add(carrusel, BorderLayout.CENTER);
        cartelera = new PresentadorCartelera(
                coordinador, carrusel, encabezado.campoBusqueda(), lblEstado, lblSinResultados);
        cartelera.cargar(this);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlFondo = new javax.swing.JPanel();
        pnlCabecera = new javax.swing.JPanel();
        pnlContenido = new javax.swing.JPanel();
        pnlEncabezado = new javax.swing.JPanel();
        lblPeliculasRecientes = new javax.swing.JLabel();
        lblEstado = new javax.swing.JLabel();
        pnlPeliculas = new javax.swing.JPanel();
        lblSinResultados = new javax.swing.JLabel();
        pnlPie = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pnlFondo.setBackground(new java.awt.Color(23, 14, 20));
        pnlFondo.setPreferredSize(new java.awt.Dimension(1110, 740));
        pnlFondo.setLayout(new java.awt.BorderLayout(16, 16));

        pnlCabecera.setOpaque(false);
        pnlCabecera.setLayout(new java.awt.BorderLayout());
        pnlFondo.add(pnlCabecera, java.awt.BorderLayout.NORTH);

        pnlContenido.setOpaque(false);
        pnlContenido.setLayout(new java.awt.BorderLayout(12, 12));

        pnlEncabezado.setOpaque(false);
        pnlEncabezado.setLayout(new java.awt.BorderLayout(12, 4));

        lblPeliculasRecientes.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblPeliculasRecientes.setForeground(new java.awt.Color(246, 241, 244));
        lblPeliculasRecientes.setText("Películas");
        pnlEncabezado.add(lblPeliculasRecientes, java.awt.BorderLayout.CENTER);

        lblEstado.setForeground(new java.awt.Color(179, 164, 175));
        lblEstado.setText("Cargando películas…");
        pnlEncabezado.add(lblEstado, java.awt.BorderLayout.EAST);

        pnlContenido.add(pnlEncabezado, java.awt.BorderLayout.NORTH);

        pnlPeliculas.setOpaque(false);
        pnlPeliculas.setPreferredSize(new java.awt.Dimension(1020, 390));
        pnlPeliculas.setLayout(new java.awt.BorderLayout());
        pnlContenido.add(pnlPeliculas, java.awt.BorderLayout.CENTER);

        lblSinResultados.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblSinResultados.setForeground(new java.awt.Color(179, 164, 175));
        lblSinResultados.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSinResultados.setText(" ");
        pnlContenido.add(lblSinResultados, java.awt.BorderLayout.SOUTH);

        pnlFondo.add(pnlContenido, java.awt.BorderLayout.CENTER);

        pnlPie.setOpaque(false);
        pnlPie.setLayout(new java.awt.BorderLayout());
        pnlFondo.add(pnlPie, java.awt.BorderLayout.SOUTH);

        getContentPane().add(pnlFondo, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblPeliculasRecientes;
    private javax.swing.JLabel lblSinResultados;
    private javax.swing.JPanel pnlCabecera;
    private javax.swing.JPanel pnlContenido;
    private javax.swing.JPanel pnlEncabezado;
    private javax.swing.JPanel pnlFondo;
    private javax.swing.JPanel pnlPeliculas;
    private javax.swing.JPanel pnlPie;
    // End of variables declaration//GEN-END:variables
}
