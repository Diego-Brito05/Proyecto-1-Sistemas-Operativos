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
 */
public class RoundRobin implements EstrategiaPlanificacion {
    private final Cola<Proceso> colaListos;

    public RoundRobin() {
        this.colaListos = new Cola<>();
    }

    @Override
    public void agregarProceso(Proceso proceso) {
        // Un proceso nuevo, o uno que agotó su quantum, va al final de la cola.
        colaListos.encolar(proceso);
    }

    @Override
    public Proceso getSiguienteProceso() {
        return colaListos.desencolar();
    }

    @Override
    public String getNombre() {
        return "Round Robin (RR)";
    }
}

