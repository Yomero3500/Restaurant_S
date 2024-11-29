package com.Restaurant.utils;

/**
 * Clase que proporciona métodos para generar tiempos basados en la distribución de Poisson.
 */
public class PoissonUtils {

    /**
     * Genera el tiempo entre llegadas de eventos basados en la distribución de Poisson.
     *
     * @param lambda Tasa de llegada de eventos (por segundo).
     * @return Tiempo entre eventos en segundos.
     */
    public static double generatePoissonTime(double lambda) {
        // Genera un número aleatorio uniforme entre 0 y 1
        double u = Math.random();
        // Calcula el tiempo entre llegadas usando la distribución exponencial
        return -Math.log(1 - u) / lambda;
    }
}
