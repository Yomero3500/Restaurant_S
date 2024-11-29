package com.Restaurant.utils;

/**
 * Clase que proporciona métodos auxiliares para generar valores aleatorios.
 */
public class RandomUtils {

    /**
     * Genera un número aleatorio entero dentro de un rango.
     *
     * @param min Valor mínimo del rango (inclusive).
     * @param max Valor máximo del rango (inclusive).
     * @return Número aleatorio dentro del rango especificado.
     */
    public static int generateRandomInt(int min, int max) {
        return (int) (Math.random() * (max - min + 1)) + min;
    }

    /**
     * Genera un número aleatorio de coma flotante dentro de un rango.
     *
     * @param min Valor mínimo del rango (inclusive).
     * @param max Valor máximo del rango (inclusive).
     * @return Número aleatorio de tipo double dentro del rango especificado.
     */
    public static double generateRandomDouble(double min, double max) {
        return Math.random() * (max - min) + min;
    }

    /**
     * Genera un tiempo de espera aleatorio en milisegundos.
     *
     * @param minTiempo Tiempo mínimo de espera en milisegundos.
     * @param maxTiempo Tiempo máximo de espera en milisegundos.
     * @return Tiempo aleatorio en milisegundos.
     */
    public static long generateRandomWaitTime(long minTiempo, long maxTiempo) {
        return (long) (Math.random() * (maxTiempo - minTiempo) + minTiempo);
    }
}
