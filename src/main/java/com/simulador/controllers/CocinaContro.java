package com.simulador.controllers;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

public class CocinaContro implements EntityFactory {
    public static final double KITCHEN_X = 10;
    public static final double KITCHEN_Y = 200;

    @Spawns("kitchen")
    public Entity spawnKitchen(SpawnData data) {
        ImageView imageView = new ImageView(new Image("img/Cocina/cocina.png"));
        imageView.setFitWidth(Juego.SPRITE_SIZE * 7);
        imageView.setFitHeight(Juego.SPRITE_SIZE * 10);

        return entityBuilder()
                .at(data.getX(), data.getY())
                .view(imageView)
                .build();
    }
}
