package com.Restaurant.models;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.dsl.FXGL;
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

    @Override
    public void run() {
        try {
            recepcionista.asignarMesa(this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void moverAMesa(double mesaX, double mesaY) {
        if (comensalEntity == null)
            return;

        FXGL.animationBuilder()
                .duration(javafx.util.Duration.seconds(2))
                .translate(comensalEntity)
                .from(comensalEntity.getPosition())
                .to(new Point2D(mesaX, mesaY))
                .buildAndPlay();

        estaComiendo = true;
        double tiempoComida = 10 + Math.random() * 5;
        FXGL.runOnce(() -> abandonarMesa(), javafx.util.Duration.seconds(tiempoComida));
    }

    public void abandonarMesa() {
        System.out.println(comensalEntity + " " + estaComiendo);
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
            mesa.liberarMesa();
        }
    }

    public void setMesa(Table mesa) {
        this.mesa = mesa;
    }

    public int getIdComensal() {
        return id;
    }
}
