package arbolE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.StringTokenizer;

/**
 * @author Omar
 */
public class Arbol_Omar {
    // Variables estáticas globales de la clase 
    private static final String espacios = "\t";
    private static final String aritmeticos = "+-*()^=";
    private static final String variables = "abcdefghijklmnopqrstuvwxyz";
    private static final String op_Multiplica = "*";
    private static final String[] temporales = {"T1", "T2", "T3", "T4", "T5"};

    // Atributos de instancia
    Stack<Nodo> ArbolNodo;
    Stack<String> caracter;
    private Nodo raiz;
    
    HashMap<String, String> tablaSimbolos;
    HashMap<String, String> erroresSemanticos;
    HashMap<String, String> producciones;
    
    int paso;
    ArrayList<String> reglasEjecutadas;
    
    public Arbol_Omar() {
        reglasEjecutadas = new ArrayList<>();
        tablaSimbolos = new HashMap<>();
        erroresSemanticos = new HashMap<>();
        producciones = new HashMap<>();
        ArbolNodo = new Stack<>();
        caracter = new Stack<>();
        paso = 0;
    } // Fin Constructor

    // ******** REGLAS EJECUTADAS ===== 1RO JULIO
    public String getReglasEjecutadas() {
        String reglasE = "";
        for (int i = 0; i < reglasEjecutadas.size(); i++) {
            System.out.println("Reglas Ejecutadas " + reglasEjecutadas.get(i));
            reglasE += reglasEjecutadas.get(i) + "\n";
        }
        return reglasE;
    } 

    public void agregarValex(String lexema, String valor) {
    } // Agregar Valor lexico - Analisis Semantico

    public String regresaValex(String lexema) {
        return this.tablaSimbolos.get(lexema);
    } 

    public void guardar() { // Permite construir el arbol
        paso++;
        
        // Se eliminaron los casteos innecesarios gracias a los genéricos del Stack
        Nodo izquierdo = ArbolNodo.pop();
        Nodo derecho = ArbolNodo.pop();
        
        String operador = caracter.peek();
        
        ArbolNodo.push(new Nodo(derecho, caracter.pop(), izquierdo));
        
        if (operador.equals("+")) {
            String reglaE = "E.Nodo = new Nodo(+, E1.nodo, T.nodo)";
            reglasEjecutadas.add("p" + paso + " " + reglaE);
        }
        
        if (operador.equals("-")) {
            String reglaE = "E.Nodo = new Nodo(-, E1.nodo, T.nodo)";
            reglasEjecutadas.add("p" + paso + " " + reglaE);
        }
        
        if (operador.equals("*")) {
            String reglaE = "E.Nodo = new Nodo(*, E1.nodo, T.nodo)";
            reglasEjecutadas.add("p" + paso + " " + reglaE);
        }
        
        if (operador.equals("/")) {
            String reglaE = "E.Nodo = new Nodo(/, E1.nodo, T.nodo)";
            reglasEjecutadas.add("p" + paso + " " + reglaE);
        }
    } // Guardar

    // metodos del ARBOL
    public Nodo crear(String expresion) {
        StringTokenizer tokenizer;
        String token;
        paso = 0;
        
        tokenizer = new StringTokenizer(expresion, espacios + aritmeticos, true);
        
        while (tokenizer.hasMoreTokens()) {
            
            token = tokenizer.nextToken();
            System.out.println(" Token " + token);
            
            // OPTIMIZACIÓN: Cambio de indexOf por contains para existencia
            if (espacios.contains(token)) {
                System.out.println("Se trata de un identificador: ");
                
            // OPTIMIZACIÓN: Cambio de indexOf por contains para existencia
            } else if (!aritmeticos.contains(token)) {
                ArbolNodo.push(new Nodo(token));
                paso++;
                String regla = "T.nodo = new Hoja(id<" + token + ">,id.entrada_" + token + ")";
                reglasEjecutadas.add("p" + paso + "" + regla);
                
            } else if (token.equals(")")) {
                
                // OPTIMIZACIÓN: Eliminación de llaves para una sola instrucción
                while (!caracter.empty() && !caracter.peek().equals("(")) 
                    guardar();
                
                caracter.pop();
                
            } else {
                if (!token.equals(" (") && !caracter.empty()) {
                    String exa = caracter.peek();
                    
                    // Aquí se mantiene indexOf porque evalúa la posición jerárquica (precedencia), no solo si existe el caracter.
                    while (!exa.equals(" (") && !caracter.empty() && aritmeticos.indexOf(exa) >= aritmeticos.indexOf(token)) {
                        guardar();
                        
                        // OPTIMIZACIÓN: Eliminación de llaves
                        if (!caracter.empty()) 
                            exa = caracter.peek();
                    }
                }
                caracter.push(token); 
            }
        } // while tokenizer
        
        while (!caracter.empty()) {
            // OPTIMIZACIÓN: Eliminación de llaves para una sola instrucción
            if (caracter.peek().equals(" (")) 
                caracter.pop();
            else {
                guardar(); 
                raiz = ArbolNodo.peek();
            }
        }
        
        return raiz;
    } // crear
} // FIN CLASE