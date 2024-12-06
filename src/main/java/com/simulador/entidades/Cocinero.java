package com.simulador.entidades;

import com.simulador.modelos.Orden;
import com.simulador.modelos.monitores.OrdenMonitor;

public class Cocinero implements Runnable {
    private final OrdenMonitor orderQueueMonitor;
    private volatile boolean isResting;
    private Orden currentOrder;

    public Cocinero(int id, OrdenMonitor orderQueueMonitor) {
        this.orderQueueMonitor = orderQueueMonitor;
        this.isResting = true;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                currentOrder = orderQueueMonitor.getNextOrder();
                isResting = false;

                Thread.sleep(currentOrder.getPreparationTime());

                orderQueueMonitor.markOrderAsReady(currentOrder);
                currentOrder = null;
                isResting = true;

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}