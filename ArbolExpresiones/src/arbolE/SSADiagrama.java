 package arbolE;   // <-- descomenta y ajusta al paquete de tu proyecto

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;


public class SSADiagrama extends JFrame {

    // -------- Paleta de colores --------
    static final Color LINE        = new Color(0x5F5E5A);
    static final Color TITLE_COLOR = new Color(0x2C2C2A);

    static final Color GRAY_FILL   = new Color(0xF1EFE8), GRAY_STROKE   = new Color(0x888780), GRAY_TEXT   = new Color(0x444441);
    static final Color BLUE_FILL   = new Color(0xE6F1FB), BLUE_STROKE   = new Color(0x185FA5), BLUE_TEXT   = new Color(0x0C447C);
    static final Color AMBER_FILL  = new Color(0xFAEEDA), AMBER_STROKE  = new Color(0x854F0B), AMBER_TEXT  = new Color(0x633806);
    static final Color PURPLE_FILL = new Color(0xEEEDFE), PURPLE_STROKE = new Color(0x534AB7), PURPLE_TEXT = new Color(0x3C3489);
    static final Color TEAL_FILL   = new Color(0xE1F5EE), TEAL_STROKE   = new Color(0x0F6E56), TEAL_TEXT   = new Color(0x085041);

    // -------- Fuentes --------
    static final Font TITLE = new Font("SansSerif", Font.BOLD, 20);
    static final Font BODY  = new Font("SansSerif", Font.PLAIN, 18);
    static final Font SMALL = new Font("SansSerif", Font.PLAIN, 14);

    public SSADiagrama() {
        setTitle("6.2.4  Asignacion individual estatica (SSA)");
        // DISPOSE_ON_CLOSE: al cerrar esta ventana NO se cierra tu aplicacion,
        // solo se libera este frame.
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(780, 620);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Funcion \u03C6 (flujo de control)", new PhiPanel());
        tabs.addTab("Versiones de una variable", new VersionesPanel());
        add(tabs);
    }

    // =====================================================================
    //  Utilidades de dibujo
    // =====================================================================

    /** Un segmento de texto: normal o subindice. */
    static class Seg {
        final String t; final boolean sub;
        Seg(String t, boolean sub) { this.t = t; this.sub = sub; }
    }
    static Seg n(String t) { return new Seg(t, false); } // texto normal
    static Seg s(String t) { return new Seg(t, true);  } // subindice

    /** Dibuja texto con subindices, centrado en (cx, cy). */
    static void richCentered(Graphics2D g2, double cx, double cy, Color color, Font base, Seg... segs) {
        Font small = base.deriveFont(base.getSize2D() * 0.72f);
        FontMetrics fmB = g2.getFontMetrics(base);
        FontMetrics fmS = g2.getFontMetrics(small);
        int total = 0;
        for (Seg sg : segs) total += (sg.sub ? fmS : fmB).stringWidth(sg.t);
        float x = (float) (cx - total / 2.0);
        float yBase = (float) (cy + fmB.getAscent() / 2.0 - 3);
        g2.setColor(color);
        for (Seg sg : segs) {
            if (sg.sub) {
                g2.setFont(small);
                g2.drawString(sg.t, x, yBase + 4);
                x += fmS.stringWidth(sg.t);
            } else {
                g2.setFont(base);
                g2.drawString(sg.t, x, yBase);
                x += fmB.stringWidth(sg.t);
            }
        }
    }

    /** Caja redondeada con relleno, borde y texto enriquecido centrado. */
    static void box(Graphics2D g2, double x, double y, double w, double h,
                    Color fill, Color stroke, Color text, Font f, float strokeW, Seg... segs) {
        RoundRectangle2D r = new RoundRectangle2D.Double(x, y, w, h, 16, 16);
        g2.setColor(fill); g2.fill(r);
        g2.setStroke(new BasicStroke(strokeW));
        g2.setColor(stroke); g2.draw(r);
        richCentered(g2, x + w / 2, y + h / 2, text, f, segs);
    }

    /** Rombo (decision) con texto centrado. */
    static void diamond(Graphics2D g2, double cx, double cy, double halfW, double halfH,
                        Color fill, Color stroke, Color text, Font f, String label) {
        Path2D p = new Path2D.Double();
        p.moveTo(cx, cy - halfH);
        p.lineTo(cx + halfW, cy);
        p.lineTo(cx, cy + halfH);
        p.lineTo(cx - halfW, cy);
        p.closePath();
        g2.setColor(fill); g2.fill(p);
        g2.setStroke(new BasicStroke(1.5f));
        g2.setColor(stroke); g2.draw(p);
        g2.setFont(f);
        FontMetrics fm = g2.getFontMetrics(f);
        g2.setColor(text);
        g2.drawString(label, (float) (cx - fm.stringWidth(label) / 2.0),
                             (float) (cy + fm.getAscent() / 2.0 - 3));
    }

