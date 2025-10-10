/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EstructuraDeDatos;

import java.util.concurrent.atomic.AtomicInteger; // Para generar IDs únicos de forma segura

public class Proceso implements Comparable<Proceso> { // Implementa Comparable para facilitar el ordenamiento en colas
    private static final AtomicInteger nextId = new AtomicInteger(0); // Generador de IDs único para todos los procesos

    private final int id;
    private String nombre;
    private EstadoProceso estado; // Enum para los estados del proceso
    private int programCounter; // Dirección de la siguiente instrucción a ejecutar
    private int memoryAddressRegister; // Dirección de memoria actual (para simular el acceso a datos)
    private int totalInstrucciones; // Longitud total del programa
    private int instruccionesEjecutadas; // Cuántas instrucciones se han ejecutado hasta ahora
    private TipoProceso tipoProceso; // CPU-bound o I/O-bound
    private int ciclosParaExcepcion; // Para I/O-bound: cuántos ciclos se necesitan para generar una excepción
    private int ciclosExcepcionCompletada; // Para I/O-bound: cuántos ciclos para satisfacer la excepción
    private int ciclosEsperaIO; // Contador interno para la espera de I/O
    private int prioridad; // Usado para algunos algoritmos de planificación (ej. HRRN, MLFQ)
    private int quantumRestante; // Usado en Round Robin

   // --- CONSTRUCTORES ---

    // Constructor completo con prioridad
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
        this.prioridad = prioridad; // si no se especifica sera igual a 0
        this.quantumRestante = 0;
    }

    // Constructor para procesos I/O-bound sin especificar prioridad (prioridad por defecto 0)
    public Proceso(String nombre, int totalInstrucciones, TipoProceso tipoProceso,
                   int ciclosParaExcepcion, int ciclosExcepcionCompletada) {
        this(nombre, totalInstrucciones, tipoProceso, ciclosParaExcepcion, ciclosExcepcionCompletada, 0); // Llama al constructor completo con prioridad 0
    }

    // Constructor para CPU-bound con prioridad
    public Proceso(String nombre, int totalInstrucciones, TipoProceso tipoProceso, int prioridad) { 
        this(nombre, totalInstrucciones, tipoProceso, -1, -1, prioridad); // Llama al constructor completo
        if (tipoProceso == TipoProceso.IO_BOUND) {
            throw new IllegalArgumentException("Un proceso I/O-bound debe especificar ciclos para excepción y completado.");
        }
    }

    // Constructor para CPU-bound sin especificar prioridad (prioridad por defecto 0)
    public Proceso(String nombre, int totalInstrucciones, TipoProceso tipoProceso) {
        this(nombre, totalInstrucciones, tipoProceso, 0); // Llama al constructor de CPU-bound con prioridad 0
    }


    // --- Métodos de gestión de proceso ---

    /**
     * Simula la ejecución de una instrucción para este proceso.
     * Incrementa el contador de programa, el registro de dirección de memoria
     * y el contador de instrucciones ejecutadas.
     */
    public void ejecutarInstruccion() {
        if (instruccionesEjecutadas < totalInstrucciones) {
            instruccionesEjecutadas++;
            programCounter++;
            memoryAddressRegister++; // Suponiendo incremento lineal del MAR por simplicidad
        }
    }

    /**
     * Verifica si el proceso ha completado todas sus instrucciones.
     * @return true si el proceso ha terminado, false en caso contrario.
     */
    public boolean haTerminado() {
        return instruccionesEjecutadas >= totalInstrucciones;
    }

    /**
     * Verifica si el proceso es I/O-bound.
     * @return true si es I/O-bound, false si es CPU-bound.
     */
    public boolean esIOBound() {
        return this.tipoProceso == TipoProceso.IO_BOUND;
    }

    /**
     * Determina si el proceso I/O-bound necesita generar una excepción de E/S
     * en el ciclo actual.
     * @return true si debe generar una excepción de E/S, false en caso contrario.
     */
    public boolean necesitaExcepcionIO() {
        // Solo aplica si es I/O-bound y se han definido ciclos para excepción
        return esIOBound() && ciclosParaExcepcion != -1 &&
               // Se activa cuando el número de instrucciones ejecutadas es un múltiplo de ciclosParaExcepcion
               // y no es la instrucción 0 (para evitar una excepción al inicio)
               (instruccionesEjecutadas > 0 && instruccionesEjecutadas % ciclosParaExcepcion == 0);
    }

    /**
     * Decrementa el contador de ciclos de espera de E/S.
     */
    public void decrementarCicloEsperaIO() {
        if (ciclosEsperaIO > 0) {
            ciclosEsperaIO--;
        }
    }

    // --- Getters y Setters ---

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
    public TipoProceso getTipoProceso() { return tipoProceso; }
    public int getCiclosParaExcepcion() { return ciclosParaExcepcion; }
    public int getCiclosExcepcionCompletada() { return ciclosExcepcionCompletada; }
    public int getCiclosEsperaIO() { return ciclosEsperaIO; }
    public void setCiclosEsperaIO(int ciclosEsperaIO) { this.ciclosEsperaIO = ciclosEsperaIO; }
    public int getPrioridad() { return prioridad; }
    public void setPrioridad(int prioridad) { this.prioridad = prioridad; }
    public int getQuantumRestante() { return quantumRestante; }
    public void setQuantumRestante(int quantumRestante) { this.quantumRestante = quantumRestante; }


    // Implementación de Comparable para ordenamiento por ID por defecto (o según necesites en un planificador)
    @Override
    public int compareTo(Proceso otro) {
        return Integer.compare(this.id, otro.id);
    }

    @Override
    public String toString() {
        return "Proceso{" +
               "id=" + id +
               ", nombre='" + nombre + '\'' +
               ", estado=" + estado +
               ", PC=" + programCounter +
               ", MAR=" + memoryAddressRegister +
               ", instrucEjec=" + instruccionesEjecutadas + "/" + totalInstrucciones +
               ", tipo=" + tipoProceso +
               '}';
    }

    // --- Enums auxiliares ---

    /**
     * Define los posibles estados de un proceso en el simulador.
     */
    public enum EstadoProceso {
        NUEVO, LISTO, EJECUCION, BLOQUEADO, TERMINADO, SUSPENDIDO_LISTO, SUSPENDIDO_BLOQUEADO
    }

    /**
     * Define el tipo de consumo de recursos principal de un proceso.
     */
    public enum TipoProceso {
        CPU_BOUND, IO_BOUND
    }
}