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
        Proceso procesoConMenorTiempo = encontrarProcesoConMenorTiempo();
        if (procesoConMenorTiempo != null) {
            listaListos.eliminar(procesoConMenorTiempo);
        }
        return procesoConMenorTiempo;
    }
    
    // --- NUEVOS MÉTODOS IMPLEMENTADOS ---

    @Override
    public Proceso peekSiguienteProceso() {
        return encontrarProcesoConMenorTiempo();
    }

    @Override
    public int getNumeroProcesosListos() {
        return listaListos.getTamano();
    }

    @Override
    public Object[] getProcesosListosComoArray() {
        return listaListos.obtenerComoArray();
    }

    // --- MÉTODO AUXILIAR ---

    private Proceso encontrarProcesoConMenorTiempo() {
        if (listaListos.estaVacia()) {
            return null;
        }
        
        Object[] procesosArray = listaListos.obtenerComoArray();
        if (procesosArray.length == 0) return null;
        
        Proceso procesoConMenorTiempo = (Proceso) procesosArray[0];

        for (int i = 1; i < procesosArray.length; i++) {
            Proceso pActual = (Proceso) procesosArray[i];
            if (pActual.getRafagaRestante() < procesoConMenorTiempo.getRafagaRestante()) {
                procesoConMenorTiempo = pActual;
            }
        }
        return procesoConMenorTiempo;
    }

    @Override
    public String getNombre() {
        return "SRT (Shortest Remaining Time)";
    }
}