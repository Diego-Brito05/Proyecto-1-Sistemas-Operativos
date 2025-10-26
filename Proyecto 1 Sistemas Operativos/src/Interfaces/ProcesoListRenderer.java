/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Interfaces;

import EstructuraDeDatos.Proceso;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;

/**
 *
 * @author Diego
 */
/**
 * Un renderizador de celdas personalizado para JList que muestra la información
 * de un Proceso en múltiples líneas, con el nombre en negrita y todo centrado.
 */
public class ProcesoListRenderer implements ListCellRenderer<Proceso> {

private final JPanel panelPrincipal;
    private final JLabel lblNombre;
    private final JLabel lblId; 
    private final JLabel lblInfoPC;
    private final JLabel lblInfoMAR;
    private final JLabel lblInfoTipo;
    
    // Fuentes 
    private final Font FONT_BOLD_14 = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_PLAIN_11 = new Font("Segoe UI", Font.PLAIN, 11);
    
    //Colores
    private final Color bajaPrioridad = new Color(115,49,49);
    private final Color mediaPrioridad = new Color(168,108,56);
    private final Color negroDefault = new Color(0,0,0);

    public ProcesoListRenderer() {
        panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Configuración de la etiqueta para el Nombre
        lblNombre = new JLabel();
        lblNombre.setFont(FONT_BOLD_14); 
        lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- CAMBIO 2: Creamos y configuramos la nueva etiqueta para el ID ---
        lblId = new JLabel();
        lblId.setFont(FONT_PLAIN_11); // Usamos la fuente normal
        lblId.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Configuración del resto de las etiquetas (sin cambios)
        lblInfoPC = new JLabel();
        lblInfoPC.setFont(FONT_PLAIN_11);
        lblInfoPC.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        lblInfoMAR = new JLabel();
        lblInfoMAR.setFont(FONT_PLAIN_11);
        lblInfoMAR.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblInfoTipo = new JLabel();
        lblInfoTipo.setFont(FONT_PLAIN_11);
        lblInfoTipo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // --- CAMBIO 3: Añadimos las etiquetas al panel en el orden deseado ---
        panelPrincipal.add(lblNombre); // Primero el nombre
        panelPrincipal.add(lblId);     // Luego el ID
        panelPrincipal.add(lblInfoPC);
        panelPrincipal.add(lblInfoMAR);
        panelPrincipal.add(lblInfoTipo);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Proceso> list, Proceso proceso, int index, boolean isSelected, boolean cellHasFocus) {
        
        
        lblNombre.setText(proceso.getNombre());
        
        lblId.setText("ID: " + proceso.getId());    
    
        lblInfoPC.setText("PC: " + proceso.getProgramCounter());
        lblInfoMAR.setText("MAR: " + proceso.getMemoryAddressRegister());
        
        if (proceso.esIOBound()) {
            lblInfoTipo.setText("Tipo: I/O Bound");
            lblInfoTipo.setForeground(Color.RED.darker());
        } else {
            lblInfoTipo.setText("Tipo: CPU Bound");
            lblInfoTipo.setForeground(Color.BLUE.darker());
        }

        // --- Lógica de selección actualizada para incluir la nueva etiqueta ---
        if (isSelected) {
            panelPrincipal.setBackground(list.getSelectionBackground());
            lblNombre.setForeground(list.getSelectionForeground());
            lblId.setForeground(list.getSelectionForeground()); // <-- Actualizar color de lblId
            lblInfoPC.setForeground(list.getSelectionForeground());
            lblInfoMAR.setForeground(list.getSelectionForeground());
            lblInfoTipo.setForeground(list.getSelectionForeground());
        } else {
            panelPrincipal.setBackground(list.getBackground());
            lblNombre.setForeground(list.getForeground());
            lblId.setForeground(list.getForeground()); // <-- Restaurar color de lblId
            lblInfoPC.setForeground(list.getForeground());
            lblInfoMAR.setForeground(list.getForeground());
            // La etiqueta de tipo conserva su color rojo/azul
        }
        
        //Si tiene prioridad entonces se le coloca en el color.
        
        if (proceso.getNivelMLFQ() == 1) {
            lblNombre.setForeground(mediaPrioridad);
        } else if (proceso.getNivelMLFQ() == 2) {
            lblNombre.setForeground(bajaPrioridad);
        } else {
            lblNombre.setForeground(negroDefault);
        }

        return panelPrincipal;
    }
}