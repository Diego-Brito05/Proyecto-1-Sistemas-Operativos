/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EstructuraDeDatos;

/**
 *
 * @author Diego
 */
import java.util.concurrent.Semaphore;

public class Proceso implements Runnable {
    public enum EstadoProceso {
        NUEVO, LISTO, EJECUCION, BLOQUEADO, TERMINADO, SUSPENDIDO_LISTO, SUSPENDIDO_BLOQUEADO
    }

    private static int nextProcessId = 0; 
    private int id;
    private String nombre;
    private EstadoProceso estado;
    private int programCounter; // Cuántas instrucciones se han ejecutado 
    private int mar; // Memory Address Register 
    private int totalInstrucciones; 
    private TipoProceso tipoProceso; // CPU_BOUND o IO_BOUND

    // Para procesos I/O bound
    private int ciclosParaExcepcion; // Cuántas "instrucciones" CPU antes de una solicitud de E/S
    private int ciclosParaSatisfacerExcepcion; // Cuántos ciclos se necesitan para completar la E/S
    private int ciclosDesdeUltimaExcepcion; 
    private int ciclosRestantesIO; // Contador para la satisfacción de la E/S actual (cuando está bloqueado)

    // Métricas para el rendimiento del proceso
    private long tiempoLlegada; // Tiempo en que el proceso llega al sistema (en ms o ciclos simulados)
    private long tiempoCompletado; // Tiempo en que el proceso termina
    private long tiempoEnCPU; // Tiempo total que el proceso ha estado en CPU
    private long tiempoEsperando; // Tiempo total que el proceso ha estado en colas de listo
    private long tiempoBloqueado; // Tiempo total que el proceso ha estado en colas de bloqueado
    private long tiempoRespuesta; // Tiempo desde llegada hasta primera vez en CPU
    private boolean primeraVezEnCPU; // Flag para calcular tiempoRespuesta

    // Semaforo para controlar la ejecución del hilo del proceso por parte del Scheduler
    private final Semaphore semaforoEjecucion; // Permiso para ejecutar (0 = pausado, 1 = reanudado)

    public enum TipoProceso {
        CPU_BOUND, IO_BOUND
    }

    public Proceso(String nombre, int totalInstrucciones, TipoProceso tipo, int ciclosExcepcion, int ciclosSatisfacer) {
        this.id = nextProcessId++;
        this.nombre = nombre;
        this.estado = EstadoProceso.NUEVO;
        this.programCounter = 0;
        this.mar = 0; // MAR se actualizará junto con PC
        this.totalInstrucciones = totalInstrucciones;
        this.tipoProceso = tipo;

        // Inicialización para I/O bound
        this.ciclosParaExcepcion = ciclosExcepcion;
        this.ciclosParaSatisfacerExcepcion = ciclosSatisfacer;
        this.ciclosDesdeUltimaExcepcion = 0; // Inicia en 0, se incrementa con cada instrucción CPU
        this.ciclosRestantesIO = ciclosSatisfacer; // Se usará cuando el proceso esté BLOQUEADO

        // Inicialización de métricas
        this.tiempoLlegada = 0;
        this.tiempoCompletado = 0;
        this.tiempoEnCPU = 0;
        this.tiempoEsperando = 0;
        this.tiempoBloqueado = 0;
        this.tiempoRespuesta = -1;
        this.primeraVezEnCPU = true;

        // El semáforo inicia en 0 (adquirido), para que el hilo esté "pausado" hasta que el Scheduler lo libere.
        this.semaforoEjecucion = new Semaphore(0);
    }

    // --- Métodos de Getters y Setters ---
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public EstadoProceso getEstado() { return estado; }
    public void setEstado(EstadoProceso estado) { this.estado = estado; }
    public int getProgramCounter() { return programCounter; } // Instrucciones ejecutadas
    public void incrementarProgramCounter() { this.programCounter++; }
    public int getMar() { return mar; }
    public void incrementarMar() { this.mar++; } // Para este proyecto, MAR sigue a PC

    public int getTotalInstrucciones() { return totalInstrucciones; }
    public TipoProceso getTipoProceso() { return tipoProceso; }

