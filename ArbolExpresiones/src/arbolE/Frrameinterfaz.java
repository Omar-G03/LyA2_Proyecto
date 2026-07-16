package arbolE;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.Color;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JColorChooser;
import static javax.swing.JOptionPane.showMessageDialog;
import java.util.ArrayList;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author omar
 */
public class Frrameinterfaz extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Frrameinterfaz.class.getName());

    /**
     * Creates new form Frrameinterfaz
     */
    //==== SECCION DE ATRIBUTOS  === 9 de Julio 2026
    String nPolaca;
    
    int temp;
    //JFrame vrntana;//Frame para arbol Grafico

    // ===== NUEVO =====
    private FrameTripletas frameTripletas;
    FrameCuadruplos cuadruplos;
    private int radioNodoGrafo = 20;
    private Color colorNodoGrafo = new Color(173, 216, 230);
    private Color colorLineaGrafo = Color.BLACK;
    private float grosorLineaGrafo = 2f;
    
    String izq, der;  // == 15 Julio
    String emuLocal="";
    int contador=0;
    
private ArrayList<String> archivosAsmGenerados = new ArrayList<>(); // NUEVO: reemplaza a ultimoArchivoAsm
    
    
    //Constructor
    public Frrameinterfaz() {
        initComponents();
        nPolaca = "";
        temp = 0;
        izq = ""; //15 Julio
        der = ""; //15 Julio
    }//Fin COnstructor
    
    
    //*********GENERARDOR DE ARCHIVO .ASM
    public void generarEmutasm(String emu, int i){
        try{
            FileWriter escritor = new FileWriter("e"+ i+ ".asm");
            escritor.write(emu);
            escritor.close();
            System.out.println("Archivo creado existosamente");
            
        }catch (Exception e){
            System.out.println("Ha ocurrido un error al crear el archivo");
        }
    }//FIN METODO ARCHIVO .ASM
    
    public void sonido(){
        try {
                    File sonido = new File("C:\\LyA 2\\ArbolExpresiones\\ArbolExpresiones\\src\\arbolE\\new-notification-022-370046.wav");
                    if (sonido.exists()) {
                        AudioInputStream audioStream = AudioSystem.getAudioInputStream(sonido);
                        Clip clip = AudioSystem.getClip();
                        clip.open(audioStream);
                        clip.start(); 
                    } else {
                        showMessageDialog(null, "No se encontró el archivo de sonido.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessageDialog(null, "Error al reproducir el sonido.");
                }
    }
    
    public void intermedio(Nodo n) {
        if (n == null) {
            return;
        }

        Nodo izquierdo = n.getIzquierdo();
        Nodo derecho = n.getDerecho();

        intermedio(izquierdo);
        intermedio(derecho);

        boolean esHoja = (izquierdo == null && derecho == null);

        if (esHoja) {
            n.setLugar(n.getDato() + " ");
            n.setCodigoIntermedio(" ");
            return;
        }

        switch (n.getDato()) {
            case "+":
            case "-":
            case "*":
            case "/":
                generarCodigoOperador(n, izquierdo, derecho, n.getDato());
                break;
            case "=":
                generarCodigoAsignacion(n, izquierdo, derecho);
                break;
            default:
                // Operador no reconocido en esta regla; no se genera código
                break;
        }
    }//Intermedio

    private void generarCodigoOperador(Nodo n, Nodo izquierdo, Nodo derecho, String operador) {
        temp++;
        String lugarActual = "T" + temp;
        n.setLugar(lugarActual);

        String codigo = new StringBuilder()
                .append(izquierdo.getCodigoIntermedio()).append(" ")
                .append(derecho.getCodigoIntermedio()).append(" ")
                .append(lugarActual).append(" = ")
                .append(izquierdo.getLugar()).append(" ")
                .append(operador).append(" ")
                .append(derecho.getLugar())
                .append("\n")
                .toString();

        n.setCodigoIntermedio(codigo);
    }//generarCodigoOperador

    private void generarCodigoAsignacion(Nodo n, Nodo izquierdo, Nodo derecho) {
        String codigo = derecho.getDato() + " " +
                izquierdo.getLugar() + " = T" + temp + "\n";
        n.setCodigoIntermedio(codigo);
    }//generarCodigoAsignacion
    //=======METODOS inOrden, postOrden  y preOrden
    public void inOrden(Nodo n){
        if (n!= null) {
           inOrden(n.getIzquierdo());
           jTextInOrden.append(n.getDato()+ "\n");
           inOrden(n.getDerecho()); 
           
           
           //15 de Julio

           switch(n.getDato()){
               case "+": 
                   System.out.println("ADD JIMENEZ PEDRAZA ");
                   izq = n.getIzquierdo().getDato();
                   der = n.getDerecho().getDato();
                   
                   System.out.println("izq:" + izq);
                   System.out.println("der:" + der);
                   emuLocal += "MOV AX, "+ n.getIzquierdo().getDato()+"\n";
                   emuLocal += "MOV BX, "+ n.getDerecho().getDato()+"\n";
                   emuLocal += "ADD AX,BX"+ "\n\n";
               break;
               case "-": 
                    izq = n.getIzquierdo().getDato();
                    der = n.getDerecho().getDato();
                   System.out.println("SUB");
               break;
               case "/": 
                   System.out.println("DIV");
                    izq = n.getIzquierdo().getDato();
                    der = n.getDerecho().getDato();
               break;
               case "*": 
                   System.out.println("MUL");
                   izq = n.getIzquierdo().getDato();
                   der = n.getDerecho().getDato();
           }//FIN Switch
        }//Fin If
        
        
    }//inOrden
    
    public void preOrden(Nodo n){
        if (n!=null) {
            jTextPreOrden.append(n.getDato()+ "\n");
            nPolaca += jNotacionPolaca.getText() + n.getDato()+" ";
            jNotacionPolaca.setText(jNotacionPolaca.getText() + n.getDato()+" ");
            
            preOrden(n.getIzquierdo());
            preOrden(n.getDerecho());
        }
    }//preOrden
    
    public void postOrden(Nodo n){
        if (n!= null) {
            postOrden(n.getIzquierdo());
            postOrden(n.getDerecho());
            jTextPostOrden.append(n.getDato() + "\n");
        }
    }//postOrden
    
    
    
    //========GUARDAR REGLAS SEMANTICAS EN ARCHIVO TXT =========
    private void guardarReglasSemanticas(String contenido) {
        // 1. Preguntar si desea guardar (Sí / No)
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Desea guardar las reglas semánticas en un archivo de texto?",
                "Guardar reglas semánticas",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
 
        if (respuesta != JOptionPane.YES_OPTION) {
            return; // el usuario eligió "No" (o cerró el cuadro): no se guarda nada
        }
        
         // 2. RUTA ESPECÍFICA donde se guardará el archivo.
        //    Cambia esta ruta por la que necesites en tu equipo.
        String ruta = "C:\\ReglasSemanticas\\reglas_semanticas.txt";
 
        try {
            File archivo = new File(ruta);
            File carpeta = archivo.getParentFile();
            if (carpeta != null && !carpeta.exists()) {
                carpeta.mkdirs(); // crea la carpeta si todavía no existe
            }
 
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
                bw.write(contenido);
            }//try-with-resources: cierra el archivo automáticamente
 
            JOptionPane.showMessageDialog(this,
                    "Archivo guardado correctamente en:\n" + ruta,
                    "Guardado exitoso", JOptionPane.INFORMATION_MESSAGE);
 
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Ocurrió un error al guardar el archivo:\n" + ex.getMessage(),
                    "Error al guardar", JOptionPane.ERROR_MESSAGE);
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
    }//guardarReglasSemanticas
    

    private void solicitarConfiguracionGrafo() {
        String entradaRadio = JOptionPane.showInputDialog(this,
                "Radio de los nodos del grafo (10 - 50 px):", radioNodoGrafo);
        try {
            if (entradaRadio != null) radioNodoGrafo = Integer.parseInt(entradaRadio.trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Valor no válido, se usará " + radioNodoGrafo + " px.");
        }

        Color nodoElegido = JColorChooser.showDialog(this, "Color de los nodos", colorNodoGrafo);
        if (nodoElegido != null) colorNodoGrafo = nodoElegido;

        Color lineaElegida = JColorChooser.showDialog(this, "Color de las líneas", colorLineaGrafo);
        if (lineaElegida != null) colorLineaGrafo = lineaElegida;

        String entradaGrosor = JOptionPane.showInputDialog(this,
                "Grosor de las líneas (1 - 10 px):", (int) grosorLineaGrafo);
        try {
            if (entradaGrosor != null) grosorLineaGrafo = Float.parseFloat(entradaGrosor.trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Valor no válido, se usará " + grosorLineaGrafo + " px.");
        }
    }    
    
    
    
    //=======ABRIR VIDEO YOUTUBE
    /**
 * Abre el video de YouTube en el navegador predeterminado del sistema.
 */
private void abrirVideoYoutube() {
    String url = "https://www.youtube.com/watch?v=P_dA92_bnXc";
    try {
        Desktop.getDesktop().browse(new URI(url));
    } catch (IOException | URISyntaxException ex) {
        JOptionPane.showMessageDialog(this,
                "No se pudo abrir el video:\n" + ex.getMessage(),
                "Error al abrir el enlace", JOptionPane.ERROR_MESSAGE);
        logger.log(java.util.logging.Level.SEVERE, null, ex);
    }
}//abrirVideoYoutube

    private void abrirVentanaEmulador() {
        FrameEmulador ventanaEmulador = new FrameEmulador(archivosAsmGenerados);
        ventanaEmulador.setVisible(true);
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPreOrden = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextPostOrden = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextReglasSemanticas = new javax.swing.JTextArea();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextInOrden = new javax.swing.JTextArea();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTextTresDirecciones = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        label6 = new java.awt.Label();
        jNotacionPolaca = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        label1 = new java.awt.Label();
        label2 = new java.awt.Label();
        label3 = new java.awt.Label();
        label4 = new java.awt.Label();
        label5 = new java.awt.Label();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 0, 0));
        jPanel1.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.setToolTipText("");
        jPanel1.setName(""); // NOI18N

        jLabel3.setText("jLabel3");

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/VID_20260530_080812_746.jpg"))); // NOI18N
        jLabel4.setText("jLabel4");

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Árbol de expresiones");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(234, 234, 234)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 373, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(360, 360, 360)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 36, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25))
        );

        jLabel1.setText("Expresión");

        jTextField1.setBackground(new java.awt.Color(255, 255, 0));

        jButton1.setText("Compila");
        jButton1.addActionListener(this::jButton1ActionPerformed);

        jTextPreOrden.setColumns(20);
        jTextPreOrden.setRows(5);
        jScrollPane1.setViewportView(jTextPreOrden);

        jTextPostOrden.setColumns(20);
        jTextPostOrden.setRows(5);
        jScrollPane2.setViewportView(jTextPostOrden);

        jTextReglasSemanticas.setColumns(20);
        jTextReglasSemanticas.setRows(5);
        jScrollPane3.setViewportView(jTextReglasSemanticas);

        jTextInOrden.setColumns(20);
        jTextInOrden.setRows(5);
        jScrollPane4.setViewportView(jTextInOrden);

        jTextTresDirecciones.setColumns(20);
        jTextTresDirecciones.setRows(5);
        jScrollPane5.setViewportView(jTextTresDirecciones);

        jPanel2.setBackground(new java.awt.Color(0, 0, 0));

        label6.setForeground(new java.awt.Color(255, 255, 255));
        label6.setText("NOtación Polaca");

        jButton2.setText("codigo 3 direccion");
        jButton2.setToolTipText("");

        jButton3.setText("clean");

        jButton4.setText("tabla imbolo");

        jButton8.setText("Cuadruplos");
        jButton8.addActionListener(this::jButton8ActionPerformed);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(label6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(jNotacionPolaca, javax.swing.GroupLayout.PREFERRED_SIZE, 555, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addGap(27, 27, 27)
                .addComponent(jButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton8)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jNotacionPolaca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton2)
                        .addComponent(jButton3)
                        .addComponent(jButton4)
                        .addComponent(jButton8))
                    .addComponent(label6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(255, 153, 0));

        label1.setText("In Orden");

        label2.setText("Pre Orden");

        label3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        label3.setText("Post Orden");

        label4.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        label4.setText("Regla emantica");

        label5.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        label5.setText("Código 3 direccione");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(97, 97, 97)
                .addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(122, 122, 122)
                .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(113, 113, 113)
                .addComponent(label3, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(116, 116, 116)
                .addComponent(label4, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(125, 125, 125)
                .addComponent(label5, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(label5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jButton5.setText("Agente IA");
        jButton5.addActionListener(this::jButton5ActionPerformed);

        jButton6.setText("Optimiza Intermedio");
        jButton6.addActionListener(this::jButton6ActionPerformed);

        jButton7.setText("Abrir Emulador");
        jButton7.addActionListener(this::jButton7ActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1)
                        .addGap(62, 62, 62)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton7)
                        .addGap(183, 183, 183))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(79, 79, 79)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton5)
                    .addComponent(jButton6)
                    .addComponent(jButton7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String datos="";
        Arbol_Omar a = new Arbol_Omar();
        datos = jTextField1.getText();
        
        Nodo arbolExpresion = a.crear(datos);//Enviar los datos
        jTextReglasSemanticas.append(a.getReglasEjecutadas());
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        String datos= "";
        
        ArbolAgenteConIA arbol = new ArbolAgenteConIA();
        datos = jTextField1.getText();
        Nodo arbolExpresiones = arbol.crear(datos);
        jTextReglasSemanticas.append(arbol.getReglasEjecutadas());
        
        JFrame ventana = new JFrame("Visualizardor de Arboles - LyA2");
        PanelArbol panel = new PanelArbol(arbolExpresiones, arbol.tablaSimbolos);
        
        ventana.add(panel);
        ventana.setSize(600, 400);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setLocationRelativeTo(null);//Centrar en pantalla
        ventana.setVisible(true);
        
        preOrden(arbolExpresiones);
        inOrden(arbolExpresiones);
        postOrden(arbolExpresiones);
        intermedio(arbolExpresiones);
        
        arbol.emu86+= ".CODE \n"+
                        "MOV AX, @DATA \n"+
                        "MOV DS,AX \n";
        
        String finalEmu = arbol.emu86 + this.emuLocal;
        finalEmu += "\n mov AX, 4c00h \n"+
                "int 21h \n end";
        showMessageDialog(null, finalEmu);
        
        contador++;
        generarEmutasm(finalEmu,contador);
        
        
        String rutaGenerada = new File("e" + contador + ".asm").getAbsolutePath();
        archivosAsmGenerados.add(rutaGenerada); // NUEVO: se acumulan todos, no se sobreescribe
        
        jTextTresDirecciones.append(arbolExpresiones.getCodigoIntermedio());
        
        //Guardar archivo
        String reglasSemanticas = arbol.getReglasEjecutadas();
        jTextReglasSemanticas.append(reglasSemanticas);
        guardarReglasSemanticas(reglasSemanticas);
        
        //
        
        solicitarConfiguracionGrafo();
        arbol.calcularValoresParaGrafo(arbolExpresiones);
        Nodo arbolGAD = arbol.convertirAGAD(arbolExpresiones);

        JFrame ventanaGrafo = new JFrame("Grafo Acíclico Dirigido - LyA2");
        PanelGrafo panelGrafo = new PanelGrafo(arbolGAD, colorNodoGrafo, radioNodoGrafo, colorLineaGrafo, grosorLineaGrafo);
        ventanaGrafo.add(panelGrafo);
        ventanaGrafo.setSize(600, 400);
        ventanaGrafo.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ventanaGrafo.setLocationRelativeTo(null);
        ventanaGrafo.setVisible(true);

        // ===== NUEVO: Tripletas =====
        frameTripletas = new FrameTripletas(arbol.getTripletas());
        frameTripletas.setVisible(true);
        
        cuadruplos = new FrameCuadruplos(arbolExpresiones);
        cuadruplos.setVisible(true);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        abrirVideoYoutube();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        abrirVentanaEmulador();
        
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
                String datos= "";
        ArbolAgenteConIA arbol = new ArbolAgenteConIA();
        datos = jTextField1.getText();
        Nodo arbolExpresiones = arbol.crear(datos);        

    }//GEN-LAST:event_jButton8ActionPerformed

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
       // java.awt.EventQueue.invokeLater(() -> new Frrameinterfaz().setVisible(true));
        

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTextField jNotacionPolaca;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextArea jTextInOrden;
    private javax.swing.JTextArea jTextPostOrden;
    private javax.swing.JTextArea jTextPreOrden;
    private javax.swing.JTextArea jTextReglasSemanticas;
    private javax.swing.JTextArea jTextTresDirecciones;
    private java.awt.Label label1;
    private java.awt.Label label2;
    private java.awt.Label label3;
    private java.awt.Label label4;
    private java.awt.Label label5;
    private java.awt.Label label6;
    // End of variables declaration//GEN-END:variables
}
