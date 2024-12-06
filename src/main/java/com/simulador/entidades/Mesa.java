package com.simulador.entidades;

import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;

public class Mesa extends Component {
    private final int number;
    private final Point2D position;
    private boolean isOccupied;
    private Comensal currentCustomer;

    public Mesa(int number, Point2D position) {
        this.number = number;
        this.position = position;
        this.isOccupied = false;
        this.currentCustomer = null;
    }

    @Override
    public void onUpdate(double tpf) {
        isOccupied = (currentCustomer != null);
    }

    public int getNumber() {
        return number;
    }

    public void release() {
        this.currentCustomer = null;
        this.isOccupied = false;
    }

    public Comensal getCurrentCustomer() {
        return currentCustomer;
    }

    public void setCurrentCustomer(Comensal customer) {
        this.currentCustomer = customer;
        this.isOccupied = (customer != null);
    }
}