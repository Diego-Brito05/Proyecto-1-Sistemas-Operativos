/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Simulador;

/**
 * Aquí se hacen todos los cálculos y comprobaciones para obtener
 * los datos estadísticos de ejecución. 
 */

public class Estadisticas {
    // Variable que indica el planificador activo.
    // Indica FCFS por defecto.
    private int planificadorActivo = 1;
    
    public Interfaces.Menu Menu;
    
    // Variables del sistema completo
    private int Ejecutados = 0;
    private int ProcesosIO = 0;
    private int ProcesosCPU = 0;
    private double Throughtput = 0.0;
    private long Ciclos = 0;
    private long CPU = 0;
    
    // Variables correspondientes a FCFS (First-Come, First-Served)
    private int FCFS_Ejecutados = 0;
    private int FCFS_ProcesosIO = 0;
    private int FCFS_ProcesosCPU = 0;
    private double FCFS_Throughtput = 0.0;
    private long FCFS_Ciclos = 0;
    private long FCFS_CPU = 0;
    private double FCFS_Ocio = 0.0;
    
    
    // Variables correspondientes a HRRN (Highest Response Ratio Next)
    private int HRRN_Ejecutados = 0;
    private int HRRN_ProcesosIO = 0;
    private int HRRN_ProcesosCPU = 0;
    private double HRRN_Throughtput = 0.0;
    private long HRRN_Ciclos = 0;
    private long HRRN_CPU = 0;
    private double HRRN_Ocio = 0.0;
    
     // Variables correspondientes a MLFQ (Multi-Level Feedback Queue)
    private int MLFQ_Ejecutados = 0;
    private int MLFQ_ProcesosIO = 0;
    private int MLFQ_ProcesosCPU = 0;
    private double MLFQ_Throughtput = 0.0;
    private long MLFQ_Ciclos = 0;
    private long MLFQ_CPU = 0;
    private double MLFQ_Ocio = 0.0;
    
    // Variables correspondientes a RR (Round Robin)
    private int RR_Ejecutados = 0;
    private int RR_ProcesosIO = 0;
    private int RR_ProcesosCPU = 0;
    private double RR_Throughtput = 0.0;
    private long RR_Ciclos = 0;
    private long RR_CPU = 0;
    private double RR_Ocio = 0.0;
    
    // Variables correspondientes a SPN (Shortest Process Next)
    private int SPN_Ejecutados = 0;
    private int SPN_ProcesosIO = 0;
    private int SPN_ProcesosCPU = 0;
    private double SPN_Throughtput = 0.0;
    private long SPN_Ciclos = 0;
    private long SPN_CPU = 0;
    private double SPN_Ocio = 0.0;
    
    // Variables correspondientes a SRT (Shortest Remaining Time)
    private int SRT_Ejecutados = 0;
    private int SRT_ProcesosIO = 0;
    private int SRT_ProcesosCPU = 0;
    private double SRT_Throughtput = 0.0;
    private long SRT_Ciclos = 0;
    private long SRT_CPU = 0;
    private double SRT_Ocio = 0.0;
    
    public void update_Ejecutados() {
        // Se actualizan la cantidad de procesos finalizados en general
        // y para cada uno de las políticas de planificación.
        switch (planificadorActivo) {
            case 1 -> {
                FCFS_Ejecutados++;
                FCFS();
            }
            case 2 -> {
                HRRN_Ejecutados++;
                HRRN();
            }
            case 3 -> {
                MLFQ_Ejecutados++;
                MLFQ();
            }
            case 4 -> {
                SPN_Ejecutados++;
                SPN();
            }
            case 5 -> {
                SRT_Ejecutados++;
                SRT();
            }
            default -> {
                RR_Ejecutados++;
                RR();
            }
        }
        Ejecutados++;
        estadisticas();
    }
    
    public void update_ProcesosIO() {
        // Se actualizan la cantidad de procesos I/O Bound finalizados en general
        // y para cada uno de las políticas de planificación.
        switch (planificadorActivo) {
            case 1 -> {
                FCFS_ProcesosIO++;
                FCFS();
            }
            case 2 -> {
                HRRN_ProcesosIO++;
                HRRN();
            }
            case 3 -> {
                MLFQ_ProcesosIO++;
                MLFQ();
            }
            case 4 -> {
                SPN_ProcesosIO++;
                SPN();
            }
            case 5 -> {
                SRT_ProcesosIO++;
                SRT();
            }
            default -> {
                RR_ProcesosIO++;
                RR();
            }
        }
        ProcesosIO++;
        estadisticas();
    }
    
