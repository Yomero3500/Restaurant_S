package com.Restaurant.ui;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;

import java.util.Objects;

public class RestaurantView extends FXGLMenu {
    public RestaurantView() {
        super(MenuType.MAIN_MENU);

        ImageView backgroundImage = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/textures/restaurant.jpg"))));
        backgroundImage.setFitWidth(getAppWidth());
        backgroundImage.setFitHeight(getAppHeight());
        backgroundImage.setPreserveRatio(false);

        GaussianBlur blurEffect = new GaussianBlur();
        blurEffect.setRadius(10);
        backgroundImage.setEffect(blurEffect);

        VBox mainContainer = new VBox(50);
        mainContainer.setAlignment(Pos.CENTER);

        Text title = new Text("Simulador de Restaurante");
        title.setFont(Font.font("Verdana", FontPosture.ITALIC, 80));
        title.setFill(Color.WHITE);

        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(10.0);
        dropShadow.setOffsetX(5.0);
        dropShadow.setOffsetY(5.0);
        dropShadow.setColor(Color.BLACK);
        title.setEffect(dropShadow);

        Button btnPlay = createImageButton("Iniciar Simulación", "/assets/icons/button.png");
        btnPlay.setOnAction(e -> fireNewGame());

        Button btnExit = createImageButton("Salir", "/assets/icons/button.png");
        btnExit.setOnAction(e -> fireExit());

        mainContainer.getChildren().addAll(title, btnPlay, btnExit);

        StackPane root = new StackPane(backgroundImage, mainContainer);
        getContentRoot().getChildren().add(root);
    }

    private Button createImageButton(String text, String imagePath) {
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
        ImageView imageView = new ImageView(image);

        imageView.setFitWidth(200);
        imageView.setFitHeight(150);

        Text buttonText = new Text(text);
        buttonText.setFont(Font.font(20));
        buttonText.setFill(Color.WHITE);
        buttonText.setEffect(new DropShadow(2, Color.BLACK));

        StackPane content = new StackPane(imageView, buttonText);
        content.setAlignment(Pos.CENTER);

        Button button = new Button();
        button.setStyle("-fx-background-color: transparent;");
        button.setGraphic(content);

        button.setPrefWidth(220);
        button.setPrefHeight(160);

        button.setOnMouseEntered(e -> imageView.setOpacity(0.8));
        button.setOnMouseExited(e -> imageView.setOpacity(1.0));

        return button;
    }
}
