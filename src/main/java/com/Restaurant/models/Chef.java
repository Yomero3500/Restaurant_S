package com.Restaurant.models;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.dsl.FXGL;

import static com.almasb.fxgl.dsl.FXGL.animationBuilder;

public class Chef {

    private boolean cocinando = false;
    private Entity cocineroEntity;

    public Entity crearCocinero(double x, double y) {
        cocineroEntity = FXGL.entityBuilder()
                .at(x, y)
                .viewWithBBox(FXGL.texture("chef.png"))
                .scale(.15, .15)
                .buildAndAttach();
        return cocineroEntity;
    }

    public void cocinar() {
        if (cocineroEntity == null) return;

        animationBuilder()
                .duration(javafx.util.Duration.seconds(1))
                .translate(cocineroEntity)
                .from(cocineroEntity.getPosition())
                .to(cocineroEntity.getPosition().subtract(0, 200)) // Ajuste de movimiento hacia abajo
                .buildAndPlay();

        FXGL.runOnce(() -> {
            animationBuilder()
                    .delay(javafx.util.Duration.seconds(5))
                    .duration(javafx.util.Duration.seconds(1))
                    .translate(cocineroEntity)
                    .from(cocineroEntity.getPosition())
                    .to(cocineroEntity.getPosition().add(0, 200)) // Ajuste de movimiento hacia arriba
                    .buildAndPlay();
        }, javafx.util.Duration.seconds(1));
    }



} 