    public void update_ProcesosCPU() {
        // Se actualizan la cantidad de procesos CPU Bound finalizados en general
        // y para cada uno de las políticas de planificación.
        switch (planificadorActivo) {
            case 1 -> {
                FCFS_ProcesosCPU++;
                FCFS();
            }
            case 2 -> {
                HRRN_ProcesosCPU++;
                HRRN();
            }
            case 3 -> {
                MLFQ_ProcesosCPU++;
                MLFQ();
            }
            case 4 -> {
                SPN_ProcesosCPU++;
                SPN();
            }
            case 5 -> {
                SRT_ProcesosCPU++;
                SRT();
            }
            default -> {
                RR_ProcesosCPU++;
                RR();
            }
        }
        ProcesosCPU++;
        estadisticas();
    }
    
    public void update_Throughtput() {
        // Se actualiza el throuughtpout finalizados en general
        // y para cada uno de las políticas de planificación.
        switch (planificadorActivo) {
            case 1 -> {
                double CicloEjecucion = FCFS_Ciclos-FCFS_Ocio;
                FCFS_Throughtput = FCFS_Ejecutados/CicloEjecucion;
                FCFS();
            }
            case 2 -> {
                double CicloEjecucion = HRRN_Ciclos-HRRN_Ocio;
                HRRN_Throughtput = HRRN_Ejecutados/CicloEjecucion;
                HRRN();
            }
            case 3 -> {
                double CicloEjecucion = MLFQ_Ciclos-MLFQ_Ocio;
                MLFQ_Throughtput = MLFQ_Ejecutados/CicloEjecucion;
                MLFQ();
            }
            case 4 -> {
                double CicloEjecucion = SPN_Ciclos-SPN_Ocio;
                SPN_Throughtput = SPN_Ejecutados/CicloEjecucion;
                SPN();
            }
            case 5 -> {
                double CicloEjecucion = SRT_Ciclos-SRT_Ocio;
                SRT_Throughtput = SRT_Ejecutados/CicloEjecucion;
                SRT();
            }
            default -> {
                double CicloEjecucion = RR_Ciclos-RR_Ocio;
                RR_Throughtput = RR_Ejecutados/CicloEjecucion;
                RR();
            }
        }
        double politicas = 0.0;
        if (FCFS_Ciclos-FCFS_Ocio != 0) {
            politicas++;
        } else if (RR_Ciclos-RR_Ocio != 0) {
            politicas++;
        } else if (HRRN_Ciclos-HRRN_Ocio != 0) {
            politicas++;
        } else if (MLFQ_Ciclos-MLFQ_Ocio != 0) {
            politicas++;
        } else if (SPN_Ciclos-SPN_Ocio != 0) {
            politicas++;
        } else if (SRT_Ciclos-SRT_Ocio != 0) {
            politicas++;
        }
        Throughtput = (FCFS_Throughtput+HRRN_Throughtput+MLFQ_Throughtput+SPN_Throughtput+SRT_Throughtput+RR_Throughtput)/politicas;
        estadisticas();
    }
    
    public void update_Ciclos() {
        // Se actualiza el throuughtpout finalizados en general
        // y para cada uno de las políticas de planificación.
        switch (planificadorActivo) {
            case 1 -> {
                FCFS_Ciclos++;
                FCFS();
            }
            case 2 -> {
                HRRN_Ciclos++;
                HRRN();
            }
            case 3 -> {
                MLFQ_Ciclos++;
                MLFQ();
            }
            case 4 -> {
                SPN_Ciclos++;
                SPN();
            }
            case 5 -> {
                SRT_Ciclos++;
                SRT();
            }
            default -> {
                RR_Ciclos++;
                RR();
            }
        }
        Ciclos++;
        estadisticas();
    }
    
