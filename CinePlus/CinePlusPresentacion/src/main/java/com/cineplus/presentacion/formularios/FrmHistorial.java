package com.cineplus.presentacion.formularios;

import com.cineplus.dto.CompraDTO;
import com.cineplus.dto.FiltroComprasDTO;
import com.cineplus.dto.PaginaDTO;
import com.cineplus.presentacion.CoordinadorAplicacion;
import com.cineplus.presentacion.CoordinadorBoletos;
import com.cineplus.presentacion.FormatoCompra;
import com.cineplus.presentacion.ModeloTablaCompras;
import com.cineplus.presentacion.Paginador;
import com.cineplus.presentacion.PanelEncabezado;
import com.cineplus.presentacion.PanelNavegacion;
import com.cineplus.presentacion.Seccion;
import com.cineplus.presentacion.Tema;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

/**
 * Pantalla "Historial de compras" (F10) en formato tabla: filtros (desde, hasta,
 * estado), tabla con ModeloTablaCompras y paginacion de 10 en 10. Doble clic en
 * una fila abre el detalle de la compra.
 *
 * @author Mario Perez
 */
public class FrmHistorial extends JFrame {

    /** Opciones del filtro de estado; "Todos" no filtra. */
    private static final String[] ESTADOS = {"Todos", "Aprobado", "Cancelado", "Denegado"};

    private CoordinadorAplicacion coordinador;
    private final ModeloTablaCompras modelo = new ModeloTablaCompras();
    private final JTable tblCompras = new JTable(modelo);
    private final JTextField txtDesde = new JTextField(9);
    private final JTextField txtHasta = new JTextField(9);
    private final JComboBox<String> cboEstado = new JComboBox<>(ESTADOS);
    private final JButton btnFiltrar = new JButton("Filtrar");
    private final JButton btnLimpiar = new JButton("Limpiar");
    private final JLabel lblTotal = new JLabel(" ");
    private Paginador paginador;

    /** Constructor para el editor visual de NetBeans. */
    public FrmHistorial() {
        initComponents();
        configurarVista();
    }

    /** Arma la vista y carga la primera pagina sin filtros. */
    public FrmHistorial(CoordinadorAplicacion coordinador) {
        this();
        this.coordinador = coordinador;
        pnlNavegacion.add(new PanelNavegacion(coordinador, Seccion.HISTORIAL), BorderLayout.CENTER);
        paginador = new Paginador(pagina -> buscar());
        pnlPaginador.add(paginador, BorderLayout.CENTER);
        conectarEventos();
        buscar();
    }

    private void configurarVista() {
        Tema.preparar(this);
        setTitle("CinePlus · Historial de compras");
        pnlCabecera.add(new PanelEncabezado(null), BorderLayout.CENTER);
        Tema.titulo(lblTitulo);

        // Panel de filtros
        JPanel pnlFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlFiltros.setOpaque(false);
        Tema.campoFigma(txtDesde, "dd/mm/aaaa", 12, 38);
        Tema.campoFigma(txtHasta, "dd/mm/aaaa", 12, 38);
        cboEstado.setPreferredSize(new java.awt.Dimension(140, 38));
        Tema.botonPrimario(btnFiltrar);
        Tema.botonSecundario(btnLimpiar);
        pnlFiltros.add(filtro("Filtros:"));
        pnlFiltros.add(filtro("Desde"));
        pnlFiltros.add(txtDesde);
        pnlFiltros.add(filtro("Hasta"));
        pnlFiltros.add(txtHasta);
        pnlFiltros.add(filtro("Estado"));
        pnlFiltros.add(cboEstado);
        pnlFiltros.add(btnFiltrar);
        pnlFiltros.add(btnLimpiar);

        ModeloTablaCompras.configurarTabla(tblCompras, ModeloTablaCompras.COLUMNA_ESTADO);
        tblCompras.setToolTipText("Doble clic para ver el detalle de la compra");
        JScrollPane scroll = new JScrollPane(tblCompras);
        scroll.setBorder(BorderFactory.createLineBorder(Tema.BORDE_TARJETA));

        lblTotal.setForeground(Tema.MUTED);
        lblTotal.setFont(Tema.fuente(Font.PLAIN, 13));

        JPanel pnlTabla = new JPanel(new BorderLayout(0, 6));
        pnlTabla.setOpaque(false);
        pnlTabla.add(scroll, BorderLayout.CENTER);
        pnlTabla.add(lblTotal, BorderLayout.SOUTH);

        pnlCuerpo.setLayout(new BorderLayout(0, 12));
        pnlCuerpo.add(pnlFiltros, BorderLayout.NORTH);
        pnlCuerpo.add(pnlTabla, BorderLayout.CENTER);

        lblError.setForeground(Tema.ERROR);
        lblEstado.setForeground(Tema.MUTED);
    }

