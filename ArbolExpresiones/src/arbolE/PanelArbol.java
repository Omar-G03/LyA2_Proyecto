/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package arbolE;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.HashMap;
import javax.swing.JColorChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author omar
 * =============================================
 * MÉTODO PARA DIBUJAR ÁRBOL GRÁFICO
 * INSTRUCCIONES:
 * a. Solicitar el ancho y color de las líneas
 * b. Solicitar el ancho y color de los nodos
 * c. Decorarlo con contenido del nodo
 * d. Agregar el método ON CLOSE con la opción de this.dispose() para EVITAR
 *    que cierre el proyecto.
 * 
 * NOMBRE:  OMAR GUADALUPE JIMENEZ PEDRAZA
 * FECHA:
 * ==============================================
 */
public class PanelArbol extends JPanel {
    private final Nodo raiz;
    private int espacioVertical; // se calcula según el tamaño de la celda

    // ===== a. Ancho y color de las LÍNEAS =====
    private int anchoLinea;
    private Color colorLinea;

    // ===== b. Color de los NODOS =====
    private Color colorNodo;

    // ===== d. FORMA del nodo: solo cuadrado o rectangular =====
    private enum FormaNodo { CUADRADO, RECTANGULAR }
    private FormaNodo forma;

    // Tamaño base de la celda (lo pide solicitarEntero) y dimensiones ya calculadas
    private int tamanoBase;
    private int anchoCelda; // ancho de CADA una de las dos celdas (identificador y valor)
    private int altoCelda;  // alto de las celdas

    // ===== d. Valores para mostrar "a = 5", "* = 20" dentro del nodo =====
    private final HashMap<String, String> tablaSimbolos;      // valores de los identificadores (a, b, c...)
    private final HashMap<Nodo, Double> valoresCalculados = new HashMap<>(); // resultado guardado por cada nodo

    public PanelArbol(Nodo raiz, HashMap<String, String> tablaSimbolos) {
        this.raiz = raiz;
        this.tablaSimbolos = (tablaSimbolos != null) ? tablaSimbolos : new HashMap<>();
        setBackground(Color.WHITE);
        solicitarConfiguracion();      // a, b y d: se piden los datos ANTES de dibujar
        calcularValores(raiz);         // d: se calcula el valor/resultado de cada nodo UNA sola vez
    }

    /**
     * a. Solicita el ancho y color de las líneas.
     * b. Solicita el color de los nodos.
     * d. Solicita el tamaño y la forma (cuadrado/rectangular) del nodo.
     * Si el usuario cancela algún cuadro de diálogo, se usa un valor por defecto.
     */
    private void solicitarConfiguracion() {
        // --- a. LÍNEAS ---
        anchoLinea = solicitarEntero("Ancho de las líneas (1 - 10 px):", 2, 1, 10);
        Color lineaElegida = JColorChooser.showDialog(this,
                "Seleccione el color de las líneas", Color.BLACK);
        colorLinea = (lineaElegida != null) ? lineaElegida : Color.BLACK;

        // --- b. NODOS (color) ---
        Color nodoElegido = JColorChooser.showDialog(this,
                "Seleccione el color de los nodos", new Color(173, 216, 230));
        colorNodo = (nodoElegido != null) ? nodoElegido : new Color(173, 216, 230);

        // --- d. TAMAÑO Y FORMA DEL NODO ---
        tamanoBase = solicitarEntero("Tamaño base de los nodos (20 - 60 px):", 30, 20, 60);
        solicitarForma();
        actualizarDimensionesCelda();
        espacioVertical = altoCelda + 50;
    }


    private void solicitarForma() {
        String[] opciones = {"Cuadrado", "Rectangular"};
        String seleccion = (String) JOptionPane.showInputDialog(this,
                "Seleccione la forma de los nodos:", "Forma del nodo",
                JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);

        if (seleccion == null || seleccion.equals("Cuadrado")) {
            forma = FormaNodo.CUADRADO;
        } else {
            forma = FormaNodo.RECTANGULAR;
        }
    }


    private void actualizarDimensionesCelda() {
        if (forma == FormaNodo.CUADRADO) {
            anchoCelda = tamanoBase;
            altoCelda = tamanoBase;
        } else { // RECTANGULAR
            altoCelda = tamanoBase;
            anchoCelda = (int) (tamanoBase * 1.6);
        }
    }


