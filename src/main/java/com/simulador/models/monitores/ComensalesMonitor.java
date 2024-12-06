package com.simulador.models.monitores;

import com.simulador.entidades.Comensal;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class ComensalesMonitor {
    private final int TOTAL_CUSTOMERS = 10; // Máximo de clientes en espera
    private final Queue<CustomerRequest> waitingCustomers; // Cola de clientes esperando
    private int currentCustomers; // Número de clientes actuales en espera

    public static class CustomerRequest {
        public final Comensal customer;
        public final int tableNumber;
        public final long arrivalTime;

        public CustomerRequest(Comensal customer, int tableNumber) {
            this.customer = customer;
            this.tableNumber = tableNumber;
            this.arrivalTime = System.currentTimeMillis();
        }
    }

    public ComensalesMonitor() {
        waitingCustomers = new LinkedList<>();
        currentCustomers = 0;
    }

    // Método para agregar un cliente
    public synchronized void addCustomer(Comensal customer, int tableNumber) {
        // Si la cola está llena, esperar
        while (currentCustomers == TOTAL_CUSTOMERS) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // Agregar el cliente a la cola
        waitingCustomers.add(new CustomerRequest(customer, tableNumber));
        currentCustomers++;
        System.out.println(Thread.currentThread().getName() + " agregó: " + customer + " a la mesa " + tableNumber);

        // Notificar que hay un cliente disponible
        notify();
    }

    // Método para obtener al siguiente cliente
    public synchronized CustomerRequest getNextCustomer() {
        // Esperar si no hay clientes
        while (waitingCustomers.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // Obtener el siguiente cliente de la cola
        CustomerRequest nextCustomer = waitingCustomers.poll();
        currentCustomers--;
        System.out.println(Thread.currentThread().getName() + " atendiendo a: " + nextCustomer.customer + " en la mesa " + nextCustomer.tableNumber);

        // Notificar que hay espacio para un nuevo cliente
        notify();
        return nextCustomer;
    }

    // Método para verificar si hay clientes esperando
    public synchronized boolean hasWaitingCustomers() {
        return !waitingCustomers.isEmpty();
    }

    // Método para obtener el número de clientes actuales esperando
    public int getCurrentCustomerCount() {
        return currentCustomers;
    }
}
