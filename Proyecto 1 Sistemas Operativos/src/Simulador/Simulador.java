/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import EstructuraDeDatos.Cola;
import EstructuraDeDatos.Proceso;
import Planificador.*; // Importa todas las clases de planificación
import Simulador.Clock;

/**
 * Núcleo del Sistema Operativo que orquesta la simulación completa.
 * Gestiona los planificadores de largo, mediano y corto plazo.
 * Implementa Runnable para ejecutarse en su propio hilo.
 */
public class Simulador implements Runnable {

    // --- Constantes de Simulación ---
    /**
     * Límite simulado de memoria. Define cuántos procesos pueden estar en las colas
     * de Listos + Bloqueados al mismo tiempo. Si se supera, se activa la suspensión.
     */
    private static final int MEMORIA_MAXIMA_PROCESOS = 5;

    // --- Componentes del Sistema ---
    private final Clock clock;
    private EstrategiaPlanificacion planificador;
    private Proceso procesoEnCPU;

    // --- Colas de Procesos ---
    private final Cola<Proceso> colaNuevos;
    private final Cola<Proceso> colaBloqueados;
    private final Cola<Proceso> colaSuspendidosListos;
    private final Cola<Proceso> colaSuspendidosBloqueados;
    private final Cola<Proceso> colaTerminados;
    // La cola de LISTOS está dentro del 'planificador'

    // --- Estado de la Simulación ---
    private volatile boolean enEjecucion = false;
    private final Thread hiloSimulacion;
    
    // --- Parámetros Configurables ---
    private int quantum = 8; // Quantum para Round Robin y nivel superior de MLFQ
    private int quantumRestante;

    public Simulador() {
        this.clock = new Clock(1000);
        this.planificador = new FCFS(); // Inicia con FCFS por defecto
        
        this.colaNuevos = new Cola<>();
        this.colaBloqueados = new Cola<>();
        this.colaSuspendidosListos = new Cola<>();
        this.colaSuspendidosBloqueados = new Cola<>();
        this.colaTerminados = new Cola<>();

        this.procesoEnCPU = null;
        this.hiloSimulacion = new Thread(this);
    }

    // --- Control de la Simulación ---
    public synchronized void iniciar() {
        if (!enEjecucion) {
            this.enEjecucion = true;
            this.hiloSimulacion.start();
        }
    }

    public synchronized void detener() {
        this.enEjecucion = false;
    }

    public void agregarNuevoProceso(Proceso proceso) {
        synchronized (colaNuevos) {
            colaNuevos.encolar(proceso);
        }
    }
    
    // --- El Bucle Principal del Kernel ---
    @Override
    public void run() {
        while (enEjecucion) {
            clock.tick();
            System.out.println("\n----- CICLO #" + clock.getCicloActual() + " | Planificador: " + planificador.getNombre() + " -----");

            // --- PLANIFICADOR DE MEDIANO PLAZO (Gestión de E/S y Suspensión) ---
            // 1. Gestionar E/S de procesos en memoria (Bloqueados -> Listos/SuspendidosListos)
            gestionarColaBloqueados();
            // 2. Gestionar E/S de procesos suspendidos (SuspendidosBloqueados -> SuspendidosListos)
            gestionarColaSuspendidosBloqueados();
            // 3. Intentar reanudar procesos si hay memoria libre (SuspendidosListos -> Listos)
            reanudarProcesosSuspendidos();

            // --- PLANIFICADOR DE LARGO PLAZO (Admisión de nuevos procesos) ---
            // 4. Intentar admitir procesos de la cola de Nuevos al sistema
            admitirNuevosProcesos();
            
            // 5. Para HRRN: Actualizar tiempo de espera de procesos en la cola de Listos
            if (planificador instanceof HRRN) {
                actualizarTiemposDeEspera();
            }

            // --- PLANIFICADOR DE CORTO PLAZO (Gestión de la CPU) ---
            // 6. Verificar si es necesaria una expropiación (para SRT)
            verificarExpropiacionSRT();
            
            // 7. Si la CPU está libre, despachar un nuevo proceso
            if (procesoEnCPU == null) {
                procesoEnCPU = planificador.getSiguienteProceso();
                if (procesoEnCPU != null) {
                    configurarProcesoParaEjecucion(procesoEnCPU);
                }
            }

            // 8. Ejecutar ciclo de CPU si hay un proceso
            if (procesoEnCPU != null) {
                ejecutarCicloCPU();
            } else {
                System.out.println("CPU OCIOSA.");
            }
            
            clock.esperar();
        }
        System.out.println("Simulación detenida.");
    }
    
