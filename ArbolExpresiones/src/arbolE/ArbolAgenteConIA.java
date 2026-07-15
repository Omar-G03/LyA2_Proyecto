package arbolE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Stack;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;

/**
 *
 * @author omar
 */
public class ArbolAgenteConIA {
    //Atributos
    Stack<Nodo> arbolNodo;
    Stack<String> caracter;
    //Identificar entre Operador y Operandos
    final String espacios="\t";
    final String aritmeticos="+-*()^=/";
    final String variables="abcdefghijklmnopqrstuvwxyz";
    final String opMultiplica="*";
    String reglaSemantica=""; 
    String r="";
    private Nodo raiz;
    // 30 Junio
    String[] temporales={"T1","T2","T3","T4","T5"};
    
    HashMap<String, String> tablaSimbolos;
    HashMap<String, String> erroresSemanticos;
    HashMap<String, String> producciones;
    int paso;
    
    //01 Julio
    ArrayList <String> reglasEjecutadas;

    // ===== NUEVO: soporte para tripletas =====
    ArrayList<String[]> tripletas = new ArrayList<>();
    private final IdentityHashMap<Nodo, Integer> indiceTripleta = new IdentityHashMap<>();

    // ===== NUEVO: soporte para valores del grafo (GAD) =====
    private final IdentityHashMap<Nodo, Double> memoValores = new IdentityHashMap<>();

    //constructor
    public ArbolAgenteConIA(){
        reglasEjecutadas = new ArrayList <String>();
        tablaSimbolos = new HashMap();
        erroresSemanticos = new HashMap();
        producciones = new HashMap();
        
        arbolNodo = new Stack<Nodo>();
        caracter = new Stack<String>();
        
        paso=0;
    }//constructor
    
    public String getReglasEjecutadas(){
        String reglasE="";
        for(int i=0; i<reglasEjecutadas.size();i++){
            System.out.println("Reglas ejecutadas"+
                    reglasEjecutadas.get(i));
            reglasE+=reglasEjecutadas.get(i)+"\n";
        }//for
        return reglasE;
    }

    // NUEVO
    public ArrayList<String[]> getTripletas(){
        return tripletas;
    }
    
    public void agregaValex (String lexema, String valor){
        // 02. Insertar en la tabla de símbolos (lexema -> valor)
        tablaSimbolos.put(lexema, valor);
    }//agregarValex - Analisis semantico
    
    public String regresaValex(String lexema){
        return this.tablaSimbolos.get(lexema);
    }//regresaValex

    /**
     * 01. Solicitar el valor del token (variable) mediante un cuadro de diálogo.
     * 02. Insertar en la tabla de símbolos.
     *
     * Si el token ya fue preguntado antes (por ejemplo, si la variable se
     * repite dentro de la misma expresión), NO se vuelve a preguntar: se
     * reutiliza el valor que ya está guardado en tablaSimbolos.
     */
    private void solicitarValorToken(String token) {
        // Si el token ya tiene valor asignado, no se pregunta de nuevo
        if (tablaSimbolos.containsKey(token)) {
            return;
        }

        String valor = null;
        boolean valorValido = false;

        // 01. Solicitar valor para el token
        while (!valorValido) {
            valor = JOptionPane.showInputDialog(null,
                    "Ingrese el valor para la variable \"" + token + "\":",
                    "Valor de " + token, JOptionPane.QUESTION_MESSAGE);

            if (valor == null) {
                // El usuario canceló: se asigna un valor por defecto
                valor = "0";
                valorValido = true;
            } else if (valor.trim().matches("-?\\d+(\\.\\d+)?")) {
                // Es un texto que representa un número (String tipo número)
                valor = valor.trim();
                valorValido = true;
            } else {
                JOptionPane.showMessageDialog(null,
                        "El valor de \"" + token + "\" debe ser numérico.",
                        "Dato inválido", JOptionPane.WARNING_MESSAGE);
            }
        }//while

        // 02. Insertar en la tabla de símbolos
        agregaValex(token, valor);
    }//solicitarValorToken
    
