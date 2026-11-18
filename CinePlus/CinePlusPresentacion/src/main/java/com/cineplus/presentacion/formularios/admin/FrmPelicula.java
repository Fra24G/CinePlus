/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cineplus.presentacion.formularios.admin;

import com.cineplus.dto.PeliculaDTO;
import com.cineplus.entidades.enums.Clasificacion;
import com.cineplus.presentacion.CoordinadorAplicacion;
import com.cineplus.presentacion.TarjetaPelicula;
import com.cineplus.presentacion.Tema;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Component;

/**
 * Pantalla F19: formulario de creacion y edicion de peliculas del catalogo.
 *
 * @author Alec Enrique Navarro Gonzalez
 */
public class FrmPelicula extends JFrame {

    private final CoordinadorAplicacion app;
    private final PeliculaDTO original;

    private final JPanel pnlPreview = new JPanel(new BorderLayout());
    private final JButton btnSubirPoster = new JButton("Subir Póster...");

    private final JTextField txtTitulo = new JTextField();
    private final JTextField txtDirector = new JTextField();
    private final JTextField txtGenero = new JTextField();
    private final JTextField txtDuracion = new JTextField();
    private final JComboBox<Clasificacion> cmbClasificacion = new JComboBox<>(Clasificacion.values());
    private final JTextField txtFechaEstreno = new JTextField();
    private final JTextField txtInicioCartelera = new JTextField();
    private final JTextField txtFinCartelera = new JTextField();
    private final JTextField txtImagen = new JTextField();
    private final JCheckBox chkDisponible = new JCheckBox("Disponible en cartelera hoy", true);
    private final JTextArea txtSinopsis = new JTextArea(4, 30);

    private final JLabel lblError = new JLabel(" ");
    private final JButton btnCancelar = new JButton("CANCELAR");
    private final JButton btnGuardar = new JButton("GUARDAR PELÍCULA");

    public FrmPelicula(CoordinadorAplicacion app, PeliculaDTO pelicula) {
        this.app = app;
        this.original = pelicula;

        boolean esEdicion = pelicula != null && pelicula.idPelicula() != null;
        setTitle("CinePlus · " + (esEdicion ? "Editar Película" : "Nueva Película"));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel raiz = new JPanel(new BorderLayout(0, 16));
        raiz.setBackground(Tema.FONDO);

        // Encabezado
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel lblTitulo = new JLabel(esEdicion ? "Editar Película" : "Nueva Película");
        Tema.titulo(lblTitulo);
        top.add(lblTitulo, BorderLayout.WEST);

        JButton btnVolver = new JButton("← Volver al Panel");
        Tema.botonSecundario(btnVolver);
        btnVolver.addActionListener(e -> app.admin().abrirPanel());
        top.add(btnVolver, BorderLayout.EAST);
        raiz.add(top, BorderLayout.NORTH);

        // Centro: vista previa izquierda + formulario derecha
        JPanel centro = new JPanel(new BorderLayout(24, 0));
        centro.setOpaque(false);
        centro.add(crearPanelLateral(), BorderLayout.WEST);
        centro.add(crearPanelFormulario(), BorderLayout.CENTER);
        raiz.add(centro, BorderLayout.CENTER);

        // Pie de botones
        JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        pie.setOpaque(false);
        Tema.botonSecundario(btnCancelar);
        Tema.botonPrimario(btnGuardar);
        btnCancelar.addActionListener(e -> app.admin().abrirPanel());
        btnGuardar.addActionListener(e -> guardar());
        pie.add(btnCancelar);
        pie.add(btnGuardar);
        raiz.add(pie, BorderLayout.SOUTH);

        configurarComboClasificacion();
        if (pelicula != null) {
            cargarDatos(pelicula);
        } else {
            txtInicioCartelera.setText(LocalDate.now().toString());
            txtFinCartelera.setText(LocalDate.now().plusWeeks(4).toString());
        }
        actualizarVistaPrevia();

        setContentPane(raiz);
        Tema.preparar(this);
    }

    private JPanel crearPanelLateral() {
        JPanel lateral = new JPanel();
        lateral.setOpaque(false);
        lateral.setLayout(new BoxLayout(lateral, BoxLayout.Y_AXIS));
        lateral.setPreferredSize(new Dimension(210, 0));

        pnlPreview.setOpaque(false);
        pnlPreview.setMaximumSize(new Dimension(200, 315));
        pnlPreview.setAlignmentX(Component.CENTER_ALIGNMENT);
        lateral.add(pnlPreview);
        lateral.add(Box.createVerticalStrut(12));

        Tema.botonSecundario(btnSubirPoster);
        btnSubirPoster.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSubirPoster.addActionListener(e -> seleccionarArchivoPoster());
        lateral.add(btnSubirPoster);

        return lateral;
    }

