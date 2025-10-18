/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Planificador;


import EstructuraDeDatos.ListaEnlazadaSincronizada;
import EstructuraDeDatos.Proceso;

/**
 *
 * @author Diego
 * Shortest Remaining Time
 */
public class SRT implements EstrategiaPlanificacion {
    private final ListaEnlazadaSincronizada<Proceso> listaListos;

    public SRT() {
        this.listaListos = new ListaEnlazadaSincronizada<>();
    }

    @Override
    public void agregarProceso(Proceso proceso) {
        listaListos.agregar(proceso);
    }

    @Override
    public Proceso getSiguienteProceso() {
        if (listaListos.estaVacia()) {
            return null;
        }
        
        Object[] procesosArray = listaListos.obtenerComoArray();
        if (procesosArray.length == 0) return null;
        
        Proceso procesoConMenorTiempo = (Proceso) procesosArray[0];

        for (int i = 1; i < procesosArray.length; i++) {
            Proceso pActual = (Proceso) procesosArray[i];
            // La única diferencia con SPN es que se compara la ráfaga RESTANTE.
            if (pActual.getRafagaRestante() < procesoConMenorTiempo.getRafagaRestante()) {
                procesoConMenorTiempo = pActual;
            }
        }

        listaListos.eliminar(procesoConMenorTiempo);
        return procesoConMenorTiempo;
    }

    @Override
    public String getNombre() {
        return "SRT (Shortest Remaining Time)";
    }
}