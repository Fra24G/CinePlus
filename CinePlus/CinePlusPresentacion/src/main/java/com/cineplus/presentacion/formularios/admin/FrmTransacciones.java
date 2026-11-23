package com.cineplus.presentacion.formularios.admin;

import com.cineplus.dto.FiltroComprasDTO;
import com.cineplus.dto.PaginaDTO;
import com.cineplus.dto.TransaccionDTO;
import com.cineplus.presentacion.CoordinadorAplicacion;
import com.cineplus.presentacion.CoordinadorBoletos;
import com.cineplus.presentacion.ModeloTablaCompras;
import com.cineplus.presentacion.ModeloTablaTransacciones;
import com.cineplus.presentacion.Paginador;
import com.cineplus.presentacion.Tema;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

/**
 * Pantalla "Historial de Transacciones" del admin (F21): barra admin (Panel /
 * Transacciones), buscador "Buscar funcion o pelicula...", tabla con
 * ModeloTablaTransacciones y paginacion de 10 en 10.
 *
 * @author Mario Perez
 */
public class FrmTransacciones extends JFrame {

    private CoordinadorAplicacion coordinador;
    private final ModeloTablaTransacciones modelo = new ModeloTablaTransacciones();
    private final JTable tblTransacciones = new JTable(modelo);
    private final JTextField txtBuscar = new JTextField(28);
    private final JButton btnBuscar = new JButton("Buscar");
    private final JButton btnPanel = new JButton("Panel");
    private final JButton btnTransacciones = new JButton("Transacciones");
    private final JButton btnCerrarSesion = new JButton("Cerrar sesión");
    private final JLabel lblTotal = new JLabel(" ");
    private Paginador paginador;

    /** Constructor para el editor visual de NetBeans. */
    public FrmTransacciones() {
        initComponents();
        configurarVista();
    }

    /** Arma la vista y carga la primera pagina sin filtro. */
    public FrmTransacciones(CoordinadorAplicacion coordinador) {
        this();
        this.coordinador = coordinador;
        paginador = new Paginador(pagina -> buscar());
        pnlPaginador.add(paginador, BorderLayout.CENTER);
        conectarEventos();
        buscar();
    }

    private void configurarVista() {
        Tema.preparar(this);
        setTitle("CinePlus · Historial de Transacciones");

        // Barra admin: Panel / Transacciones
        JPanel barra = new JPanel(new BorderLayout());
        barra.setOpaque(false);
        JPanel secciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        secciones.setOpaque(false);
        JLabel lblLogo = Tema.logo(26);
        secciones.add(lblLogo);
        Tema.botonSecundario(btnPanel);
        Tema.botonPrimario(btnTransacciones);
        secciones.add(btnPanel);
        secciones.add(btnTransacciones);
        barra.add(secciones, BorderLayout.WEST);
        Tema.botonSecundario(btnCerrarSesion);
        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        derecha.setOpaque(false);
        derecha.add(btnCerrarSesion);
        barra.add(derecha, BorderLayout.EAST);
        pnlCabecera.add(barra, BorderLayout.CENTER);

        Tema.titulo(lblTitulo);

        // Buscador
        JPanel pnlBuscador = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlBuscador.setOpaque(false);
        Tema.campoFigma(txtBuscar, "Buscar función o película...", 20, 40);
        txtBuscar.putClientProperty("JTextField.showClearButton", true);
        Tema.botonPrimario(btnBuscar);
        pnlBuscador.add(txtBuscar);
        pnlBuscador.add(btnBuscar);

        ModeloTablaCompras.configurarTabla(tblTransacciones, ModeloTablaTransacciones.COLUMNA_ESTADO);
        JScrollPane scroll = new JScrollPane(tblTransacciones);
        scroll.setBorder(BorderFactory.createLineBorder(Tema.BORDE_TARJETA));

        lblTotal.setForeground(Tema.MUTED);
        lblTotal.setFont(Tema.fuente(Font.PLAIN, 13));

        JPanel pnlTabla = new JPanel(new BorderLayout(0, 6));
        pnlTabla.setOpaque(false);
        pnlTabla.add(scroll, BorderLayout.CENTER);
        pnlTabla.add(lblTotal, BorderLayout.SOUTH);

        pnlCuerpo.setLayout(new BorderLayout(0, 12));
        pnlCuerpo.add(pnlBuscador, BorderLayout.NORTH);
        pnlCuerpo.add(pnlTabla, BorderLayout.CENTER);

        lblError.setForeground(Tema.ERROR);
        lblEstado.setForeground(Tema.MUTED);
    }

