package com.simulador.entities;

import com.simulador.models.Order;
import com.simulador.models.monitors.MonitorOrder;

public class Chef implements Runnable {
    private final MonitorOrder orderQueueMonitor;
    private volatile boolean isResting;
    private Order currentOrder;

    public Chef(int id, MonitorOrder orderQueueMonitor) {
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