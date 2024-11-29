package com.Restaurant.models;

import com.almasb.fxgl.entity.Entity;
import java.util.List;
import com.almasb.fxgl.dsl.FXGL;

public class Recepcionista {
    private Entity recepcionistaEntity;
    private List<Table> mesas;

    public Recepcionista(List<Table> mesas) {
        this.mesas = mesas;
    }

    public Entity crearRecepcionista(double x, double y) {
        recepcionistaEntity = FXGL.entityBuilder()
                .at(x, y)
                .viewWithBBox(FXGL.texture("receptionist.png"))
                .scale(.2, .2)
                .buildAndAttach();
        return recepcionistaEntity;
    }

    public Entity getRecepcionistaEntity() {
        return recepcionistaEntity;
    }

    public synchronized void asignarMesa(Customer comensal) throws InterruptedException {
        while (true) {
            for (Table mesa : mesas) {
                System.out.println(
                        "Comensal " + comensal.getIdComensal() + " intentando ocupar la mesa " + mesa.getIdMesa());
                if (mesa.ocuparMesa(comensal)) {
                    System.out.println(
                            "Comensal " + comensal.getIdComensal() + " asignado a la mesa " + mesa.getIdMesa());
                    comensal.setMesa(mesa);
                    comensal.moverAMesa(mesa.getX(), mesa.getY());
                    return;
                }
            }
            System.out.println("Comensal " + comensal.getIdComensal() + " esperando por una mesa.");
            wait();
        }
    }

    public synchronized void liberarMesa(Table mesa) {
        mesa.liberarMesa();
        System.out.println("Mesa " + mesa.getIdMesa() + " ha sido liberada. Notificando comensales...");
        notifyAll();
    }

}