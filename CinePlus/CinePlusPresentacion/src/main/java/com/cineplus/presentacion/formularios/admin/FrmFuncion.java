/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cineplus.presentacion.formularios.admin;

import com.cineplus.dto.CineDTO;
import com.cineplus.dto.FuncionDTO;
import com.cineplus.dto.GuardarFuncionDTO;
import com.cineplus.dto.PeliculaDTO;
import com.cineplus.dto.SalaDTO;
import com.cineplus.entidades.enums.FormatoFuncion;
import com.cineplus.entidades.enums.TipoFuncion;
import com.cineplus.presentacion.CoordinadorAplicacion;
import com.cineplus.presentacion.Tema;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 * Pantalla F20: programacion y gestion de funciones de una pelicula (sin campo de exclusividad).
 *
 * @author Alec Enrique Navarro Gonzalez
 */
public class FrmFuncion extends JFrame {

    private static final DateTimeFormatter FORMATO_TABLA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final CoordinadorAplicacion app;
    private final PeliculaDTO pelicula;

    private final JLabel lblModoFormulario = new JLabel("Programar Nueva Función");
    private final JComboBox<CineDTO> cmbCine = new JComboBox<>();
    private final JComboBox<SalaDTO> cmbSala = new JComboBox<>();
    private final JTextField txtFecha = new JTextField();
    private final JTextField txtHora = new JTextField();
    private final JComboBox<TipoFuncion> cmbTipo = new JComboBox<>(TipoFuncion.values());
    private final JComboBox<FormatoFuncion> cmbFormato = new JComboBox<>(FormatoFuncion.values());
    private final JLabel lblError = new JLabel(" ");

    private final JButton btnLimpiar = new JButton("Nueva / Limpiar");
    private final JButton btnGuardar = new JButton("GUARDAR FUNCIÓN");
    private final JButton btnEliminar = new JButton("Eliminar Seleccionada");

    private final DefaultTableModel modeloTabla;
    private final JTable tablaFunciones;

    private List<FuncionDTO> funcionesActuales = List.of();
    private Integer idFuncionEdicion;
    private Integer idSalaPendiente;
    private boolean ignorarCambioCine;

