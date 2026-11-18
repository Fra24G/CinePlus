/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cineplus.presentacion.formularios.admin;

import com.cineplus.dto.PeliculaAdminDTO;
import com.cineplus.dto.PeliculaDTO;
import com.cineplus.dto.ResumenAdminDTO;
import com.cineplus.presentacion.CoordinadorAplicacion;
import com.cineplus.presentacion.Tema;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 * Pantalla F18: panel de control del administrador (metricas semanales,
 * importacion desde TMDB y catalogo local de peliculas).
 *
 * @author Alec Enrique Navarro Gonzalez
 */
public class FrmPanelAdmin extends JFrame {

    private final CoordinadorAplicacion app;

    private final JLabel lblBoletosSemana = new JLabel("0");
    private final JLabel lblFuncionesSemana = new JLabel("0");

    private final JTextField txtBuscarTmdb = new JTextField();
    private final JButton btnBuscarTmdb = new JButton("Buscar en TMDB");
    private final JPanel pnlResultadosTmdb = new JPanel();

    private final DefaultTableModel modeloTabla;
    private final JTable tablaPeliculas;

    private final JButton btnNuevaPelicula = new JButton("+ Nueva Película");
    private final JButton btnEditar = new JButton("Editar Película");
    private final JButton btnFunciones = new JButton("Gestionar Funciones");
    private final JButton btnDisponibilidad = new JButton("Activar / Desactivar");
    private final JButton btnTransacciones = new JButton("Transacciones");
    private final JButton btnCerrarSesion = new JButton("Cerrar sesión");

    private List<PeliculaAdminDTO> peliculasCatalogo = List.of();

