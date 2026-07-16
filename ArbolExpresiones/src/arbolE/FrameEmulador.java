package arbolE;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Ventana para abrir, en emu8086, alguno de los archivos .asm generados
 * a partir de las expresiones compiladas en Frrameinterfaz.
 *
 * @author omar
 */
public class FrameEmulador extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(FrameEmulador.class.getName());

    private JTextField txtRutaEmu;
    private JComboBox<String> comboArchivosAsm;

    public FrameEmulador(List<String> archivosGenerados) {
        setTitle("Abrir en emu8086 - LyA2");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(560, 200);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panelPrincipal = new JPanel(new GridLayout(3, 1, 10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ===== Fila 1: ruta de emu8086.exe =====
        JPanel filaEmu = new JPanel(new BorderLayout(8, 0));
        txtRutaEmu = new JTextField();
        JButton btnBuscarEmu = new JButton("Buscar emu8086.exe");
        btnBuscarEmu.addActionListener(e -> buscarEjecutable());
        filaEmu.add(new JLabel("emu8086.exe:"), BorderLayout.WEST);
        filaEmu.add(txtRutaEmu, BorderLayout.CENTER);
        filaEmu.add(btnBuscarEmu, BorderLayout.EAST);

        // ===== Fila 2: selector del .asm generado (uno por cada expresión compilada) =====
        JPanel filaAsm = new JPanel(new BorderLayout(8, 0));
        comboArchivosAsm = new JComboBox<>(new DefaultComboBoxModel<>(
                archivosGenerados.toArray(new String[0])));
        if (comboArchivosAsm.getItemCount() > 0) {
            // Selecciona por defecto el último generado (la compilación más reciente)
            comboArchivosAsm.setSelectedIndex(comboArchivosAsm.getItemCount() - 1);
        }
        JButton btnBuscarAsm = new JButton("Buscar otro...");
        btnBuscarAsm.addActionListener(e -> buscarArchivoAsm());
        filaAsm.add(new JLabel("Expresión compilada:"), BorderLayout.WEST);
        filaAsm.add(comboArchivosAsm, BorderLayout.CENTER);
        filaAsm.add(btnBuscarAsm, BorderLayout.EAST);

        // ===== Fila 3: botón para abrir =====
        JButton btnAbrir = new JButton("Abrir en emu8086");
        btnAbrir.addActionListener(e -> abrirEnEmulador());

        panelPrincipal.add(filaEmu);
        panelPrincipal.add(filaAsm);
        panelPrincipal.add(btnAbrir);

        add(panelPrincipal, BorderLayout.CENTER);

        if (comboArchivosAsm.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Todavía no has compilado ninguna expresión con 'Agente IA'.\n"
                    + "Puedes usar 'Buscar otro...' para abrir un .asm manualmente.",
                    "Sin archivos generados", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void buscarEjecutable() {
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Selecciona emu8086.exe");
        selector.setFileFilter(new FileNameExtensionFilter("Ejecutables (*.exe)", "exe"));
        int resultado = selector.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            txtRutaEmu.setText(selector.getSelectedFile().getAbsolutePath());
        }
    }

    private void buscarArchivoAsm() {
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Selecciona el archivo .asm");
        selector.setFileFilter(new FileNameExtensionFilter("Archivos ASM (*.asm)", "asm"));
        selector.setCurrentDirectory(new File(System.getProperty("user.dir")));
        int resultado = selector.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            String ruta = selector.getSelectedFile().getAbsolutePath();
            comboArchivosAsm.addItem(ruta);
            comboArchivosAsm.setSelectedItem(ruta);
        }
    }

    private void abrirEnEmulador() {
        String rutaEmu = txtRutaEmu.getText().trim();
        Object seleccionado = comboArchivosAsm.getSelectedItem();
        String rutaAsm = (seleccionado != null) ? seleccionado.toString().trim() : "";

        if (rutaEmu.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Primero selecciona la ubicación de emu8086.exe.",
                    "Falta el ejecutable", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (rutaAsm.isEmpty() || !new File(rutaAsm).exists()) {
            JOptionPane.showMessageDialog(this,
                    "El archivo .asm seleccionado no existe.\n"
                    + "Compila una expresión con 'Agente IA' primero, o usa 'Buscar otro...'.",
                    "Archivo no encontrado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // emu8086 acepta la ruta del .asm como argumento para
            // abrirlo directamente en su editor.
            new ProcessBuilder(rutaEmu, rutaAsm).start();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo abrir emu8086:\n" + ex.getMessage(),
                    "Error al ejecutar", JOptionPane.ERROR_MESSAGE);
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
    }
}