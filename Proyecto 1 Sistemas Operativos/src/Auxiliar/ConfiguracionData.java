/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Auxiliar;

/**
 * clase simple para contener los datos de configuración leídos desde el JSON.
 * @author Diego
 */

  
 
public class ConfiguracionData {
    private final String politica;
    private final double duracionCiclo;

    public ConfiguracionData(String politica, double duracionCiclo) {
        this.politica = politica;
        this.duracionCiclo = duracionCiclo;
    }

    public String getPolitica() {
        return politica;
    }

    public double getDuracionCiclo() {
        return duracionCiclo;
    }
}