    public FrmPanelAdmin(CoordinadorAplicacion app) {
        this.app = app;

        setTitle("CinePlus · Panel de Administración");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel raiz = new JPanel(new BorderLayout(0, 16));
        raiz.setBackground(Tema.FONDO);

        // Encabezado superior
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel lblTitulo = new JLabel("Panel de Administración");
        Tema.titulo(lblTitulo);
        top.add(lblTitulo, BorderLayout.WEST);

        JPanel accionesTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        accionesTop.setOpaque(false);
        Tema.botonPrimario(btnNuevaPelicula);
        Tema.botonSecundario(btnTransacciones);
        Tema.botonSecundario(btnCerrarSesion);

        btnNuevaPelicula.addActionListener(e -> app.admin().abrirAgregarPelicula());
        btnTransacciones.addActionListener(e -> app.admin().abrirTransacciones());
        btnCerrarSesion.addActionListener(e -> app.cerrarSesion());

        accionesTop.add(btnNuevaPelicula);
        accionesTop.add(btnTransacciones);
        accionesTop.add(btnCerrarSesion);
        top.add(accionesTop, BorderLayout.EAST);

        raiz.add(top, BorderLayout.NORTH);

        // Centro: KPIs + Importar TMDB + Tabla catalogo
        JPanel centro = new JPanel(new BorderLayout(0, 14));
        centro.setOpaque(false);

        JPanel seccionSuperior = new JPanel();
        seccionSuperior.setOpaque(false);
        seccionSuperior.setLayout(new BoxLayout(seccionSuperior, BoxLayout.Y_AXIS));

        // Tarjetas de resumen semanal
        JPanel gridKpis = new JPanel(new GridLayout(1, 2, 16, 0));
        gridKpis.setOpaque(false);
        gridKpis.setMaximumSize(new Dimension(Integer.MAX_VALUE, 95));
        gridKpis.add(crearTarjetaKpi("Boletos vendidos (semana actual)", lblBoletosSemana));
        gridKpis.add(crearTarjetaKpi("Funciones publicadas (semana actual)", lblFuncionesSemana));
        seccionSuperior.add(gridKpis);
        seccionSuperior.add(Box.createVerticalStrut(12));

        // Barra de busqueda TMDB
        JPanel barraTmdb = new JPanel(new BorderLayout(10, 8));
        Tema.tarjeta(barraTmdb);
        barraTmdb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        JPanel filaInputTmdb = new JPanel(new BorderLayout(10, 0));
        filaInputTmdb.setOpaque(false);
        Tema.campo(txtBuscarTmdb, "Buscar película en TMDB para importar al catálogo...");
        Tema.botonSecundario(btnBuscarTmdb);
        btnBuscarTmdb.addActionListener(e -> app.admin().buscarEnTmdb(this, txtBuscarTmdb.getText()));
        txtBuscarTmdb.addActionListener(e -> app.admin().buscarEnTmdb(this, txtBuscarTmdb.getText()));

        filaInputTmdb.add(txtBuscarTmdb, BorderLayout.CENTER);
        filaInputTmdb.add(btnBuscarTmdb, BorderLayout.EAST);
        barraTmdb.add(filaInputTmdb, BorderLayout.NORTH);

        pnlResultadosTmdb.setOpaque(false);
        pnlResultadosTmdb.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 4));
        barraTmdb.add(pnlResultadosTmdb, BorderLayout.CENTER);

        seccionSuperior.add(barraTmdb);
        centro.add(seccionSuperior, BorderLayout.NORTH);

        // Tabla de catalogo local
        String[] columnas = {"ID", "Título", "Clasificación", "Duración", "Cartelera", "Funciones futuras", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaPeliculas = new JTable(modeloTabla);
        tablaPeliculas.setRowHeight(32);
        tablaPeliculas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaPeliculas.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollTabla = new JScrollPane(tablaPeliculas);
        centro.add(scrollTabla, BorderLayout.CENTER);

        raiz.add(centro, BorderLayout.CENTER);

        // Pie con acciones sobre la pelicula seleccionada
        JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        pie.setOpaque(false);
        Tema.botonSecundario(btnDisponibilidad);
        Tema.botonSecundario(btnEditar);
        Tema.botonPrimario(btnFunciones);

        btnEditar.addActionListener(e -> editarSeleccionada());
        btnFunciones.addActionListener(e -> gestionarFuncionesSeleccionada());
        btnDisponibilidad.addActionListener(e -> alternarDisponibilidadSeleccionada());

        pie.add(btnDisponibilidad);
        pie.add(btnEditar);
        pie.add(btnFunciones);
        raiz.add(pie, BorderLayout.SOUTH);

        setContentPane(raiz);
        Tema.preparar(this);

        SwingUtilities.invokeLater(() -> app.admin().cargarResumen(this));
    }

    private JPanel crearTarjetaKpi(String titulo, JLabel valor) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        Tema.tarjeta(card);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setForeground(Tema.MUTED);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        valor.setForeground(Tema.TEXTO);
        valor.setFont(new Font("Segoe UI", Font.BOLD, 28));

        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(valor, BorderLayout.CENTER);
        return card;
    }

    private PeliculaAdminDTO obtenerSeleccionada() {
        int fila = tablaPeliculas.getSelectedRow();
        if (fila < 0 || fila >= peliculasCatalogo.size()) {
            mostrarError("Selecciona una película de la tabla.");
            return null;
        }
        return peliculasCatalogo.get(fila);
    }

    private void editarSeleccionada() {
        PeliculaAdminDTO sel = obtenerSeleccionada();
        if (sel != null) {
            app.admin().abrirEditarPelicula(sel.pelicula());
        }
    }

    private void gestionarFuncionesSeleccionada() {
        PeliculaAdminDTO sel = obtenerSeleccionada();
        if (sel != null) {
            app.admin().abrirFunciones(sel.pelicula(), null);
        }
    }

    private void alternarDisponibilidadSeleccionada() {
        PeliculaAdminDTO sel = obtenerSeleccionada();
        if (sel != null) {
            PeliculaDTO p = sel.pelicula();
            app.admin().cambiarDisponibilidad(this, p.idPelicula(), !p.disponible());
        }
    }

    public void ocupado(boolean cargando) {
        btnBuscarTmdb.setEnabled(!cargando);
        btnNuevaPelicula.setEnabled(!cargando);
        btnEditar.setEnabled(!cargando);
        btnFunciones.setEnabled(!cargando);
        btnDisponibilidad.setEnabled(!cargando);
        setCursor(cargando ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
    }

    public void mostrarResumen(ResumenAdminDTO resumen) {
        lblBoletosSemana.setText(String.valueOf(resumen.boletosSemana()));
        lblFuncionesSemana.setText(String.valueOf(resumen.funcionesSemana()));
        this.peliculasCatalogo = resumen.peliculas();

        modeloTabla.setRowCount(0);
        for (PeliculaAdminDTO item : peliculasCatalogo) {
            PeliculaDTO p = item.pelicula();
            String periodo = (p.inicioCartelera() != null && p.finCartelera() != null)
                    ? p.inicioCartelera() + " al " + p.finCartelera()
                    : "Sin fechas";
            modeloTabla.addRow(new Object[]{
                p.idPelicula(),
                p.titulo(),
                p.clasificacion(),
                p.duracionMinutos() + " min",
                periodo,
                item.funcionesDisponibles(),
                p.disponible() ? "Activa" : "Inactiva"
            });
        }
    }

    public void mostrarResultadosTmdb(List<PeliculaDTO> resultados) {
        pnlResultadosTmdb.removeAll();
        if (resultados == null || resultados.isEmpty()) {
            JLabel sinResultados = new JLabel("No se encontraron coincidencias en TMDB.");
            sinResultados.setForeground(Tema.MUTED);
            pnlResultadosTmdb.add(sinResultados);
        } else {
            int limite = Math.min(resultados.size(), 5);
            for (int i = 0; i < limite; i++) {
                PeliculaDTO externa = resultados.get(i);
                String anio = externa.fechaEstreno() != null
                        ? " (" + externa.fechaEstreno().getYear() + ")"
                        : "";
                JButton btnImportar = new JButton("Importar: " + externa.titulo() + anio);
                Tema.botonSecundario(btnImportar);
                btnImportar.addActionListener(e -> app.admin().importar(this, externa));
                pnlResultadosTmdb.add(btnImportar);
            }
        }
        pnlResultadosTmdb.revalidate();
        pnlResultadosTmdb.repaint();
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "CinePlus · Administración", JOptionPane.ERROR_MESSAGE);
    }
}
