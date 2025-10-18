/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Planificador;

import EstructuraDeDatos.Cola;
import EstructuraDeDatos.Proceso;

/**
 *
 * @author Diego
 * Multilevel Feedback Queue
 */
public class MLFQ implements EstrategiaPlanificacion {
    private final Cola<Proceso>[] colasDePrioridad;
    private final int numNiveles = 3; // Ejemplo: 3 niveles de prioridad

    @SuppressWarnings("unchecked")
    public MLFQ() {
        this.colasDePrioridad = (Cola<Proceso>[]) new Cola[numNiveles];
        for (int i = 0; i < numNiveles; i++) {
            colasDePrioridad[i] = new Cola<>();
        }
    }

    @Override
    public void agregarProceso(Proceso proceso) {
        // Los procesos nuevos o que vuelven de E/S entran a la cola de máxima prioridad (Q0)
        colasDePrioridad[0].encolar(proceso);
    }
    
    /**
     * Método específico para MLFQ. Mueve un proceso a una cola de menor prioridad.
     * El SO lo llamará cuando un proceso agote su quantum.
     * @param proceso El proceso a degradar.
     * @param nivelActual El nivel de la cola en el que estaba.
     */
    public void degradarProceso(Proceso proceso, int nivelActual) {
        int siguienteNivel = Math.min(nivelActual + 1, numNiveles - 1);
        colasDePrioridad[siguienteNivel].encolar(proceso);
    }

    @Override
    public Proceso getSiguienteProceso() {
        // Buscar desde la cola de mayor prioridad (índice 0) hacia abajo
        for (int i = 0; i < numNiveles; i++) {
            if (!colasDePrioridad[i].estaVacia()) {
                return colasDePrioridad[i].desencolar();
            }
        }
        return null; // No hay procesos en ninguna cola
    }

    @Override
    public String getNombre() {
        return "MLFQ (Multi-level Feedback Queue)";
    }
}