package com.Restaurant.models;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Waiter extends Entity {
    public Waiter(SpawnData data) {
        FXGL.entityBuilder(data)
                .view(new Circle(10, Color.BLUE))
                .type(EntityType.WAITER)
                .buildAndAttach();
    }
}
