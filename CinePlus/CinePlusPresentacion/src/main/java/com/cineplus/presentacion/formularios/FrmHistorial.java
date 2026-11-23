package com.cineplus.presentacion.formularios;

import com.cineplus.dto.CompraDTO;
import com.cineplus.dto.FiltroComprasDTO;
import com.cineplus.dto.PaginaDTO;
import com.cineplus.presentacion.CoordinadorAplicacion;
import com.cineplus.presentacion.ModeloTablaCompras;
import com.cineplus.presentacion.PanelEncabezado;
import com.cineplus.presentacion.PanelNavegacion;
import com.cineplus.presentacion.Seccion;
import com.cineplus.presentacion.Tema;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;

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
    /** Filas por pagina de la tabla. */
    private static final int FILAS_POR_PAGINA = 10;
    /** Cuantos numeros de pagina se muestran como maximo en el paginador. */
    private static final int PAGINAS_VISIBLES = 7;
    /** Columna "Estado" de ModeloTablaCompras. */
    private static final int COLUMNA_ESTADO = 6;
    /** Verde de "Aprobado". TODO: pedir a Desarrollador 1 que lo agregue a Tema. */
    private static final Color VERDE = new Color(0x4cc38a);
    /** Formato de captura de las fechas del filtro. */
    private static final DateTimeFormatter FECHA_CAPTURA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private CoordinadorAplicacion coordinador;
    private final ModeloTablaCompras modelo = new ModeloTablaCompras();
    private final JTable tblCompras = new JTable(modelo);
    private final JTextField txtDesde = new JTextField(9);
    private final JTextField txtHasta = new JTextField(9);
    private final JComboBox<String> cboEstado = new JComboBox<>(ESTADOS);
    private final JButton btnFiltrar = new JButton("Filtrar");
    private final JButton btnLimpiar = new JButton("Limpiar");
    private final JLabel lblTotal = new JLabel(" ");
    private int paginaActual;
    private int totalPaginas;
    private boolean paginadorActivo = true;

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

        configurarTabla();
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

    /**
     * Estilo de la tabla: filas altas, sin reordenar columnas, una sola seleccion y
     * el estado con color (Aprobado verde, Denegado rojo, Cancelado gris).
     */
    private void configurarTabla() {
        tblCompras.setRowHeight(36);
        tblCompras.setFont(Tema.fuente(Font.PLAIN, 14));
        tblCompras.getTableHeader().setFont(Tema.fuente(Font.BOLD, 14));
        tblCompras.getTableHeader().setReorderingAllowed(false);
        tblCompras.getTableHeader().setBackground(Tema.NAV_FONDO);
        tblCompras.getTableHeader().setForeground(Tema.TEXTO);
        tblCompras.getTableHeader().putClientProperty("FlatLaf.style",
                "background: " + Tema.hex(Tema.NAV_FONDO) + "; separatorColor: " + Tema.hex(Tema.NAV_BORDE)
                + "; bottomSeparatorColor: " + Tema.hex(Tema.NAV_BORDE) + "; height: 38");
        tblCompras.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCompras.setShowVerticalLines(false);
        tblCompras.setFillsViewportHeight(true);
        tblCompras.setBackground(Tema.TARJETA_MORADA);
        tblCompras.setForeground(Tema.TEXTO);
        tblCompras.setGridColor(Tema.BORDE_TARJETA);
        tblCompras.setSelectionBackground(Tema.NAV_ACTIVO);
        tblCompras.setSelectionForeground(Tema.TEXTO);
        tblCompras.getColumnModel().getColumn(COLUMNA_ESTADO).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionada,
                    boolean foco, int fila, int columna) {
                JLabel celda = (JLabel) super.getTableCellRendererComponent(tabla, valor, seleccionada, foco, fila, columna);
                String estado = valor == null ? "" : valor.toString();
                celda.setFont(celda.getFont().deriveFont(Font.BOLD));
                if (!seleccionada) {
                    celda.setForeground(switch (estado) {
                        case "Aprobado" -> VERDE;
                        case "Denegado" -> Tema.ERROR;
                        default -> Tema.MUTED;
                    });
                }
                return celda;
            }
        });
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
        paginaActual = 0;
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
        paginaActual = pagina.pagina();
        totalPaginas = pagina.totalPaginas();
        dibujarPaginador();
        lblTotal.setText(pagina.total() == 0
                ? "No hay compras con estos filtros."
                : pagina.total() + (pagina.total() == 1 ? " compra" : " compras"));
    }

    /** Paginador del Figma "&lt; 1 - 2 - 3 &gt;" dentro de pnlPaginador. */
    private void dibujarPaginador() {
        pnlPaginador.removeAll();
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        botones.setOpaque(false);
        if (totalPaginas > 1) {
            botones.add(botonPagina("<", paginaActual - 1, paginaActual > 0, false));
            int inicio = Math.max(0, Math.min(paginaActual - PAGINAS_VISIBLES / 2, totalPaginas - PAGINAS_VISIBLES));
            int fin = Math.min(totalPaginas, inicio + PAGINAS_VISIBLES);
            for (int i = inicio; i < fin; i++) {
                if (i > inicio) {
                    JLabel guion = new JLabel("-");
                    guion.setForeground(Tema.MUTED);
                    botones.add(guion);
                }
                botones.add(botonPagina(String.valueOf(i + 1), i, true, i == paginaActual));
            }
            botones.add(botonPagina(">", paginaActual + 1, paginaActual < totalPaginas - 1, false));
        }
        pnlPaginador.add(botones, BorderLayout.CENTER);
        pnlPaginador.revalidate();
        pnlPaginador.repaint();
    }

    /** Boton de una pagina; al pulsarlo busca esa pagina con los filtros actuales. */
    private JButton botonPagina(String texto, int destino, boolean activo, boolean actual) {
        JButton boton = new JButton(texto);
        Tema.enlace(boton, actual ? Tema.TEXTO : Tema.MUTED, actual);
        boton.setFont(Tema.fuente(actual ? Font.BOLD : Font.PLAIN, 16));
        boton.setEnabled(activo && paginadorActivo);
        boton.addActionListener(e -> {
            if (!actual) {
                paginaActual = destino;
                buscar();
            }
        });
        return boton;
    }

    /** Lee una fecha "dd/mm/aaaa". Vacio = null. Si no es valida lanza Exception con el mensaje. */
    private static LocalDate leerFecha(String texto, String campo) throws Exception {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(texto.strip(), FECHA_CAPTURA);
        } catch (DateTimeParseException e) {
            throw new Exception("La fecha \"" + campo + "\" debe tener el formato dd/mm/aaaa.");
        }
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
            desde = leerFecha(txtDesde.getText(), "Desde");
        } catch (Exception e) {
            Tema.marcarError(txtDesde, true);
            mostrarError(e.getMessage());
            return null;
        }
        try {
            hasta = leerFecha(txtHasta.getText(), "Hasta");
        } catch (Exception e) {
            Tema.marcarError(txtHasta, true);
            mostrarError(e.getMessage());
            return null;
        }
        String estado = cboEstado.getSelectedIndex() <= 0 ? null : (String) cboEstado.getSelectedItem();
        return new FiltroComprasDTO(null, desde, hasta, estado, paginaActual, FILAS_POR_PAGINA);
    }

    /** Deshabilita botones y muestra "Cargando…" mientras corre la tarea. */
    public void ocupado(boolean ocupado) {
        btnFiltrar.setEnabled(!ocupado);
        btnLimpiar.setEnabled(!ocupado);
        paginadorActivo = !ocupado;
        dibujarPaginador();
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
