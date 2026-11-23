
package com.cineplus.presentacion.formularios;

import com.cineplus.dto.CineDTO;
import com.cineplus.dto.FuncionDTO;
import com.cineplus.dto.PeliculaDTO;
import com.cineplus.presentacion.CoordinadorAplicacion;
import com.cineplus.presentacion.PanelNavegacion;
import com.cineplus.presentacion.Seccion;
import com.cineplus.presentacion.TarjetaPelicula;
import com.cineplus.presentacion.Tema;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * Pantalla para seleccionar el cine y la funcion.
 *
 * @author Juan Ramon Acuña Gutierrez
 */
public class FrmSeleccionFuncion extends JFrame {

    private CoordinadorAplicacion coordinador;
    private PeliculaDTO pelicula;


    private JPanel pnlContenido;
    private JPanel pnlPelicula;
    private JPanel pnlSeleccion;
    private JPanel pnlFunciones;

    private JScrollPane scrFunciones;

    private JLabel lblEstado;
    private JLabel lblSinFunciones;

    private JComboBox<CineDTO> cboCines;
    private JButton btnVolver;

    private PanelNavegacion navegacion;

    private boolean actualizandoCines;

    private static final Locale LOCALE = Locale.forLanguageTag("es-MX");

    private static final DateTimeFormatter FORMATO_HORA =
            DateTimeFormatter.ofPattern("HH:mm");

    private static final DateTimeFormatter FORMATO_DIA =
            DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", LOCALE);

    public FrmSeleccionFuncion() {
        initComponents();
        configurarVista();
    }

    public FrmSeleccionFuncion(
            CoordinadorAplicacion coordinador,
            PeliculaDTO pelicula) {

        this.coordinador = coordinador;
        this.pelicula = pelicula;

        initComponents();
        configurarVista();
        mostrarPelicula();
    }

    /** Configura la estructura y el estilo de la pantalla. */
    private void configurarVista() {

        setTitle("CinePlus · Selección de función");

        Tema.preparar(this);

        pnlFondo.setBackground(Tema.FONDO);
        pnlFondo.setBorder(new EmptyBorder(24, 28, 20, 28));
        pnlFondo.setLayout(new BorderLayout(20, 20));

        // Encabezado con el nombre de la aplicacion y boton para regresar.
        JPanel pnlEncabezado = new JPanel(new BorderLayout(12, 8));
        pnlEncabezado.setOpaque(false);

        JPanel pnlTitulos = new JPanel(new GridLayout(2, 1, 0, 4));
        pnlTitulos.setOpaque(false);

        JLabel lblMarca = new JLabel("CinePLUS");
        lblMarca.setFont(
                new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 24));
        lblMarca.setForeground(Tema.TEXTO);

        JLabel lblTitulo = new JLabel("Selección de función");
        Tema.titulo(lblTitulo);

        pnlTitulos.add(lblMarca);
        pnlTitulos.add(lblTitulo);

        btnVolver = new JButton("← Volver a cartelera");
        Tema.botonSecundario(btnVolver);
        btnVolver.addActionListener(e -> volverCartelera());

        pnlEncabezado.add(pnlTitulos, BorderLayout.WEST);
        pnlEncabezado.add(btnVolver, BorderLayout.EAST);

        // Tarjeta donde se muestra la pelicula seleccionada.
        pnlPelicula = new JPanel(new BorderLayout());
        Tema.tarjeta(pnlPelicula);
        pnlPelicula.setPreferredSize(
                new java.awt.Dimension(205, 350));

        // Panel de seleccion de cine y horarios.
        pnlSeleccion = new JPanel(new BorderLayout(0, 14));
        Tema.tarjeta(pnlSeleccion);

        JPanel pnlSuperiorSeleccion = new JPanel(
                new BorderLayout(0, 8));
        pnlSuperiorSeleccion.setOpaque(false);

