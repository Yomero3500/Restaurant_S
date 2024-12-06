package com.simulador.controllers;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.simulador.entidades.Mesa;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

public class Juego implements EntityFactory {
    public static final int WINDOW_WIDTH = 1000;
    public static final int WINDOW_HEIGHT = 700;
    public static final int TOTAL_TABLES = 20;

    public static final double ENTRANCE_X = 50;
    public static final double ENTRANCE_Y = 500;

    // Tiempos (en milisegundos)
    public static final int MIN_EATING_TIME = 5000;
    public static final int MAX_EATING_TIME = 10000;

    // Dimensiones de los sprites
    public static final int SPRITE_SIZE = 32;

    @Spawns("table")
    public Entity spawnTable(SpawnData data) {
        ImageView imageView = new ImageView(new Image("img/Mesa/mesa.png"));
        imageView.setFitWidth(Juego.SPRITE_SIZE * 2);
        imageView.setFitHeight(Juego.SPRITE_SIZE * 2);

        return entityBuilder()
                .at(data.getX(), data.getY())
                .viewWithBBox(imageView)
                .with(new Mesa(data.get("tableNumber"), new Point2D(data.getX(), data.getY())))
                .build();
    }
}
