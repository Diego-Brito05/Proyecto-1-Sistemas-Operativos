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
 * Shortest Process Next
 */
public class SPN implements EstrategiaPlanificacion {
    private final ListaEnlazadaSincronizada<Proceso> listaListos;

    public SPN() {
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
        
        Proceso procesoMasCorto = (Proceso) procesosArray[0];
        
        for (int i = 1; i < procesosArray.length; i++) {
            Proceso pActual = (Proceso) procesosArray[i];
            if (pActual.getTotalInstrucciones() < procesoMasCorto.getTotalInstrucciones()) {
                procesoMasCorto = pActual;
            }
        }

        listaListos.eliminar(procesoMasCorto);
        return procesoMasCorto;
    }

    @Override
    public String getNombre() {
        return "SPN (Shortest Process Next)";
    }
}
