package com.simulador.models.monitores;

import com.simulador.models.Orden;
import com.simulador.models.OrderStatus;

import java.util.LinkedList;
import java.util.Queue;

public class OrdenMonitor {
    private final Queue<Orden> pendingOrders;
    private final Queue<Orden> inProcessOrders;
    private final Queue<Orden> readyOrders;

    public OrdenMonitor() {
        pendingOrders = new LinkedList<>();
        inProcessOrders = new LinkedList<>();
        readyOrders = new LinkedList<>();
    }

    public synchronized void addOrder(Orden order) {
        pendingOrders.add(order);
        System.out.println("Nueva orden agregada: " + order.getId() + " para la mesa " + order.getTableNumber());
        System.out.println("Órdenes pendientes: " + pendingOrders.size());
        notifyAll(); // Notificar a los threads que esperan por nuevas órdenes.
    }

    public synchronized Orden getNextOrder() throws InterruptedException {
        while (pendingOrders.isEmpty()) {
            System.out.println("Esperando por órdenes pendientes...");
            wait(); // Esperar hasta que haya órdenes pendientes.
        }
        Orden order = pendingOrders.poll();
        order.setStatus(OrderStatus.IN_PROCESS);
        inProcessOrders.add(order);
        System.out.println("Orden tomada por mesero: " + order.getId() + " para la mesa " + order.getTableNumber());
        System.out.println("Órdenes en proceso: " + inProcessOrders.size());
        return order;
    }

    public synchronized void markOrderAsReady(Orden order) {
        order.setStatus(OrderStatus.READY);
        inProcessOrders.remove(order);
        readyOrders.add(order);
        System.out.println("Orden lista para entregar: " + order.getId() + " para la mesa " + order.getTableNumber());
        System.out.println("Órdenes listas: " + readyOrders.size());
        notifyAll(); // Notificar a los threads que esperan órdenes listas.
    }

    public synchronized Orden checkReadyOrder(int tableNumber) throws InterruptedException {
        while (readyOrders.isEmpty()) {
            System.out.println("Esperando por órdenes listas...");
            wait(); // Esperar hasta que haya órdenes listas.
        }
        for (Orden order : readyOrders) {
            if (order.getTableNumber() == tableNumber) {
                readyOrders.remove(order);
                order.setStatus(OrderStatus.DELIVERED);
                System.out.println("Orden entregada: " + order.getId() + " para la mesa " + tableNumber);
                return order;
            }
        }
        System.out.println("No se encontró una orden lista para la mesa " + tableNumber);
        return null; // Si no se encuentra la orden específica.
    }
}
