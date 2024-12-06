package com.simulador.controllers;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List;
import java.util.Random;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

public class CocineroContro implements EntityFactory {
    public static final int TOTAL_COOKS = 3;

    @Spawns("cook")
    public Entity spawnCook(SpawnData data) {
        List<String> imagePaths = List.of(
                "img/Cocinero/cocinero.png"
        );

        Random rand = new Random();
        int randomIndex = rand.nextInt(imagePaths.size());
        ImageView imageView = new ImageView(new Image(imagePaths.get(randomIndex)));
        imageView.setFitWidth(Juego.SPRITE_SIZE * 5);
        imageView.setFitHeight(Juego.SPRITE_SIZE * 5);

        return entityBuilder()
                .at(data.getX(), data.getY())
                .viewWithBBox(imageView)
                .build();
    }

}
