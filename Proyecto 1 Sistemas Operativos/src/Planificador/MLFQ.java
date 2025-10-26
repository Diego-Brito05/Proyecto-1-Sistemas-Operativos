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
    private final int numNiveles = 3;
    private int mediumCounter;
    private int lowCounter;

    @SuppressWarnings("unchecked")
    public MLFQ() {
        this.colasDePrioridad = (Cola<Proceso>[]) new Cola[numNiveles];
        for (int i = 0; i < numNiveles; i++) {
            colasDePrioridad[i] = new Cola<>();
        }
        this.mediumCounter = 0;
        this.lowCounter = 0;
    }

    @Override
    public void agregarProceso(Proceso proceso) {
        colasDePrioridad[0].encolar(proceso);
    }
    
    public void degradarProceso(Proceso proceso, int nivelActual) {
        int siguienteNivel = Math.min(nivelActual + 1, numNiveles - 1);
        colasDePrioridad[siguienteNivel].encolar(proceso);
    }

    @Override
    public Proceso getSiguienteProceso() {
        for (int i = 0; i < numNiveles; i++) {
            if (!colasDePrioridad[i].estaVacia()) {
                return colasDePrioridad[i].desencolar();
            }
        }
        return null;
    }

    // --- NUEVOS MÉTODOS IMPLEMENTADOS ---

    @Override
    public Proceso peekSiguienteProceso() {
        if (mediumCounter >= 4) {
            // El proceso con prioridad media que esté al frente se ejecutará
            mediumCounter = 0;
            if (!colasDePrioridad[2].estaVacia()){
                lowCounter++;
            }
            return colasDePrioridad[1].verFrente();
        } else if (lowCounter >= 7) {
            // El proceso con prioridad baja que esté al frente se ejecutará
            lowCounter = 0;
            if (!colasDePrioridad[1].estaVacia()) {
                mediumCounter++;
            }
            return colasDePrioridad[2].verFrente();
        } else {
            if (!colasDePrioridad[1].estaVacia()) {
                mediumCounter++;
            }
            if (!colasDePrioridad[2].estaVacia()){
                lowCounter++;
            }
            for (int i = 0; i < numNiveles; i++) {
            if (!colasDePrioridad[i].estaVacia()) {
                // Usamos verFrente() para no eliminarlo
                return colasDePrioridad[i].verFrente();
            }
            }
        }
        return null;
    }

    @Override
    public int getNumeroProcesosListos() {
        int total = 0;
        for (int i = 0; i < numNiveles; i++) {
            total += colasDePrioridad[i].getTamano();
        }
        return total;
    }

    @Override
    public Object[] getProcesosListosComoArray() {
        // Necesitamos combinar los arrays de todas las colas
        Object[] resultado = new Object[getNumeroProcesosListos()];
        int indiceActual = 0;
        
        for (int i = 0; i < numNiveles; i++) {
            Object[] arrayCola = colasDePrioridad[i].toArray(new Proceso[0]);
            for (Object proceso : arrayCola) {
                resultado[indiceActual++] = proceso;
            }
        }
        return resultado;
    }

    @Override
    public String getNombre() {
        return "MLFQ (Multi-level Feedback Queue)";
    }
}
