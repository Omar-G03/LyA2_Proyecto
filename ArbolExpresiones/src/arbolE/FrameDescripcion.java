
package arbolE;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author omar
 */
public class FrameDescripcion extends javax.swing.JFrame {
    private CardLayout cardLayout;
private int paginaActual = 0;

private final String[] nombresEjemplos = {
    "Ejemplo 1: Ciclo WHILE con condición relacional",
    "Ejemplo 2: Condicional IF / ELSE con asignación"
};
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FrameDescripcion.class.getName());

    
    
private void mostrarPagina() {
    cardLayout.show(panelTarjetas, "pagina" + (paginaActual + 1));
    actualizarEtiquetaPagina();
}

private void actualizarEtiquetaPagina() {
    lblPagina.setText(
        nombresEjemplos[paginaActual]
        + " (" + (paginaActual + 1)
        + " de " + nombresEjemplos.length + ")"
    );
}


    public FrameDescripcion() {
        initComponents();
      

    cardLayout = new CardLayout();
    panelTarjetas.setLayout(cardLayout);

    panelTarjetas.add(crearTarjetaEjemplo1(), "pagina1");
    panelTarjetas.add(crearTarjetaEjemplo2(), "pagina2");

    actualizarEtiquetaPagina();
    
    panelTarjetas.setPreferredSize(new java.awt.Dimension(700, 380));

    pack();                       // recalcula el tamaño de la ventana según su contenido
    setLocationRelativeTo(null);  // centra la ventana en la pantalla
    setResizable(false); 
}
    
    
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
    
   //fin ejemplo 1

    // ================= EJEMPLO 2 =================
//crear
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
  //construir ejemplo 2
    
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblPagina = new javax.swing.JLabel();
        panelTarjetas = new javax.swing.JPanel();
        btnSiguiente = new javax.swing.JButton();
        btnAnterior = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblPagina.setText("a");

        javax.swing.GroupLayout panelTarjetasLayout = new javax.swing.GroupLayout(panelTarjetas);
        panelTarjetas.setLayout(panelTarjetasLayout);
        panelTarjetasLayout.setHorizontalGroup(
            panelTarjetasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 359, Short.MAX_VALUE)
        );
        panelTarjetasLayout.setVerticalGroup(
            panelTarjetasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 130, Short.MAX_VALUE)
        );

        btnSiguiente.setText("Siguiente");
        btnSiguiente.addActionListener(this::btnSiguienteActionPerformed);

        btnAnterior.setText("Anterior");
        btnAnterior.addActionListener(this::btnAnteriorActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addComponent(btnAnterior)
                        .addGap(18, 18, 18)
                        .addComponent(lblPagina, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSiguiente))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(panelTarjetas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(150, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(107, Short.MAX_VALUE)
                .addComponent(panelTarjetas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAnterior)
                    .addComponent(lblPagina)
                    .addComponent(btnSiguiente))
                .addGap(29, 29, 29))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSiguienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSiguienteActionPerformed
      paginaActual = (paginaActual + 1) % nombresEjemplos.length;
    mostrarPagina();
    }//GEN-LAST:event_btnSiguienteActionPerformed

    private void btnAnteriorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnteriorActionPerformed
  paginaActual = (paginaActual == 0)
            ? nombresEjemplos.length - 1
            : paginaActual - 1;

    mostrarPagina();        
    }//GEN-LAST:event_btnAnteriorActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new FrameDescripcion().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAnterior;
    private javax.swing.JButton btnSiguiente;
    private javax.swing.JLabel lblPagina;
    private javax.swing.JPanel panelTarjetas;
    // End of variables declaration//GEN-END:variables
}
