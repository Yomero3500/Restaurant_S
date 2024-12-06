package com.simulador.controllers;

import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.Entity;
import com.simulador.entidades.Comensal;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List;
import java.util.Random;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

public class ComensalContro implements EntityFactory {
    public static final double CUSTOMER_SPEED = 150.0;
    private static final int SPRITE_SIZE = 64;

    @Spawns("customer")
    public Entity spawnCustomer(SpawnData data) {
        List<String> imagePaths = List.of(
                "img/Gente/comensal.png"
        );

        Random rand = new Random();
        int randomIndex = rand.nextInt(imagePaths.size());
        ImageView imageView = new ImageView(new Image(imagePaths.get(randomIndex)));
        imageView.setFitWidth(SPRITE_SIZE * 2);
        imageView.setFitHeight(SPRITE_SIZE * 2);

        return entityBuilder()
                .at(data.getX(), data.getY())
                .viewWithBBox(imageView)
                .with(data.<Comensal>get("customerComponent"))
                .build();
    }
}