    private JScrollPane crearPanelFormulario() {
        JPanel card = new JPanel(new GridBagLayout());
        Tema.tarjeta(card);

        Tema.campo(txtTitulo, "Ej. Interestelar");
        Tema.campo(txtDirector, "Ej. Christopher Nolan");
        Tema.campo(txtGenero, "Ej. Ciencia Ficción");
        Tema.campo(txtDuracion, "Ej. 169");
        Tema.campo(txtFechaEstreno, "YYYY-MM-DD");
        Tema.campo(txtInicioCartelera, "YYYY-MM-DD");
        Tema.campo(txtFinCartelera, "YYYY-MM-DD");
        Tema.campo(txtImagen, "URL o ruta archivo:...");

        chkDisponible.setOpaque(false);
        chkDisponible.setForeground(Tema.TEXTO);

        txtSinopsis.setLineWrap(true);
        txtSinopsis.setWrapStyleWord(true);
        txtSinopsis.setBackground(Tema.CAMPO);
        txtSinopsis.setForeground(Tema.TEXTO);
        txtSinopsis.setCaretColor(Tema.TEXTO);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        agregarFila(card, gbc, 0, "Título *", txtTitulo, "Director *", txtDirector);
        agregarFila(card, gbc, 1, "Género *", txtGenero, "Duración (min) *", txtDuracion);
        agregarFila(card, gbc, 2, "Clasificación *", cmbClasificacion, "Estreno (YYYY-MM-DD)", txtFechaEstreno);
        agregarFila(card, gbc, 3, "Inicio cartelera", txtInicioCartelera, "Fin cartelera", txtFinCartelera);

        // Ruta imagen + disponibilidad
        gbc.gridy = 8;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        card.add(etiqueta("Ruta / URL Póster"), gbc);
        gbc.gridx = 1;
        card.add(etiqueta("Estado"), gbc);

        gbc.gridy = 9;
        gbc.gridx = 0;
        card.add(txtImagen, gbc);
        gbc.gridx = 1;
        card.add(chkDisponible, gbc);

        // Sinopsis
        gbc.gridy = 10;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        card.add(etiqueta("Sinopsis"), gbc);

        gbc.gridy = 11;
        card.add(new JScrollPane(txtSinopsis), gbc);

        // Error
        gbc.gridy = 12;
        lblError.setForeground(Tema.ERROR);
        card.add(lblError, gbc);

        JScrollPane scroll = new JScrollPane(card);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        return scroll;
    }

    private void agregarFila(JPanel panel, GridBagConstraints gbc, int bloque,
            String lblIzq, Component compIzq, String lblDer, Component compDer) {
        int filaLabel = bloque * 2;
        int filaCampo = filaLabel + 1;

        gbc.gridwidth = 1;
        gbc.gridy = filaLabel;
        gbc.gridx = 0;
        panel.add(etiqueta(lblIzq), gbc);
        gbc.gridx = 1;
        panel.add(etiqueta(lblDer), gbc);

        gbc.gridy = filaCampo;
        gbc.gridx = 0;
        panel.add(compIzq, gbc);
        gbc.gridx = 1;
        panel.add(compDer, gbc);
    }

    private JLabel etiqueta(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setForeground(Tema.MUTED);
        return lbl;
    }

