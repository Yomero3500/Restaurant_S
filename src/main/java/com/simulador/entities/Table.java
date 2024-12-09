package com.simulador.entities;

import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;
import java.util.logging.Logger;

/**
 * Representa una mesa en el restaurante.
 */
public class Table extends Component {
    private static final Logger logger = Logger.getLogger(Table.class.getName());

    public final int number; // Número de mesa, público y final para acceso directo.
    private final Point2D position; // Posición de la mesa, inmutable.
    private boolean isOccupied; // Indica si la mesa está ocupada.
    private Customer currentCustomer; // Cliente actual en la mesa (null si está vacía).

    /**
     * Crea una nueva mesa.
     * @param number El número de la mesa.
     * @param position La posición de la mesa.
     * @throws IllegalArgumentException si el número de mesa es negativo.
     */
    public Table(int number, Point2D position) {
        if (number < 0) {
            throw new IllegalArgumentException("El número de mesa no puede ser negativo.");
        }
        this.number = number;
        this.position = position;
        this.isOccupied = false;
        this.currentCustomer = null;
        logger.info("Mesa creada: " + number + " en " + position);
    }



    /**
     * Libera la mesa, indicando que ya no está ocupada.
     */
    public void release() {
        this.currentCustomer = null;
        this.isOccupied = false;
        logger.info("Mesa liberada: " + number);
    }

    /**
     * Obtiene el cliente actual en la mesa.
     * @return El cliente actual o null si la mesa está libre.
     */
    public Customer getCurrentCustomer() {
        return currentCustomer;
    }

    /**
     * Asigna un cliente a la mesa.
     * @param customer El cliente que ocupará la mesa, o null para liberarla.
     */
    public void setCurrentCustomer(Customer customer) {
        this.currentCustomer = customer;
        this.isOccupied = (customer != null);

        if (isOccupied) {
            logger.info("Cliente " + customer + " asignado a la mesa " + number);
        } else {
            logger.info("Mesa " + number + " liberada.");
        }
    }

    /**
     * Devuelve el número de la mesa.
     * @return El número de la mesa.
     */
    public int getNumber() { //Redundante, pero se mantiene para compatibilidad
        return number;
    }


    /**
     * Indica si la mesa está actualmente ocupada.
     * @return true si la mesa está ocupada, false en caso contrario.
     */
    public boolean isOccupied() {
        return isOccupied;
    }



    @Override
    public void onUpdate(double tpf) {
        // Método vacío en la interfaz Component, no se usa en esta clase.
    }


    public Point2D getPosition() {
        return position;
    }
}