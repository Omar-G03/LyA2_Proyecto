package arbolE;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.QuadCurve2D;
import java.util.IdentityHashMap;
import javax.swing.JPanel;

/**
 *
 * MÉTODO PARA DIBUJAR ÁRBOL GRÁFICO
 *
 * a. Solicitar el ancho y color de las líneas
 * b. Solicitar el ancho y color de los nodos
 * c. Decorarlo con contenido del nodo
 * d. La ventana debe abrirse con DISPOSE_ON_CLOSE
 *
 * NOMBRE: EDDIE DAVID ALTAMIRANO PLANTILLAS
 * FECHA: 7/8/2026
 *
 */
public class PanelGrafo extends JPanel {

    private final Nodo raiz;

    // Propiedades configurables
    private final int radioNodo;
    private final Color colorNodo;
    private final Color colorLinea;
    private final float grosorLinea;

    private final int ESPACIO_VERTICAL = 70;
    private final int MARGEN_SUPERIOR = 50;

    // Separación horizontal fija entre hojas consecutivas.
    // Al ser fija (no se divide entre 2 en cada nivel), las ramas
    // nunca se comprimen sin importar qué tan profundo sea el árbol.
    private final int ANCHO_SLOT;

    // Posición final calculada de cada nodo (clave por identidad,
    // así los nodos compartidos del GAD comparten una sola posición)
    private final IdentityHashMap<Nodo, Point> posiciones = new IdentityHashMap<>();

    // Contador de líneas entrantes por nodo, para desplazar en curva
    // las conexiones repetidas hacia un mismo nodo compartido
    private final IdentityHashMap<Nodo, Integer> contadorEntradas = new IdentityHashMap<>();

    // Contador de "slot" horizontal para ir asignando hojas de izquierda a derecha
    private int siguienteSlot;

    public PanelGrafo(Nodo raiz, Color colorNodo, int radioNodo, Color colorLinea, float grosorLinea) {

        this.raiz = raiz;
        this.colorNodo = colorNodo;
        this.radioNodo = radioNodo;
        this.colorLinea = colorLinea;
        this.grosorLinea = grosorLinea;
        this.ANCHO_SLOT = Math.max(100, radioNodo * 6); // Separación mínima entre hojas

        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (raiz == null) return;

        // Reiniciar estado de cada repintado
        posiciones.clear();
        contadorEntradas.clear();
        siguienteSlot = 0;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(grosorLinea));

        // PASO 1: Calcular la posición (x,y) final de cada nodo único
        calcularPosiciones(raiz, 0);
        centrarHorizontalmente();

        // PASO 2: Dibujar TODAS las líneas primero (para que queden al fondo)
        IdentityHashMap<Nodo, Boolean> lineasExpandidas = new IdentityHashMap<>();
        dibujarLineas(g2, raiz, lineasExpandidas);

