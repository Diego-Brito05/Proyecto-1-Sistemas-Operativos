/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Simulador;


import java.util.concurrent.atomic.AtomicLong;
/**
 *
 * @author Diego
 * Gestiona el tiempo de la simulación.
 * Permite modificar la duración de un ciclo en tiempo de ejecución de forma segura.
 */
public class Clock {
     // Usamos volatile para garantizar que los cambios hechos por un hilo 
    // sean visibles inmediatamente para otro hilo .
    private volatile long duracionCicloMs;

    // Usamos AtomicLong para el contador de ciclos para garantizar operaciones atómicas
    // si múltiples hilos necesitaran consultarlo o modificarlo
    private final AtomicLong cicloActual;

    public Clock(long duracionInicialMs) {
        this.duracionCicloMs = duracionInicialMs;
        this.cicloActual = new AtomicLong(0);
    }

    /**
     * Avanza el reloj de la simulación en un ciclo.
     */
    public void tick() {
        cicloActual.incrementAndGet();
    }

    /**
     * Obtiene el número del ciclo actual de la simulación.
     * @return El ciclo actual.
     */
    public long getCicloActual() {
        return cicloActual.get();
    }

    /**
     * Obtiene la duración de un ciclo en milisegundos.
     * @return La duración del ciclo en ms.
     */
    public long getDuracionCicloMs() {
        return duracionCicloMs;
    }

    /**
     * Establece una nueva duración para el ciclo. Este cambio afectará
     * al siguiente ciclo que se ejecute.
     * @param duracionCicloMs La nueva duración en ms.
     */
    public void setDuracionCicloMs(long duracionCicloMs) {
        // Aseguramos que la duración no sea negativa.
        this.duracionCicloMs = Math.max(0, duracionCicloMs);
    }

    /**
     * Pausa el hilo actual por la duración de un ciclo de reloj.
     * Es el método que el bucle principal de la simulación usará para esperar.
     */
    public void esperar() {
        try {
            Thread.sleep(duracionCicloMs);
        } catch (InterruptedException e) {
            // Restaura el estado de interrupción para que el código superior pueda manejarlo.
            Thread.currentThread().interrupt();
            System.err.println("El hilo del reloj fue interrumpido.");
        }
    }
}

