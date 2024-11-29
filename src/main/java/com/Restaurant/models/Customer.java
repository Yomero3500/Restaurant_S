package com.Restaurant.models;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.dsl.FXGL;
import javafx.application.Platform;
import javafx.geometry.Point2D;

public class Customer extends Thread {
    private Entity comensalEntity;
    private boolean estaComiendo = false;
    private Recepcionista recepcionista;
    private Table mesa;
    private final int id;
    private static int contadorId = 0;

    public Customer(Recepcionista recepcionista) {
        this.recepcionista = recepcionista;
        this.id = ++contadorId;
    }

    public Entity crearComensal(double x, double y) {
        comensalEntity = FXGL.entityBuilder()
                .at(x, y)
                .viewWithBBox(FXGL.texture("customer.png"))
                .scale(.15, .15)
                .buildAndAttach();
        return comensalEntity;
    }

    public void moverAMesa(double mesaX, double mesaY) {
        if (comensalEntity == null)
            return;

        Platform.runLater(() -> {
            FXGL.animationBuilder()
                    .duration(javafx.util.Duration.seconds(2))
                    .translate(comensalEntity)
                    .from(comensalEntity.getPosition())
                    .to(new Point2D(mesaX, mesaY))
                    .buildAndPlay();
        });

        estaComiendo = true;
        double tiempoComida = 10 + Math.random() * 5;
        FXGL.runOnce(() -> abandonarMesa(), javafx.util.Duration.seconds(tiempoComida));
    }

    public void abandonarMesa() {
        if (comensalEntity == null || !estaComiendo)
            return;

        estaComiendo = false;

        FXGL.animationBuilder()
                .duration(javafx.util.Duration.seconds(2))
                .translate(comensalEntity)
                .from(comensalEntity.getPosition())
                .to(new Point2D(900, 320))
                .buildAndPlay();

        if (mesa != null) {
            recepcionista.liberarMesa(mesa);
        }
    }

    public void setMesa(Table mesa) {
        this.mesa = mesa;
    }

    public int getIdComensal() {
        return id;
    }

    @Override
    public void run() {
        try {
            recepcionista.asignarMesa(this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void wakeUp(String name) {
        System.out.println("Comensal " + name + " ha sido despertado y le ha sido asignada una mesa.");
        if (mesa != null) {
            moverAMesa(mesa.getX(), mesa.getY());
        }
    }
}