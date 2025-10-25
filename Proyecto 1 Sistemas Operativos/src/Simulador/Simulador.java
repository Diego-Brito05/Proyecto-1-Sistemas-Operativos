package Simulador;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import EstructuraDeDatos.Cola;
import EstructuraDeDatos.Proceso;
import Planificador.*; // Importa todas las clases de planificación


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
    private static final int MEMORIA_MAXIMA_PROCESOS = 10;

    // --- Componentes del Sistema ---
    private Clock clock;
    private EstrategiaPlanificacion planificador;
    private Proceso procesoEnCPU;
    
    private final Cola<String> colaDeLogs;
    // VARIABLE PARA GESTIÓN DE MEMORIA SIMULADA
    private int proximaDireccionMemoria = 1024;

    // --- Colas de Procesos ---
    private final Cola<Proceso> colaNuevos;
    private final Cola<Proceso> colaBloqueados;
    private final Cola<Proceso> colaSuspendidosListos;
    private final Cola<Proceso> colaSuspendidosBloqueados;
    private final Cola<Proceso> colaTerminados;
    // La cola de LISTOS está dentro del 'planificador'

    // --- Estado de la Simulación ---
    private volatile boolean enEjecucion = false;
    private Thread hiloSimulacion;
    public Estadisticas stats;
    
    // --- Parámetros Configurables ---
    private int quantum = 8; // Quantum para Round Robin y nivel superior de MLFQ
    private int quantumRestante;

    public Simulador() {
        this.clock = new Clock(1000);
        this.planificador = new FCFS(); // Inicia con FCFS por defecto
        
        this.colaDeLogs = new Cola<>();
        
        this.colaNuevos = new Cola<>();
        this.colaBloqueados = new Cola<>();
        this.colaSuspendidosListos = new Cola<>();
        this.colaSuspendidosBloqueados = new Cola<>();
        this.colaTerminados = new Cola<>();

        this.procesoEnCPU = null;
        this.hiloSimulacion = null; 
    }

    // --- Control de la Simulación ---
   public synchronized void iniciar() {
    // Solo inicia si no se está ejecutando
    if (!enEjecucion) {
        this.enEjecucion = true;
        
        // Si el hilo es nulo o está muerto, crea uno nuevo.
        if (hiloSimulacion == null || !hiloSimulacion.isAlive()) {
            // Creamos una nueva instancia del hilo CADA VEZ que iniciamos desde cero.
            hiloSimulacion = new Thread(this);
            hiloSimulacion.start(); // Iniciamos el nuevo hilo
        }
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
    
    /**
    * Cambia la estrategia de planificación en tiempo de ejecución.
    * TODO: En una versión avanzada, este método debería transferir de forma segura
    * los procesos de la cola de listos del planificador antiguo al nuevo.
    * Por ahora, simplemente reemplazará el planificador.
    * @param nuevaEstrategia La nueva instancia del algoritmo de planificación.
    */
    public void setPlanificador(EstrategiaPlanificacion nuevaEstrategia) {
    // Aquí es donde se manejaría la migración de procesos de la cola de listos.
    // Para simplificar, asumiremos que cambiar el planificador vacía la cola de listos.
    // Una implementación más robusta es más compleja.
    this.planificador = nuevaEstrategia;
    stats.update_planificador(nuevaEstrategia.getNombre());
    System.out.println("PLANIFICADOR CAMBIADO A: " + nuevaEstrategia.getNombre());
    
    }
    
    /**
    * Permite a la GUI cambiar la duración de un ciclo de reloj en tiempo real.
    * Delega la llamada al objeto Clock interno.
     * @param milisegundos La nueva duración de un ciclo en milisegundos.
    */
    public void setDuracionCiclo(long milisegundos) {
    if (this.clock != null) {
        this.clock.setDuracionCicloMs(milisegundos);
    }
    }
    
    // --- El Bucle Principal del Kernel ---
     @Override
    public void run() {
        while (enEjecucion) {
            clock.tick();
            log("\n----- CICLO #" + clock.getCicloActual() + " | Planificador: " + planificador.getNombre() + " -----"); 
            stats.update_Ciclos();
            stats.update_CPU();
            
            gestionarColaBloqueados();
            gestionarColaSuspendidosBloqueados();
            reanudarProcesosSuspendidos();
            admitirNuevosProcesos();
            
            if (planificador instanceof HRRN) {
                actualizarTiemposDeEspera();
            }

            verificarExpropiacionSRT();
            
            if (procesoEnCPU == null) {
                procesoEnCPU = planificador.getSiguienteProceso();
                if (procesoEnCPU != null) {
                    configurarProcesoParaEjecucion(procesoEnCPU);
                }
            }

            if (procesoEnCPU != null) {
                ejecutarCicloCPU();
            } else {
                log("CPU OCIOSA.");
                stats.update_OCIO();
            }
            
            clock.esperar();
        }
        log("Simulación detenida."); 
    }
    
    private void ejecutarCicloCPU() {
        log("CPU | Ejecutando: " + procesoEnCPU.getId() + " (" + procesoEnCPU.getNombre() + "), PC=" + procesoEnCPU.getProgramCounter()); 
        procesoEnCPU.ejecutarInstruccion();
        quantumRestante--;

        if (procesoEnCPU.haTerminado()) {
            log("EVENTO | Proceso " + procesoEnCPU.getId() + " ha TERMINADO.");
            stats.update_Ejecutados();
            stats.update_Throughtput();
            if (procesoEnCPU.esIOBound()) {
                stats.update_ProcesosIO();
            } else {
                // Si no es I/O Bound entonces es CPU Bound
                stats.update_ProcesosCPU();
            }
            procesoEnCPU.setEstado(Proceso.EstadoProceso.TERMINADO);
            colaTerminados.encolar(procesoEnCPU);
            procesoEnCPU = null;
        } else if (procesoEnCPU.necesitaExcepcionIO()) {
            log("EVENTO | Proceso " + procesoEnCPU.getId() + " solicita E/S -> BLOQUEADO"); 
            procesoEnCPU.setEstado(Proceso.EstadoProceso.BLOQUEADO);
            procesoEnCPU.setCiclosEsperaIO(procesoEnCPU.getCiclosExcepcionCompletada());
            colaBloqueados.encolar(procesoEnCPU);
            procesoEnCPU = null;
        } else if (quantumRestante <= 0 && (planificador instanceof RoundRobin || planificador instanceof MLFQ)) {
            manejarFinDeQuantum();
        }
    }

    private void configurarProcesoParaEjecucion(Proceso p) {
        log("SCHEDULER | Despachando Proceso " + p.getId() + " a la CPU."); 
        p.setEstado(Proceso.EstadoProceso.EJECUCION);
        
        if (planificador instanceof RoundRobin || planificador instanceof MLFQ) {
            quantumRestante = quantum; 
        }
    }
    
    private void manejarFinDeQuantum() {
        log("EVENTO | Proceso " + procesoEnCPU.getId() + " - Fin de Quantum.");
        procesoEnCPU.setEstado(Proceso.EstadoProceso.LISTO);
        
        if (planificador instanceof MLFQ) {
            MLFQ mlfqScheduler = (MLFQ) planificador;
            int nivelActual = procesoEnCPU.getNivelMLFQ();
            mlfqScheduler.degradarProceso(procesoEnCPU, nivelActual);
            procesoEnCPU.setNivelMLFQ(Math.min(nivelActual + 1, 2));
        } else {
            planificador.agregarProceso(procesoEnCPU);
        }
        procesoEnCPU = null;
    }
    
    private void admitirNuevosProcesos() {
        while (!colaNuevos.estaVacia()) {
            Proceso pNuevo = colaNuevos.verFrente();

            if (hayMemoriaLibre()) {
                pNuevo = colaNuevos.desencolar();
                pNuevo.setEstado(Proceso.EstadoProceso.LISTO);
                pNuevo.setTiempoLlegada(clock.getCicloActual());
                planificador.agregarProceso(pNuevo);
                log("MEMORIA | Proceso " + pNuevo.getId() + " admitido en memoria -> LISTO"); 
            } else {
                Proceso candidatoASuspender = buscarCandidatoParaSuspender();
                if (candidatoASuspender != null) {
                    pNuevo = colaNuevos.desencolar();
                    if (candidatoASuspender.getEstado() == Proceso.EstadoProceso.BLOQUEADO) {
                        log("SWAP-OUT | Memoria llena. Suspendiendo proceso BLOQUEADO " + candidatoASuspender.getId() + " para hacer espacio."); 
                        colaBloqueados.eliminar(candidatoASuspender);
                        candidatoASuspender.setEstado(Proceso.EstadoProceso.SUSPENDIDO_BLOQUEADO);
                        colaSuspendidosBloqueados.encolar(candidatoASuspender);
                    } 
                    pNuevo.setEstado(Proceso.EstadoProceso.LISTO);
                    pNuevo.setTiempoLlegada(clock.getCicloActual());
                    planificador.agregarProceso(pNuevo);
                    log("MEMORIA | Proceso " + pNuevo.getId() + " admitido en memoria -> LISTO (gracias al swap-out)"); // <<-- CAMBIO
                } else {
                    log("MEMORIA | Llena y sin candidatos para suspender. Proceso " + pNuevo.getId() + " sigue esperando en la cola de Nuevos."); 
                    break;
                }
            }
        }
    }

    private void gestionarColaBloqueados() {
        // Usamos una cola temporal para los procesos que aún no han terminado su E/S.
        Cola<Proceso> tempCola = new Cola<>();

        // Procesamos cada proceso en la cola de bloqueados.
        while (!colaBloqueados.estaVacia()) {
            Proceso p = colaBloqueados.desencolar();
            p.decrementarCicloEsperaIO();

            if (p.getCiclosEsperaIO() <= 0) {
                // El proceso terminó su E/S, lo movemos a su siguiente estado.
                log("E/S | Proceso " + p.getId() + " completó E/S.");
                if (hayMemoriaLibre()) {
                    p.setEstado(Proceso.EstadoProceso.LISTO);
                    planificador.agregarProceso(p);
                    log("MEMORIA | Proceso " + p.getId() + " vuelve a -> LISTO");
                } else {
                    p.setEstado(Proceso.EstadoProceso.SUSPENDIDO_LISTO);
                    colaSuspendidosListos.encolar(p);
                    log("MEMORIA | Llena. Proceso " + p.getId() + " va a -> SUSPENDIDO_LISTO");
                }
            } else {
                // El proceso aún necesita esperar, lo ponemos en la cola temporal.
                tempCola.encolar(p);
            }
        }

        // Al final, re-encolamos todos los procesos que aún están esperando.
        // Esto evita modificar la cola mientras la recorremos.
        while (!tempCola.estaVacia()) {
            colaBloqueados.encolar(tempCola.desencolar());
        }
    }

    private void gestionarColaSuspendidosBloqueados() {
        // Misma lógica con una cola temporal.
        Cola<Proceso> tempCola = new Cola<>();

        while (!colaSuspendidosBloqueados.estaVacia()) {
            Proceso p = colaSuspendidosBloqueados.desencolar();
            p.decrementarCicloEsperaIO();

            if (p.getCiclosEsperaIO() <= 0) {
                // El proceso terminó su E/S, se mueve a Suspendido Listo.
                p.setEstado(Proceso.EstadoProceso.SUSPENDIDO_LISTO);
                colaSuspendidosListos.encolar(p);
                log("E/S | Proceso suspendido " + p.getId() + " completó E/S -> SUSPENDIDO_LISTO");
            } else {
                // Aún esperando, va a la cola temporal.
                tempCola.encolar(p);
            }
        }

        // Devolvemos los procesos que siguen esperando a la cola original.
        while (!tempCola.estaVacia()) {
            colaSuspendidosBloqueados.encolar(tempCola.desencolar());
        }
    }

    private void reanudarProcesosSuspendidos() {
        while (hayMemoriaLibre() && !colaSuspendidosListos.estaVacia()) {
            Proceso p = colaSuspendidosListos.desencolar();
            p.setEstado(Proceso.EstadoProceso.LISTO);
            planificador.agregarProceso(p);
            log("SWAP-IN | Proceso " + p.getId() + " reanudado -> LISTO"); // 
        }
    }
    
    private void verificarExpropiacionSRT() {
        if (planificador instanceof SRT && procesoEnCPU != null) {
            Proceso mejorCandidato = planificador.peekSiguienteProceso();
            if (mejorCandidato != null && mejorCandidato.getRafagaRestante() < procesoEnCPU.getRafagaRestante()) {
                log("EXPROPIACIÓN | Proceso entrante " + mejorCandidato.getId() + " (RT=" + mejorCandidato.getRafagaRestante() + 
                                   ") es más corto que el actual " + procesoEnCPU.getId() + " (RT=" + procesoEnCPU.getRafagaRestante() + ")."); 
                procesoEnCPU.setEstado(Proceso.EstadoProceso.LISTO);
                planificador.agregarProceso(procesoEnCPU);
                procesoEnCPU = null;
            }
        }
    }

    private void actualizarTiemposDeEspera() {
        Object[] procesosListos = planificador.getProcesosListosComoArray();
        for (Object obj : procesosListos) {
            ((Proceso) obj).incrementarTiempoEspera();
        }
    }
    
    
    /**
    * Busca un proceso en memoria para ser suspendido (swapped-out).
    * Política simple: da prioridad a los procesos en estado BLOQUEADO.
    * @return El proceso candidato a ser suspendido, o null si no se encuentra ninguno.
    */
   private Proceso buscarCandidatoParaSuspender() {
       // Primero, buscamos en la cola de bloqueados. Son los mejores candidatos
       // porque no están compitiendo por la CPU en este momento.
       if (!colaBloqueados.estaVacia()) {
           // Devolvemos el primer proceso bloqueado. Una política más compleja
           // podría buscar el que lleva más tiempo bloqueado, o el de menor prioridad.
           return colaBloqueados.verFrente();
       }

       // Si no hay procesos bloqueados, podríamos buscar en la cola de listos.
       // Por ejemplo, podríamos suspender al proceso con la prioridad más baja en la cola de listos.
       // Por ahora, para mantenerlo simple, no suspenderemos procesos listos.
       return null; 
   }
    
    
    // MÉTODO DE CREACIÓN DE PROCESO CENTRALIZADA ---
    /**
     * Este método es llamado por la GUI para solicitar la creación de un nuevo proceso.
     * El Simulador (OS) asigna la memoria y crea el objeto Proceso.
     * @param nombre El nombre del proceso.
     * @param totalInstrucciones La cantidad de instrucciones.
     * @param tipo El tipo de proceso (CPU o I/O Bound).
     * @param ciclosGenerar Ciclos para generar E/S (si aplica).
     * @param ciclosSatisfacer Ciclos para completar E/S (si aplica).
     */
    public void solicitarCreacionProceso(String nombre, int totalInstrucciones, Proceso.TipoProceso tipo, int ciclosGenerar, int ciclosSatisfacer) {
        //Asignar la dirección de memoria actual
        int direccionInicio = this.proximaDireccionMemoria;

        //Crear la instancia del Proceso con su dirección de inicio única
        Proceso nuevoProceso;
        if (tipo == Proceso.TipoProceso.IO_BOUND) {
            nuevoProceso = new Proceso(nombre, totalInstrucciones, tipo, ciclosGenerar, ciclosSatisfacer, 0, direccionInicio);
        } else { // CPU_BOUND
            nuevoProceso = new Proceso(nombre, totalInstrucciones, tipo, -1, -1, 0, direccionInicio);
        }
        
        
        // Le sumamos un pequeño espacio extra (16).
        this.proximaDireccionMemoria += totalInstrucciones + 16;

        this.agregarNuevoProceso(nuevoProceso);
    }

    // --- Helpers ---
    private boolean hayMemoriaLibre() {
        int procesosEnMemoria = planificador.getNumeroProcesosListos() + colaBloqueados.getTamano();
        return procesosEnMemoria < MEMORIA_MAXIMA_PROCESOS;
    }
    
    private void log(String mensaje) {
    System.out.println(mensaje);
    colaDeLogs.encolar(mensaje);
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
    
    
     public Cola<String> getColaDeLogs() {
        return this.colaDeLogs;
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

    /**
     * @param clock the clock to set
     */
    public void setClock(Clock clock) {
        this.clock = clock;
    }
}