    private int solicitarEntero(String mensaje, int valorPorDefecto, int min, int max) {
        while (true) {
            String entrada = JOptionPane.showInputDialog(this, mensaje,
                    String.valueOf(valorPorDefecto));
            if (entrada == null) {
                return valorPorDefecto; // el usuario canceló
            }
            try {
                int valor = Integer.parseInt(entrada.trim());
                if (valor >= min && valor <= max) {
                    return valor;
                }
                JOptionPane.showMessageDialog(this,
                        "Ingrese un número entre " + min + " y " + max + ".",
                        "Valor fuera de rango", JOptionPane.WARNING_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Ingrese un número entero válido.",
                        "Dato inválido", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    /**
     */
    private double calcularValores(Nodo nodo) {
        if (nodo == null) return 0;

        boolean esHoja = (nodo.getIzquierdo() == null && nodo.getDerecho() == null);
        double valor;

        if (esHoja) {
            String valorTexto = tablaSimbolos.get(nodo.getDato());
            valor = (valorTexto != null) ? Double.parseDouble(valorTexto) : 0;
        } else {
            // Es un operador: primero se calculan los hijos, luego se opera
            double izq = calcularValores(nodo.getIzquierdo());
            double der = calcularValores(nodo.getDerecho());
            valor = operar(nodo.getDato(), izq, der);
        }

        valoresCalculados.put(nodo, valor);
        return valor;
    }//calcularValores

    /**
     * Aplica el operador aritmético indicado sobre dos valores.
     */
    private double operar(String operador, double izquierdo, double derecho) {
        switch (operador) {
            case "+": return izquierdo + derecho;
            case "-": return izquierdo - derecho;
            case "*": return izquierdo * derecho;
            case "/": return (derecho != 0) ? izquierdo / derecho : 0;
            case "^": return Math.pow(izquierdo, derecho);
            default:  return 0; // operador desconocido
        }
    }//operar


    private String formatearValor(double valor) {
        if (valor == Math.floor(valor) && !Double.isInfinite(valor)) {
            return String.valueOf((long) valor);
        }
        return String.valueOf(valor);
    }//formatearValor

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (raiz != null) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                    RenderingHints.VALUE_ANTIALIAS_ON);
            // a. Aplicar el ancho de línea solicitado al usuario
            g2.setStroke(new BasicStroke(anchoLinea));
            

            dibujarNodo(g2, raiz, getWidth() / 2 - anchoCelda / 2, 40, getWidth() / 4);
        }
    }

    private void dibujarNodo(Graphics2D g, Nodo nodo, int x, int y, int espacioHorizontal) {
        if (nodo == null) return;


        g.setColor(colorLinea); // a. Color de línea solicitado al usuario
        if (nodo.getIzquierdo() != null) {
            g.drawLine(x, y, x - espacioHorizontal, y + espacioVertical);
            dibujarNodo(g, nodo.getIzquierdo(), x - espacioHorizontal,
                    y + espacioVertical, espacioHorizontal / 2);
        }
        if (nodo.getDerecho() != null) {
            g.setColor(colorLinea); 
                                     // ya modificó el color del pincel (nodo, texto, etc.)
            g.drawLine(x, y, x + espacioHorizontal, y + espacioVertical);
            dibujarNodo(g, nodo.getDerecho(), x 
                    + espacioHorizontal, y + espacioVertical, espacioHorizontal / 2);
        }

        // d. DIBUJAR EL NODO COMO DOS CELDAS PEGADAS: [ identificador/operador | valor ]
        dibujarNodoDoble(g, nodo, x, y);
    }//dibujarNodo

    /**
  
     */
    private void dibujarNodoDoble(Graphics2D g, Nodo nodo, int x, int y) {
        // La celda del identificador/operador queda CENTRADA en (x, y),
        // que es exactamente el punto de donde salen las líneas.
        int top = y - altoCelda / 2;
        int xCeldaIzq = x - anchoCelda / 2;
        int xCeldaDer = xCeldaIzq + anchoCelda; // celda de valor, pegada a la derecha

        // ---- Celda izquierda: identificador u operador ----
        g.setColor(colorNodo);
        g.fillRect(xCeldaIzq, top, anchoCelda, altoCelda);
        g.setColor(colorNodo.darker());
        g.drawRect(xCeldaIzq, top, anchoCelda, altoCelda);

        // ---- Celda derecha: valor / resultado ----
        Double valor = valoresCalculados.get(nodo);
        String textoValor = (valor != null) ? formatearValor(valor) : "?";

        g.setColor(colorNodo.brighter()); // un tono distinto para diferenciarla
        g.fillRect(xCeldaDer, top, anchoCelda, altoCelda);
        g.setColor(colorNodo.darker());
        g.drawRect(xCeldaDer, top, anchoCelda, altoCelda);

        // ---- Texto centrado dentro de cada celda ----
        g.setColor(Color.BLACK);
        FontMetrics fm = g.getFontMetrics();

        // Texto de la celda izquierda (dato: variable u operador)
        int anchoTextoDato = fm.stringWidth(nodo.getDato());
        int altoTexto = fm.getAscent();
        g.drawString(nodo.getDato(),
                xCeldaIzq + (anchoCelda - anchoTextoDato) / 2,
                y + (altoTexto / 4));

        // Texto de la celda derecha (valor/resultado)
        int anchoTextoValor = fm.stringWidth(textoValor);
        g.drawString(textoValor,
                xCeldaDer + (anchoCelda - anchoTextoValor) / 2,
                y + (altoTexto / 4));
    }//dibujarNodoDoble
    
}//FIN CLASE