    private static JLabel filtro(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(Tema.fuente(Font.BOLD, 14));
        etiqueta.setForeground(Tema.TEXTO_SUAVE);
        return etiqueta;
    }

    private void conectarEventos() {
        btnFiltrar.addActionListener(e -> filtrar());
        txtDesde.addActionListener(e -> filtrar());
        txtHasta.addActionListener(e -> filtrar());
        btnLimpiar.addActionListener(e -> {
            txtDesde.setText("");
            txtHasta.setText("");
            cboEstado.setSelectedIndex(0);
            filtrar();
        });
        tblCompras.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    CompraDTO compra = modelo.compraEn(tblCompras.rowAtPoint(e.getPoint()));
                    if (compra != null) {
                        coordinador.boletos().abrirDetalle(compra.idCompra());
                    }
                }
            }
        });
    }

    /** Al cambiar los filtros se regresa a la pagina 1. */
    private void filtrar() {
        paginador.reiniciar();
        buscar();
    }

    private void buscar() {
        FiltroComprasDTO filtro = leerFiltro();
        if (filtro != null) {
            coordinador.boletos().cargarHistorial(this, filtro);
        }
    }

    /** Llena la tabla y actualiza el paginador. */
    public void mostrarCompras(PaginaDTO<CompraDTO> pagina) {
        modelo.mostrar(pagina.elementos());
        paginador.mostrar(pagina);
        lblTotal.setText(pagina.total() == 0
                ? "No hay compras con estos filtros."
                : pagina.total() + (pagina.total() == 1 ? " compra" : " compras"));
    }

    /**
     * Filtros de la pantalla + pagina actual + tamano 10. Si una fecha no tiene
     * el formato correcto marca el campo, muestra el error y devuelve null.
     */
    public FiltroComprasDTO leerFiltro() {
        Tema.marcarError(txtDesde, false);
        Tema.marcarError(txtHasta, false);
        LocalDate desde;
        LocalDate hasta;
        try {
            desde = FormatoCompra.leerFecha(txtDesde.getText(), "Desde");
        } catch (Exception e) {
            Tema.marcarError(txtDesde, true);
            mostrarError(e.getMessage());
            return null;
        }
        try {
            hasta = FormatoCompra.leerFecha(txtHasta.getText(), "Hasta");
        } catch (Exception e) {
            Tema.marcarError(txtHasta, true);
            mostrarError(e.getMessage());
            return null;
        }
        String estado = cboEstado.getSelectedIndex() <= 0 ? null : (String) cboEstado.getSelectedItem();
        int pagina = paginador == null ? 0 : paginador.pagina();
        return new FiltroComprasDTO(null, desde, hasta, estado, pagina, CoordinadorBoletos.FILAS_POR_PAGINA);
    }

    /** Deshabilita botones y muestra "Cargando…" mientras corre la tarea. */
    public void ocupado(boolean ocupado) {
        btnFiltrar.setEnabled(!ocupado);
        btnLimpiar.setEnabled(!ocupado);
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

        lblTitulo.setText("Historial de compras");
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
