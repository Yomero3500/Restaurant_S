package com.Restaurant.models;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Chef extends Entity {
    public Chef(SpawnData data) {
        Image chefImage = new Image("/assets/textures/chef.png");
        ImageView imageView = new ImageView(chefImage);

        imageView.setFitWidth(50);
        imageView.setFitHeight(50);

        FXGL.entityBuilder(data)
                .view(imageView)
                .type(EntityType.CHEF)
                .buildAndAttach();
    }
}
