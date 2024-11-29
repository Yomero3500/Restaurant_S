package com.Restaurant.models;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.dsl.FXGL;

public class Food {
    private Entity comidaEntity;

    public Entity crearComida(double x, double y) {
        comidaEntity = FXGL.entityBuilder()
                .at(x, y)
                .viewWithBBox(FXGL.texture("food.png"))
                .scale(.1, .1)
                .buildAndAttach();
        return comidaEntity;
    }

    public Entity getComidaEntity() {
        return comidaEntity;
    }
}
