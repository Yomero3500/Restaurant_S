package com.Restaurant.models;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Chef extends Entity {
    public Chef(SpawnData data) {
        FXGL.entityBuilder(data)
                .view(new Circle(10, Color.RED))
                .type(EntityType.CHEF)
                .buildAndAttach();
    }
}