    private void conectarEventos() {
        btnPanel.addActionListener(e -> coordinador.admin().abrirPanel());
        btnCerrarSesion.addActionListener(e -> coordinador.cerrarSesion());
        btnBuscar.addActionListener(e -> filtrar());
        txtBuscar.addActionListener(e -> filtrar());
    }

    /** Al cambiar la busqueda se regresa a la pagina 1. */
    private void filtrar() {
        paginador.reiniciar();
        buscar();
    }

    private void buscar() {
        coordinador.boletos().cargarTransacciones(this, leerFiltro());
    }

    /** Llena la tabla y el paginador. */
    public void mostrarTransacciones(PaginaDTO<TransaccionDTO> pagina) {
        modelo.mostrar(pagina.elementos());
        paginador.mostrar(pagina);
        lblTotal.setText(pagina.total() == 0
                ? "No hay transacciones."
                : pagina.total() + (pagina.total() == 1 ? " transacción" : " transacciones"));
    }

    /** Texto del buscador + pagina actual + tamano 10. */
    public FiltroComprasDTO leerFiltro() {
        String texto = txtBuscar.getText().isBlank() ? null : txtBuscar.getText().strip();
        int pagina = paginador == null ? 0 : paginador.pagina();
        return new FiltroComprasDTO(texto, null, null, null, pagina, CoordinadorBoletos.FILAS_POR_PAGINA);
    }

    /** Deshabilita botones y muestra "Cargando…" mientras corre la tarea. */
    public void ocupado(boolean ocupado) {
        btnBuscar.setEnabled(!ocupado);
        btnPanel.setEnabled(!ocupado);
        if (paginador != null) {
            paginador.setEnabled(!ocupado);
        }
        lblEstado.setText(ocupado ? "Cargando…" : "");
        if (ocupado) {
            mostrarError(null);
        }
    }

    /** Muestra el mensaje de la Exception en la etiqueta roja (null la limpia). */
    public void mostrarError(String mensaje) {
        lblError.setText(mensaje == null || mensaje.isBlank()
                ? ""
                : "<html>" + Tema.escapar(mensaje) + "</html>");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlFondo = new javax.swing.JPanel();
        pnlCabecera = new javax.swing.JPanel();
        pnlContenido = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        pnlCuerpo = new javax.swing.JPanel();
        pnlPie = new javax.swing.JPanel();
        lblError = new javax.swing.JLabel();
        pnlPaginador = new javax.swing.JPanel();
        lblEstado = new javax.swing.JLabel();
        pnlNavegacion = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pnlFondo.setBackground(new java.awt.Color(23, 14, 20));
        pnlFondo.setPreferredSize(new java.awt.Dimension(1110, 740));
        pnlFondo.setLayout(new java.awt.BorderLayout(16, 16));

        pnlCabecera.setOpaque(false);
        pnlCabecera.setLayout(new java.awt.BorderLayout());
        pnlFondo.add(pnlCabecera, java.awt.BorderLayout.NORTH);

        pnlContenido.setOpaque(false);
        pnlContenido.setLayout(new java.awt.BorderLayout(12, 12));

        lblTitulo.setText("Historial de Transacciones");
        pnlContenido.add(lblTitulo, java.awt.BorderLayout.NORTH);

        pnlCuerpo.setOpaque(false);
        pnlCuerpo.setLayout(new java.awt.BorderLayout());
        pnlContenido.add(pnlCuerpo, java.awt.BorderLayout.CENTER);

        pnlPie.setOpaque(false);
        pnlPie.setLayout(new java.awt.BorderLayout(8, 8));

        lblError.setText("");
        pnlPie.add(lblError, java.awt.BorderLayout.NORTH);

        pnlPaginador.setOpaque(false);
        pnlPaginador.setLayout(new java.awt.BorderLayout());
        pnlPie.add(pnlPaginador, java.awt.BorderLayout.CENTER);

        lblEstado.setText("");
        pnlPie.add(lblEstado, java.awt.BorderLayout.SOUTH);

        pnlContenido.add(pnlPie, java.awt.BorderLayout.SOUTH);

        pnlFondo.add(pnlContenido, java.awt.BorderLayout.CENTER);

        pnlNavegacion.setOpaque(false);
        pnlNavegacion.setLayout(new java.awt.BorderLayout());
        pnlFondo.add(pnlNavegacion, java.awt.BorderLayout.SOUTH);

        getContentPane().add(pnlFondo, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblError;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel pnlCabecera;
    private javax.swing.JPanel pnlContenido;
    private javax.swing.JPanel pnlCuerpo;
    private javax.swing.JPanel pnlFondo;
    private javax.swing.JPanel pnlNavegacion;
    private javax.swing.JPanel pnlPaginador;
    private javax.swing.JPanel pnlPie;
    // End of variables declaration//GEN-END:variables
}
