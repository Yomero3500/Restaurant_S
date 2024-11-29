package com.Restaurant.models;

import com.almasb.fxgl.entity.Entity;
import com.Restaurant.utils.ConstanstRestaurant;
import com.almasb.fxgl.dsl.FXGL;

public class Table {
    private final int id;
    private Entity mesaEntity;
    private Customer comensalActual;
    private boolean ocupado = false;

    public Table(int id) {
        this.id = id;
        this.comensalActual = null;
    }

    public synchronized boolean estaDisponible() {
        return !ocupado;
    }

    public synchronized boolean ocuparMesa(Customer comensal) {
        if (ocupado) {
            return false; 
        }

        ocupado = true; 
        this.comensalActual = comensal; 
        System.out.println("Comensal " + comensal.getIdComensal() + " ocupa la mesa " + id + ".");
        return true;
    }



    public synchronized void liberarMesa() {
        System.out.println(ocupado);
        if (!ocupado) {
            System.out.println("Mesa " + id + " ya está libre.");
            return;
        }

        // Liberar la mesa
        ocupado = false;
        this.comensalActual = null; // Eliminar referencia al comensal actual
        System.out.println("Mesa " + id + " ha sido liberada.");
        notifyAll(); // Notificar a todos los hilos que están esperando
    }

    public int getIdMesa() {
        return id;
    }

    public Entity getMesaEntity() {
        return mesaEntity;
    }

    public Entity crearMesa(double x, double y) {
        mesaEntity = FXGL.entityBuilder()
                .at(x, y)
                .viewWithBBox(FXGL.texture("table.png"))
                .scale(ConstanstRestaurant.MESA_SCALE, ConstanstRestaurant.MESA_SCALE)
                .buildAndAttach();
        return mesaEntity;
    }

    public double getX() {
        return mesaEntity != null ? mesaEntity.getX() : 0; // Devuelve X o 0 si no existe
    }

    public double getY() {
        return mesaEntity != null ? mesaEntity.getY() : 0; // Devuelve Y o 0 si no existe
    }
}
