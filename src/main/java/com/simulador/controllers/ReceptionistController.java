package com.simulador.controllers;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.simulador.entities.Receptionist;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

public class ReceptionistController implements EntityFactory {
    public static final double RECEPTIONIST_X = 500;
    public static final double RECEPTIONIST_Y = 296;

    @Spawns("receptionist")
    public Entity spawnReceptionist(SpawnData data) {
        ImageView imageView = new ImageView(new Image("img/recepcion/Recepcionista.png"));
        imageView.setFitWidth(RestaurantController.SPRITE_SIZE * 5);
        imageView.setFitHeight(RestaurantController.SPRITE_SIZE * 5);

        return entityBuilder()
                .at(ReceptionistController.RECEPTIONIST_X, ReceptionistController.RECEPTIONIST_Y)
                .viewWithBBox(imageView)
                .with(data.<Receptionist>get("receptionistComponent"))
                .build();
    }
}
