/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Planificador;
import EstructuraDeDatos.Proceso;


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

    /**
     * Añade un proceso a las colas de listos.
     * @param proceso El proceso a añadir.
     */
    void agregarProceso(Proceso proceso);

    /**
     * Selecciona el siguiente proceso a ejecutar según la política,
     * eliminándolo de la(s) cola(s) de listos.
     * @return El proceso seleccionado, o null si no hay ninguno.
     */
    Proceso getSiguienteProceso();
    
    /**
     * Devuelve el nombre del algoritmo para mostrar en la GUI.
     * @return String con el nombre de la estrategia.
     */
    String getNombre();
}