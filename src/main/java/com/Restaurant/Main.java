package com.Restaurant;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.Restaurant.config.ConfigRestaurant;
import com.Restaurant.factories.RestaurantFactory;
import com.almasb.fxgl.dsl.FXGL;

public class Main extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        ConfigRestaurant.configureGame(settings);
    }

    @Override
    protected void initGame() {
        RestaurantFactory.crearFondo();
        RestaurantFactory.crearMesas();
        
        // Retraso para cocineros
        FXGL.runOnce(() -> {
            RestaurantFactory.crearCocineros();
        }, javafx.util.Duration.seconds(1));
        
        // Retraso para recepcionista
        FXGL.runOnce(() -> {
            RestaurantFactory.crearRecepcionista();
        }, javafx.util.Duration.seconds(2));
        
        // Retraso para mesero
        FXGL.runOnce(() -> {
            RestaurantFactory.crearMeseros();
        }, javafx.util.Duration.seconds(3));
        
        // Retraso para comensales
        FXGL.runOnce(() -> {
            RestaurantFactory.crearComensales();
        }, javafx.util.Duration.seconds(4));
        
        // Retraso para comida y órdenes
        FXGL.runOnce(() -> {
            RestaurantFactory.crearComida();
            RestaurantFactory.crearOrden();
        }, javafx.util.Duration.seconds(5));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
