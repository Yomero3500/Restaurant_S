package com.simulador.models.monitors;

import com.simulador.entities.Customer;

import java.util.Arrays;

public class MonitorCustomer {
    private final int CAPACIDAD = 10; // Tamaño fijo del buffer
    private final CustomerRequest[] buffer;
    private int lleno; // Contador de elementos llenos
    private int indiceInsercion; // Índice para insertar
    private int indiceExtraccion; // Índice para extraer

    public synchronized boolean hasWaitingCustomers() {
        return lleno > 0; // Devuelve true si hay elementos en el buffer
    }


    public static class CustomerRequest {
        public final Customer customer;
        public final int tableNumber;
        public final long arrivalTime;

        public CustomerRequest(Customer customer, int tableNumber) {
            this.customer = customer;
            this.tableNumber = tableNumber;
            this.arrivalTime = System.currentTimeMillis();
        }

        @Override
        public String toString() {
            return "CustomerRequest{" +
                    "customer=" + customer +
                    ", tableNumber=" + tableNumber +
                    ", arrivalTime=" + arrivalTime +
                    '}';
        }
    }

    public MonitorCustomer() {
        buffer = new CustomerRequest[CAPACIDAD];
        lleno = 0;
        indiceInsercion = 0;
        indiceExtraccion = 0;
    }

    @Override
    public String toString() {
        return "ComensalesMonitor{" +
                "buffer=" + Arrays.toString(buffer) +
                '}';
    }

    public synchronized void addCustomer(Customer customer, int tableNumber) {
        while (lleno == CAPACIDAD) { // Esperar si el buffer está lleno
            try {
                this.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // Insertar en el buffer circular
        buffer[indiceInsercion] = new CustomerRequest(customer, tableNumber);
        indiceInsercion = (indiceInsercion + 1) % CAPACIDAD;
        lleno++;
        System.out.println(Thread.currentThread().getName() + " - Añadido: " + this.toString());

        // Notificar que hay un nuevo cliente disponible
        this.notify();
    }

    public synchronized CustomerRequest getNextCustomer() {
        while (lleno == 0) { // Esperar si el buffer está vacío
            try {
                this.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // Extraer del buffer circular
        CustomerRequest request = buffer[indiceExtraccion];
        buffer[indiceExtraccion] = null; // Limpiar el espacio
        indiceExtraccion = (indiceExtraccion + 1) % CAPACIDAD;
        lleno--;
        System.out.println(Thread.currentThread().getName() + " - Extraído: " + this.toString());

        // Notificar que hay espacio disponible
        this.notify();
        return request;
    }
}
