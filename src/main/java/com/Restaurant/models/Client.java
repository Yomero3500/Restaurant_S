package com.Restaurant.models;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Client extends Entity {
    public Client(SpawnData data) {
        // Cargar la imagen
        Image clientImage = new Image("/assets/textures/customer.png"); // Ruta de la imagen

        // Crear la vista de la entidad usando ImageView
        ImageView imageView = new ImageView(clientImage);

        // Configurar la escala de la imagen si es necesario
        imageView.setFitWidth(50); // Ajusta el ancho según sea necesario
        imageView.setFitHeight(50); // Ajusta la altura según sea necesario

        // Construir y adjuntar la entidad con la imagen
        FXGL.entityBuilder(data)
                .view(imageView) // Usar ImageView en lugar de Circle
                .type(EntityType.CLIENT)
                .buildAndAttach();
    }
}
