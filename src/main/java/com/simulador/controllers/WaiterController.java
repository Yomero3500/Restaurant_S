package com.simulador.controllers;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.simulador.entities.Waiter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

public class WaiterController implements EntityFactory {
    public static int TOTAL_WAITERS = 4;
    public static double WAITER_X = 20;
    public static double WAITER_Y = 200;
    public static double WAITER_SPEED = 180.0;

    @Spawns("waiter")
    public Entity spawnWaiter(SpawnData data) {
        ImageView imageView = new ImageView(new Image("img/Meseros/mesero.png"));
        imageView.setFitWidth(RestaurantController.SPRITE_SIZE * 2);
        imageView.setFitHeight(RestaurantController.SPRITE_SIZE * 2);

        return entityBuilder()
                .at(data.getX(), data.getY())
                .viewWithBBox(imageView)
                .with(data.<Waiter>get("waiterComponent"))
                .build();
    }
}
