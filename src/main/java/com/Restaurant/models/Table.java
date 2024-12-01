package com.Restaurant.models;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Table extends Entity {
    private boolean isOccupied; // Puedes agregar un estado para saber si la mesa está ocupada o no.

    public Table(SpawnData data, String texturePath) {
        Image tableImage = new Image(texturePath);
        ImageView imageView = new ImageView(tableImage);

        imageView.setFitWidth(70);
        imageView.setFitHeight(70);

        FXGL.entityBuilder(data)
                .view(imageView)
                .type(EntityType.TABLE)  // Define un nuevo tipo de entidad "TABLE"
                .buildAndAttach();

        this.isOccupied = false; // La mesa está inicialmente libre
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }
}
