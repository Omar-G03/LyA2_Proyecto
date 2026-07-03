package arbolE;


/**
 *
 * @author Omar
 * CLASE PARA ARMAR EL ARBOL
 * NOMBRE
 * Parte q. analisis sintactivo
 * parte 2. Analisis Semantiico
 * Parte 3 Codigo intermedi
 * Parte 4 Codigo Objeto
 */
public class Nodo {
    //Atributos
    private String dato;
    private Nodo padre;
    private Nodo izquierdo;
    private Nodo derecho;
    private String codigoIntermedio;
    private String lugar;

    public Nodo(String dato) {
        this.dato = dato;
    }

    public Nodo(Nodo derecho,String dato,  Nodo izquierdo) {
        this.dato = dato;
        this.padre = null;
        this.izquierdo = izquierdo;
        this.derecho = derecho;
        this.codigoIntermedio = "";
        this.lugar = "";
    }

    public String getDato() {
        return dato;
    }

    public void setDato(String dato) {
        this.dato = dato;
    }

    public Nodo getPadre() {
        return padre;
    }

    public void setPadre(Nodo padre) {
        this.padre = padre;
    }

    public Nodo getIzquierdo() {
        return izquierdo;
    }

    public void setIzquierdo(Nodo izquierdo) {
        this.izquierdo = izquierdo;
    }

    public Nodo getDerecho() {
        return derecho;
    }

    public void setDerecho(Nodo derecho) {
        this.derecho = derecho;
    }

    public String getCodigoIntermedio() {
        return codigoIntermedio;
    }

    public void setCodigoIntermedio(String codigoIntermedio) {
        this.codigoIntermedio = codigoIntermedio;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }
    
    
}
