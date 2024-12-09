package com.simulador.entities;

import com.almasb.fxgl.entity.component.Component;
import com.simulador.Observer.Observer;
import com.simulador.models.Restaurant;
import com.simulador.models.CustomersStats;
import javafx.geometry.Point2D;

import java.util.Queue;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.logging.Logger;

public class Receptionist extends Component implements Observer {
    private static final Logger logger = Logger.getLogger(Receptionist.class.getName());
    private final Point2D position;
    private final Restaurant restaurantMonitor;
    private final Queue<Customer> waitingCustomers;
    private final ReentrantLock lock;
    private final Condition customerWaiting;
    private volatile boolean isBusy; // volatile para acceso concurrente
    private Customer currentCustomer;
    private final CustomersStats customerStats;

    public Receptionist(Restaurant restaurantMonitor, Point2D position, CustomersStats customerStats) {
        this.restaurantMonitor = restaurantMonitor;
        this.position = position;
        this.customerStats = customerStats;
        this.waitingCustomers = new LinkedList<>();
        this.lock = new ReentrantLock();
        this.customerWaiting = lock.newCondition();
        this.isBusy = false;

        // Se registra como observador del monitor
        restaurantMonitor.addObserver(this);
        startReceptionistBehavior();
    }

    private void startReceptionistBehavior() {
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Customer customer = getNextCustomer();
                    if (customer != null) {
                        handleCustomer(customer);
                    } else {
                        // Pequeña pausa si no hay clientes para evitar un bucle apretado
                        Thread.sleep(500);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.warning("Recepcionista interrumpido.");
                    break; // Sale del bucle si hay interrupción
                }
            }
        }).start();
    }

    public void addCustomerToQueue(Customer customer) {
        lock.lock();
        try {
            waitingCustomers.add(customer);
            customerWaiting.signal(); // Notifica a un recepcionista en espera
        } finally {
            lock.unlock();
        }
    }

    private Customer getNextCustomer() throws InterruptedException {
        lock.lock();
        try {
            while (waitingCustomers.isEmpty() && !Thread.currentThread().isInterrupted()) { // Manejo de interrupción en la espera
                customerWaiting.await();
            }
            // Verifica si se interrumpió después de despertar
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException();
            }

            currentCustomer = waitingCustomers.poll();
            isBusy = true;
            return currentCustomer;
        } finally {
            lock.unlock();
        }
    }


    private void handleCustomer(Customer customer) {
        try {
            Thread.sleep(200);

            int tableNumber = restaurantMonitor.findAvailableTable();

            if (tableNumber != -1) {
                restaurantMonitor.occupyTable(tableNumber);
                customer.assignTable(tableNumber);
                logger.info("Cliente asignado a la mesa " + tableNumber);
            } else {
                customerStats.incrementWaitingForTable();
                customer.waitForTable();
                logger.info("Cliente esperando mesa.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warning("Recepcionista interrumpido atendiendo a un cliente.");
        } finally {
            // Asegurar que isBusy se actualiza incluso con interrupciones
            lock.lock();
            try {
                currentCustomer = null;
                isBusy = false;
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public void onTableAvailable() {
        logger.info("Recepcionista: Una mesa está disponible.");
        lock.lock();
        try {
            customerWaiting.signal(); // Notifica a un recepcionista en espera
        } finally {
            lock.unlock();
        }
    }


    public boolean isBusy() {
        return isBusy; //  No necesita bloqueo si isBusy es volatile
    }

    public Customer getCurrentCustomer() {
        lock.lock();
        try {
            return currentCustomer;
        } finally {
            lock.unlock();
        }
    }

    public Point2D getPosition() {
        return position; // Point2D es inmutable, no necesita bloqueo.
    }

    @Override
    public void onUpdate(double tpf) {
        // Método vacío en la interfaz, no se usa
        }
}
