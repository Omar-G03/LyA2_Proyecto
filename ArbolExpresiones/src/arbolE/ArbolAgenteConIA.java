package arbolE;

import java.util.ArrayList;
import java.util.HashMap;
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

        arbolNodo.push(new Nodo(derecho, operador, izquierdo));

        // OPTIMIZACIÓN: switch en lugar de 4 if independientes
        String reglaE = "E.nodo = new Nodo(" + operador + ", E1.nodo, T.nodo";
        reglasEjecutadas.add("p" + paso + " " + reglaE);
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