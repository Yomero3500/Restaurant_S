package com.Restaurant.models;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Chef extends Entity {
    public Chef(SpawnData data) {
        // Cargar la imagen
        Image chefImage = new Image("/assets/textures/chef.png"); // Ruta de la imagen

        // Crear la vista de la entidad usando ImageView
        ImageView imageView = new ImageView(chefImage);

        // Configurar la escala de la imagen si es necesario
        imageView.setFitWidth(50); // Ajusta el ancho según sea necesario
        imageView.setFitHeight(50); // Ajusta la altura según sea necesario

        // Construir y adjuntar la entidad con la imagen
        FXGL.entityBuilder(data)
                .view(imageView)
                .type(EntityType.CHEF)
                .buildAndAttach();
    }
}
