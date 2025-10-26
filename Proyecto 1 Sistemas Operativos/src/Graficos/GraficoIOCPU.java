/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Graficos;

/**
 *
 * @author Esteacosta
 */
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;
import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class GraficoIOCPU extends JPanel {

    private CategoryChart chart;
    private XChartPanel<CategoryChart> chartPanel;
    private List<String> procesos;
    private Integer[] valores;

    public GraficoIOCPU() {
        procesos = Arrays.asList("I/O bound", "CPU bound");
        valores = new Integer[]{0, 0};

        chart = new CategoryChartBuilder()
                .width(800)
                .height(500)
                .title("Procesos completados I/O y CPU")
                .xAxisTitle("Tipo de Proceso")
                .yAxisTitle("Procesos")
                .theme(Styler.ChartTheme.Matlab)
                .build();

        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setAvailableSpaceFill(0.9);
        chart.getStyler().setYAxisMin(0.0);
        chart.getStyler().setYAxisMax(500.0);

        chart.addSeries("Procesos", procesos, Arrays.asList(valores));
    

        chartPanel = new XChartPanel<>(chart) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 12));

                int panelWidth = getWidth();
                int panelHeight = getHeight();
                int numBarras = procesos.size();
                double anchoBarra = panelWidth * 0.75 / numBarras; // usa 75% del ancho
                double margen = panelWidth * 0.125;                 // 12.5% márgenes laterales

                for (int i = 0; i < valores.length; i++) {
                    double valor = valores[i];
                    String texto = String.format("%.2f", valor);

                    double x = margen + i * anchoBarra + anchoBarra / 2 - 10;
                    double y = panelHeight * (1 - (valor / chart.getStyler().getYAxisMax()) * 0.7);

                    g2.drawString(texto, (int) x, (int) y);
                }
            }
        };

        setLayout(new BorderLayout());
        add(chartPanel, BorderLayout.CENTER);
    }

    public void actualizar(Integer[] nuevosValores) {
        this.valores = nuevosValores;
        chart.updateCategorySeries("Procesos", procesos, Arrays.asList(valores), null);
        chartPanel.repaint();
    }
}

