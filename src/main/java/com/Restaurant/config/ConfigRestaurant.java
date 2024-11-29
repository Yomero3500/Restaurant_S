package com.Restaurant.config;

import com.almasb.fxgl.app.GameSettings;
import javafx.scene.image.Image;

public class ConfigRestaurant {

    public static void configureGame(GameSettings settings) {
        Image imagen = new Image(ConfigRestaurant.class.getResourceAsStream("/assets/textures/background.png"));
        settings.setWidth((int) imagen.getWidth());
        settings.setHeight((int) imagen.getHeight());
        settings.setTitle("Fonda MX");
        settings.setVersion("1.0");
    }

}
