package com.simulador.entities;

import com.simulador.models.Order;
import com.simulador.models.monitors.MonitorOrder;
import java.util.logging.Logger;


/**
 * Representa un chef en el simulador de restaurante.
 * El chef toma órdenes de la cola de órdenes y las procesa.
 */
public class Chef implements Runnable {

    private static final Logger logger = Logger.getLogger(Chef.class.getName());

    private final MonitorOrder orderQueueMonitor;
    private volatile boolean isIdle; // Nombre más descriptivo
    private Order currentOrder;

    /**
     * Crea un nuevo Chef.
     * @param id El ID del chef. (Actualmente no se usa, considera eliminarlo si no es necesario)
     * @param orderQueueMonitor El monitor para acceder a la cola de órdenes.
     */
    public Chef(int id, MonitorOrder orderQueueMonitor) {
        this.orderQueueMonitor = orderQueueMonitor;
        this.isIdle = true;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                currentOrder = orderQueueMonitor.getNextOrder();
                isIdle = false;
                logger.info("Chef processing order: " + currentOrder); // Ejemplo de logging

                Thread.sleep(currentOrder.getPreparationTime());

                orderQueueMonitor.markOrderAsReady(currentOrder);
                logger.info("Order completed: " + currentOrder); // Ejemplo de logging
                currentOrder = null;
                isIdle = true;

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warning("Chef interrupted."); // Ejemplo de logging
            }
            // No es necesario el 'break' aquí.
        }
    }
}