    public FrmFuncion(CoordinadorAplicacion app, PeliculaDTO pelicula, FuncionDTO funcionInicial) {
        this.app = app;
        this.pelicula = pelicula;

        setTitle("CinePlus · Funciones de " + (pelicula != null ? pelicula.titulo() : "Película"));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel raiz = new JPanel(new BorderLayout(0, 16));
        raiz.setBackground(Tema.FONDO);

        // Encabezado
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel lblTitulo = new JLabel("Funciones · " + (pelicula != null ? pelicula.titulo() : ""));
        Tema.titulo(lblTitulo);
        top.add(lblTitulo, BorderLayout.WEST);

        JButton btnVolver = new JButton("← Volver al Panel");
        Tema.botonSecundario(btnVolver);
        btnVolver.addActionListener(e -> app.admin().abrirPanel());
        top.add(btnVolver, BorderLayout.EAST);
        raiz.add(top, BorderLayout.NORTH);

        // Centro: formulario a la izquierda + tabla de funciones a la derecha
        JPanel centro = new JPanel(new BorderLayout(20, 0));
        centro.setOpaque(false);
        centro.add(crearPanelFormulario(), BorderLayout.WEST);
        centro.add(crearPanelTabla(), BorderLayout.CENTER);
        raiz.add(centro, BorderLayout.CENTER);

        // Tabla de funciones
        String[] columnas = {"ID", "Cine", "Sala", "Fecha y Hora", "Idioma", "Formato", "Aforo"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaFunciones = new JTable(modeloTabla);
        tablaFunciones.setRowHeight(30);
        tablaFunciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaFunciones.getTableHeader().setReorderingAllowed(false);
        tablaFunciones.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = tablaFunciones.getSelectedRow();
                if (fila >= 0 && fila < funcionesActuales.size()) {
                    cargarFuncionEnFormulario(funcionesActuales.get(fila));
                }
            }
        });

        // Reemplazar placeholder de scroll en panel derecho
        JPanel panelDerecho = (JPanel) centro.getComponent(1);
        panelDerecho.add(new JScrollPane(tablaFunciones), BorderLayout.CENTER);

        configurarRenderers();
        limpiarFormulario();

        if (funcionInicial != null) {
            cargarFuncionEnFormulario(funcionInicial);
        }

        setContentPane(raiz);
        Tema.preparar(this);

        SwingUtilities.invokeLater(() -> {
            if (pelicula != null) {
                app.admin().cargarDatosFuncion(this, pelicula.idPelicula());
            }
        });
    }

    private JPanel crearPanelFormulario() {
        JPanel card = new JPanel();
        Tema.tarjeta(card);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(330, 0));

        lblModoFormulario.setForeground(Tema.TEXTO);
        lblModoFormulario.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblModoFormulario.setAlignmentX(LEFT_ALIGNMENT);
        card.add(lblModoFormulario);
        card.add(Box.createVerticalStrut(14));

        Tema.campo(txtFecha, "YYYY-MM-DD");
        Tema.campo(txtHora, "HH:mm (ej. 18:30)");

        cmbCine.addActionListener(e -> {
            if (!ignorarCambioCine && cmbCine.getSelectedItem() instanceof CineDTO cine) {
                app.admin().cargarSalas(this, cine.idCine());
            }
        });

        agregarCampo(card, "Cine *", cmbCine);
        agregarCampo(card, "Sala *", cmbSala);
        agregarCampo(card, "Fecha (YYYY-MM-DD) *", txtFecha);
        agregarCampo(card, "Hora (HH:mm) *", txtHora);
        agregarCampo(card, "Idioma *", cmbTipo);
        agregarCampo(card, "Formato *", cmbFormato);

        lblError.setForeground(Tema.ERROR);
        lblError.setAlignmentX(LEFT_ALIGNMENT);
        card.add(lblError);
        card.add(Box.createVerticalStrut(12));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);
        botones.setAlignmentX(LEFT_ALIGNMENT);
        botones.setMaximumSize(new Dimension(310, 42));

        Tema.botonSecundario(btnLimpiar);
        Tema.botonPrimario(btnGuardar);
        btnLimpiar.addActionListener(e -> {
            tablaFunciones.clearSelection();
            limpiarFormulario();
        });
        btnGuardar.addActionListener(e -> guardar());

        botones.add(btnLimpiar);
        botones.add(btnGuardar);
        card.add(botones);

        return card;
    }

    private JPanel crearPanelTabla() {
        JPanel derecho = new JPanel(new BorderLayout(0, 10));
        derecho.setOpaque(false);

        JLabel lblSubtitulo = new JLabel("Funciones Programadas");
        lblSubtitulo.setForeground(Tema.TEXTO);
        lblSubtitulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        derecho.add(lblSubtitulo, BorderLayout.NORTH);

        JPanel pieTabla = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pieTabla.setOpaque(false);
        Tema.botonSecundario(btnEliminar);
        btnEliminar.addActionListener(e -> app.admin().eliminarFuncion(this, idFuncionEdicion));
        pieTabla.add(btnEliminar);
        derecho.add(pieTabla, BorderLayout.SOUTH);

        return derecho;
    }

    private void agregarCampo(JPanel contenedor, String etiqueta, Component campo) {
        JLabel lbl = new JLabel(etiqueta);
        lbl.setForeground(Tema.MUTED);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        contenedor.add(lbl);
        contenedor.add(Box.createVerticalStrut(4));

        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        if (campo instanceof javax.swing.JComponent jc) {
            jc.setAlignmentX(LEFT_ALIGNMENT);
        }
        contenedor.add(campo);
        contenedor.add(Box.createVerticalStrut(10));
    }

    private void configurarRenderers() {
        cmbCine.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof CineDTO c) {
                    setText(c.nombre() + " · " + c.ciudad());
                }
                return this;
            }
        });
        cmbSala.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof SalaDTO s) {
                    setText("Sala " + s.numeroSala() + " (" + s.tipoSala() + " · Aforo: " + s.capacidadTotal() + ")");
                }
                return this;
            }
        });
        cmbTipo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof TipoFuncion t) {
                    setText(t.getEtiqueta());
                }
                return this;
            }
        });
        cmbFormato.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof FormatoFuncion f) {
                    setText(f.getEtiqueta());
                }
                return this;
            }
        });
    }

    private void cargarFuncionEnFormulario(FuncionDTO f) {
        this.idFuncionEdicion = f.idFuncion();
        this.idSalaPendiente = f.idSala();
        lblModoFormulario.setText("Editando Función #" + f.idFuncion());

        if (f.fechaHora() != null) {
            txtFecha.setText(f.fechaHora().toLocalDate().toString());
            txtHora.setText(f.fechaHora().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        }
        for (TipoFuncion t : TipoFuncion.values()) {
            if (t.name().equalsIgnoreCase(f.tipo()) || t.getEtiqueta().equalsIgnoreCase(f.tipo())) {
                cmbTipo.setSelectedItem(t);
                break;
            }
        }
        for (FormatoFuncion fmt : FormatoFuncion.values()) {
            if (fmt.name().equalsIgnoreCase(f.formato()) || fmt.getEtiqueta().equalsIgnoreCase(f.formato())) {
                cmbFormato.setSelectedItem(fmt);
                break;
            }
        }
        for (int i = 0; i < cmbCine.getItemCount(); i++) {
            CineDTO c = cmbCine.getItemAt(i);
            if (c.nombre().equalsIgnoreCase(f.cine())) {
                cmbCine.setSelectedIndex(i);
                break;
            }
        }
        lblError.setText(" ");
    }

    private void guardar() {
        lblError.setText(" ");
        Tema.marcarError(txtFecha, false);
        Tema.marcarError(txtHora, false);

        if (pelicula == null || pelicula.idPelicula() == null) {
            mostrarError("Película inválida.");
            return;
        }
        if (!(cmbSala.getSelectedItem() instanceof SalaDTO sala)) {
            mostrarError("Selecciona un cine y una sala disponibles.");
            return;
        }

        LocalDate fecha;
        try {
            fecha = LocalDate.parse(txtFecha.getText().trim());
        } catch (DateTimeParseException e) {
            Tema.marcarError(txtFecha, true);
            mostrarError("Fecha inválida. Usa el formato YYYY-MM-DD.");
            return;
        }

        LocalTime hora;
        try {
            hora = LocalTime.parse(txtHora.getText().trim(), DateTimeFormatter.ofPattern("HH:mm"));
        } catch (DateTimeParseException e) {
            Tema.marcarError(txtHora, true);
            mostrarError("Hora inválida. Usa el formato HH:mm (ej. 18:30).");
            return;
        }

        TipoFuncion tipo = (TipoFuncion) cmbTipo.getSelectedItem();
        FormatoFuncion formato = (FormatoFuncion) cmbFormato.getSelectedItem();

        GuardarFuncionDTO dto = new GuardarFuncionDTO(
                idFuncionEdicion,
                pelicula.idPelicula(),
                sala.idSala(),
                LocalDateTime.of(fecha, hora),
                tipo != null ? tipo.name() : TipoFuncion.ESPANOL.name(),
                formato != null ? formato.name() : FormatoFuncion.DOS_D.name());

        app.admin().guardarFuncion(this, dto);
    }

    public Integer idPelicula() {
        return pelicula != null ? pelicula.idPelicula() : null;
    }

    public void ocupado(boolean cargando) {
        btnGuardar.setEnabled(!cargando);
        btnEliminar.setEnabled(!cargando);
        btnLimpiar.setEnabled(!cargando);
        setCursor(cargando ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
    }

    public void mostrarCines(List<CineDTO> cines) {
        ignorarCambioCine = true;
        try {
            cmbCine.removeAllItems();
            if (cines != null) {
                for (CineDTO c : cines) {
                    cmbCine.addItem(c);
                }
            }
        } finally {
            ignorarCambioCine = false;
        }
        if (cmbCine.getItemCount() > 0) {
            cmbCine.setSelectedIndex(0);
        }
    }

    public void mostrarSalas(List<SalaDTO> salas) {
        cmbSala.removeAllItems();
        if (salas != null) {
            for (SalaDTO s : salas) {
                cmbSala.addItem(s);
                if (idSalaPendiente != null && idSalaPendiente.equals(s.idSala())) {
                    cmbSala.setSelectedItem(s);
                }
            }
        }
        idSalaPendiente = null;
    }

    public void mostrarFunciones(List<FuncionDTO> funciones) {
        this.funcionesActuales = funciones != null ? funciones : List.of();
        modeloTabla.setRowCount(0);
        for (FuncionDTO f : funcionesActuales) {
            modeloTabla.addRow(new Object[]{
                f.idFuncion(),
                f.cine(),
                "Sala " + f.numeroSala(),
                f.fechaHora() != null ? f.fechaHora().format(FORMATO_TABLA) : "",
                f.tipo(),
                f.formato(),
                f.aforo()
            });
        }
    }

    public void limpiarFormulario() {
        this.idFuncionEdicion = null;
        this.idSalaPendiente = null;
        lblModoFormulario.setText("Programar Nueva Función");
        txtFecha.setText(LocalDate.now().plusDays(1).toString());
        txtHora.setText("18:30");
        if (cmbTipo.getItemCount() > 0) cmbTipo.setSelectedIndex(0);
        if (cmbFormato.getItemCount() > 0) cmbFormato.setSelectedIndex(0);
        lblError.setText(" ");
        Tema.marcarError(txtFecha, false);
        Tema.marcarError(txtHora, false);
    }

    public void mostrarError(String mensaje) {
        lblError.setText(mensaje);
    }
}
