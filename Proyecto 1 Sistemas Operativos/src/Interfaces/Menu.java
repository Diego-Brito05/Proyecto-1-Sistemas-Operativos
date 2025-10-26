/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Interfaces;

import EstructuraDeDatos.Cola;
import EstructuraDeDatos.Proceso;
import Planificador.EstrategiaPlanificacion;
import Planificador.FCFS;
import Planificador.HRRN;
import Planificador.MLFQ;
import Planificador.RoundRobin;
import Planificador.SPN;
import Planificador.SRT;
import Simulador.Simulador;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.*; 
import Simulador.Estadisticas;
import Graficos.*;

/**
 *
 * @author Diego
 */
    public class Menu extends javax.swing.JFrame {

    // se Declara el logger aquí, como un miembro ESTÁTICO y FINAL de la clase.
    // De esta forma, es visible para el método estático 'main'.
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Menu.class.getName());
    
    
    
    private Simulador miSimulador;
    private javax.swing.Timer guiUpdateTimer;
    private Estadisticas stats;
    private int autoProcessCounter = 1;
    
    public GraficoThroughputs GraphThr;
    public GraficoPoliticas GraphPoliticas;
    public GraficoIOCPU GraphIOCPU;
    

    // En el constructor de tu clase Menu
    public Menu() {
        initComponents(); // Método de NetBeans que crea los componentes

        
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        stats = new Estadisticas();
        stats.Menu=this;
        miSimulador = new Simulador();
        miSimulador.stats = stats;
        
        
        this.GraphIOCPU = new GraficoIOCPU();
        this.GraphPoliticas = new GraficoPoliticas();
        this.GraphThr = new GraficoThroughputs();
        
        jPanel2.setLayout(new BorderLayout());
        jPanel2.add(GraphThr, BorderLayout.CENTER);
        jPanel2.revalidate();
        jPanel2.repaint();   
    
        jPanel4.setLayout(new BorderLayout());
        jPanel4.add(GraphPoliticas, BorderLayout.CENTER);
        jPanel4.revalidate();
        jPanel4.repaint(); 
        
        jPanel3.setLayout(new BorderLayout());
        jPanel3.add(GraphIOCPU, BorderLayout.CENTER);
        jPanel3.revalidate();
        jPanel3.repaint(); 
        
        stats.setGraphThr(this.GraphThr);
        stats.setGraphPoliticas(this.GraphPoliticas);
        stats.setGraphIOCPU(this.GraphIOCPU);
        
        configurarEstadoInicial();
        
        
         ProcesoListRenderer renderer = new ProcesoListRenderer();

        ListaNuevo.setCellRenderer(renderer);
        ListaListo.setCellRenderer(renderer);
        ListaBloqueado.setCellRenderer(renderer);
        ListaFinalizado.setCellRenderer(renderer);
        ListaSuspListo.setCellRenderer(renderer);
        ListaSuspBloqueado.setCellRenderer(renderer);
        
        
        actualizarDisplayDuracionCiclo();
        // Timer para que actualice la GUI cada 250ms (4 veces por segundo)
        guiUpdateTimer = new javax.swing.Timer(250, new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                actualizarGUI(); // Este será nuestro método principal de refresco
            }
        });
        guiUpdateTimer.start(); // Inicia el timer
        
    }

    // Método para establecer los valores iniciales de la GUI
    private void configurarEstadoInicial() {
        IniciarPausarButton.setSelected(false);
        IniciarPausarButton.setText("Iniciar");
        IndicadorActivo.setSelected(false);
        IndicadorActivo.setBackground(java.awt.Color.RED);
        modoAct.setText("Modo Supervisor");
    }

    // método principal llamado por el Timer
    private void actualizarGUI() {
        // código para actualizar TODOS los componentes visuales
        actualizarCicloYModo();
        actualizarTodasLasListas();
        actualizarLog();
    }
    
    private void actualizarCicloYModo() {
        // Actualizar el ciclo de reloj
        CicloActual.setText(String.valueOf(miSimulador.getClock().getCicloActual()));

        // Actualizar el modo de operación
        if (miSimulador.isSistemaOperativoActivo()) {
            modoAct.setText("Supervisor");
        } else {
            modoAct.setText("Usuario");
        }
    }
    
    
    private void actualizarListaProcesos(javax.swing.JList<Proceso> listaComponente, Object[] procesosArray) {
        // Creamos un DefaultListModel que contendrá objetos Proceso
        javax.swing.DefaultListModel<Proceso> modelo = new javax.swing.DefaultListModel<>();

        // Llenamos el modelo con los objetos Proceso del array.
        for (Object obj : procesosArray) {
            modelo.addElement((Proceso) obj);
        }

        //Asignamos el modelo a la JList.
        listaComponente.setModel(modelo);
    }
    
    
    private void actualizarTodasLasListas() {
    // Obtener los datos de cada cola del simulador y pasarlos al método helper para dibujarlos
    actualizarListaProcesos(ListaNuevo, miSimulador.getColaNuevos());
    actualizarListaProcesos(ListaListo, miSimulador.getColaListos());
    actualizarListaProcesos(ListaBloqueado, miSimulador.getColaBloqueados());
    actualizarListaProcesos(ListaFinalizado, miSimulador.getColaTerminados());
    actualizarListaProcesos(ListaSuspListo, miSimulador.getColaSuspendidosListos());
    actualizarListaProcesos(ListaSuspBloqueado, miSimulador.getColaSuspendidosBloqueados());
    }
    
    private void actualizarDisplayDuracionCiclo() {
    if (miSimulador != null) {
        
        long duracionMs = miSimulador.getClock().getDuracionCicloMs();

        
        double duracionSegundos = duracionMs / 1000.0;

        
        String textoDuracion = String.format("%.2f", duracionSegundos);

       
        DurCicloRelojAct.setText(textoDuracion);
        }
    }
    
    public void actualizarEstadisticas(int finalizados, int IObound, int CPUbound, long ciclos, double throughtput, long cpu) {
        this.Ciclos.setText(String.valueOf(ciclos));
        this.ProcesosIO.setText(String.valueOf(IObound));
        this.ProcesosCPU.setText(String.valueOf(CPUbound));
        this.ProcesosTerminados.setText(String.valueOf(finalizados));
        this.Throughtput.setText(String.format("%.2f", throughtput)+" procesos/ciclos");
        this.UsoCPU.setText(String.valueOf(cpu)+"%");
    }
    
    public void actualizarFCFS(int finalizados, int IObound, int CPUbound, long ciclos, double throughtput, long cpu) {
        this.FSFC_1.setText(String.valueOf(finalizados));
        this.FSFC_2.setText(String.valueOf(IObound));
        this.FSFC_3.setText(String.valueOf(CPUbound));
        this.FSFC_5.setText(String.valueOf(ciclos));
        this.FSFC_4.setText(String.format("%.2f", throughtput)+" procesos/ciclos");
        this.FSFC_6.setText(String.valueOf(cpu)+"%");
    }
    
    public void actualizarHRRN(int finalizados, int IObound, int CPUbound, long ciclos, double throughtput, long cpu) {
        this.HRRN_1.setText(String.valueOf(finalizados));
        this.HRRN_2.setText(String.valueOf(IObound));
        this.HRRN_3.setText(String.valueOf(CPUbound));
        this.HRRN_5.setText(String.valueOf(ciclos));
        this.HRRN_4.setText(String.format("%.2f", throughtput)+" procesos/ciclos");
        this.HRRN_6.setText(String.valueOf(cpu)+"%");
    }
    
    public void actualizarMLFQ(int finalizados, int IObound, int CPUbound, long ciclos, double throughtput, long cpu) {
        this.MLFQ_1.setText(String.valueOf(finalizados));
        this.MLFQ_2.setText(String.valueOf(IObound));
        this.MLFQ_3.setText(String.valueOf(CPUbound));
        this.MLFQ_5.setText(String.valueOf(ciclos));
        this.MLFQ_4.setText(String.format("%.2f", throughtput)+" procesos/ciclos");
        this.MLFQ_6.setText(String.valueOf(cpu)+"%");
    }
    
    public void actualizarSPN(int finalizados, int IObound, int CPUbound, long ciclos, double throughtput, long cpu) {
        this.SPN_1.setText(String.valueOf(finalizados));
        this.SPN_2.setText(String.valueOf(IObound));
        this.SPN_3.setText(String.valueOf(CPUbound));
        this.SPN_5.setText(String.valueOf(ciclos));
        this.SPN_4.setText(String.format("%.2f", throughtput)+" procesos/ciclos");
        this.SPN_6.setText(String.valueOf(cpu)+"%");
    }
    
    public void actualizarSRT(int finalizados, int IObound, int CPUbound, long ciclos, double throughtput, long cpu) {
        this.SRT_1.setText(String.valueOf(finalizados));
        this.SRT_2.setText(String.valueOf(IObound));
        this.SRT_3.setText(String.valueOf(CPUbound));
        this.SRT_5.setText(String.valueOf(ciclos));
        this.SRT_4.setText(String.format("%.2f", throughtput)+" procesos/ciclos");
        this.SRT_6.setText(String.valueOf(cpu)+"%");
    }
    
    public void actualizarRR(int finalizados, int IObound, int CPUbound, long ciclos, double throughtput, long cpu) {
        this.RR_1.setText(String.valueOf(finalizados));
        this.RR_2.setText(String.valueOf(IObound));
        this.RR_3.setText(String.valueOf(CPUbound));
        this.RR_5.setText(String.valueOf(ciclos));
        this.RR_4.setText(String.format("%.2f", throughtput)+" procesos/ciclos");
        this.RR_6.setText(String.valueOf(cpu)+"%");
    }
    
    
    private void actualizarLog() {
    // Obtenemos la referencia a la cola de logs del simulador
    Cola<String> logs = miSimulador.getColaDeLogs();

    // Mientras haya mensajes en la cola, los procesamos
    while (!logs.estaVacia()) {
        String mensaje = logs.desencolar();
        if (mensaje != null) {
            // Añadimos el mensaje al JTextArea
            enEjec.append(mensaje + "\n");
        }
    }
    
    // --- Auto-scroll ---
    // Hacemos que el JTextArea siempre muestre la última línea añadida
    enEjec.setCaretPosition(enEjec.getDocument().getLength());
    
    
}
    
    
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane2 = new javax.swing.JTabbedPane();
        Simulador = new javax.swing.JPanel();
        jScrollPane17 = new javax.swing.JScrollPane();
        ListaListo = new javax.swing.JList<>();
        jLabel27 = new javax.swing.JLabel();
        IndicadorActivo = new javax.swing.JToggleButton();
        jScrollPane18 = new javax.swing.JScrollPane();
        ListaSuspListo = new javax.swing.JList<>();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jScrollPane20 = new javax.swing.JScrollPane();
        ListaSuspBloqueado = new javax.swing.JList<>();
        jLabel33 = new javax.swing.JLabel();
        jScrollPane21 = new javax.swing.JScrollPane();
        ListaNuevo = new javax.swing.JList<>();
        jScrollPane22 = new javax.swing.JScrollPane();
        ListaBloqueado = new javax.swing.JList<>();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        NombreProceso = new javax.swing.JTextField();
        NInstrucciones = new javax.swing.JSpinner();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        TipoProceso = new javax.swing.JComboBox<>();
        jLabel41 = new javax.swing.JLabel();
        ciclosGenerarSpinner = new javax.swing.JSpinner();
        jLabel42 = new javax.swing.JLabel();
        ciclosSatisfacerSpinner = new javax.swing.JSpinner();
        CrearP = new javax.swing.JButton();
        CrearPAuto = new javax.swing.JButton();
        jScrollPane24 = new javax.swing.JScrollPane();
        ListaFinalizado = new javax.swing.JList<>();
        jLabel43 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel44 = new javax.swing.JLabel();
        PoliticaPlanificacion = new javax.swing.JComboBox<>();
        CambiarPolitica = new javax.swing.JButton();
        IniciarPausarButton = new javax.swing.JToggleButton();
        modoAct = new javax.swing.JTextArea();
        CicloActual = new javax.swing.JTextArea();
        jScrollPane1 = new javax.swing.JScrollPane();
        enEjec = new javax.swing.JTextArea();
        jLabel72 = new javax.swing.JLabel();
        Graficos = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        Estadísticas = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        ProcesosTerminados = new javax.swing.JLabel();
        ProcesosIO = new javax.swing.JLabel();
        ProcesosCPU = new javax.swing.JLabel();
        Ciclos = new javax.swing.JLabel();
        Throughtput = new javax.swing.JLabel();
        UsoCPU = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        jLabel63 = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        jLabel65 = new javax.swing.JLabel();
        jLabel66 = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();
        jLabel70 = new javax.swing.JLabel();
        jLabel71 = new javax.swing.JLabel();
        FSFC_1 = new javax.swing.JLabel();
        FSFC_2 = new javax.swing.JLabel();
        FSFC_3 = new javax.swing.JLabel();
        FSFC_4 = new javax.swing.JLabel();
        FSFC_5 = new javax.swing.JLabel();
        FSFC_6 = new javax.swing.JLabel();
        SRT_1 = new javax.swing.JLabel();
        SRT_2 = new javax.swing.JLabel();
        SRT_3 = new javax.swing.JLabel();
        SRT_4 = new javax.swing.JLabel();
        SRT_5 = new javax.swing.JLabel();
        SRT_6 = new javax.swing.JLabel();
        RR_1 = new javax.swing.JLabel();
        RR_2 = new javax.swing.JLabel();
        RR_3 = new javax.swing.JLabel();
        RR_4 = new javax.swing.JLabel();
        RR_5 = new javax.swing.JLabel();
        RR_6 = new javax.swing.JLabel();
        HRRN_1 = new javax.swing.JLabel();
        HRRN_2 = new javax.swing.JLabel();
        HRRN_3 = new javax.swing.JLabel();
        HRRN_4 = new javax.swing.JLabel();
        HRRN_5 = new javax.swing.JLabel();
        HRRN_6 = new javax.swing.JLabel();
        SPN_1 = new javax.swing.JLabel();
        SPN_2 = new javax.swing.JLabel();
        SPN_3 = new javax.swing.JLabel();
        SPN_4 = new javax.swing.JLabel();
        SPN_5 = new javax.swing.JLabel();
        SPN_6 = new javax.swing.JLabel();
        MLFQ_1 = new javax.swing.JLabel();
        MLFQ_2 = new javax.swing.JLabel();
        MLFQ_3 = new javax.swing.JLabel();
        MLFQ_4 = new javax.swing.JLabel();
        MLFQ_5 = new javax.swing.JLabel();
        MLFQ_6 = new javax.swing.JLabel();
        Configuracion = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        DurCicloReloj = new javax.swing.JSpinner();
        jPanel6 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        DurCicloRelojAct = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        CambiarCicloReloj = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTabbedPane2.setBackground(new java.awt.Color(255, 153, 153));

        Simulador.setBackground(new java.awt.Color(102, 204, 255));

        jScrollPane17.setViewportView(ListaListo);

        jLabel27.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setText("SupsListo");

        IndicadorActivo.setBackground(new java.awt.Color(51, 255, 0));
        IndicadorActivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                IndicadorActivoActionPerformed(evt);
            }
        });

        jScrollPane18.setViewportView(ListaSuspListo);

        jLabel28.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel28.setText("Simulador");

        jLabel29.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setText("Bloqueado");

        jLabel30.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel30.setText("Ciclo de Reloj:");

        jLabel31.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setText("Listo");

        jLabel32.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel32.setText("Nuevo");

        jScrollPane20.setViewportView(ListaSuspBloqueado);

        jLabel33.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel33.setText("Modo:");

        jScrollPane21.setViewportView(ListaNuevo);

        jScrollPane22.setViewportView(ListaBloqueado);

        jLabel34.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jLabel35.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel35.setText("SuspBloqueado");

        jPanel8.setBackground(new java.awt.Color(204, 204, 255));

        jLabel36.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel36.setText("Crear Proceso");

        jLabel37.setText("Nombre:");

        NInstrucciones.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));

        jLabel38.setText("Num. Instrucciones:");

        jLabel39.setText("Tipo:");

        TipoProceso.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "I/O Bound", "CPU Bound" }));
        TipoProceso.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                TipoProcesoItemStateChanged(evt);
            }
        });
        TipoProceso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TipoProcesoActionPerformed(evt);
            }
        });

        jLabel41.setText("Ciclos para generar exepcion:");

        ciclosGenerarSpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));

        jLabel42.setText("Ciclos para satisfacer exepcion:");

        ciclosSatisfacerSpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));

        CrearP.setText("Crear Proceso");
        CrearP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CrearPActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel36)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel40)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel41)
                            .addComponent(jLabel37, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel38, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel39, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(TipoProceso, 0, 135, Short.MAX_VALUE)
                            .addComponent(NInstrucciones)
                            .addComponent(NombreProceso)
                            .addComponent(ciclosGenerarSpinner)))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel42)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ciclosSatisfacerSpinner)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(117, 117, 117)
                .addComponent(CrearP)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel36)
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel37)
                    .addComponent(NombreProceso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(NInstrucciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel38))
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel39)
                    .addComponent(TipoProceso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel40)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ciclosGenerarSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel42)
                    .addComponent(ciclosSatisfacerSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(CrearP)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        CrearPAuto.setText("Crear 10 procesos automáticamente");
        CrearPAuto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CrearPAutoActionPerformed(evt);
            }
        });

        jScrollPane24.setViewportView(ListaFinalizado);

        jLabel43.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel43.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel43.setText("Finalizado");

        jPanel1.setBackground(new java.awt.Color(204, 204, 255));

        jLabel44.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel44.setText("Cambiar Política de Planificación");

        PoliticaPlanificacion.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "FCFS", "HRRN", "MLFQ", "RoundRobin", "SPN", "SRT" }));

        CambiarPolitica.setText("Guardar Cambios");
        CambiarPolitica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CambiarPoliticaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel44, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(PoliticaPlanificacion, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(CambiarPolitica, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(347, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel44)
                .addGap(18, 18, 18)
                .addComponent(PoliticaPlanificacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(CambiarPolitica)
                .addContainerGap(34, Short.MAX_VALUE))
        );

        IniciarPausarButton.setText("Iniciar");
        IniciarPausarButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                IniciarPausarButtonItemStateChanged(evt);
            }
        });
        IniciarPausarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                IniciarPausarButtonActionPerformed(evt);
            }
        });

        modoAct.setColumns(20);
        modoAct.setRows(5);

        CicloActual.setColumns(20);
        CicloActual.setRows(5);

        enEjec.setColumns(20);
        enEjec.setRows(5);
        jScrollPane1.setViewportView(enEjec);

        jLabel72.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel72.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel72.setText("Log de ejecución");

        javax.swing.GroupLayout SimuladorLayout = new javax.swing.GroupLayout(Simulador);
        Simulador.setLayout(SimuladorLayout);
        SimuladorLayout.setHorizontalGroup(
            SimuladorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SimuladorLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(SimuladorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(SimuladorLayout.createSequentialGroup()
                        .addGroup(SimuladorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(SimuladorLayout.createSequentialGroup()
                                .addGroup(SimuladorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane21, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(IniciarPausarButton, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(SimuladorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(SimuladorLayout.createSequentialGroup()
                                        .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(modoAct, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(SimuladorLayout.createSequentialGroup()
                                        .addComponent(jScrollPane22, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jScrollPane17, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jScrollPane24, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(SimuladorLayout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(73, 73, 73)
                                .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(57, 57, 57)
                                .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(63, 63, 63)
                                .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(38, 38, 38)
                        .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(55, 55, 55)
                        .addComponent(jLabel35)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(SimuladorLayout.createSequentialGroup()
                        .addComponent(IndicadorActivo)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 286, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(SimuladorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(SimuladorLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(CicloActual, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(SimuladorLayout.createSequentialGroup()
                                .addGap(124, 124, 124)
                                .addComponent(jLabel72, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(SimuladorLayout.createSequentialGroup()
                        .addGap(276, 276, 276)
                        .addGroup(SimuladorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(SimuladorLayout.createSequentialGroup()
                                .addComponent(jScrollPane18, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane20, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 530, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(281, 281, 281)
                        .addGroup(SimuladorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(CrearPAuto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(SimuladorLayout.createSequentialGroup()
                                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(4, 4, 4)))))
                .addGap(31, 31, 31))
            .addGroup(SimuladorLayout.createSequentialGroup()
                .addGap(645, 645, 645)
                .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        SimuladorLayout.setVerticalGroup(
            SimuladorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SimuladorLayout.createSequentialGroup()
                .addGroup(SimuladorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(SimuladorLayout.createSequentialGroup()
                        .addGroup(SimuladorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(SimuladorLayout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addGroup(SimuladorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(IndicadorActivo, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel28)))
                            .addGroup(SimuladorLayout.createSequentialGroup()
                                .addGap(34, 34, 34)
                                .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, SimuladorLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(CicloActual, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(78, 78, 78))
                    .addGroup(SimuladorLayout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(jLabel72)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(jLabel34)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(SimuladorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29)
                    .addComponent(jLabel31)
                    .addComponent(jLabel32)
                    .addComponent(jLabel43)
                    .addComponent(jLabel27)
                    .addComponent(jLabel35))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(SimuladorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(SimuladorLayout.createSequentialGroup()
                        .addGroup(SimuladorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane21, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane22, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane17, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane24, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane18)
                            .addComponent(jScrollPane20))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(SimuladorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(modoAct, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, SimuladorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel33)
                                .addComponent(IniciarPausarButton)))
                        .addGap(16, 16, 16))
                    .addGroup(SimuladorLayout.createSequentialGroup()
                        .addComponent(CrearPAuto)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(132, Short.MAX_VALUE))))
        );

        jTabbedPane2.addTab("Simulador", Simulador);

        Graficos.setBackground(new java.awt.Color(204, 255, 204));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1756, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 761, Short.MAX_VALUE)
        );

        Graficos.addTab("Comparación Throughputs", jPanel2);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1756, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 761, Short.MAX_VALUE)
        );

        Graficos.addTab("Comparación I/O Bound y CPU Bound", jPanel3);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1756, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 761, Short.MAX_VALUE)
        );

        Graficos.addTab("Comparación Tiempo Políticas", jPanel4);

        jTabbedPane2.addTab("Graficos", Graficos);

        Estadísticas.setBackground(new java.awt.Color(204, 204, 255));

        jPanel7.setBackground(new java.awt.Color(153, 153, 255));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setText("Estadísticas generales");

        jLabel6.setText("Procesos totales terminados:");

        jLabel7.setText("Procesos I/O Bound terminados:");

        jLabel8.setText("Procesos CPU Bound terminados:");

        jLabel9.setText("Ciclo actual:");

        jLabel10.setText("Throughtput general:");

        jLabel21.setText("Utilización del CPU general:");

        ProcesosTerminados.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        ProcesosTerminados.setText("0");

        ProcesosIO.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        ProcesosIO.setText("0");

        ProcesosCPU.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        ProcesosCPU.setText("0");

        Ciclos.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        Ciclos.setText("0");

        Throughtput.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        Throughtput.setText("0.0 Procesos/Ciclos");

        UsoCPU.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        UsoCPU.setText("0%");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(71, 71, 71)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(ProcesosIO, javax.swing.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE)
                                    .addComponent(ProcesosCPU, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addGap(18, 18, 18)
                                .addComponent(ProcesosTerminados, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 512, Short.MAX_VALUE)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addComponent(jLabel9)
                                        .addGap(18, 18, 18)
                                        .addComponent(Ciclos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addComponent(jLabel21))
                                .addGap(18, 18, 18)
                                .addComponent(UsoCPU, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addGap(18, 18, 18)
                                .addComponent(Throughtput, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(612, Short.MAX_VALUE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel5)
                .addGap(27, 27, 27)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel9)
                    .addComponent(ProcesosTerminados)
                    .addComponent(Ciclos))
                .addGap(28, 28, 28)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel10)
                    .addComponent(ProcesosIO)
                    .addComponent(Throughtput))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel21)
                    .addComponent(ProcesosCPU)
                    .addComponent(UsoCPU))
                .addContainerGap(73, Short.MAX_VALUE))
        );

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel11.setText("Estadísticas SPN");

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel12.setText("Estadísticas SRT");

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel13.setText("Estadísticas RoundRobin");

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel14.setText("Estadísticas FSFC");

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel15.setText("Estadísticas HRRN");

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel16.setText("Estadísticas MLFQ");

        jLabel17.setText("Procesos totales ejecutados durante política:");

        jLabel18.setText("Throughtput durante la política:");

        jLabel19.setText("Procesos I/O Bound ejecutados durante política:");

        jLabel20.setText("Procesos CPU Bound ejecutados durante política:");

        jLabel22.setText("Ciclos durante la política:");

        jLabel23.setText("Procesos totales ejecutados durante política:");

        jLabel24.setText("Throughtput durante la política:");

        jLabel25.setText("Procesos I/O Bound ejecutados durante política:");

        jLabel26.setText("Procesos CPU Bound ejecutados durante política:");

        jLabel45.setText("Ciclos durante la política:");

        jLabel46.setText("Procesos totales ejecutados durante política:");

        jLabel47.setText("Throughtput durante la política:");

        jLabel48.setText("Procesos I/O Bound ejecutados durante política:");

        jLabel49.setText("Procesos CPU Bound ejecutados durante política:");

        jLabel50.setText("Ciclos durante la política:");

        jLabel51.setText("Procesos totales ejecutados durante política:");

        jLabel52.setText("Throughtput durante la política:");

        jLabel53.setText("Procesos I/O Bound ejecutados durante política:");

        jLabel54.setText("Procesos CPU Bound ejecutados durante política:");

        jLabel55.setText("Ciclos durante la política:");

        jLabel56.setText("Procesos totales ejecutados durante política:");

        jLabel57.setText("Throughtput durante la política:");

        jLabel58.setText("Procesos I/O Bound ejecutados durante política:");

        jLabel59.setText("Procesos CPU Bound ejecutados durante política:");

        jLabel60.setText("Ciclos durante la política:");

        jLabel61.setText("Procesos totales ejecutados durante política:");

        jLabel62.setText("Throughtput durante la política:");

        jLabel63.setText("Procesos I/O Bound ejecutados durante política:");

        jLabel64.setText("Procesos CPU Bound ejecutados durante política:");

        jLabel65.setText("Ciclos durante la política:");

        jLabel66.setText("Utilización del CPU durante la política:");

        jLabel67.setText("Utilización del CPU durante la política:");

        jLabel68.setText("Utilización del CPU durante la política:");

        jLabel69.setText("Utilización del CPU durante la política:");

        jLabel70.setText("Utilización del CPU durante la política:");

        jLabel71.setText("Utilización del CPU durante la política:");

        FSFC_1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        FSFC_1.setText("0");

        FSFC_2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        FSFC_2.setText("0");

        FSFC_3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        FSFC_3.setText("0");

        FSFC_4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        FSFC_4.setText("0.0 Procesos/Ciclos");

        FSFC_5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        FSFC_5.setText("0");

        FSFC_6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        FSFC_6.setText("0%");

        SRT_1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        SRT_1.setText("0");

        SRT_2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        SRT_2.setText("0");

        SRT_3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        SRT_3.setText("0");

        SRT_4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        SRT_4.setText("0.0 Procesos/Ciclos");

        SRT_5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        SRT_5.setText("0");

        SRT_6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        SRT_6.setText("0%");

        RR_1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        RR_1.setText("0");

        RR_2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        RR_2.setText("0");

        RR_3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        RR_3.setText("0");

        RR_4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        RR_4.setText("0.0 Procesos/Ciclos");

        RR_5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        RR_5.setText("0");

        RR_6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        RR_6.setText("0%");

        HRRN_1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        HRRN_1.setText("0");

        HRRN_2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        HRRN_2.setText("0");

        HRRN_3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        HRRN_3.setText("0");

        HRRN_4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        HRRN_4.setText("0.0 Procesos/Ciclos");

        HRRN_5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        HRRN_5.setText("0");

        HRRN_6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        HRRN_6.setText("0%");

        SPN_1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        SPN_1.setText("0");

        SPN_2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        SPN_2.setText("0");

        SPN_3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        SPN_3.setText("0");

        SPN_4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        SPN_4.setText("0.0 Procesos/Ciclos");

        SPN_5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        SPN_5.setText("0");

        SPN_6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        SPN_6.setText("0%");

        MLFQ_1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        MLFQ_1.setText("0");

        MLFQ_2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        MLFQ_2.setText("0");

        MLFQ_3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        MLFQ_3.setText("0");

        MLFQ_4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        MLFQ_4.setText("0.0 Procesos/Ciclos");

        MLFQ_5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        MLFQ_5.setText("0");

        MLFQ_6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        MLFQ_6.setText("0%");

        javax.swing.GroupLayout EstadísticasLayout = new javax.swing.GroupLayout(Estadísticas);
        Estadísticas.setLayout(EstadísticasLayout);
        EstadísticasLayout.setHorizontalGroup(
            EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(EstadísticasLayout.createSequentialGroup()
                .addGap(85, 85, 85)
                .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(EstadísticasLayout.createSequentialGroup()
                        .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(EstadísticasLayout.createSequentialGroup()
                                .addComponent(jLabel17)
                                .addGap(18, 18, 18)
                                .addComponent(FSFC_1, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(EstadísticasLayout.createSequentialGroup()
                                .addComponent(jLabel19)
                                .addGap(18, 18, 18)
                                .addComponent(FSFC_2, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(EstadísticasLayout.createSequentialGroup()
                                .addComponent(jLabel61)
                                .addGap(18, 18, 18)
                                .addComponent(SRT_1, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(EstadísticasLayout.createSequentialGroup()
                                .addComponent(jLabel65)
                                .addGap(18, 18, 18)
                                .addComponent(SRT_5, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(EstadísticasLayout.createSequentialGroup()
                                .addComponent(jLabel64)
                                .addGap(18, 18, 18)
                                .addComponent(SRT_3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(EstadísticasLayout.createSequentialGroup()
                                .addComponent(jLabel63)
                                .addGap(18, 18, 18)
                                .addComponent(SRT_2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(EstadísticasLayout.createSequentialGroup()
                                .addComponent(jLabel62)
                                .addGap(18, 18, 18)
                                .addComponent(SRT_4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(EstadísticasLayout.createSequentialGroup()
                                .addComponent(jLabel20)
                                .addGap(18, 18, 18)
                                .addComponent(FSFC_3, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(EstadísticasLayout.createSequentialGroup()
                                .addComponent(jLabel18)
                                .addGap(18, 18, 18)
                                .addComponent(FSFC_4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, EstadísticasLayout.createSequentialGroup()
                                .addComponent(jLabel22)
                                .addGap(18, 18, 18)
                                .addComponent(FSFC_5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 246, Short.MAX_VALUE)
                        .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(EstadísticasLayout.createSequentialGroup()
                                .addComponent(jLabel45)
                                .addGap(18, 18, 18)
                                .addComponent(RR_5, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(EstadísticasLayout.createSequentialGroup()
                                .addComponent(jLabel56)
                                .addGap(18, 18, 18)
                                .addComponent(HRRN_1, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(EstadísticasLayout.createSequentialGroup()
                                .addComponent(jLabel60)
                                .addGap(18, 18, 18)
                                .addComponent(HRRN_5, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(EstadísticasLayout.createSequentialGroup()
                                .addComponent(jLabel23)
                                .addGap(18, 18, 18)
                                .addComponent(RR_1, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(EstadísticasLayout.createSequentialGroup()
                                .addComponent(jLabel25)
                                .addGap(18, 18, 18)
                                .addComponent(RR_2, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(EstadísticasLayout.createSequentialGroup()
                                .addComponent(jLabel58)
                                .addGap(18, 18, 18)
                                .addComponent(HRRN_2, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(EstadísticasLayout.createSequentialGroup()
                                .addComponent(jLabel26)
                                .addGap(18, 18, 18)
                                .addComponent(RR_3, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(EstadísticasLayout.createSequentialGroup()
                                .addComponent(jLabel24)
                                .addGap(18, 18, 18)
                                .addComponent(RR_4, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, EstadísticasLayout.createSequentialGroup()
                                    .addComponent(jLabel57)
                                    .addGap(18, 18, 18)
                                    .addComponent(HRRN_4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, EstadísticasLayout.createSequentialGroup()
                                    .addComponent(jLabel59)
                                    .addGap(18, 18, 18)
                                    .addComponent(HRRN_3, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(170, 170, 170))
                    .addGroup(EstadísticasLayout.createSequentialGroup()
                        .addComponent(jLabel67)
                        .addGap(18, 18, 18)
                        .addComponent(SRT_6, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel68)
                        .addGap(18, 18, 18)
                        .addComponent(HRRN_6, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(246, 246, 246))
                    .addGroup(EstadísticasLayout.createSequentialGroup()
                        .addComponent(jLabel66)
                        .addGap(18, 18, 18)
                        .addComponent(FSFC_6, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel71)
                        .addGap(18, 18, 18)
                        .addComponent(RR_6, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(232, 232, 232)))
                .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(EstadísticasLayout.createSequentialGroup()
                            .addComponent(jLabel55)
                            .addGap(18, 18, 18)
                            .addComponent(MLFQ_5, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(EstadísticasLayout.createSequentialGroup()
                            .addComponent(jLabel46)
                            .addGap(18, 18, 18)
                            .addComponent(SPN_1, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(EstadísticasLayout.createSequentialGroup()
                            .addComponent(jLabel48)
                            .addGap(18, 18, 18)
                            .addComponent(SPN_2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(EstadísticasLayout.createSequentialGroup()
                            .addComponent(jLabel49)
                            .addGap(18, 18, 18)
                            .addComponent(SPN_3, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(EstadísticasLayout.createSequentialGroup()
                            .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, EstadísticasLayout.createSequentialGroup()
                                    .addComponent(jLabel50)
                                    .addGap(18, 18, 18)
                                    .addComponent(SPN_5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addComponent(jLabel51, javax.swing.GroupLayout.Alignment.LEADING))
                            .addGap(18, 18, 18)
                            .addComponent(MLFQ_1, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(EstadísticasLayout.createSequentialGroup()
                            .addComponent(jLabel70)
                            .addGap(18, 18, 18)
                            .addComponent(SPN_6, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(EstadísticasLayout.createSequentialGroup()
                            .addComponent(jLabel54)
                            .addGap(18, 18, 18)
                            .addComponent(MLFQ_3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(EstadísticasLayout.createSequentialGroup()
                            .addComponent(jLabel53)
                            .addGap(18, 18, 18)
                            .addComponent(MLFQ_2, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(EstadísticasLayout.createSequentialGroup()
                            .addComponent(jLabel69)
                            .addGap(18, 18, 18)
                            .addComponent(MLFQ_6, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(EstadísticasLayout.createSequentialGroup()
                        .addComponent(jLabel52)
                        .addGap(18, 18, 18)
                        .addComponent(MLFQ_4, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(EstadísticasLayout.createSequentialGroup()
                        .addComponent(jLabel47)
                        .addGap(18, 18, 18)
                        .addComponent(SPN_4, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(165, 165, 165))
        );
        EstadísticasLayout.setVerticalGroup(
            EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(EstadísticasLayout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(jLabel13)
                    .addComponent(jLabel11))
                .addGap(18, 18, 18)
                .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(EstadísticasLayout.createSequentialGroup()
                        .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17)
                            .addComponent(FSFC_1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel19)
                            .addComponent(FSFC_2))
                        .addGap(18, 18, 18)
                        .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel20)
                            .addComponent(FSFC_3))
                        .addGap(13, 13, 13)
                        .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel18)
                            .addComponent(FSFC_4))
                        .addGap(18, 18, 18)
                        .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel22)
                            .addComponent(FSFC_5)))
                    .addGroup(EstadísticasLayout.createSequentialGroup()
                        .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel23)
                            .addComponent(RR_1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel25)
                            .addComponent(RR_2))
                        .addGap(18, 18, 18)
                        .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel26)
                            .addComponent(RR_3))
                        .addGap(13, 13, 13)
                        .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel24)
                            .addComponent(RR_4))
                        .addGap(18, 18, 18)
                        .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel45)
                            .addComponent(RR_5)))
                    .addGroup(EstadísticasLayout.createSequentialGroup()
                        .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel46)
                            .addComponent(SPN_1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel48)
                            .addComponent(SPN_2))
                        .addGap(18, 18, 18)
                        .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel49)
                            .addComponent(SPN_3))
                        .addGap(13, 13, 13)
                        .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel47)
                            .addComponent(SPN_4))
                        .addGap(18, 18, 18)
                        .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel50)
                            .addComponent(SPN_5))))
                .addGap(18, 18, 18)
                .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel66)
                        .addComponent(FSFC_6))
                    .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel70)
                        .addComponent(SPN_6))
                    .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel71)
                        .addComponent(RR_6)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jLabel15)
                    .addComponent(jLabel16))
                .addGap(18, 18, 18)
                .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(EstadísticasLayout.createSequentialGroup()
                        .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel51)
                            .addComponent(MLFQ_1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel53)
                            .addComponent(MLFQ_2))
                        .addGap(18, 18, 18)
                        .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel54)
                            .addComponent(MLFQ_3))
                        .addGap(13, 13, 13)
                        .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel52)
                            .addComponent(MLFQ_4))
                        .addGap(18, 18, 18)
                        .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel55)
                            .addComponent(MLFQ_5)))
                    .addGroup(EstadísticasLayout.createSequentialGroup()
                        .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel56)
                            .addComponent(HRRN_1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel58)
                            .addComponent(HRRN_2))
                        .addGap(18, 18, 18)
                        .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel59)
                            .addComponent(HRRN_3))
                        .addGap(13, 13, 13)
                        .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel57)
                            .addComponent(HRRN_4))
                        .addGap(18, 18, 18)
                        .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel60)
                            .addComponent(HRRN_5)))
                    .addGroup(EstadísticasLayout.createSequentialGroup()
                        .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel61)
                            .addComponent(SRT_1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel63)
                            .addComponent(SRT_2))
                        .addGap(18, 18, 18)
                        .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel64)
                            .addComponent(SRT_3))
                        .addGap(13, 13, 13)
                        .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel62)
                            .addComponent(SRT_4))
                        .addGap(18, 18, 18)
                        .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel65)
                            .addComponent(SRT_5))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel67)
                        .addComponent(SRT_6))
                    .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel68)
                        .addComponent(HRRN_6))
                    .addGroup(EstadísticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel69)
                        .addComponent(MLFQ_6)))
                .addGap(46, 46, 46))
        );

        jTabbedPane2.addTab("Estadísticas", Estadísticas);

        Configuracion.setBackground(new java.awt.Color(153, 153, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("Configuración");

        jPanel5.setBackground(new java.awt.Color(204, 204, 255));

        DurCicloReloj.setModel(new javax.swing.SpinnerNumberModel(0.25d, 0.0d, null, 0.25d));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setText("Duración Actual del Ciclo de reloj");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setText("Segundos");

        DurCicloRelojAct.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        DurCicloRelojAct.setText("0,25");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(jLabel2))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(87, 87, 87)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3)
                            .addComponent(DurCicloRelojAct))))
                .addContainerGap(17, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                .addComponent(DurCicloRelojAct)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addGap(35, 35, 35))
        );

        jLabel4.setText("Cambiar duración de ciclos de reloj");

        CambiarCicloReloj.setText("Guardar Cambios");
        CambiarCicloReloj.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CambiarCicloRelojActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap(17, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(DurCicloReloj, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(CambiarCicloReloj)
                        .addGap(44, 44, 44)))
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(54, 54, 54)
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(DurCicloReloj, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(CambiarCicloReloj)))
                .addContainerGap(40, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout ConfiguracionLayout = new javax.swing.GroupLayout(Configuracion);
        Configuracion.setLayout(ConfiguracionLayout);
        ConfiguracionLayout.setHorizontalGroup(
            ConfiguracionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ConfiguracionLayout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addGroup(ConfiguracionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(1215, Short.MAX_VALUE))
        );
        ConfiguracionLayout.setVerticalGroup(
            ConfiguracionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ConfiguracionLayout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(454, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Configuración", Configuracion);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane2)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane2)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void TipoProcesoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TipoProcesoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TipoProcesoActionPerformed

    private void CrearPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CrearPActionPerformed
       try {
        // --- 1. Recolectar datos de la Interfaz ---
        String nombre = NombreProceso.getText().trim();
        int numInstrucciones = (Integer) NInstrucciones.getValue();
        String tipoSeleccionado = (String) TipoProceso.getSelectedItem();

        // --- 2. Validar los datos de entrada ---
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre del proceso no puede estar vacío.", "Error de Entrada", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (numInstrucciones <= 0) {
            JOptionPane.showMessageDialog(this, "El número de instrucciones debe ser mayor que cero.", "Error de Entrada", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // --- 3. Solicitar la creación del proceso al Simulador (LA ÚNICA FORMA) ---
        if ("I/O Bound".equals(tipoSeleccionado)) {
            int ciclosGenerar = (Integer) ciclosGenerarSpinner.getValue();
            int ciclosSatisfacer = (Integer) ciclosSatisfacerSpinner.getValue();

            if (ciclosGenerar <= 0 || ciclosSatisfacer <= 0) {
                JOptionPane.showMessageDialog(this, "Para un proceso I/O Bound, los ciclos de excepción deben ser mayores que cero.", "Error de Entrada", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Llamamos al método centralizado del simulador
            miSimulador.solicitarCreacionProceso(nombre, numInstrucciones, Proceso.TipoProceso.IO_BOUND, ciclosGenerar, ciclosSatisfacer);

        } else { // "CPU Bound"
            // Llamamos al método centralizado del simulador
            miSimulador.solicitarCreacionProceso(nombre, numInstrucciones, Proceso.TipoProceso.CPU_BOUND, -1, -1);
        }
       
        // --- 4. Confirmar al usuario y limpiar la interfaz ---
        System.out.println("Solicitud de creación para proceso '" + nombre + "' enviada al simulador.");
        JOptionPane.showMessageDialog(this, "Proceso '" + nombre + "' creado exitosamente.", "Proceso Creado", JOptionPane.INFORMATION_MESSAGE);
        
        // Limpiar los campos para el siguiente proceso
        NombreProceso.setText("");
        NInstrucciones.setValue(0);
        TipoProceso.setSelectedIndex(0);
        ciclosGenerarSpinner.setValue(0);
        ciclosSatisfacerSpinner.setValue(0);
        
    } catch (Exception ex) {
        // Captura cualquier otro error inesperado
        JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
    }//GEN-LAST:event_CrearPActionPerformed

    private void CrearPAutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CrearPAutoActionPerformed
    java.util.Random random = new java.util.Random();
    
    // se puede modificar el numero de procesos quue genera cambiando el for.
    for (int i = 0; i < 10; i++) {
        // Generar el nombre usando el contador de la clase
        String nombre = "Proceso" + this.autoProcessCounter; 
        
        int numInstrucciones = random.nextInt(21) + 10;

        
        if (random.nextBoolean()) {
            // I/O BOUND
            int ciclosParaGenerar;
            int ciclosParaSatisfacer;
            do {
                ciclosParaGenerar = random.nextInt(Math.max(1, numInstrucciones / 2)) + 2;
                ciclosParaSatisfacer = random.nextInt(13) + 3;
            } while (ciclosParaGenerar >= numInstrucciones);
            
            if (miSimulador != null) {
                miSimulador.solicitarCreacionProceso(
                    nombre, 
                    numInstrucciones, 
                    Proceso.TipoProceso.IO_BOUND, 
                    ciclosParaGenerar, 
                    ciclosParaSatisfacer
                );
            }
        } else {
            // CPU BOUND
            if (miSimulador != null) {
                miSimulador.solicitarCreacionProceso(
                    nombre, 
                    numInstrucciones, 
                    Proceso.TipoProceso.CPU_BOUND, 
                    -1, 
                    -1
                );
            }
        }
        
        // Incrementar el contador para el siguiente proceso ---
        this.autoProcessCounter++;
    }
    
    JOptionPane.showMessageDialog(this, "Se han solicitado 10 nuevos procesos automáticamente.", "Solicitud Enviada", JOptionPane.INFORMATION_MESSAGE);


    }//GEN-LAST:event_CrearPAutoActionPerformed

    private void TipoProcesoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_TipoProcesoItemStateChanged
       // 1. Verificar que el estado es "SELECTED" para evitar doble ejecución
    if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {

        // 2. Obtener la opción seleccionada como String
        String tipoSeleccionado = (String) TipoProceso.getSelectedItem();

        // 3. Evaluar la opción
        if ("CPU Bound".equals(tipoSeleccionado)) {
            // Si es CPU Bound:
            // Deshabilitar ambos Spinners
            ciclosGenerarSpinner.setEnabled(false);
            ciclosSatisfacerSpinner.setEnabled(false);

            // Opcional: Poner sus valores a 0 para claridad
            ciclosGenerarSpinner.setValue(0);
            ciclosSatisfacerSpinner.setValue(0);

        } else if ("I/O Bound".equals(tipoSeleccionado)) {
            // Si es I/O Bound:
            // Habilitar ambos Spinners
            ciclosGenerarSpinner.setEnabled(true);
            ciclosSatisfacerSpinner.setEnabled(true);
        }
    }

    }//GEN-LAST:event_TipoProcesoItemStateChanged

    private void CambiarPoliticaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CambiarPoliticaActionPerformed
                                           
    
                                                   
    // 1. Obtener el nombre del algoritmo seleccionado en el JComboBox
    String nombreAlgoritmo = (String) PoliticaPlanificacion.getSelectedItem();
    
    // 2. Crear la instancia del planificador correspondiente
    EstrategiaPlanificacion nuevoPlanificador = null;
    
    switch (nombreAlgoritmo) {
        case "FCFS":
            nuevoPlanificador = new FCFS();
            break;
        case "HRRN":
            nuevoPlanificador = new HRRN();
            break;
        case "MLFQ":
            nuevoPlanificador = new MLFQ();
            break;
        case "RoundRobin":
            nuevoPlanificador = new RoundRobin();
            break;
        case "SPN":
            nuevoPlanificador = new SPN();
            break;
        case "SRT":
            nuevoPlanificador = new SRT();
            break;
        default:
            // Caso por si acaso algo sale mal
            System.err.println("Algoritmo de planificación desconocido: " + nombreAlgoritmo);
            return;
    }
    
    // 3. Pasar el nuevo planificador al simulador
    if (miSimulador != null && nuevoPlanificador != null) {
        miSimulador.setPlanificador(nuevoPlanificador);
        JOptionPane.showMessageDialog(this, "La política de planificación ha cambiado a " + nombreAlgoritmo, "Cambio Exitoso", JOptionPane.INFORMATION_MESSAGE);
    }

    }//GEN-LAST:event_CambiarPoliticaActionPerformed

    private void IniciarPausarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_IniciarPausarButtonActionPerformed
         // 1. Preguntarle al botón si está actualmente en estado "seleccionado".
    // El clic acaba de cambiar su estado, así que si ahora está seleccionado,
    // significa que el usuario acaba de INICIAR/REANUDAR.
    if (IniciarPausarButton.isSelected()) {
        // El botón ha sido PRESIONADO (estado "Iniciar/Reanudar")
        miSimulador.iniciar();
        IniciarPausarButton.setText("Pausar");
        IndicadorActivo.setSelected(true); // Sincroniza el otro botón
        IndicadorActivo.setBackground(java.awt.Color.GREEN);
        
    } else {
        // Si no está seleccionado, significa que el usuario acaba de PAUSAR.
        miSimulador.detener();
        IniciarPausarButton.setText("Iniciar");
        IndicadorActivo.setSelected(false); // Sincroniza el otro botón
        IndicadorActivo.setBackground(java.awt.Color.RED);
    }

    }//GEN-LAST:event_IniciarPausarButtonActionPerformed

    private void IniciarPausarButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_IniciarPausarButtonItemStateChanged
       
        

    }//GEN-LAST:event_IniciarPausarButtonItemStateChanged

    private void IndicadorActivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_IndicadorActivoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_IndicadorActivoActionPerformed

    private void CambiarCicloRelojActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CambiarCicloRelojActionPerformed
      try {
        Object spinnerValue = DurCicloReloj.getValue();
        Double duracionEnSegundos = (Double) spinnerValue;

        long duracionEnMilisegundos = (long) (duracionEnSegundos * 1000.0);

        if (duracionEnMilisegundos < 0) {
            JOptionPane.showMessageDialog(this, "La duración del ciclo no puede ser negativa.", "Valor Inválido", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 1. Comunicar el cambio al simulador (esto no cambia)
        if (miSimulador != null) {
            miSimulador.setDuracionCiclo(duracionEnMilisegundos);
        } else {
            JOptionPane.showMessageDialog(this, "Error: El simulador no está inicializado.", "Error Interno", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // 2. Actualizar el display INMEDIATAMENTE después de cambiar el valor.
        // Ahora esta es la única vez que se actualiza el display de duración.
        actualizarDisplayDuracionCiclo();

        // 3. Mostrar un mensaje de confirmación (esto no cambia)
        JOptionPane.showMessageDialog(this, "La duración del ciclo se ha actualizado a " + duracionEnSegundos + " segundos.", "Cambio Guardado", JOptionPane.INFORMATION_MESSAGE);

    } catch (ClassCastException ex) {
        JOptionPane.showMessageDialog(this, "Hubo un error al leer el valor de duración.", "Error de Tipo", JOptionPane.ERROR_MESSAGE);
    }

    }//GEN-LAST:event_CambiarCicloRelojActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new Menu().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton CambiarCicloReloj;
    private javax.swing.JButton CambiarPolitica;
    private javax.swing.JTextArea CicloActual;
    private javax.swing.JLabel Ciclos;
    private javax.swing.JPanel Configuracion;
    private javax.swing.JButton CrearP;
    private javax.swing.JButton CrearPAuto;
    private javax.swing.JSpinner DurCicloReloj;
    private javax.swing.JLabel DurCicloRelojAct;
    private javax.swing.JPanel Estadísticas;
    private javax.swing.JLabel FSFC_1;
    private javax.swing.JLabel FSFC_2;
    private javax.swing.JLabel FSFC_3;
    private javax.swing.JLabel FSFC_4;
    private javax.swing.JLabel FSFC_5;
    private javax.swing.JLabel FSFC_6;
    private javax.swing.JTabbedPane Graficos;
    private javax.swing.JLabel HRRN_1;
    private javax.swing.JLabel HRRN_2;
    private javax.swing.JLabel HRRN_3;
    private javax.swing.JLabel HRRN_4;
    private javax.swing.JLabel HRRN_5;
    private javax.swing.JLabel HRRN_6;
    private javax.swing.JToggleButton IndicadorActivo;
    private javax.swing.JToggleButton IniciarPausarButton;
    private javax.swing.JList<EstructuraDeDatos.Proceso> ListaBloqueado;
    private javax.swing.JList<EstructuraDeDatos.Proceso> ListaFinalizado;
    private javax.swing.JList<EstructuraDeDatos.Proceso> ListaListo;
    private javax.swing.JList<EstructuraDeDatos.Proceso> ListaNuevo;
    private javax.swing.JList<EstructuraDeDatos.Proceso> ListaSuspBloqueado;
    private javax.swing.JList<EstructuraDeDatos.Proceso> ListaSuspListo;
    private javax.swing.JLabel MLFQ_1;
    private javax.swing.JLabel MLFQ_2;
    private javax.swing.JLabel MLFQ_3;
    private javax.swing.JLabel MLFQ_4;
    private javax.swing.JLabel MLFQ_5;
    private javax.swing.JLabel MLFQ_6;
    private javax.swing.JSpinner NInstrucciones;
    private javax.swing.JTextField NombreProceso;
    private javax.swing.JComboBox<String> PoliticaPlanificacion;
    private javax.swing.JLabel ProcesosCPU;
    private javax.swing.JLabel ProcesosIO;
    private javax.swing.JLabel ProcesosTerminados;
    private javax.swing.JLabel RR_1;
    private javax.swing.JLabel RR_2;
    private javax.swing.JLabel RR_3;
    private javax.swing.JLabel RR_4;
    private javax.swing.JLabel RR_5;
    private javax.swing.JLabel RR_6;
    private javax.swing.JLabel SPN_1;
    private javax.swing.JLabel SPN_2;
    private javax.swing.JLabel SPN_3;
    private javax.swing.JLabel SPN_4;
    private javax.swing.JLabel SPN_5;
    private javax.swing.JLabel SPN_6;
    private javax.swing.JLabel SRT_1;
    private javax.swing.JLabel SRT_2;
    private javax.swing.JLabel SRT_3;
    private javax.swing.JLabel SRT_4;
    private javax.swing.JLabel SRT_5;
    private javax.swing.JLabel SRT_6;
    private javax.swing.JPanel Simulador;
    private javax.swing.JLabel Throughtput;
    private javax.swing.JComboBox<String> TipoProceso;
    private javax.swing.JLabel UsoCPU;
    private javax.swing.JSpinner ciclosGenerarSpinner;
    private javax.swing.JSpinner ciclosSatisfacerSpinner;
    private javax.swing.JTextArea enEjec;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane17;
    private javax.swing.JScrollPane jScrollPane18;
    private javax.swing.JScrollPane jScrollPane20;
    private javax.swing.JScrollPane jScrollPane21;
    private javax.swing.JScrollPane jScrollPane22;
    private javax.swing.JScrollPane jScrollPane24;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTextArea modoAct;
    // End of variables declaration//GEN-END:variables
}