        JLabel lblEligeCine = new JLabel("Selecciona un cine");
        lblEligeCine.setFont(
                new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 17));
        lblEligeCine.setForeground(Tema.TEXTO);

        cboCines = new JComboBox<>();
        cboCines.setMaximumSize(
                new java.awt.Dimension(Integer.MAX_VALUE, 38));

        cboCines.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> lista,
                    Object valor,
                    int indice,
                    boolean seleccionado,
                    boolean enfocado) {

                super.getListCellRendererComponent(
                        lista, valor, indice, seleccionado, enfocado);

                if (valor instanceof CineDTO cine) {
                    setText(cine.nombre() + " · " + cine.ciudad());
                }

                return this;
            }
        });

        cboCines.addActionListener(e -> cargarFuncionesSeleccionadas());

        lblEstado = new JLabel(" ");
        lblEstado.setForeground(Tema.MUTED);
        lblEstado.setVisible(false);

        pnlSuperiorSeleccion.add(lblEligeCine, BorderLayout.NORTH);
        pnlSuperiorSeleccion.add(cboCines, BorderLayout.CENTER);
        pnlSuperiorSeleccion.add(lblEstado, BorderLayout.SOUTH);

        // Aqui se agregan los grupos de dias y horarios.
        pnlFunciones = new JPanel();
        pnlFunciones.setOpaque(false);
        pnlFunciones.setLayout(
                new javax.swing.BoxLayout(
                        pnlFunciones, javax.swing.BoxLayout.Y_AXIS));

        lblSinFunciones = new JLabel(
                "Selecciona un cine para consultar sus horarios.",
                SwingConstants.CENTER);
        lblSinFunciones.setForeground(Tema.MUTED);

        pnlFunciones.add(lblSinFunciones);

        scrFunciones = new JScrollPane(pnlFunciones);
        scrFunciones.setBorder(BorderFactory.createEmptyBorder());
        scrFunciones.setOpaque(false);
        scrFunciones.getViewport().setOpaque(false);
        scrFunciones.getVerticalScrollBar().setUnitIncrement(18);

        pnlSeleccion.add(pnlSuperiorSeleccion, BorderLayout.NORTH);
        pnlSeleccion.add(scrFunciones, BorderLayout.CENTER);

        // Distribucion central.
        pnlContenido = new JPanel(new BorderLayout(22, 0));
        pnlContenido.setOpaque(false);
        pnlContenido.add(pnlPelicula, BorderLayout.WEST);
        pnlContenido.add(pnlSeleccion, BorderLayout.CENTER);

        // Navegacion inferior compartida.
        if (coordinador != null) {
            navegacion = new PanelNavegacion(
                    coordinador, Seccion.CARTELERA);
        }

        pnlFondo.add(pnlEncabezado, BorderLayout.NORTH);
        pnlFondo.add(pnlContenido, BorderLayout.CENTER);

        if (navegacion != null) {
            pnlFondo.add(navegacion, BorderLayout.SOUTH);
        }

        pack();
        setLocationRelativeTo(null);
    }

    /** Muestra el poster y los datos de la pelicula elegida. */
    private void mostrarPelicula() {

        pnlPelicula.removeAll();

        if (pelicula == null) {
            mostrarError("No se recibió una película válida.");
            return;
        }

        TarjetaPelicula tarjeta = new TarjetaPelicula(
                pelicula,
                coordinador,
                null
        );

        pnlPelicula.add(tarjeta, BorderLayout.CENTER);

        pnlPelicula.revalidate();
        pnlPelicula.repaint();
    }

    
