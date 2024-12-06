package com.simulador.entidades;

import com.almasb.fxgl.entity.component.Component;
import com.simulador.models.monitores.RestauranteMonitor;
import com.simulador.models.ComensalesStats;
import javafx.geometry.Point2D;
import java.util.Queue;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class Recepcionista extends Component {
    private final Point2D position;
    private final RestauranteMonitor restaurantMonitor;
    private final Queue<Comensal> waitingCustomers;
    private final ReentrantLock lock;
    private final Condition customerWaiting;
    private boolean isBusy;
    private Comensal currentCustomer;
    private final ComensalesStats customerStats;

    public Recepcionista(RestauranteMonitor restaurantMonitor, Point2D position, ComensalesStats customerStats) {
        this.restaurantMonitor = restaurantMonitor;
        this.position = position;
        this.customerStats = customerStats;
        this.waitingCustomers = new LinkedList<>();
        this.lock = new ReentrantLock();
        this.customerWaiting = lock.newCondition();
        this.isBusy = false;

        startReceptionistBehavior();
    }

    private void startReceptionistBehavior() {
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Comensal customer = getNextCustomer();
                    if (customer != null) {
                        handleCustomer(customer);
                    }
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();
    }

    public void addCustomerToQueue(Comensal customer) {
        lock.lock();
        try {
            waitingCustomers.add(customer);
            customerWaiting.signal();
        } finally {
            lock.unlock();
        }
    }

    private Comensal getNextCustomer() throws InterruptedException {
        lock.lock();
        try {
            while (waitingCustomers.isEmpty()) {
                customerWaiting.await();
            }
            currentCustomer = waitingCustomers.poll();
            isBusy = true;
            return currentCustomer;
        } finally {
            lock.unlock();
        }
    }

    private void handleCustomer(Comensal customer) {
        try {
            Thread.sleep(200);

            int tableNumber = restaurantMonitor.findAvailableTable();

            if (tableNumber != -1) {
                restaurantMonitor.occupyTable(tableNumber);
                customer.assignTable(tableNumber);
            } else {
                customerStats.incrementWaitingForTable();
                customer.waitForTable();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.lock();
            try {
                currentCustomer = null;
                isBusy = false;
            } finally {
                lock.unlock();
            }
        }
    }

    public boolean isBusy() {
        lock.lock();
        try {
            return isBusy;
        } finally {
            lock.unlock();
        }
    }

    public Comensal getCurrentCustomer() {
        lock.lock();
        try {
            return currentCustomer;
        } finally {
            lock.unlock();
        }
    }

    public Point2D getPosition() {
        return position;
    }

    @Override
    public void onUpdate(double tpf) {

    }
}