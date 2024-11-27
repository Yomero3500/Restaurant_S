package com.Restaurant;

import com.Restaurant.factories.RestaurantFactory;
import com.Restaurant.ui.RestaurantView;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.Objects;

public class Main extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1300);
        settings.setHeight(600);
        settings.setTitle("Simulador Restaurante");
        settings.setVersion("0.1");
        settings.setMainMenuEnabled(true);
        settings.setSceneFactory(new SceneFactory() {
            @Override
            public FXGLMenu newMainMenu() {
                return new RestaurantView();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected void initGame() {
        createGameEntities();
        FXGL.getGameWorld().addEntityFactory(new RestaurantFactory());
        spawnInitialEntities();
    }

    private void createGameEntities() {
        FXGL.entityBuilder()
                .at(0, 0)
                .view(createImageView("/assets/textures/background.jpeg", 1300, 850))
                .buildAndAttach();

        // Cambiar la cocina al lado izquierdo
        FXGL.entityBuilder()
                .at(-80, 120) // Coordenadas ajustadas para mover la cocina a la izquierda
                .view(createImageView("/assets/textures/kitchen.png", 500, 400))
                .buildAndAttach();

        // Cambiar el área de espera al lado derecho
        FXGL.entityBuilder()
                .at(1150, 150) // Coordenadas ajustadas para mover el área de espera a la derecha
                .view(createImageView("/assets/textures/waiting.png", 100, 500))
                .buildAndAttach();

        // Cambiar etiquetas
        addLabel("Cocina", 70, 30); // Etiqueta para la cocina al lado izquierdo
        addLabel("Área de espera", 1150, 30); // Etiqueta para el área de espera al lado derecho
        addLabel("Área de mesas", 550, 30);

        createTables();
    }


    private void createTables() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                FXGL.entityBuilder()
                        .at(300 + j * 150, 110 + i * 120)
                        .view(createImageView("/assets/textures/table.png", 70, 70))
                        .buildAndAttach();
            }
        }
    }

    private void addLabel(String text, double x, double y) {
        Text label = new Text(text);
        label.setFill(Color.WHITE);
        label.setFont(Font.font(20));
        FXGL.entityBuilder()
                .at(x, y)
                .view(label)
                .buildAndAttach();
    }

    private void spawnWaiters(int count) {
        for (int i = 0; i < count; i++) {
            FXGL.spawn("waiter", 500 + i * 50, 300);
        }
    }

    private void spawnChefs(int count) {
        for (int i = 0; i < count; i++) {
            FXGL.spawn("chef", 100, 300 + i * 100); // Ajustar chefs al lado izquierdo
        }
    }

    private void spawnClients(int count) {
        for (int i = 0; i < count; i++) {
            FXGL.spawn("client", 1210, 190 + i * 50); // Ajustar clientes al lado derecho
        }
    }

    private void spawnReceptionist() {
        FXGL.spawn("receptionist", 1100, 130); // Ajustar recepcionista al lado derecho
    }

    private Image loadImage(String path) {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
    }

    private void spawnInitialEntities() {
        spawnWaiters(3);
        spawnChefs(2);
        spawnClients(3);
        spawnReceptionist();
    }

    private ImageView createImageView(String path, double width, double height) {
        ImageView imageView = new ImageView(loadImage(path));
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        return imageView;
    }
}
