package com.simulador.models.monitors;

import com.simulador.models.Order;
import com.simulador.models.OrderStatus;

import java.util.LinkedList;
import java.util.Queue;

public class MonitorOrder {
    private final Queue<Order> pendingOrders;
    private final Queue<Order> inProcessOrders;
    private final Queue<Order> readyOrders;
    private final Object orderLock;
    private final Object foodLock;

    public MonitorOrder() {
        pendingOrders = new LinkedList<>();
        inProcessOrders = new LinkedList<>();
        readyOrders = new LinkedList<>();
        orderLock = new Object();
        foodLock = new Object();
    }

    public void addOrder(Order order) {
        synchronized (orderLock) {
            pendingOrders.add(order);
            orderLock.notify();
        }
    }

    public Order getNextOrder() throws InterruptedException {
        synchronized (orderLock) {
            while (pendingOrders.isEmpty()) {
                orderLock.wait();
            }
            Order order = pendingOrders.poll();
            order.setStatus(OrderStatus.IN_PROCESS);
            inProcessOrders.add(order);
            return order;
        }
    }

    public void markOrderAsReady(Order order) {
        synchronized (foodLock) {
            synchronized (orderLock) {
                order.setStatus(OrderStatus.READY);
                inProcessOrders.remove(order);
                readyOrders.add(order);
            }
            foodLock.notifyAll();
        }
    }

    public Order checkReadyOrder(int tableNumber) throws InterruptedException {
        synchronized (foodLock) {
            for (Order order : readyOrders) {
                if (order.getTableNumber() == tableNumber) {
                    readyOrders.remove(order);
                    order.setStatus(OrderStatus.DELIVERED);
                    return order;
                }
            }
            return null;
        }
    }
}