    // Métodos para I/O bound
    public int getCiclosParaExcepcion() { return ciclosParaExcepcion; }
    public int getCiclosParaSatisfacerExcepcion() { return ciclosParaSatisfacerExcepcion; }
    public int getCiclosDesdeUltimaExcepcion() { return ciclosDesdeUltimaExcepcion; }
    public void incrementarCiclosDesdeUltimaExcepcion() { this.ciclosDesdeUltimaExcepcion++; }
    public void resetCiclosDesdeUltimaExcepcion() { this.ciclosDesdeUltimaExcepcion = 0; } // Resetea al solicitar I/O

    public int getCiclosRestantesIO() { return ciclosRestantesIO; }
    public void decrementarCiclosRestantesIO() { this.ciclosRestantesIO--; }
    public void resetCiclosRestantesIO() { this.ciclosRestantesIO = ciclosParaSatisfacerExcepcion; } // Resetea al iniciar I/O

    public boolean haTerminado() { return programCounter >= totalInstrucciones; }
    public int getInstruccionesRestantes() { return totalInstrucciones - programCounter; }

    // Getters y Setters para las métricas
    public long getTiempoLlegada() { return tiempoLlegada; }
    public void setTiempoLlegada(long tiempoLlegada) { this.tiempoLlegada = tiempoLlegada; }
    public long getTiempoCompletado() { return tiempoCompletado; }
    public void setTiempoCompletado(long tiempoCompletado) { this.tiempoCompletado = tiempoCompletado; }
    public long getTiempoEnCPU() { return tiempoEnCPU; }
    public void addTiempoEnCPU(long tiempo) { this.tiempoEnCPU += tiempo; }
    public long getTiempoEsperando() { return tiempoEsperando; }
    public void addTiempoEsperando(long tiempo) { this.tiempoEsperando += tiempo; }
    public long getTiempoBloqueado() { return tiempoBloqueado; }
    public void addTiempoBloqueado(long tiempo) { this.tiempoBloqueado += tiempo; }
    public long getTiempoRespuesta() { return tiempoRespuesta; }
    public void setTiempoRespuesta(long tiempoRespuesta) { this.tiempoRespuesta = tiempoRespuesta; }
    public boolean isPrimeraVezEnCPU() { return primeraVezEnCPU; }
    public void setPrimeraVezEnCPU(boolean primeraVezEnCPU) { this.primeraVezEnCPU = primeraVezEnCPU; }

    // --- Métodos de sincronización con el Scheduler ---
    public void pausarEjecucion() {
        // Si el semáforo tiene permisos, los toma para "pausar" el proceso.
        // Si ya está en 0, no hace nada, el hilo ya está esperando.
        if (semaforoEjecucion.availablePermits() > 0) {
            semaforoEjecucion.acquireUninterruptibly(); // Toma el permiso, no se interrumpe
        }
    }

    public void reanudarEjecucion() {
        // Libera un permiso para que el hilo pueda continuar (si está esperando)
        if (semaforoEjecucion.availablePermits() == 0) {
            semaforoEjecucion.release();
        }
    }

    @Override
    public String toString() {
        return "Proceso [ID: " + id + ", Nombre: " + nombre + ", Estado: " + estado +
               ", PC: " + programCounter + "/" + totalInstrucciones +
               ", Tipo: " + tipoProceso + "]";
    }

    // run method for Threads - A proceso *is* a Thread
    @Override
    public void run() {
        // Este bucle representa la vida del proceso como un hilo.
        // El hilo de cada proceso *espera* hasta que el Scheduler le dé permiso para "ejecutar" un ciclo.
        while (estado != EstadoProceso.TERMINADO) {
            try {
                // El proceso "espera" por un permiso del semáforo.
                // El Scheduler llamará a reanudarEjecucion() para darle el permiso.
                semaforoEjecucion.acquire(); // Bloquea hasta que el Scheduler le dé un permiso
                
                
                // la lógica de *ejecución real* (incrementar PC, etc.)
                // NO ocurre aquí. Ocurre en el hilo principal del Scheduler/SistemaOperativo.
               
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Proceso " + nombre + " interrumpido mientras esperaba permiso de ejecución.");
                return; // Salir del bucle si es interrumpido
            }
        }
        System.out.println("Proceso " + nombre + " ha terminado su ejecución simulada.");
    }
}