    private void configurarComboClasificacion() {
        cmbClasificacion.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Clasificacion c) {
                    setText(c.getEtiqueta());
                }
                return this;
            }
        });
    }

    private void cargarDatos(PeliculaDTO p) {
        txtTitulo.setText(p.titulo() != null ? p.titulo() : "");
        txtDirector.setText(p.director() != null ? p.director() : "");
        txtGenero.setText(p.genero() != null ? p.genero() : "");
        txtDuracion.setText(p.duracionMinutos() > 0 ? String.valueOf(p.duracionMinutos()) : "");
        Clasificacion clasif = Clasificacion.porEtiqueta(p.clasificacion());
        if (clasif != null) {
            cmbClasificacion.setSelectedItem(clasif);
        }
        txtFechaEstreno.setText(p.fechaEstreno() != null ? p.fechaEstreno().toString() : "");
        txtInicioCartelera.setText(p.inicioCartelera() != null ? p.inicioCartelera().toString() : "");
        txtFinCartelera.setText(p.finCartelera() != null ? p.finCartelera().toString() : "");
        txtImagen.setText(p.imagen() != null ? p.imagen() : "");
        chkDisponible.setSelected(p.disponible());
        txtSinopsis.setText(p.sinopsis() != null ? p.sinopsis() : "");
    }

    private void seleccionarArchivoPoster() {
        JFileChooser selector = new JFileChooser();
        selector.setFileFilter(new FileNameExtensionFilter("Imágenes (JPG, PNG, WEBP)", "jpg", "jpeg", "png", "webp"));
        if (selector.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File archivo = selector.getSelectedFile();
            app.admin().subirPoster(this, archivo);
        }
    }

    private void actualizarVistaPrevia() {
        PeliculaDTO muestra = new PeliculaDTO(
                original != null ? original.idPelicula() : null,
                original != null ? original.idExterno() : null,
                txtTitulo.getText().isBlank() ? "Vista previa" : txtTitulo.getText().trim(),
                txtDirector.getText().trim(),
                txtGenero.getText().trim(),
                120,
                parsearFechaOpcional(txtFechaEstreno.getText()),
                ((Clasificacion) cmbClasificacion.getSelectedItem()).getEtiqueta(),
                txtSinopsis.getText().trim(),
                txtImagen.getText().trim(),
                chkDisponible.isSelected(),
                null,
                null);
        pnlPreview.removeAll();
        pnlPreview.add(new TarjetaPelicula(muestra, app, null), BorderLayout.CENTER);
        pnlPreview.revalidate();
        pnlPreview.repaint();
    }

    private void guardar() {
        limpiarErrores();
        String titulo = txtTitulo.getText().trim();
        if (titulo.isEmpty()) {
            Tema.marcarError(txtTitulo, true);
            mostrarError("El título de la película es obligatorio.");
            return;
        }
        int duracion;
        try {
            duracion = Integer.parseInt(txtDuracion.getText().trim());
            if (duracion <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            Tema.marcarError(txtDuracion, true);
            mostrarError("Ingresa una duración válida en minutos (mayor a 0).");
            return;
        }

        LocalDate estreno;
        LocalDate inicio;
        LocalDate fin;
        try {
            estreno = parsearFecha(txtFechaEstreno, "fecha de estreno");
            inicio = parsearFecha(txtInicioCartelera, "inicio en cartelera");
            fin = parsearFecha(txtFinCartelera, "fin en cartelera");
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
            return;
        }

        Clasificacion clasif = (Clasificacion) cmbClasificacion.getSelectedItem();
        PeliculaDTO dto = new PeliculaDTO(
                original != null ? original.idPelicula() : null,
                original != null ? original.idExterno() : null,
                titulo,
                txtDirector.getText().trim(),
                txtGenero.getText().trim(),
                duracion,
                estreno,
                clasif != null ? clasif.getEtiqueta() : "B",
                txtSinopsis.getText().trim(),
                txtImagen.getText().trim(),
                chkDisponible.isSelected(),
                inicio,
                fin);

        app.admin().guardarPelicula(this, dto);
    }

    private LocalDate parsearFecha(JTextField campo, String nombreCampo) {
        String texto = campo.getText().trim();
        if (texto.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(texto);
        } catch (DateTimeParseException e) {
            Tema.marcarError(campo, true);
            throw new IllegalArgumentException("Formato inválido en " + nombreCampo + " (usa YYYY-MM-DD).");
        }
    }

    private static LocalDate parsearFechaOpcional(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(texto.trim());
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private void limpiarErrores() {
        lblError.setText(" ");
        Tema.marcarError(txtTitulo, false);
        Tema.marcarError(txtDuracion, false);
        Tema.marcarError(txtFechaEstreno, false);
        Tema.marcarError(txtInicioCartelera, false);
        Tema.marcarError(txtFinCartelera, false);
    }

    public void ponerImagen(String ruta) {
        txtImagen.setText(ruta != null ? ruta : "");
        actualizarVistaPrevia();
    }

    public void ocupado(boolean cargando) {
        btnGuardar.setEnabled(!cargando);
        btnCancelar.setEnabled(!cargando);
        btnSubirPoster.setEnabled(!cargando);
        setCursor(cargando ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
    }

    public void mostrarError(String mensaje) {
        lblError.setText(mensaje);
    }
}
