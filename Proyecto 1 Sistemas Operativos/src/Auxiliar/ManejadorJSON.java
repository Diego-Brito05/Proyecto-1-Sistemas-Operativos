/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Auxiliar;

/**
 *
 * @author Diego
 */
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JFileChooser; // <<-- Importa JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter; // <<-- Importa FileNameExtensionFilter
import org.json.JSONException;
import org.json.JSONObject;

public class ManejadorJSON {

    // ... (El método guardarConfiguracion no necesita cambios) ...
    public String guardarConfiguracion(String politica, double duracionCiclo) {
        // ... (código existente) ...
        JSONObject configJson = new JSONObject();
        configJson.put("politicaPlanificacion", politica);
        configJson.put("duracionCicloSegundos", duracionCiclo);

        // --- LÓGICA PARA CREAR UN NOMBRE DE ARCHIVO ÚNICO ---
        // 1. Obtener la fecha y hora actual
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        // 2. Crear el nombre del archivo
        String nombreArchivo = "config_" + timeStamp + ".json";
        
        // Escribir el objeto JSON en el nuevo archivo
        try (FileWriter file = new FileWriter(nombreArchivo)) {
            file.write(configJson.toString(4)); 
            System.out.println("Configuración guardada en: " + nombreArchivo);
            return nombreArchivo; // Devolvemos el nombre del archivo para informar al usuario
        } catch (IOException e) {
            System.err.println("Error al escribir el archivo JSON: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Abre un diálogo para que el usuario seleccione un archivo de configuración .json
     * y lo carga.
     * @return Un objeto ConfiguracionData con los datos cargados, o null si el usuario cancela o hay un error.
     */
    public ConfiguracionData cargarConfiguracionInteractivo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar archivo de configuración");
        fileChooser.setCurrentDirectory(new File(".")); // Directorio inicial
        
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos JSON", "json");
        fileChooser.setFileFilter(filter);

        int resultado = fileChooser.showOpenDialog(null); // 'null' para centrar en la pantalla

        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivoSeleccionado = fileChooser.getSelectedFile();
            // Llamamos a nuestro método privado para hacer el trabajo de lectura
            return cargarConfiguracionDesdeRuta(archivoSeleccionado.getAbsolutePath());
        } else {
            System.out.println("El usuario canceló la selección de archivo.");
            return null; // El usuario canceló
        }
    }
    
    /**
     * Método auxiliar privado que carga la configuración desde una ruta de archivo específica.
     * @param rutaArchivo La ruta completa al archivo JSON.
     * @return Un objeto ConfiguracionData, o null si hay un error.
     */
    private ConfiguracionData cargarConfiguracionDesdeRuta(String rutaArchivo) {
        try {
            String contenido = new String(Files.readAllBytes(Paths.get(rutaArchivo)));
            JSONObject configJson = new JSONObject(contenido);
            String politica = configJson.getString("politicaPlanificacion");
            double duracionCiclo = configJson.getDouble("duracionCicloSegundos");
            return new ConfiguracionData(politica, duracionCiclo);
        } catch (IOException | JSONException e) {
            System.err.println("Error al cargar o parsear el archivo de configuración: " + e.getMessage());
            return null;
        }
    }
}