    private void ejecutarCicloCPU() {
        System.out.println("CPU | Ejecutando: " + procesoEnCPU.getId() + " (" + procesoEnCPU.getNombre() + "), PC=" + procesoEnCPU.getProgramCounter());
        procesoEnCPU.ejecutarInstruccion();
        quantumRestante--;

        // --- Verificación de Eventos post-ejecución ---
        if (procesoEnCPU.haTerminado()) {
            System.out.println("EVENTO | Proceso " + procesoEnCPU.getId() + " ha TERMINADO.");
            procesoEnCPU.setEstado(Proceso.EstadoProceso.TERMINADO);
            colaTerminados.encolar(procesoEnCPU);
            procesoEnCPU = null;
        } else if (procesoEnCPU.necesitaExcepcionIO()) {
            System.out.println("EVENTO | Proceso " + procesoEnCPU.getId() + " solicita E/S -> BLOQUEADO");
            procesoEnCPU.setEstado(Proceso.EstadoProceso.BLOQUEADO);
            procesoEnCPU.setCiclosEsperaIO(procesoEnCPU.getCiclosExcepcionCompletada());
            colaBloqueados.encolar(procesoEnCPU);
            procesoEnCPU = null;
        } else if (quantumRestante <= 0 && (planificador instanceof RoundRobin || planificador instanceof MLFQ)) {
            manejarFinDeQuantum();
        }
    }

    private void configurarProcesoParaEjecucion(Proceso p) {
        System.out.println("SCHEDULER | Despachando Proceso " + p.getId() + " a la CPU.");
        p.setEstado(Proceso.EstadoProceso.EJECUCION);
        
        // Configurar quantum según el planificador
        if (planificador instanceof RoundRobin) {
            quantumRestante = quantum;
        } else if (planificador instanceof MLFQ) {
            // Quantum depende del nivel, aquí simplificamos con el valor base
            // Una implementación más avanzada leería el nivel del proceso desde p.getNivelMLFQ()
            quantumRestante = quantum; 
        }
    }
    
    private void manejarFinDeQuantum() {
        System.out.println("EVENTO | Proceso " + procesoEnCPU.getId() + " - Fin de Quantum.");
        procesoEnCPU.setEstado(Proceso.EstadoProceso.LISTO);
        
        if (planificador instanceof MLFQ) {
            // Lógica de degradación para MLFQ
            MLFQ mlfqScheduler = (MLFQ) planificador;
            int nivelActual = procesoEnCPU.getNivelMLFQ(); // Necesitas añadir este campo en Proceso.java
            mlfqScheduler.degradarProceso(procesoEnCPU, nivelActual);
            procesoEnCPU.setNivelMLFQ(Math.min(nivelActual + 1, 2)); // Suponiendo 3 niveles (0, 1, 2)
        } else {
            // Lógica estándar para Round Robin
            planificador.agregarProceso(procesoEnCPU);
        }
        procesoEnCPU = null;
    }
    
    // --- Lógica de Planificadores de Largo y Mediano Plazo ---
    
    private void admitirNuevosProcesos() {
        while (!colaNuevos.estaVacia()) {
            if (hayMemoriaLibre()) {
                Proceso p = colaNuevos.desencolar();
                p.setEstado(Proceso.EstadoProceso.LISTO);
                p.setTiempoLlegada(clock.getCicloActual());
                planificador.agregarProceso(p);
                System.out.println("MEMORIA | Proceso " + p.getId() + " admitido en memoria -> LISTO");
            } else {
                System.out.println("MEMORIA | Llena. No se pueden admitir nuevos procesos. Esperando...");
                break; // No hay memoria, no podemos admitir más por ahora.
            }
        }
    }

    private void gestionarColaBloqueados() {
        int tamano = colaBloqueados.getTamano();
        for (int i = 0; i < tamano; i++) {
            Proceso p = colaBloqueados.desencolar();
            p.decrementarCicloEsperaIO();
            if (p.getCiclosEsperaIO() <= 0) {
                System.out.println("E/S | Proceso " + p.getId() + " completó E/S.");
                if (hayMemoriaLibre()) {
                    p.setEstado(Proceso.EstadoProceso.LISTO);
                    planificador.agregarProceso(p);
                    System.out.println("MEMORIA | Proceso " + p.getId() + " vuelve a -> LISTO");
                } else {
                    p.setEstado(Proceso.EstadoProceso.SUSPENDIDO_LISTO);
                    colaSuspendidosListos.encolar(p);
                    System.out.println("MEMORIA | Llena. Proceso " + p.getId() + " va a -> SUSPENDIDO_LISTO");
                }
            } else {
                colaBloqueados.encolar(p); // Sigue esperando
            }
        }
    }

    private void gestionarColaSuspendidosBloqueados() {
        int tamano = colaSuspendidosBloqueados.getTamano();
        for (int i = 0; i < tamano; i++) {
            Proceso p = colaSuspendidosBloqueados.desencolar();
            p.decrementarCicloEsperaIO();
            if (p.getCiclosEsperaIO() <= 0) {
                p.setEstado(Proceso.EstadoProceso.SUSPENDIDO_LISTO);
                colaSuspendidosListos.encolar(p);
                System.out.println("E/S | Proceso suspendido " + p.getId() + " completó E/S -> SUSPENDIDO_LISTO");
            } else {
                colaSuspendidosBloqueados.encolar(p); // Sigue esperando
            }
        }
    }

