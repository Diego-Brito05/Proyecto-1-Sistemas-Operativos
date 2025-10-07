/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EstructuraDeDatos;

/**
 *
 * @author Diego
 */
public class Semaforo {
    private int permisos; // Número actual de permisos disponibles
    private final int maxPermisos; // Número máximo de permisos (capacidad)
    private final Object bloqueo = new Object(); // Objeto para la sincronización (mutex interno)

    /**
     * Constructor para crear un semáforo con un número inicial y máximo de permisos.
     * Si los permisos iniciales son mayores que el máximo, se ajustan al máximo.
     *
     * @param initialPermits El número inicial de permisos.
     * @param maxPermisos     El número máximo de permisos que el semáforo puede tener.
     */
    public Semaforo(int initialPermits, int maxPermisos) {
        if (maxPermisos < 0 || initialPermits < 0) {
            throw new IllegalArgumentException("Los permisos iniciales y máximos no pueden ser negativos.");
        }
        if (initialPermits > maxPermisos) {
            System.err.println("Advertencia: Permisos iniciales (" + initialPermits + ") mayores que máximos (" + maxPermisos + "). Ajustando a máximos.");
            this.permisos = maxPermisos;
        } else {
            this.permisos = initialPermits;
        }
        this.maxPermisos = maxPermisos;
    }

    /**
     * Adquiere un permiso del semáforo.
     * Si no hay permisos disponibles, el hilo actual espera hasta que haya uno.
     *
     * @throws InterruptedException Si el hilo es interrumpido mientras espera.
     */
    public void adquirir() throws InterruptedException {
        synchronized (bloqueo) {
            while (permisos <= 0) { // Mientras no haya permisos disponibles
                bloqueo.wait(); // El hilo espera. Libera el bloqueo del 'bloqueo' objeto.
            }
            permisos--; // Cuando hay un permiso, lo toma.
        }
    }

    /**
     * Libera un permiso al semáforo.
     * Si hay hilos esperando por permisos, uno de ellos será notificado.
     *
     * @throws IllegalStateException Si se intenta liberar un permiso cuando ya se ha alcanzado el máximo (opcional, pero buena práctica).
     */
    public void liberar() {
        synchronized (bloqueo) {
            if (permisos < maxPermisos) { // Solo si no hemos alcanzado el máximo de permisos
                permisos++; // Libera un permiso.
                bloqueo.notifyAll(); // Notifica a *todos* los hilos que puedan estar esperando.
                                    
            } else {
                // Esto es una condición de error o un uso inesperado del semáforo.
               
                System.err.println("Advertencia: Se intentó liberar un permiso, pero el semáforo ya está en su máximo de " + maxPermisos + " permisos.");
            }
        }
    }

    /**
     * Obtiene el número actual de permisos disponibles.
     * @return El número de permisos.
     */
    public int getPermits() {
        synchronized (bloqueo) {
            return permisos;
        }
    }

   
}