    public void guardar() {
        paso++;
        r = "r" + paso;
        // OPTIMIZACIÓN: eliminada variable 'operador' redundante;
        // se obtiene el operador directamente con peek() antes de pop()
        Nodo izquierdo = arbolNodo.pop();
        Nodo derecho   = arbolNodo.pop();
        String operador      = caracter.pop();

        Nodo nuevoNodo = new Nodo(derecho, operador, izquierdo);
        arbolNodo.push(nuevoNodo);

        // ===== NUEVO: Tripleta {operador, arg1, arg2} =====
        // Nota: por cómo funciona el stack aquí, el operando pusheado
        // PRIMERO (el izquierdo real de la expresión) queda en la
        // variable local "derecho", y el pusheado SEGUNDO (el derecho
        // real) queda en la variable local "izquierdo". Es una
        // confusión de nombres que ya traía el código original, así
        // que el orden correcto para la tripleta es (derecho, izquierdo):
        String arg1 = obtenerReferenciaTripleta(derecho);
        String arg2 = obtenerReferenciaTripleta(izquierdo);
        tripletas.add(new String[]{operador, arg1, arg2});
        indiceTripleta.put(nuevoNodo, tripletas.size() - 1);

        // OPTIMIZACIÓN: switch en lugar de 4 if independientes
        String reglaE = "E.nodo = new Nodo(" + operador + ", E1.nodo, T.nodo";
        reglasEjecutadas.add("p" + paso + " " + reglaE);
    }

    /**
     * NUEVO
     * Determina cómo se representa un operando dentro de una tripleta:
     * - Si es una hoja (variable), se muestra su nombre tal cual.
     * - Si es el resultado de una tripleta anterior, se referencia por
     *   su índice entre paréntesis, ej. "(0)" = resultado de la tripleta #0.
     */
    private String obtenerReferenciaTripleta(Nodo n) {
        boolean esHoja = (n.getIzquierdo() == null && n.getDerecho() == null);
        if (esHoja) {
            return n.getDato();
        }
        Integer indice = indiceTripleta.get(n);
        return (indice != null) ? "(" + indice + ")" : n.getDato();
    }

    // ================= NUEVO: cálculo de valores para el grafo =================
    public void calcularValoresParaGrafo(Nodo nodo) {
        calcularValor(nodo);
    }

    private double calcularValor(Nodo nodo) {
        if (nodo == null) return 0;
        if (memoValores.containsKey(nodo)) return memoValores.get(nodo);

        boolean esHoja = (nodo.getIzquierdo() == null && nodo.getDerecho() == null);
        double valor;

        if (esHoja) {
            String texto = tablaSimbolos.get(nodo.getDato());
            valor = (texto != null) ? Double.parseDouble(texto) : 0;
        } else {
            double izq = calcularValor(nodo.getIzquierdo());
            double der = calcularValor(nodo.getDerecho());
            valor = operarValor(nodo.getDato(), izq, der);
        }

        memoValores.put(nodo, valor);
        nodo.setValor(formatearValorGrafo(valor));
        return valor;
    }

    private double operarValor(String operador, double izq, double der) {
        switch (operador) {
            case "+": return izq + der;
            case "-": return izq - der;
            case "*": return izq * der;
            case "/": return (der != 0) ? izq / der : 0;
            case "^": return Math.pow(izq, der);
            default:  return 0;
        }
    }

    private String formatearValorGrafo(double valor) {
        if (valor == Math.floor(valor) && !Double.isInfinite(valor)) {
            return String.valueOf((long) valor);
        }
        return String.valueOf(valor);
    }

    // ================= NUEVO: conversión a GAD =================
    public Nodo convertirAGAD(Nodo raizAST) {
        HashMap<String, Nodo> tabla = new HashMap<>();
        return convertir(raizAST, tabla);
    }

