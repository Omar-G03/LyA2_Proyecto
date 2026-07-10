/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
//
package arbolE;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JColorChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author marie
 * =============================================
 * MÉTODO PARA DIBUJAR ÁRBOL GRÁFICO
 * INSTRUCCIONES:
 * a. Solicitar el ancho y color de las líneas
 * b. Solicitar el ancho y color de los nodos
 * c. Decorarlo con contenido del nodo
 * d. Agregar el método ON CLOSE con la opción de this.dispose() para EVITAR
 *    que cierre el proyecto.
 * 
 * NOMBRE:  
 * FECHA:
 * ==============================================
 */
public class PanelArbol_Respaldo extends JPanel {
    private final Nodo raiz;
    private final int ESPACIO_VERTICAL = 60;

    // ===== a. Ancho y color de las LÍNEAS =====
    private int anchoLinea;
    private Color colorLinea;

    // ===== b. Ancho (radio) y color de los NODOS =====
    private int radioNodo;
    private Color colorNodo;

    public PanelArbol_Respaldo(Nodo raiz) {
        this.raiz = raiz;
        setBackground(Color.WHITE);
        solicitarConfiguracion(); // a y b: se piden los datos ANTES de dibujar
    }

    /**
     * a. Solicita el ancho y color de las líneas.
     * b. Solicita el ancho y color de los nodos.
     * Si el usuario cancela algún cuadro de diálogo, se usa un valor por defecto.
     */
    private void solicitarConfiguracion() {
        // --- a. LÍNEAS ---
        anchoLinea = solicitarEntero("Ancho de las líneas (1 - 10 px):", 2, 1, 10);
        Color lineaElegida = JColorChooser.showDialog(this,
                "Seleccione el color de las líneas", Color.BLACK);
        colorLinea = (lineaElegida != null) ? lineaElegida : Color.BLACK;

        // --- b. NODOS ---
        radioNodo = solicitarEntero("Radio (ancho) de los nodos (10 - 50 px):", 20, 10, 50);
        Color nodoElegido = JColorChooser.showDialog(this,
                "Seleccione el color de los nodos", new Color(173, 216, 230));
        colorNodo = (nodoElegido != null) ? nodoElegido : new Color(173, 216, 230);
    }

    /**
     * Pide un número entero al usuario mediante JOptionPane, validando que
     * esté dentro del rango [min, max]. Repite la petición si el valor no es
     * válido y usa valorPorDefecto si el usuario cancela el cuadro.
     */
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (raiz != null) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                    RenderingHints.VALUE_ANTIALIAS_ON);
            // a. Aplicar el ancho de línea solicitado al usuario
            g2.setStroke(new BasicStroke(anchoLinea));
            
            // INICIA DESDE EL CENTRO SUPERIOR
            dibujarNodo(g2, raiz, getWidth() / 2, 40, getWidth() / 4);
        }
    }

    private void dibujarNodo(Graphics2D g, Nodo nodo, int x, int y, int espacioHorizontal) {
        if (nodo == null) return;

        // Dibujar NODOS IZQUIERDO Y DERECHO 
        g.setColor(colorLinea); // a. Color de línea solicitado al usuario
        if (nodo.getIzquierdo() != null) {
            g.drawLine(x, y, x - espacioHorizontal, y + ESPACIO_VERTICAL);
            dibujarNodo(g, nodo.getIzquierdo(), x - espacioHorizontal,
                    y + ESPACIO_VERTICAL, espacioHorizontal / 2);
        }
        if (nodo.getDerecho() != null) {
            g.drawLine(x, y, x + espacioHorizontal, y + ESPACIO_VERTICAL);
            dibujarNodo(g, nodo.getDerecho(), x 
                    + espacioHorizontal, y + ESPACIO_VERTICAL, espacioHorizontal / 2);
        }

        // FORMATO DEL NODO
        g.setColor(colorNodo); // b. Color de nodo solicitado al usuario
        g.fillOval(x - radioNodo, y - radioNodo, 2 * radioNodo, 2 * radioNodo);
        g.setColor(colorNodo.darker());
        g.drawOval(x - radioNodo, y - radioNodo, 2 * radioNodo, 2 * radioNodo);

        //TEXTO CENTRADO DEL NODO
        g.setColor(Color.BLACK);
        FontMetrics fm = g.getFontMetrics();
        int anchoTexto = fm.stringWidth(nodo.getDato());
        int altoTexto = fm.getAscent();
        g.drawString(nodo.getDato(), x - (anchoTexto / 2), y + (altoTexto / 4));
    }//dibujarNodo
    
}//FIN CLASE