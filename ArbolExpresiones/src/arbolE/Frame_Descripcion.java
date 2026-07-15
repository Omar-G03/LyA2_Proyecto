package arbolE;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;



/**
 *
 * @author oma98
 */
/**
 * Ventana de ejemplos didácticos: muestra la conversión de código de alto
 * nivel a código de tres direcciones. Navegable con Anterior / Siguiente.
 *
 * @author omar
 */
public class Frame_Descripcion extends JDialog {

    private final CardLayout cardLayout;
    private final JPanel panelTarjetas;
    private final JLabel lblPagina;
    private final String[] nombresEjemplos = {
        "Ejemplo 1: Ciclo WHILE con condición relacional",
        "Ejemplo 2: Condicional IF / ELSE con asignación"
    };
    private int paginaActual = 0;

    public Frame_Descripcion(JFrame padre) {
        super(padre, "Ejemplos - Código de Tres Direcciones", true);

        cardLayout = new CardLayout();
        panelTarjetas = new JPanel(cardLayout);

        panelTarjetas.add(crearTarjetaEjemplo1(), "pagina1");
        panelTarjetas.add(crearTarjetaEjemplo2(), "pagina2");

        // ===== Encabezado con el indicador de página =====
        lblPagina = new JLabel("", SwingConstants.CENTER);
        lblPagina.setFont(new Font("Dialog", Font.BOLD, 16));
        actualizarEtiquetaPagina();

        // ===== Barra de navegación inferior =====
        JButton btnAnterior = new JButton("◀ Anterior");
        JButton btnSiguiente = new JButton("Siguiente ▶");

        btnAnterior.addActionListener(e -> {
            paginaActual = (paginaActual == 0) ? nombresEjemplos.length - 1 : paginaActual - 1;
            mostrarPagina();
        });

        btnSiguiente.addActionListener(e -> {
            paginaActual = (paginaActual + 1) % nombresEjemplos.length;
            mostrarPagina();
        });

        JPanel panelNavegacion = new JPanel();
        panelNavegacion.add(btnAnterior);
        panelNavegacion.add(btnSiguiente);

        // ===== Ensamblar ventana =====
        setLayout(new BorderLayout(10, 10));
        add(lblPagina, BorderLayout.NORTH);
        add(panelTarjetas, BorderLayout.CENTER);
        add(panelNavegacion, BorderLayout.SOUTH);

        setSize(750, 500);
        setLocationRelativeTo(padre);
    }

    private void mostrarPagina() {
        cardLayout.show(panelTarjetas, "pagina" + (paginaActual + 1));
        actualizarEtiquetaPagina();
    }

    private void actualizarEtiquetaPagina() {
        lblPagina.setText(nombresEjemplos[paginaActual]
                + "   (" + (paginaActual + 1) + " de " + nombresEjemplos.length + ")");
    }

    // ================= EJEMPLO 1 =================
    private JPanel crearTarjetaEjemplo1() {
        String alto =
                "while (a < b) {\n" +
                "    a = a + c;\n" +
                "}";

        String intermedio =
                "L1: ifFalse a < b goto L2\n" +
                "    t1 = a + c\n" +
                "    a  = t1\n" +
                "    goto L1\n" +
                "L2: ...";

        String explicacion =
                "1. Se evalúa la condición relacional (a < b).\n" +
                "2. Si es falsa, salta directo a L2 (sale del ciclo).\n" +
                "3. Si es verdadera, ejecuta el cuerpo: crea un temporal t1\n" +
                "   para 'a + c', lo asigna a 'a' y regresa a L1.";

        return construirTarjeta(alto, intermedio, explicacion);
    }

    // ================= EJEMPLO 2 =================
    private JPanel crearTarjetaEjemplo2() {
        String alto =
                "if (a < b)\n" +
                "    x = a + b;\n" +
                "else\n" +
                "    x = a - b;";

        String intermedio =
                "    ifFalse a < b goto L1\n" +
                "    t1 = a + b\n" +
                "    x  = t1\n" +
                "    goto L2\n" +
                "L1: t2 = a - b\n" +
                "    x  = t2\n" +
                "L2: ...";

        String explicacion =
                "1. Se evalúa la condición (a < b).\n" +
                "2. Si es falsa, salta a L1 (rama del else).\n" +
                "3. Si es verdadera, ejecuta la rama del if (t1 = a+b, x = t1)\n" +
                "   y salta a L2 para no ejecutar el else.";

        return construirTarjeta(alto, intermedio, explicacion);
    }

    // ================= PLANTILLA VISUAL DE CADA TARJETA =================
    private JPanel construirTarjeta(String codigoAlto, String codigoIntermedio, String explicacion) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JPanel panelCodigos = new JPanel(new GridLayout(1, 2, 15, 0));
        panelCodigos.add(crearBloqueCodigo("Código de alto nivel", codigoAlto, new Color(230, 240, 255)));
        panelCodigos.add(crearBloqueCodigo("Código de tres direcciones", codigoIntermedio, new Color(230, 255, 230)));

        JTextArea txtExplicacion = new JTextArea(explicacion);
        txtExplicacion.setEditable(false);
        txtExplicacion.setLineWrap(true);
        txtExplicacion.setWrapStyleWord(true);
        txtExplicacion.setFont(new Font("Dialog", Font.PLAIN, 13));
        txtExplicacion.setBorder(BorderFactory.createTitledBorder("¿Qué está pasando?"));
        txtExplicacion.setBackground(new Color(255, 250, 230));

        panel.add(panelCodigos, BorderLayout.CENTER);
        panel.add(txtExplicacion, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearBloqueCodigo(String titulo, String codigo, Color fondo) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(titulo));

        JTextArea txt = new JTextArea(codigo);
        txt.setEditable(false);
        txt.setFont(new Font("Monospaced", Font.PLAIN, 14));
        txt.setBackground(fondo);
        txt.setMargin(new java.awt.Insets(8, 8, 8, 8));

        JScrollPane scroll = new JScrollPane(txt);
        scroll.setPreferredSize(new Dimension(320, 180));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }
}