    /** Flecha de (x1,y1) a (x2,y2) con punta. */
    static void arrow(Graphics2D g2, double x1, double y1, double x2, double y2) {
        g2.setStroke(new BasicStroke(1.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(LINE);
        g2.draw(new Line2D.Double(x1, y1, x2, y2));
        double ang = Math.atan2(y2 - y1, x2 - x1);
        double len = 11, spread = Math.toRadians(22);
        double xa = x2 - len * Math.cos(ang - spread), ya = y2 - len * Math.sin(ang - spread);
        double xb = x2 - len * Math.cos(ang + spread), yb = y2 - len * Math.sin(ang + spread);
        Path2D head = new Path2D.Double();
        head.moveTo(xa, ya); head.lineTo(x2, y2); head.lineTo(xb, yb);
        g2.draw(head);
    }

    static void smallLabel(Graphics2D g2, double x, double y, String t, Color c) {
        g2.setFont(SMALL); g2.setColor(c);
        g2.drawString(t, (float) x, (float) y);
    }

    static void title(Graphics2D g2, String t, int width) {
        g2.setFont(TITLE); g2.setColor(TITLE_COLOR);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(t, (width - fm.stringWidth(t)) / 2f, 40);
    }

    static Graphics2D prepare(Graphics g, int w, int h) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, w, h);
        return g2;
    }

    // =====================================================================
    //  Pestana 1: funcion phi
    // =====================================================================
    static class PhiPanel extends JPanel {
        PhiPanel() { setPreferredSize(new Dimension(760, 540)); }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = prepare(g, getWidth(), getHeight());
            int cx = getWidth() / 2;

            title(g2, "La funcion \u03C6 combina dos rutas de flujo", getWidth());

            // Rombo de decision
            diamond(g2, cx, 105, 72, 42, PURPLE_FILL, PURPLE_STROKE, PURPLE_TEXT, BODY, "\u00BFbandera?");

            // Cajas de las ramas
            double lx = cx - 265, rx = cx + 95, by = 185, bw = 170, bh = 58;
            box(g2, lx, by, bw, bh, BLUE_FILL, BLUE_STROKE, BLUE_TEXT, BODY, 1.5f,
                    n("x"), s("1"), n(" = \u22121"));
            box(g2, rx, by, bw, bh, BLUE_FILL, BLUE_STROKE, BLUE_TEXT, BODY, 1.5f,
                    n("x"), s("2"), n(" = 1"));

            // Flechas rombo -> ramas
            arrow(g2, cx - 45, 130, lx + bw / 2 + 15, by - 3);
            arrow(g2, cx + 45, 130, rx + bw / 2 - 15, by - 3);
            smallLabel(g2, lx + 8, 165, "verdadero", BLUE_TEXT);
            smallLabel(g2, rx + bw - 42, 165, "falso", BLUE_TEXT);

            // Caja de la funcion phi (resaltada)
            double px = cx - 105, py = 315, pw = 210, ph = 66;
            box(g2, px, py, pw, ph, AMBER_FILL, AMBER_STROKE, AMBER_TEXT, BODY, 2.2f,
                    n("x"), s("3"), n(" = \u03C6(x"), s("1"), n(", x"), s("2"), n(")"));

            // Flechas ramas -> phi (convergen)
            arrow(g2, lx + bw / 2, by + bh, px + 30, py - 3);
            arrow(g2, rx + bw / 2, by + bh, px + pw - 30, py - 3);

            // Flecha phi -> uso final
            double fy = 425;
            arrow(g2, cx, py + ph, cx, fy - 3);
            box(g2, cx - 95, fy, 190, 56, GRAY_FILL, GRAY_STROKE, GRAY_TEXT, BODY, 1.5f,
                    n("y = x"), s("3"), n(" * a"));

            // Nota al pie
            g2.setFont(SMALL); g2.setColor(GRAY_TEXT);
            String nota = "\u03C6 devuelve x\u2081 si el flujo paso por la parte verdadera, y x\u2082 si paso por la falsa.";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(nota, (getWidth() - fm.stringWidth(nota)) / 2f, (float) (fy + 90));
        }
    }

    // =====================================================================
    //  Pestana 2: versiones de una variable
    // =====================================================================
    static class VersionesPanel extends JPanel {
        VersionesPanel() { setPreferredSize(new Dimension(760, 540)); }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = prepare(g, getWidth(), getHeight());
            int w = getWidth();

            title(g2, "Cada asignacion estrena un nombre distinto", w);

            double midY = 230;

            // Caja original "p"
            double ox = 70, ow = 150, oh = 90;
            box(g2, ox, midY - oh / 2, ow, oh, GRAY_FILL, GRAY_STROKE, GRAY_TEXT, TITLE, 1.5f, n("p"));
            smallLabel(g2, ox + 18, midY + oh / 2 + 30, "reasignada 3 veces", GRAY_TEXT);

            // Flecha grande
            arrow(g2, ox + ow + 10, midY, ox + ow + 150, midY);
            smallLabel(g2, ox + ow + 45, midY - 12, "SSA", TEAL_TEXT);

            // Tres versiones numeradas
            double startX = ox + ow + 170, vw = 78, vh = 90, gap = 18;
            for (int i = 0; i < 3; i++) {
                double x = startX + i * (vw + gap);
                box(g2, x, midY - vh / 2, vw, vh, TEAL_FILL, TEAL_STROKE, TEAL_TEXT, TITLE, 1.5f,
                        n("p"), s(String.valueOf(i + 1)));
            }
            smallLabel(g2, startX + 20, midY + vh / 2 + 30, "un nombre por cada asignacion", TEAL_TEXT);

            // Nota al pie
            g2.setFont(SMALL); g2.setColor(GRAY_TEXT);
            String nota = "Cada valor tiene un nombre unico: el compilador siempre sabe de donde viene.";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(nota, (w - fm.stringWidth(nota)) / 2f, (float) (midY + 170));
        }
    }
    
}