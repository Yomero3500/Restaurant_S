package com.Restaurant.models;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Receptionist extends Entity {
    public Receptionist(SpawnData data) {
        Image receptionistImage = new Image("/assets/textures/receptionist.png");
        ImageView imageView = new ImageView(receptionistImage);

        imageView.setFitWidth(50);
        imageView.setFitHeight(50);

        FXGL.entityBuilder(data)
                .view(imageView)
                .type(EntityType.RECEPTIONIST)
                .buildAndAttach();
    }
}