        // PASO 3: Dibujar TODOS los nodos después (quedan al frente, tapando las líneas)
        IdentityHashMap<Nodo, Boolean> nodosDibujados = new IdentityHashMap<>();
        dibujarNodos(g2, raiz, nodosDibujados);
    }

    // ================== PASO 1: LAYOUT (calcular posiciones) ==================

    /*
     * Asigna (x,y) a cada nodo único del árbol/GAD.
      - Las hojas reciben carriles horizontales fijos y consecutivos (0,1,2,...).
      - Los nodos internos se ubican en el promedio de sus hijos.
      - "profundidad" define la altura (y), separada por ESPACIO_VERTICAL fijo.
      - Si el nodo ya tiene posición (nodo compartido del GAD), no se recalcula.
     */
    private void calcularPosiciones(Nodo nodo, int profundidad) {
        if (nodo == null) return;
        if (posiciones.containsKey(nodo)) return; // ya calculado antes (compartido)

        // Procesar hijos primero (post-orden) para poder promediar sus X
        calcularPosiciones(nodo.getIzquierdo(), profundidad + 1);
        calcularPosiciones(nodo.getDerecho(), profundidad + 1);

        int x;
        boolean tieneIzq = nodo.getIzquierdo() != null;
        boolean tieneDer = nodo.getDerecho() != null;

        if (!tieneIzq && !tieneDer) {
            // Hoja: siguiente carril horizontal disponible
            x = siguienteSlot * ANCHO_SLOT;
            siguienteSlot++;
        } else if (tieneIzq && tieneDer) {
            int xIzq = posiciones.get(nodo.getIzquierdo()).x;
            int xDer = posiciones.get(nodo.getDerecho()).x;
            x = (xIzq + xDer) / 2;
        } else if (tieneIzq) {
            x = posiciones.get(nodo.getIzquierdo()).x;
        } else {
            x = posiciones.get(nodo.getDerecho()).x;
        }

        int y = MARGEN_SUPERIOR + profundidad * ESPACIO_VERTICAL;
        posiciones.put(nodo, new Point(x, y));
    }

    /* Centra el árbol completo dentro del ancho del panel. */
    private void centrarHorizontalmente() {
        if (posiciones.isEmpty()) return;

        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
        for (Point p : posiciones.values()) {
            minX = Math.min(minX, p.x);
            maxX = Math.max(maxX, p.x);
        }
        int anchoArbol = maxX - minX;
        int offsetX = (getWidth() - anchoArbol) / 2 - minX;

        for (Point p : posiciones.values()) {
            p.x += offsetX;
        }
    }

    // ================== PASO 2: DIBUJAR LÍNEAS (fondo) ==================

    private void dibujarLineas(Graphics2D g, Nodo nodo, IdentityHashMap<Nodo, Boolean> expandido) {
        if (nodo == null) return;
        if (expandido.containsKey(nodo)) return; // sus líneas de salida ya se dibujaron
        expandido.put(nodo, true);

        Point p = posiciones.get(nodo);

        if (nodo.getDerecho() != null) {
            Point pDer = posiciones.get(nodo.getDerecho());
            dibujarConexion(g, p.x, p.y, pDer.x, pDer.y, nodo.getDerecho());
            dibujarLineas(g, nodo.getDerecho(), expandido);
        }

        if (nodo.getIzquierdo() != null) {
            Point pIzq = posiciones.get(nodo.getIzquierdo());
            dibujarConexion(g, p.x, p.y, pIzq.x, pIzq.y, nodo.getIzquierdo());
            dibujarLineas(g, nodo.getIzquierdo(), expandido);
        }
    }

    /*
      Dibuja la línea hacia "destino". Si es la primera conexión entrante
      a ese nodo, se traza recta. Si el nodo ya recibió una conexión antes
      (nodo compartido del GAD), se traza como curva desplazada para que
      ambas conexiones sean visibles por separado.
     */
    private void dibujarConexion(Graphics2D g, int x1, int y1, int x2, int y2, Nodo destino) {
        int entrada = contadorEntradas.getOrDefault(destino, 0);
        contadorEntradas.put(destino, entrada + 1);

        g.setColor(colorLinea);

        if (entrada == 0) {
            g.drawLine(x1, y1, x2, y2);
        } else {
            int desplazamiento = 20 * entrada;
            int signo = (entrada % 2 == 0) ? 1 : -1;
            int ctrlX = (x1 + x2) / 2 + (desplazamiento * signo);
            int ctrlY = (y1 + y2) / 2;
            QuadCurve2D curva = new QuadCurve2D.Float();
            curva.setCurve(x1, y1, ctrlX, ctrlY, x2, y2);
            g.draw(curva);
        }
    }

    // ================== PASO 3: DIBUJAR NODOS (frente) ==================

    private void dibujarNodos(Graphics2D g, Nodo nodo, IdentityHashMap<Nodo, Boolean> dibujados) {
        if (nodo == null) return;
        if (dibujados.containsKey(nodo)) return; // ya se dibujó (nodo compartido)
        dibujados.put(nodo, true);

        Point p = posiciones.get(nodo);
        int x = p.x;
        int y = p.y;

        // FORMATO DEL NODO
        g.setColor(colorNodo);
        g.fillRect(x - radioNodo, y - radioNodo, radioNodo * 2, radioNodo * 2);

        g.setColor(colorNodo);
        g.fillRect(x - radioNodo, y - radioNodo, radioNodo * 4, radioNodo * 2);
        g.setColor(Color.BLACK);
        g.drawRect(x - radioNodo, y - radioNodo, radioNodo * 2, radioNodo * 2);
        g.setColor(Color.BLACK);
        g.drawRect(x - radioNodo, y - radioNodo, radioNodo * 4, radioNodo * 2);
        
        
        //===========================
        // TEXTO
        //===========================

        g.setFont(new Font("Arial", Font.BOLD, (radioNodo - 2)));
        if ("+-*/=".contains(nodo.getDato())) {
            g.setColor(Color.RED);
        } else {
            g.setColor(Color.BLUE);
        }
        FontMetrics fm = g.getFontMetrics();
        int anchoTexto = fm.stringWidth(nodo.getDato());
        int altoTexto = fm.getAscent();
        g.drawString(nodo.getDato() + "    " + nodo.getValor(),
                x - anchoTexto / 2,
                y + altoTexto / 4);

        // Continuar dibujando el resto del árbol/GAD
        dibujarNodos(g, nodo.getDerecho(), dibujados);
        dibujarNodos(g, nodo.getIzquierdo(), dibujados);
    }
}