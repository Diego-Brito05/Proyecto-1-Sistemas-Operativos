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
 * First-Come, First-Served
 */

public class FCFS implements EstrategiaPlanificacion {
    private final Cola<Proceso> colaListos;

    public FCFS() {
        this.colaListos = new Cola<>();
    }

    @Override
    public void agregarProceso(Proceso proceso) {
        colaListos.encolar(proceso);
    }

    @Override
    public Proceso getSiguienteProceso() {
        return colaListos.desencolar();
    }

    @Override
    public String getNombre() {
        return "FCFS (First-Come, First-Served)";
    }

    // --- NUEVOS MÉTODOS IMPLEMENTADOS ---

    @Override
    public Proceso peekSiguienteProceso() {
        // verFrente() nos permite ver el siguiente elemento sin quitarlo.
        return colaListos.verFrente();
    }

    @Override
    public int getNumeroProcesosListos() {
        return colaListos.getTamano();
    }

    @Override
    public Object[] getProcesosListosComoArray() {
        return colaListos.toArray(new Proceso[0]);
    }
}