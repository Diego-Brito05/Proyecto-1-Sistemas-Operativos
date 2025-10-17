/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EstructuraDeDatos;

import java.util.concurrent.atomic.AtomicInteger;

public class Proceso implements Comparable<Proceso> {
    private static final AtomicInteger nextId = new AtomicInteger(0);

    // --- Datos de Identificación y Estado (Sin cambios) ---
    private final int id;
    private String nombre;
    private EstadoProceso estado;
    private int programCounter;
    private int memoryAddressRegister;
    private int totalInstrucciones;
    private int instruccionesEjecutadas;
    private TipoProceso tipoProceso;
    private int ciclosParaExcepcion;
    private int ciclosExcepcionCompletada;
    private int ciclosEsperaIO;
    private int prioridad;
    private int quantumRestante; // Este ya lo tenías, ¡perfecto para Round Robin!

    // --- CAMPOS PARA MÉTRICAS Y PLANIFICACIÓN AVANZADA ---
    /**
     * Almacena el ciclo de reloj exacto en el que el proceso entra por primera vez
     * a la cola de listos.
     */
    private long tiempoLlegada;

    /**
     * Acumula el número total de ciclos de reloj que el proceso ha pasado en el estado LISTO.
     * Es la métrica clave para HRRN y para el "tiempo de espera promedio".
     */
    private long tiempoEsperaTotal;

    /**
     * Acumula el número total de ciclos de reloj que el proceso ha pasado en el estado EJECUCION.
     */
    private long tiempoServicioAcumulado;
    
    /**
     * Almacena el nivel de la cola en la que se encuentra el proceso.
     * para el planificador MLFQ.
     */
    private int nivelMLFQ;


    // --- CONSTRUCTORES  ---
    public Proceso(String nombre, int totalInstrucciones, TipoProceso tipoProceso,
                   int ciclosParaExcepcion, int ciclosExcepcionCompletada, int prioridad) {
        this.id = nextId.getAndIncrement();
        this.nombre = nombre;
        this.estado = EstadoProceso.NUEVO;
        this.programCounter = 0;
        this.memoryAddressRegister = 0;
        this.totalInstrucciones = totalInstrucciones;
        this.instruccionesEjecutadas = 0;
        this.tipoProceso = tipoProceso;
        this.ciclosParaExcepcion = ciclosParaExcepcion;
        this.ciclosExcepcionCompletada = ciclosExcepcionCompletada;
        this.ciclosEsperaIO = 0;
        this.prioridad = prioridad;
        this.quantumRestante = 0;

        // Inicialización de los nuevos campos
        this.tiempoLlegada = -1; // -1 indica que aún no ha llegado a la cola de listos
        this.tiempoEsperaTotal = 0;
        this.tiempoServicioAcumulado = 0;
        this.nivelMLFQ = 0; // Por defecto, todos los procesos empiezan en el nivel más alto (0)
    }

    // Los otros constructores llaman al principal, por lo que se inicializan automáticamente.
    // ... (constructores existentes sin cambios) ...
    public Proceso(String nombre, int totalInstrucciones, TipoProceso tipoProceso, int ciclosParaExcepcion, int ciclosExcepcionCompletada) {
        this(nombre, totalInstrucciones, tipoProceso, ciclosParaExcepcion, ciclosExcepcionCompletada, 0);
    }
    public Proceso(String nombre, int totalInstrucciones, TipoProceso tipoProceso, int prioridad) { 
        this(nombre, totalInstrucciones, tipoProceso, -1, -1, prioridad);
        if (tipoProceso == TipoProceso.IO_BOUND) {
            throw new IllegalArgumentException("Un proceso I/O-bound debe especificar ciclos para excepción y completado.");
        }
    }
    public Proceso(String nombre, int totalInstrucciones, TipoProceso tipoProceso) {
        this(nombre, totalInstrucciones, tipoProceso, 0);
    }


    // --- MÉTODOS DE GESTIÓN  ---

    public void ejecutarInstruccion() {
        if (instruccionesEjecutadas < totalInstrucciones) {
            instruccionesEjecutadas++;
            programCounter++;
            memoryAddressRegister++;
            // Cada vez que se ejecuta, incrementamos su tiempo de servicio
            this.incrementarTiempoServicio(); 
        }
    }

    // --- MÉTODOS PARA GESTIÓN DE TIEMPO Y PLANIFICACIÓN ---

