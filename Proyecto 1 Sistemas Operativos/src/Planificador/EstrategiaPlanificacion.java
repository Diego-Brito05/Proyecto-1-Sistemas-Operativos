/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Planificador;
import EstructuraDeDatos.Proceso;
import EstructuraDeDatos.Cola;


/**
 *
 * @author Diego
 */


/**
 * Interfaz común para todas las estrategias de planificación.
 * Define los métodos esenciales que el Sistema Operativo usará
 * para interactuar con el algoritmo de planificación actual.
 */
public interface EstrategiaPlanificacion {

    void agregarProceso(Proceso proceso);
    Proceso getSiguienteProceso();
    String getNombre();

    // --- NUEVOS MÉTODOS NECESARIOS ---

    /**
     * Devuelve el siguiente proceso que sería elegido, PERO SIN QUITARLO de la cola.
     * Esencial para que el planificador SRT pueda decidir si debe expropiar al proceso actual.
     * @return El próximo proceso, o null si no hay ninguno.
     */
    Proceso peekSiguienteProceso();
    
    Cola<Proceso> getListos();
    

    /**
     * Devuelve el número de procesos actualmente en estado LISTO.
     * Necesario para que el planificador de mediano plazo sepa si hay memoria disponible.
     * @return El conteo de procesos listos.
     */
    int getNumeroProcesosListos();
    
    /**
     * Devuelve todos los procesos en estado LISTO en un array.
     * Necesario para que el simulador pueda actualizar los tiempos de espera (HRRN).
     * @return Un array de Object con los procesos listos.
     */
    Object[] getProcesosListosComoArray();
}