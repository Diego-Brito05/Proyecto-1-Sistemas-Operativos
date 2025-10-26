/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EstructuraDeDatos;

/**
 *
 * @author Diego
 * Implementación de una lista enlazada, diseñada para ser
 * thread-safe usando bloques 'synchronized',
 * 
 * @param <T> El tipo de dato a almacenar en la lista.
 */

public class ListaEnlazadaSincronizada<T> {

    private Nodo<T> cabeza;
    private int tamano;

    public ListaEnlazadaSincronizada() {
        this.cabeza = null;
        this.tamano = 0;
    }

    /**
     * Agrega un elemento al final de la lista de forma sincronizada.
     * @param dato El dato a agregar.
     */
    public void agregar(T dato) {
        synchronized (this) {
            Nodo<T> nuevoNodo = new Nodo<>(dato);
            if (estaVacia()) {
                cabeza = nuevoNodo;
            } else {
                Nodo<T> actual = cabeza;
                while (actual.getSiguiente() != null) {
                    actual = actual.getSiguiente();
                }
                actual.setSiguiente(nuevoNodo);
            }
            tamano++;
        }
    }

    /**
     * Elimina la primera ocurrencia de un elemento específico de la lista de forma sincronizada.
     * @param dato El dato a eliminar.
     * @return true si el elemento fue encontrado y eliminado, false en caso contrario.
     */
    public boolean eliminar(T dato) {
        synchronized (this) {
            if (estaVacia()) {
                return false;
            }

            if (cabeza.getDato().equals(dato)) {
                cabeza = cabeza.getSiguiente();
                tamano--;
                return true;
            }

            Nodo<T> previo = cabeza;
            Nodo<T> actual = cabeza.getSiguiente();
            while (actual != null) {
                if (actual.getDato().equals(dato)) {
                    previo.setSiguiente(actual.getSiguiente());
                    tamano--;
                    return true;
                }
                previo = actual;
                actual = actual.getSiguiente();
            }
            return false;
        }
    }

    /**
     * Devuelve una "instantánea" de los datos de la lista en un array de Objects.
     * El código que llama a este método es responsable de castear los objetos
     * al tipo correcto. Se devuelve un array para evitar el uso de librerías de colecciones.
     * @return un nuevo Object[] con todos los elementos de esta lista.
     */
    public Object[] obtenerComoArray() {
        synchronized (this) {
            Object[] arrayCopia = new Object[tamano];
            Nodo<T> actual = cabeza;
            int i = 0;
            while (actual != null) {
                arrayCopia[i] = actual.getDato();
                i++;
                actual = actual.getSiguiente();
            }
            return arrayCopia;
        }
    }
    
    public boolean estaVacia() {
        synchronized (this) {
            return cabeza == null;
        }
    }

    public int getTamano() {
        synchronized (this) {
            return tamano;
        }
    }
}
