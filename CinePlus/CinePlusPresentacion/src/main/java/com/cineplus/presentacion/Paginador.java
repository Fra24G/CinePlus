package com.cineplus.presentacion;

import com.cineplus.dto.PaginaDTO;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.function.IntConsumer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Paginador del Figma: "&lt; 1 - 2 - 3 &gt;". Lo usan boletos, historial y transacciones.
 * Al hacer clic en una pagina llama a alCambiar con su numero (empieza en 0).
 *
 * @author Mario Perez
 */
public final class Paginador extends JPanel {

    /** Cuantos numeros de pagina se muestran como maximo. */
    private static final int MAXIMO_VISIBLES = 7;

    private final IntConsumer alCambiar;
    private int pagina;
    private int totalPaginas;
    private boolean habilitado = true;

    public Paginador(IntConsumer alCambiar) {
        super(new FlowLayout(FlowLayout.CENTER, 4, 0));
        this.alCambiar = alCambiar;
        setOpaque(false);
    }

    /** Pagina actual (empieza en 0). */
    public int pagina() {
        return pagina;
    }

    /** Redibuja los botones con los datos de la pagina recibida. */
    public void mostrar(PaginaDTO<?> datos) {
        pagina = datos.pagina();
        totalPaginas = datos.totalPaginas();
        redibujar();
    }

    /** Vuelve a la primera pagina sin avisar (por ejemplo al cambiar los filtros). */
    public void reiniciar() {
        pagina = 0;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        habilitado = enabled;
        redibujar();
    }

    private void redibujar() {
        removeAll();
        if (totalPaginas > 1) {
            add(boton("<", pagina - 1, pagina > 0, false));
            int inicio = Math.max(0, Math.min(pagina - MAXIMO_VISIBLES / 2, totalPaginas - MAXIMO_VISIBLES));
            int fin = Math.min(totalPaginas, inicio + MAXIMO_VISIBLES);
            for (int i = inicio; i < fin; i++) {
                if (i > inicio) {
                    JLabel guion = new JLabel("-");
                    guion.setForeground(Tema.MUTED);
                    add(guion);
                }
                add(boton(String.valueOf(i + 1), i, true, i == pagina));
            }
            add(boton(">", pagina + 1, pagina < totalPaginas - 1, false));
        }
        revalidate();
        repaint();
    }

    private JButton boton(String texto, int destino, boolean activo, boolean actual) {
        JButton boton = new JButton(texto);
        Tema.enlace(boton, actual ? Tema.TEXTO : Tema.MUTED, actual);
        boton.setFont(Tema.fuente(actual ? Font.BOLD : Font.PLAIN, 16));
        boton.setEnabled(activo && habilitado);
        boton.addActionListener(e -> {
            if (actual) {
                return;
            }
            pagina = destino;
            alCambiar.accept(destino);
        });
        return boton;
    }
}
