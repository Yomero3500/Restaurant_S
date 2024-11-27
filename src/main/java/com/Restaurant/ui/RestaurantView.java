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

        // Configuración de la imagen de fondo
        ImageView backgroundImage = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/textures/restaurant.jpg"))));
        backgroundImage.setFitWidth(getAppWidth());
        backgroundImage.setFitHeight(getAppHeight());
        backgroundImage.setPreserveRatio(false);

        GaussianBlur blurEffect = new GaussianBlur();
        blurEffect.setRadius(10);
        backgroundImage.setEffect(blurEffect);

        // Contenedor principal
        VBox mainContainer = new VBox(50);
        mainContainer.setAlignment(Pos.CENTER);

        // Título del menú con estilo cursivo
        Text title = new Text("Simulador de Restaurante");
        title.setFont(Font.font("Verdana", FontPosture.ITALIC, 80));  // Estilo cursivo y tamaño grande
        title.setFill(Color.WHITE);

        // Agregar contorno para mejorar la visibilidad
        title.setStroke(Color.BLACK);
        title.setStrokeWidth(2);

        // Aplicar sombra
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(10.0);
        dropShadow.setOffsetX(5.0);
        dropShadow.setOffsetY(5.0);
        dropShadow.setColor(Color.BLACK);
        title.setEffect(dropShadow);

        // Efecto adicional de animación al pasar el ratón (opcional)
        title.setOnMouseEntered(e -> {
            title.setFill(Color.GOLD);  // Cambiar a dorado al pasar el ratón
        });

        title.setOnMouseExited(e -> {
            title.setFill(Color.WHITE);  // Restaurar el color original
        });

        // Botones del menú con texto sobre la imagen
        Button btnPlay = createImageButton("Iniciar Simulación", "/assets/icons/button.png");
        btnPlay.setOnAction(e -> fireNewGame());

        Button btnExit = createImageButton("Salir", "/assets/icons/button.png");
        btnExit.setOnAction(e -> fireExit());

        mainContainer.getChildren().addAll(title, btnPlay, btnExit);

        // Contenedor principal con el fondo
        StackPane root = new StackPane(backgroundImage, mainContainer);
        getContentRoot().getChildren().add(root);
    }

    /**
     * Crea un botón con texto superpuesto a la imagen.
     *
     * @param text Texto que aparecerá sobre la imagen.
     * @param imagePath Ruta de la imagen.
     * @return Botón con texto superpuesto.
     */
    private Button createImageButton(String text, String imagePath) {
        // Cargar la imagen
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
        ImageView imageView = new ImageView(image);

        // Ajustar el tamaño de la imagen
        imageView.setFitWidth(200); // Cambia el ancho según tus necesidades
        imageView.setFitHeight(150); // Cambia la altura según tus necesidades

        // Crear el texto
        Text buttonText = new Text(text);
        buttonText.setFont(Font.font(20));
        buttonText.setFill(Color.WHITE);
        buttonText.setEffect(new DropShadow(2, Color.BLACK)); // Sombras para mejor visibilidad

        // Contenedor para superponer el texto sobre la imagen
        StackPane content = new StackPane(imageView, buttonText);
        content.setAlignment(Pos.CENTER);

        // Botón estilizado
        Button button = new Button();
        button.setStyle("-fx-background-color: transparent;"); // Fondo transparente
        button.setGraphic(content); // Contenido del botón (imagen + texto)

        // Ajustar tamaño del botón
        button.setPrefWidth(220); // Ajusta el ancho
        button.setPrefHeight(160); // Ajusta la altura

        // Efectos al pasar el ratón
        button.setOnMouseEntered(e -> imageView.setOpacity(0.8)); // Disminuye la opacidad
        button.setOnMouseExited(e -> imageView.setOpacity(1.0)); // Restaura la opacidad

        return button;
    }
}
