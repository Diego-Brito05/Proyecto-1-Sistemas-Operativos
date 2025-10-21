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
        // Procesamos todos los procesos que estén en la cola de Nuevos en este ciclo.
        while (!colaNuevos.estaVacia()) {
            Proceso pNuevo = colaNuevos.verFrente(); // Solo miramos, no lo sacamos todavía

            if (hayMemoriaLibre()) {
                // --- Caso 1: Hay memoria libre, admitimos directamente ---
                pNuevo = colaNuevos.desencolar();
                pNuevo.setEstado(Proceso.EstadoProceso.LISTO);
                pNuevo.setTiempoLlegada(clock.getCicloActual());
                planificador.agregarProceso(pNuevo);
                System.out.println("MEMORIA | Proceso " + pNuevo.getId() + " admitido en memoria -> LISTO");

            } else {
                // --- Caso 2: No hay memoria. Intentamos hacer un SWAP-OUT ---
                // Buscamos un candidato para suspender, dando prioridad a los bloqueados.
                Proceso candidatoASuspender = buscarCandidatoParaSuspender();

                if (candidatoASuspender != null) {
                    // ¡Encontramos a alguien a quien suspender!
                    pNuevo = colaNuevos.desencolar(); // Ahora sí admitimos el nuevo

                    // Suspendemos al candidato
                    if (candidatoASuspender.getEstado() == Proceso.EstadoProceso.BLOQUEADO) {
                        System.out.println("SWAP-OUT | Memoria llena. Suspendiendo proceso BLOQUEADO " + candidatoASuspender.getId() + " para hacer espacio.");
                        colaBloqueados.eliminar(candidatoASuspender); // Necesitarás un método 'eliminar' en tu Cola
                        candidatoASuspender.setEstado(Proceso.EstadoProceso.SUSPENDIDO_BLOQUEADO);
                        colaSuspendidosBloqueados.encolar(candidatoASuspender);

                    } 

                    
                    pNuevo.setEstado(Proceso.EstadoProceso.LISTO);
                    pNuevo.setTiempoLlegada(clock.getCicloActual());
                    planificador.agregarProceso(pNuevo);
                    System.out.println("MEMORIA | Proceso " + pNuevo.getId() + " admitido en memoria -> LISTO (gracias al swap-out)");

                } else {
                    // No pudimos suspender a nadie (ej. no hay bloqueados), así que el nuevo proceso espera.
                    // Podrías moverlo a SUSPENDIDO_LISTO como antes, o simplemente dejarlo en NUEVO.
                    System.out.println("MEMORIA | Llena y sin candidatos para suspender. Proceso " + pNuevo.getId() + " sigue esperando en la cola de Nuevos.");
                    break; // Detenemos la admisión por este ciclo.
                }
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

    /**
     * @param clock the clock to set
     */
    public void setClock(Clock clock) {
        this.clock = clock;
    }
}