    private void reanudarProcesosSuspendidos() {
        while (hayMemoriaLibre() && !colaSuspendidosListos.estaVacia()) {
            Proceso p = colaSuspendidosListos.desencolar();
            p.setEstado(Proceso.EstadoProceso.LISTO);
            planificador.agregarProceso(p);
            System.out.println("SWAP-IN | Proceso " + p.getId() + " reanudado -> LISTO");
        }
    }
    
    // --- Lógica Específica de Planificadores ---
    
    private void verificarExpropiacionSRT() {
        if (planificador instanceof SRT && procesoEnCPU != null) {
            Proceso mejorCandidato = planificador.peekSiguienteProceso();
            if (mejorCandidato != null && mejorCandidato.getRafagaRestante() < procesoEnCPU.getRafagaRestante()) {
                System.out.println("EXPROPIACIÓN | Proceso entrante " + mejorCandidato.getId() + " (RT=" + mejorCandidato.getRafagaRestante() + 
                                   ") es más corto que el actual " + procesoEnCPU.getId() + " (RT=" + procesoEnCPU.getRafagaRestante() + ").");
                procesoEnCPU.setEstado(Proceso.EstadoProceso.LISTO);
                planificador.agregarProceso(procesoEnCPU);
                procesoEnCPU = null; // Liberar la CPU para que el despachador elija al nuevo, más corto
            }
        }
    }

    private void actualizarTiemposDeEspera() {
        Object[] procesosListos = planificador.getProcesosListosComoArray();
        for (Object obj : procesosListos) {
            ((Proceso) obj).incrementarTiempoEspera();
        }
    }

    // --- Helpers ---
    private boolean hayMemoriaLibre() {
        int procesosEnMemoria = planificador.getNumeroProcesosListos() + colaBloqueados.getTamano();
        return procesosEnMemoria < MEMORIA_MAXIMA_PROCESOS;
    }
    
   // --- GETTERS PARA LA INTERFAZ GRÁFICA  ---
    // Estos métodos son 'thread-safe' porque devuelven copias de los datos,
    // no las referencias a las colas originales.

    /**
     * Devuelve el estado actual del proceso en la CPU.
     * La GUI usará esto para mostrar qué proceso se está ejecutando.
     * @return El Proceso en la CPU, o null si está ociosa.
     */
    public Proceso getProcesoEnCPU() {
        return this.procesoEnCPU;
    }

    /**
     * Devuelve una instantánea de los procesos en la cola de Nuevos.
     * @return Un array de Object que contiene los procesos.
     */
    public Object[] getColaNuevos() {
        synchronized (colaNuevos) { // Sincronizamos para obtener una copia consistente
            return colaNuevos.toArray(new Proceso[0]);
        }
    }

    /**
     * Devuelve una instantánea de los procesos en la cola de Listos.
     * Delega la llamada al planificador actual.
     * @return Un array de Object que contiene los procesos listos.
     */
    public Object[] getColaListos() {
        if (planificador != null) {
            return planificador.getProcesosListosComoArray();
        }
        return new Object[0]; // Devuelve un array vacío si no hay planificador
    }

    /**
     * Devuelve una instantánea de los procesos en la cola de Bloqueados.
     * @return Un array de Object que contiene los procesos bloqueados.
     */
    public Object[] getColaBloqueados() {
        return colaBloqueados.toArray(new Proceso[0]);
    }

    /**
     * Devuelve una instantánea de los procesos en la cola de Suspendidos-Listos.
     * @return Un array de Object que contiene los procesos en dicho estado.
     */
    public Object[] getColaSuspendidosListos() {
        return colaSuspendidosListos.toArray(new Proceso[0]);
    }

    /**
     * Devuelve una instantánea de los procesos en la cola de Suspendidos-Bloqueados.
     * @return Un array de Object que contiene los procesos en dicho estado.
     */
    public Object[] getColaSuspendidosBloqueados() {
        return colaSuspendidosBloqueados.toArray(new Proceso[0]);
    }

    /**
     * Devuelve una instantánea de los procesos en la cola de Terminados.
     * @return Un array de Object que contiene los procesos terminados.
     */
    public Object[] getColaTerminados() {
        return colaTerminados.toArray(new Proceso[0]);
    }

    /**
     * Devuelve el objeto Clock para que la GUI pueda consultar el ciclo actual.
     * @return El objeto Clock de la simulación.
     */
    public Clock getClock() {
        return this.clock;
    }

    /**
     * Devuelve el planificador actual para que la GUI pueda mostrar su nombre.
     * @return El objeto EstrategiaPlanificacion actual.
     */
    public EstrategiaPlanificacion getPlanificador() {
        return this.planificador;
    }

    /**
     * Indica si el sistema operativo está realizando tareas (CPU ociosa)
     * o si un proceso de usuario se está ejecutando.
     * @return true si la CPU está ociosa (SO activo), false si un proceso se está ejecutando.
     */
    public boolean isSistemaOperativoActivo() {
        return this.procesoEnCPU == null;
    }
}