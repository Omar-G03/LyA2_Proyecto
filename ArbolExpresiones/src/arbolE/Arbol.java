package arbolE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.StringTokenizer;


/**
 *
 * @author Omar
 */
public class Arbol {
    //Tributos
    Stack<Nodo> ArbolNodo;
    Stack<String> caracter;
    
    final String espacios= "\t";
    final String aritmeticos = "+-*()^=";
    final String variables = "abcdefghijklmnopqrstuvwxyz";
    final String opMultiplica= "*";
    private Nodo raiz;
    
    //30 junio 2026
    String[] temporales = {"T1","T2","T3","T4","T5"};
    
    HashMap<String, String> tablaSimbolos;
    HashMap<String, String> erroresSemanticos;
    HashMap<String, String> producciones;
    
    
    int paso;
    
    //1RO JULIO
    ArrayList <String> reglasEjecutadas;
    
    //Constructor
    public Arbol(){
        reglasEjecutadas = new ArrayList <String> ();//1ro JULIO
        tablaSimbolos = new HashMap();
        erroresSemanticos = new HashMap();
        producciones = new HashMap();
        
        ArbolNodo = new Stack<Nodo> ();
        caracter = new Stack<String> ();
        
        paso = 0;
        
        
    }//Fin Constructor
    //********REGGLAS EJECUTADAS ===== 1RO JULIO
    public String getReglasEjecutadas(){
        String reglasE = "";
        for (int i = 0; i < reglasEjecutadas.size(); i++) {
            System.out.println("Reglas Ejecutadas "+ 
                    reglasEjecutadas.get(i));
            reglasE += reglasEjecutadas.get(i) + "\n";
        }//for
        return reglasE;
    }//getReglasEjecutadas
    
    public void agregarValex(String lexema, String valor){
        
    }//Agregar Valor lexico - Analisis Semantico
    
    public String regresaValex(String lexema){
        return this.tablaSimbolos.get(lexema);
    }//RegresaValex
    
    public void guardar(){//Permite construir el arbol
        paso++;
        
        Nodo izquierdo = (Nodo) ArbolNodo.pop();
        Nodo derecho = (Nodo) ArbolNodo.pop();
        
        String operador = caracter.peek();
        //Investigar qie hace el Peek  Escribir lo que hace
        
        /*
        En programación (especialmente cuando trabajas con estructuras de datos 
        como Pilas/Stacks o Colas/Queues), el método peek() sirve para observar 
        o leer el elemento que está en la cima (la parte más alta) de la pila, 
        pero sin eliminarlo ni sacarlo de la estructura.
        */
        ArbolNodo.push(new Nodo(derecho,caracter.pop(),izquierdo));
        //
        if(operador.equals("+")){
            String reglaE = "E.Nodo = new Nodo(+, E1.nodo, T.nodo)";
            reglasEjecutadas.add("p"+ paso+" "+reglaE);
        }//El Operador +
        
        if(operador.equals("-")){
            String reglaE = "E.Nodo = new Nodo(-, E1.nodo, T.nodo)";
            reglasEjecutadas.add("p"+ paso+" "+reglaE);
        }//El Operador -
        
        if(operador.equals("*")){
            String reglaE = "E.Nodo = new Nodo(*, E1.nodo, T.nodo)";
            reglasEjecutadas.add("p"+ paso+" "+reglaE);
        }//El Operador *
        
        if(operador.equals("/")){
            String reglaE = "E.Nodo = new Nodo(/, E1.nodo, T.nodo)";
            reglasEjecutadas.add("p"+ paso+" "+reglaE);
        }//El Operador division
        
    }//Guardar
    
    //metodos del ARBOL
    public Nodo crear(String expresion){
        //1. Considerar la expresion como un conjunto de tokens
        StringTokenizer tokenizer;
        String token;
        //0. Inicializar valores para variables ejecuciones
        paso=0;//Paso de las reglas semanticas
        
        //Describe Stringtokenizer
        /*
        En Java, StringTokenizer es una clase que sirve para partir una cadena 
        de texto (String) larga en pedazos más pequeños, a los cuales se les llama "tokens".

        Para que haga estos cortes, tú le debes indicar qué caracteres va a usar 
        como "tijeras" (a esto se le llama delimitadores). Generalmente, las tijeras
        suelen ser espacios en blanco, comas o signos de puntuación.
        */
        
        //2. Separacion de tokens de la expresion
        tokenizer = new StringTokenizer(expresion,espacios+aritmeticos,true);
        
        //3. Mientras existan tokens
        while(tokenizer.hasMoreTokens()){
            
            //4. Omitir espacios en blanco
            token = tokenizer.nextToken();
            System.out.println(" Token "+token);
            if(espacios.indexOf(token)>=0){
                //5. trata de in identificar
                System.out.println("Se trata de un identificador: ");
                //
            }else if(aritmeticos.indexOf(token)<0){
                //6. Extraer de ña pila de los terminos que estaban
                ArbolNodo.push(new Nodo(token));
               paso++;
               String regla = "T.nodo = new Hoja(id<" +token+ ">,id.entrada_"+token+")";
               reglasEjecutadas.add("p"+paso+""+regla);
            }else if(token.equals(")")){
                //7. Tratar tokens que no son parentesis
                
                
                    while(!caracter.empty() && !caracter.peek().equals("(")){
                        guardar();
                    }//while
                    caracter.pop();
            }else{
                if(!token.equals(" (") && !caracter.empty()){
                    String exa = (String) caracter.peek();
                    while(!exa.equals(" (") && caracter.empty() && aritmeticos.indexOf(exa) >= aritmeticos.indexOf(token)){
                        guardar();
                        if (!caracter.empty()) {
                            exa= (String) caracter.peek();
                            
                        }//IF !caracter.empty
                    }//While !exa
                }//if-token
                caracter.push(token);//Guardar el token
            
            }//if else
            //8. guardar el token
        }//whiletokenizer-hasmoreTokenizer
        while(!caracter.empty()){
            if (caracter.peek().equals(" (")) {//El caracter tiene simbolos de apertura
                caracter.pop();
                
            }else{
                guardar(); //Aqui se insertan los operadores
                raiz = (Nodo) ArbolNodo.peek();
                
            }//if
        }//While !caracter.empty
            return raiz;
    }//crear
}//FIN CLASE
