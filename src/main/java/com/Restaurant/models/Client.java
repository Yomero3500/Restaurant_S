package com.Restaurant.models;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Client extends Entity {
    public Client(SpawnData data) {
        Image clientImage = new Image("/assets/textures/customer.png");
        ImageView imageView = new ImageView(clientImage);

        imageView.setFitWidth(50);
        imageView.setFitHeight(50);

        FXGL.entityBuilder(data)
                .view(imageView)
                .type(EntityType.CLIENT)
                .buildAndAttach();
    }
}
