package com.simulador.controllers;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.simulador.entidades.Recepcionista;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

public class RecepcionistaContro implements EntityFactory {
    public static final double RECEPTIONIST_X = 150;
    public static final double RECEPTIONIST_Y = 296;

    @Spawns("receptionist")
    public Entity spawnReceptionist(SpawnData data) {
        ImageView imageView = new ImageView(new Image("img/recepcion/Recepcionista.png"));
        imageView.setFitWidth(Juego.SPRITE_SIZE * 2);
        imageView.setFitHeight(Juego.SPRITE_SIZE * 2);

        return entityBuilder()
                .at(RecepcionistaContro.RECEPTIONIST_X, RecepcionistaContro.RECEPTIONIST_Y)
                .viewWithBBox(imageView)
                .with(data.<Recepcionista>get("receptionistComponent"))
                .build();
    }
}
