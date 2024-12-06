package com.simulador.modelos.monitores;

import com.simulador.modelos.Orden;
import com.simulador.modelos.OrderStatus;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class OrdenMonitor {
    private final Queue<Orden> pendingOrders;
    private final Queue<Orden> inProcessOrders;
    private final Queue<Orden> readyOrders;
    private final ReentrantLock lock;
    private final Condition orderAvailable;
    private final Condition foodReady;

    public OrdenMonitor() {
        pendingOrders = new LinkedList<>();
        inProcessOrders = new LinkedList<>();
        readyOrders = new LinkedList<>();
        lock = new ReentrantLock();
        orderAvailable = lock.newCondition();
        foodReady = lock.newCondition();
    }

    public void addOrder(Orden order) {
        lock.lock();
        try {
            pendingOrders.add(order);
            orderAvailable.signal();
        } finally {
            lock.unlock();
        }
    }

    public Orden getNextOrder() throws InterruptedException {
        lock.lock();
        try {
            while (pendingOrders.isEmpty()) {
                orderAvailable.await();
            }
            Orden order = pendingOrders.poll();
            order.setStatus(OrderStatus.IN_PROCESS);
            inProcessOrders.add(order);
            return order;
        } finally {
            lock.unlock();
        }
    }

    public void markOrderAsReady(Orden order) {
        lock.lock();
        try {
            order.setStatus(OrderStatus.READY);
            inProcessOrders.remove(order);
            readyOrders.add(order);
            foodReady.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public Orden checkReadyOrder(int tableNumber) throws InterruptedException {
        lock.lock();
        try {
            for (Orden order : readyOrders) {
                if (order.getTableNumber() == tableNumber) {
                    readyOrders.remove(order);
                    order.setStatus(OrderStatus.DELIVERED);
                    return order;
                }
            }
            return null;
        } finally {
            lock.unlock();
        }
    }
}