/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EstructuraDeDatos;

/**
 *
 * @author Diego
 */
public class Cola<T> {
    private Nodo<T> frente;
    private Nodo<T> finalCola;
    private int tamano;

    public Cola() {
        this.frente = null;
        this.finalCola = null;
        this.tamano = 0;
    }

    public void encolar(T elemento) {
        Nodo<T> nuevoNodo = new Nodo<>(elemento);
        if (estaVacia()) {
            frente = nuevoNodo;
            finalCola = nuevoNodo;
        } else {
            finalCola.setSiguiente(nuevoNodo);
            finalCola = nuevoNodo;
        }
        tamano++;
    }

    public T desencolar() {
        if (estaVacia()) {
            return null;
        }
        T dato = frente.getDato();
        frente = frente.getSiguiente();
        if (frente == null) { // La cola se vació
            finalCola = null;
        }
        tamano--;
        return dato;
    }

    public T verFrente() {
        if (estaVacia()) {
            return null;
        }
        return frente.getDato();
    }

    public boolean estaVacia() {
        return frente == null;
    }

    public int getTamano() {
        return tamano;
    }

    // Método para obtener todos los elementos de la cola como un array (útil para visualización)
    public T[] toArray(T[] a) {
        if (tamano == 0) {
            return (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), 0);
        }

        if (a.length < tamano) {
            a = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), tamano);
        }

        int i = 0;
        Nodo<T> actual = frente;
        while (actual != null) {
            a[i++] = actual.getDato();
            actual = actual.getSiguiente();
        }

        // Si el array proporcionado era más grande, establece el resto a null
        if (a.length > tamano) {
            for (int j = tamano; j < a.length; j++) {
                a[j] = null;
            }
        }
        return a;
    }
}