    /**
     * Este método debe ser llamado por el Sistema Operativo en cada ciclo de reloj
     * para CADA proceso que se encuentre en la cola de LISTOS.
     */
    public void incrementarTiempoEspera() {
        this.tiempoEsperaTotal++;
    }

    /**
     * Este método es llamado por ejecutarInstruccion() cada vez que el proceso
     * consume un ciclo de CPU.
     */
    private void incrementarTiempoServicio() {
        this.tiempoServicioAcumulado++;
    }

    /**
     * Calcula dinámicamente las instrucciones que faltan por ejecutar.
     * Esencial para el algoritmo SRT (Shortest Remaining Time).
     * @return El número de instrucciones restantes.
     */
    public int getRafagaRestante() {
        return totalInstrucciones - instruccionesEjecutadas;
    }
    
    /**
     * Calcula el tiempo de retorno (Turnaround Time) del proceso.
     * Solo tiene sentido llamarlo cuando el proceso ha terminado.
     * @param cicloTerminacion El ciclo de reloj actual en el que el proceso termina.
     * @return El número total de ciclos desde que llegó hasta que terminó.
     */
    public long getTiempoRetorno(long cicloTerminacion) {
        if (this.tiempoLlegada == -1) return -1; // Error, nunca llegó
        return cicloTerminacion - this.tiempoLlegada;
    }


    // --- GETTERS Y SETTERS  ---

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public EstadoProceso getEstado() { return estado; }
    public void setEstado(EstadoProceso estado) { this.estado = estado; }
    public int getProgramCounter() { return programCounter; }
    public void setProgramCounter(int programCounter) { this.programCounter = programCounter; }
    public int getMemoryAddressRegister() { return memoryAddressRegister; }
    public void setMemoryAddressRegister(int memoryAddressRegister) { this.memoryAddressRegister = memoryAddressRegister; }
    public int getTotalInstrucciones() { return totalInstrucciones; }
    public int getInstruccionesEjecutadas() { return instruccionesEjecutadas; }
    public boolean haTerminado() { return instruccionesEjecutadas >= totalInstrucciones; }
    public boolean esIOBound() { return this.tipoProceso == TipoProceso.IO_BOUND; }
    public boolean necesitaExcepcionIO() { return esIOBound() && ciclosParaExcepcion > 0 && (instruccionesEjecutadas > 0 && instruccionesEjecutadas % ciclosParaExcepcion == 0); }
    public void decrementarCicloEsperaIO() { if (ciclosEsperaIO > 0) ciclosEsperaIO--; }
    public int getCiclosParaExcepcion() { return ciclosParaExcepcion; }
    public int getCiclosExcepcionCompletada() { return ciclosExcepcionCompletada; }
    public int getCiclosEsperaIO() { return ciclosEsperaIO; }
    public void setCiclosEsperaIO(int ciclosEsperaIO) { this.ciclosEsperaIO = ciclosEsperaIO; }
    public int getPrioridad() { return prioridad; }
    public void setPrioridad(int prioridad) { this.prioridad = prioridad; }
    public int getQuantumRestante() { return quantumRestante; }
    public void setQuantumRestante(int quantumRestante) { this.quantumRestante = quantumRestante; }

   
    public long getTiempoLlegada() { return tiempoLlegada; }
    public void setTiempoLlegada(long tiempoLlegada) { this.tiempoLlegada = tiempoLlegada; }
    public long getTiempoEsperaTotal() { return tiempoEsperaTotal; }
    public long getTiempoServicioAcumulado() { return tiempoServicioAcumulado; }
    public int getNivelMLFQ() { return nivelMLFQ; }
    public void setNivelMLFQ(int nivelMLFQ) { this.nivelMLFQ = nivelMLFQ; }


    @Override
    public int compareTo(Proceso otro) {
        return Integer.compare(this.id, otro.id);
    }
    @Override
    public String toString() {
        return "Proceso{" + "id=" + id + ", nombre='" + nombre + '\'' + ", estado=" + estado + ", PC=" + programCounter + ", instrucEjec=" + instruccionesEjecutadas + "/" + totalInstrucciones + '}';
    }
    public enum EstadoProceso { NUEVO, LISTO, EJECUCION, BLOQUEADO, TERMINADO, SUSPENDIDO_LISTO, SUSPENDIDO_BLOQUEADO }
    public enum TipoProceso { CPU_BOUND, IO_BOUND }
}