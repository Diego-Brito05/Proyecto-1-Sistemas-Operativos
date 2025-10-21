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
    private final JLabel lblInfoPC;
    private final JLabel lblInfoMAR;
    private final JLabel lblInfoTipo;
    
    // Fuentes que usaremos
    private final Font FONT_BOLD_14 = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_PLAIN_11 = new Font("Segoe UI", Font.PLAIN, 11);

    public ProcesoListRenderer() {
        panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        lblNombre = new JLabel();
        // --- APLICAMOS LA NUEVA FUENTE ---
        lblNombre.setFont(FONT_BOLD_14); 
        lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblInfoPC = new JLabel();
        lblInfoPC.setFont(FONT_PLAIN_11);
        lblInfoPC.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        lblInfoMAR = new JLabel();
        lblInfoMAR.setFont(FONT_PLAIN_11);
        lblInfoMAR.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblInfoTipo = new JLabel();
        lblInfoTipo.setFont(FONT_PLAIN_11);
        lblInfoTipo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panelPrincipal.add(lblNombre);
        panelPrincipal.add(lblInfoPC);
        panelPrincipal.add(lblInfoMAR);
        panelPrincipal.add(lblInfoTipo);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Proceso> list, Proceso proceso, int index, boolean isSelected, boolean cellHasFocus) {
        
        // Configurar los valores de las etiquetas (esto no cambia)
        lblNombre.setText(String.format("ID: %d - %s", proceso.getId(), proceso.getNombre()));
        lblInfoPC.setText("PC: " + proceso.getProgramCounter());
        lblInfoMAR.setText("MAR: " + proceso.getMemoryAddressRegister());
        
        // --- LÓGICA DE COLOR MODIFICADA ---
        if (proceso.esIOBound()) {
            lblInfoTipo.setText("Tipo: I/O Bound");
            lblInfoTipo.setForeground(Color.RED.darker()); // Un rojo oscuro es más legible
        } else {
            lblInfoTipo.setText("Tipo: CPU Bound");
            lblInfoTipo.setForeground(Color.BLUE.darker()); // Un azul oscuro es más legible
        }

        // --- Lógica de selección ---
        // Esto gestiona el color de fondo y el color del texto cuando se selecciona un item.
        // Importante: Si un item está seleccionado, los colores de texto personalizados se sobreescriben.
        if (isSelected) {
            panelPrincipal.setBackground(list.getSelectionBackground());
            // Para que el texto sea visible en un fondo de selección oscuro, lo ponemos en blanco
            lblNombre.setForeground(list.getSelectionForeground());
            lblInfoPC.setForeground(list.getSelectionForeground());
            lblInfoMAR.setForeground(list.getSelectionForeground());
            lblInfoTipo.setForeground(list.getSelectionForeground());
        } else {
            panelPrincipal.setBackground(list.getBackground());
            // Si no está seleccionado, restauramos los colores por defecto para la mayoría de las etiquetas
            lblNombre.setForeground(list.getForeground());
            lblInfoPC.setForeground(list.getForeground());
            lblInfoMAR.setForeground(list.getForeground());
            // ¡OJO! La etiqueta de tipo ya tiene su color (rojo/azul) establecido arriba,
            // por lo que no la tocamos aquí.
        }

        return panelPrincipal;
    }
}