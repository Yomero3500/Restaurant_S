package com.Restaurant.models;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Waiter extends Entity {
    public Waiter(SpawnData data) {
        Image waiterImage = new Image("/assets/textures/waiter.png");
        ImageView imageView = new ImageView(waiterImage);

        imageView.setFitWidth(50);
        imageView.setFitHeight(50);

        FXGL.entityBuilder(data)
                .view(imageView)
                .type(EntityType.WAITER)
                .buildAndAttach();
    }
}