    public void update_CPU() {
        // Se actualiza el throuughtpout finalizados en general
        // y para cada uno de las políticas de planificación.
        switch (planificadorActivo) {
            case 1 -> {
                FCFS_CPU = (long) (((FCFS_Ciclos-FCFS_Ocio)/FCFS_Ciclos)*100);
                FCFS();
            }
            case 2 -> {
                HRRN_CPU = (long) (((HRRN_Ciclos-HRRN_Ocio)/HRRN_Ciclos)*100);
                HRRN();
            }
            case 3 -> {
                MLFQ_CPU = (long) (((MLFQ_Ciclos-MLFQ_Ocio)/MLFQ_Ciclos)*100);
                MLFQ();
            }
            case 4 -> {
                SPN_CPU = (long) (((SPN_Ciclos-SPN_Ocio)/SPN_Ciclos)*100);
                SPN();
            }
            case 5 -> {
                SRT_CPU = (long) (((SRT_Ciclos-SRT_Ocio)/SRT_Ciclos)*100);
                SRT();
            }
            default -> {
                RR_CPU = (long) (((RR_Ciclos-RR_Ocio)/RR_Ciclos)*100);
                RR();
            }
        }
        CPU = (long) (((Ciclos-(FCFS_Ocio+MLFQ_Ocio+HRRN_Ocio+SPN_Ocio+SRT_Ocio+RR_Ocio))/Ciclos)*100);
        estadisticas();
    }
    
    public void update_OCIO() {
        // Se actualiza el throuughtpout finalizados en general
        // y para cada uno de las políticas de planificación.
        switch (planificadorActivo) {
            case 1 -> {
                FCFS_Ocio++;
                FCFS();
            }
            case 2 -> {
                HRRN_Ocio++;
                HRRN();
            }
            case 3 -> {
                MLFQ_Ocio++;
                MLFQ();
            }
            case 4 -> {
                SPN_Ocio++;
                SPN();
            }
            case 5 -> {
                SRT_Ocio++;
                SRT();
            }
            default -> {
                RR_Ocio++;
                RR();
            }
        }
        CPU++;
        estadisticas();
    }
    
    public void update_planificador(String planificador){
        if ("MLFQ (Multi-level Feedback Queue)".equals(planificador)) {
            planificadorActivo = 3;
        } else if ("FCFS (First-Come, First-Served)".equals(planificador)) {
            planificadorActivo = 1;
        } else if ("HRRN (Highest Response Ratio Next)".equals(planificador)) {
            planificadorActivo = 2;
        } else if ("SPN (Shortest Process Next)".equals(planificador)) {
            planificadorActivo = 4;
        } else if ("SRT (Shortest Remaining Time)".equals(planificador)) {
            planificadorActivo = 5;
        } else {
            planificadorActivo = 6;
        }
    }
    
    // Funciones para llamar a los métodos que actualizan los JLabel en el Menú.
    private void estadisticas() {
        Menu.actualizarEstadisticas(Ejecutados, ProcesosIO, ProcesosCPU, Ciclos, Throughtput, CPU);
    }
    
    private void FCFS() {
        Menu.actualizarFCFS(FCFS_Ejecutados, FCFS_ProcesosIO, FCFS_ProcesosCPU, FCFS_Ciclos, FCFS_Throughtput, FCFS_CPU);
    }
    
    private void HRRN() {
        Menu.actualizarHRRN(HRRN_Ejecutados, HRRN_ProcesosIO, HRRN_ProcesosCPU, HRRN_Ciclos, HRRN_Throughtput, HRRN_CPU);
    }
    
    private void MLFQ() {
        Menu.actualizarMLFQ(MLFQ_Ejecutados, MLFQ_ProcesosIO, MLFQ_ProcesosCPU, MLFQ_Ciclos, MLFQ_Throughtput, MLFQ_CPU);
    }
    
    private void SPN() {
        Menu.actualizarSPN(SPN_Ejecutados, SPN_ProcesosIO, SPN_ProcesosCPU, SPN_Ciclos, SPN_Throughtput, SPN_CPU);
    }
    
    private void SRT() {
        Menu.actualizarSRT(SRT_Ejecutados, SRT_ProcesosIO, SRT_ProcesosCPU, SRT_Ciclos, SRT_Throughtput, SRT_CPU);
    }
    
    private void RR() {
        Menu.actualizarRR(RR_Ejecutados, RR_ProcesosIO, RR_ProcesosCPU, RR_Ciclos, RR_Throughtput, RR_CPU);
    }
}
