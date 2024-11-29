package com.Restaurant.models;

import com.almasb.fxgl.entity.Entity;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;
import com.almasb.fxgl.dsl.FXGL;

public class Recepcionista {
    private Entity recepcionistaEntity;
    private List<Table> mesas;
    private Queue<Customer> colaEsperando;

    public Recepcionista(List<Table> mesas) {
        this.mesas = mesas;
        this.colaEsperando = new LinkedList<>();
    }

    public Entity crearRecepcionista(double x, double y) {
        recepcionistaEntity = FXGL.entityBuilder()
                .at(x, y)
                .viewWithBBox(FXGL.texture("receptionist.png"))
                .scale(.2, .2)
                .buildAndAttach();
        return recepcionistaEntity;
    }

    public synchronized void asignarMesa(Customer comensal) throws InterruptedException {
        while (!Thread.currentThread().isInterrupted()) {
            for (Table mesa : mesas) {
                if (mesa.ocuparMesa(comensal)) {
                    comensal.setMesa(mesa);
                    comensal.moverAMesa(mesa.getX(), mesa.getY());
                    return;
                }
            }

            System.out.println("Comensal " + Thread.currentThread().getName() + " no encontró mesa. Esperando...");
            colaEsperando.add(comensal);
            wait();
        }
    }

    public synchronized void notificarComensalEnEspera() {
        if (!colaEsperando.isEmpty()) {
            Customer siguienteComensal = colaEsperando.poll();
            notify();
        }
    }

    public synchronized void liberarMesa(Table mesa) {
        mesa.liberarMesa(this);
    }
}