    private Nodo convertir(Nodo n, HashMap<String, Nodo> tabla) {
        if (n == null) return null;

        if (n.getIzquierdo() == null && n.getDerecho() == null) {
            String clave = "HOJA#" + n.getDato();
            Nodo existente = tabla.get(clave);
            if (existente != null) return existente; // reutiliza
            tabla.put(clave, n);
            return n;
        }

        // Procesar hijos primero (post-orden): así al llegar al padre
        // ya sabemos si los hijos son nodos compartidos o no.
        Nodo izqNuevo = convertir(n.getIzquierdo(), tabla);
        Nodo derNuevo = convertir(n.getDerecho(), tabla);

        // Reasignar hijos (puede que ahora apunten a nodos ya existentes)
        n.setIzquierdo(izqNuevo);
        n.setDerecho(derNuevo);

        String clave = n.getDato() + "#" 
                     + System.identityHashCode(izqNuevo) + "#" 
                     + System.identityHashCode(derNuevo);

        Nodo existente = tabla.get(clave);
        if (existente != null) return existente; 

        tabla.put(clave, n);
        return n;
    }
    
    //METODO DEL ARBOL
    public Nodo crear(String expresion){
        //1. Considerar la expresion como un conjunto de tokens
        StringTokenizer tokenizer;
        String token;
        //0. Inicializar valores para varias ejecuciones
        paso=0;//Paso de las reglas semanticas  
        reglaSemantica = ""; r ="";
        //2. Separacion de tokens de la expresion
        tokenizer = new StringTokenizer(expresion, espacios+aritmeticos+"/",true);
        //3. Mientras existan tokens
        while(tokenizer.hasMoreTokens()){
            //4. Omintir espacios en blanco
            token = tokenizer.nextToken();
            if(espacios.contains(token)) continue;
                //5. Se trata de un identificador
            if(aritmeticos.indexOf(token)<0){
           //no es un operador aritmetico
                //6. Extraer de la pila los terminos que estaban
                arbolNodo.push(new Nodo(token));
                paso++;
                String regla ="T.nodo = new Hoja(id<"+token+">,id.entrada_"+token+")";
                reglasEjecutadas.add("p"+paso+""+regla);

                // 01. Solicitar el valor del token y 02. insertar en tabla de simbolos
                solicitarValorToken(token);
            }else  if(token.equals(")")){
                //7. Tratar tokens que no son parentesis
                    
                while(!caracter.empty()&& !caracter.peek().equals("(")){
                    guardar();
                            
                }//while
                caracter.pop();
                //if
            }else{
              if(!token.equals("(")&&!caracter.empty()){
                  String exa=(String)caracter.peek();
                  while(!caracter.empty() && !exa.equals("(") 
                          && aritmeticos.indexOf(exa)>=aritmeticos.indexOf(token)){
                            guardar();
                            if(!caracter.empty()){
                                exa=(String)caracter.peek();
                            }//IF !caracter.empty
                  }//while !exa
              }//if-token
              caracter.push(token);
            }//if else
            //8. Guardar el token
        }//while
        while(!caracter.empty()){
            if(caracter.peek().equals(")")){
                caracter.pop();
            }else{
                guardar();
                raiz=(Nodo) arbolNodo.peek();
            }//else
        }//while !caracter.empty

        //Mostrar en consola la tabla de simbolos al finalizar
        System.out.println("=== TABLA DE SIMBOLOS ===");
        for (String clave : tablaSimbolos.keySet()) {
            System.out.println(clave + " = " + tablaSimbolos.get(clave));
        }//for

        return raiz;
    }//crear
    
    private int obtenerPrioridad(String operador){
        switch (operador){
            case "^":
                return 3;
            case "*": case "/":
                return 2;
            case "+": case "-":
                return 1;
            case "=":
                return 0;
            default:
                return -1;//Para parentesis u otros caracteres
        }//Switch
    }//Operador prioridad
}//class