public void mostrarCines(List<CineDTO> cines) {

    actualizandoCines = true;

    cboCines.setModel(new DefaultComboBoxModel<>());

    if (cines != null) {
        for (CineDTO cine : cines) {
            cboCines.addItem(cine);
        }
    }

    boolean hayCines = cboCines.getItemCount() > 0;

    if (hayCines) {
        cboCines.setSelectedIndex(0);
    }

    actualizandoCines = false;

    if (!hayCines) {
        cboCines.setEnabled(false);
        mostrarMensajeFunciones("No hay cines disponibles.");
        return;
    }

    cboCines.setEnabled(true);
    cargarFuncionesSeleccionadas();
}


    /** Consulta las funciones cuando cambia el cine seleccionado. */
    private void cargarFuncionesSeleccionadas() {

        if (actualizandoCines
                || coordinador == null
                || pelicula == null
                || pelicula.idPelicula() == null) {
            return;
        }

        CineDTO cine = (CineDTO) cboCines.getSelectedItem();

        if (cine != null) {
            coordinador.cartelera().cargarFunciones(
                    this,
                    pelicula.idPelicula(),
                    cine.idCine()
            );
        }
    }

    /** Muestra las funciones agrupadas por dia y tipo. */
    public void mostrarFunciones(List<FuncionDTO> funciones) {

        pnlFunciones.removeAll();

        if (funciones == null || funciones.isEmpty()) {
            mostrarMensajeFunciones(
                    "No hay funciones disponibles para este cine.");
            return;
        }

        Map<LocalDate, Map<String, List<FuncionDTO>>> agrupadas =
                new TreeMap<>();

        List<FuncionDTO> ordenadas = funciones.stream()
                .filter(funcion -> funcion != null
                        && funcion.fechaHora() != null)
                .sorted(Comparator.comparing(FuncionDTO::fechaHora))
                .toList();

        for (FuncionDTO funcion : ordenadas) {

            LocalDate dia = funcion.fechaHora().toLocalDate();

            String tipo = funcion.tipo() == null
                    || funcion.tipo().isBlank()
                    ? "Otros"
                    : funcion.tipo();

            agrupadas
                    .computeIfAbsent(dia, clave -> new LinkedHashMap<>())
                    .computeIfAbsent(tipo, clave -> new ArrayList<>())
                    .add(funcion);
        }

        if (agrupadas.isEmpty()) {
            mostrarMensajeFunciones(
                    "No hay horarios disponibles para mostrar.");
            return;
        }

        for (Map.Entry<LocalDate, Map<String, List<FuncionDTO>>> dia
                : agrupadas.entrySet()) {

            JPanel pnlDia = new JPanel();
            pnlDia.setOpaque(false);
            pnlDia.setLayout(
                    new javax.swing.BoxLayout(
                            pnlDia, javax.swing.BoxLayout.Y_AXIS));

            Tema.tarjeta(pnlDia);

            JLabel lblDia = new JLabel(formatearDia(dia.getKey()));
            lblDia.setFont(
                    new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 17));
            lblDia.setForeground(Tema.TEXTO);

            lblDia.setAlignmentX(Component.LEFT_ALIGNMENT);
            pnlDia.add(lblDia);

            for (Map.Entry<String, List<FuncionDTO>> tipo
                    : dia.getValue().entrySet()) {

                JPanel pnlTipo = new JPanel(
                        new BorderLayout(0, 8));
                pnlTipo.setOpaque(false);
                pnlTipo.setBorder(
                        new EmptyBorder(12, 0, 2, 0));

                JLabel lblTipo = new JLabel(tipo.getKey());
                lblTipo.setForeground(Tema.MUTED);
                lblTipo.setFont(
                        new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));

                JPanel pnlHorarios = new JPanel(
                        new FlowLayout(FlowLayout.LEFT, 9, 4));
                pnlHorarios.setOpaque(false);

                for (FuncionDTO funcion : tipo.getValue()) {

                    String hora = funcion.fechaHora()
                            .format(FORMATO_HORA);

                    JButton btnHora = new JButton(hora);
                    btnHora.putClientProperty(
                            "JButton.buttonType", "roundRect");
                    Tema.botonSecundario(btnHora);

                    btnHora.setToolTipText(
                            "Sala " + funcion.numeroSala()
                            + " · " + funcion.formato());

                    btnHora.setCursor(Cursor.getPredefinedCursor(
                            Cursor.HAND_CURSOR));

                    btnHora.addActionListener(e
                            -> coordinador.cartelera()
                                    .abrirAsientos(funcion));

                    pnlHorarios.add(btnHora);
                }

                pnlTipo.add(lblTipo, BorderLayout.NORTH);
                pnlTipo.add(pnlHorarios, BorderLayout.CENTER);

                pnlDia.add(pnlTipo);
            }

            pnlDia.setAlignmentX(Component.LEFT_ALIGNMENT);
            pnlDia.setMaximumSize(
                    new java.awt.Dimension(Integer.MAX_VALUE,
                            pnlDia.getPreferredSize().height));

            pnlFunciones.add(pnlDia);
            pnlFunciones.add(javax.swing.Box.createVerticalStrut(12));
        }

        pnlFunciones.revalidate();
        pnlFunciones.repaint();
    }

    /** Convierte la fecha en un texto facil de leer. */
    private String formatearDia(LocalDate fecha) {

        String texto = fecha.format(FORMATO_DIA);

        return texto.substring(0, 1).toUpperCase(LOCALE)
                + texto.substring(1);
    }

    /** Muestra un mensaje dentro del espacio de horarios. */
    private void mostrarMensajeFunciones(String mensaje) {

        pnlFunciones.removeAll();

        JLabel etiqueta = new JLabel(mensaje, SwingConstants.CENTER);
        etiqueta.setForeground(Tema.MUTED);
        etiqueta.setAlignmentX(Component.CENTER_ALIGNMENT);

        pnlFunciones.add(javax.swing.Box.createVerticalStrut(24));
        pnlFunciones.add(etiqueta);

        pnlFunciones.revalidate();
        pnlFunciones.repaint();
    }

    /** Regresa a la pantalla de cartelera. */
    private void volverCartelera() {

        if (coordinador != null) {
            coordinador.cartelera().abrir();
        }
    }

    /** Muestra el estado de carga y deshabilita los controles. */
    public void ocupado(boolean ocupado) {

        cboCines.setEnabled(!ocupado);
        btnVolver.setEnabled(!ocupado);

        cambiarHabilitacion(pnlFunciones, !ocupado);

        if (navegacion != null) {
            cambiarHabilitacion(navegacion, !ocupado);

            if (!ocupado) {
                navegacion.actualizar();
            }
        }

        if (ocupado) {
            lblEstado.setText("Cargando...");
            lblEstado.setForeground(Tema.MUTED);
            lblEstado.setVisible(true);

            setCursor(Cursor.getPredefinedCursor(
                    Cursor.WAIT_CURSOR));

        } else {

            setCursor(Cursor.getDefaultCursor());

            if ("Cargando...".equals(lblEstado.getText())) {
                lblEstado.setText(" ");
                lblEstado.setVisible(false);
            }
        }
    }

    /** Muestra los errores recibidos desde el coordinador. */
    public void mostrarError(String mensaje) {

        lblEstado.setText(
                mensaje == null || mensaje.isBlank()
                ? "Ocurrió un error al consultar las funciones."
                : mensaje
        );

        lblEstado.setForeground(Tema.ERROR);
        lblEstado.setVisible(true);
    }

    /** Habilita o deshabilita los componentes de un contenedor. */
    private void cambiarHabilitacion(
            Component componente, boolean habilitado) {

        componente.setEnabled(habilitado);

        if (componente instanceof JPanel panel) {
            for (Component hijo : panel.getComponents()) {
                cambiarHabilitacion(hijo, habilitado);
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlFondo = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new java.awt.BorderLayout());

        pnlFondo.setBackground(Tema.FONDO);
        pnlFondo.setPreferredSize(new java.awt.Dimension(1110, 740));
        pnlFondo.setLayout(new java.awt.BorderLayout(16, 16));

        getContentPane().add(pnlFondo, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel pnlFondo;
    // End of variables declaration//GEN-END:variables
}
