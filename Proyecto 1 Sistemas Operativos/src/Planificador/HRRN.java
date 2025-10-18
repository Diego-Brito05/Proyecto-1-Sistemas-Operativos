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
 * Highest Response Ratio Next
 */
public class HRRN implements EstrategiaPlanificacion {
    private final ListaEnlazadaSincronizada<Proceso> listaListos;

    public HRRN() {
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
        
        Proceso mejorProceso = null;
        double maxRatio = -1.0;

        for (Object obj : procesosArray) {
            Proceso p = (Proceso) obj;
            
            long tiempoEspera = p.getTiempoEsperaTotal();
            long tiempoServicio = p.getTotalInstrucciones();
            
            if (tiempoServicio == 0) continue; // Evitar división por cero
            
            double ratio = (double) (tiempoEspera + tiempoServicio) / tiempoServicio;
            
            if (ratio > maxRatio) {
                maxRatio = ratio;
                mejorProceso = p;
            }
        }
        
        if (mejorProceso != null) {
            listaListos.eliminar(mejorProceso);
        }
        
        return mejorProceso;
    }

    @Override
    public String getNombre() {
        return "HRRN (Highest Response Ratio Next)";
    }
}
