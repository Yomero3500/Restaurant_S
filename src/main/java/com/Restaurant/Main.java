package com.Restaurant;

import com.Restaurant.models.*; // Asegúrate de importar tus modelos
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main extends GameApplication {

    private List<Entity> mesas; // Lista para almacenar las entidades de las mesas
    private Lock lockMesas;        // Lock para sincronizar el acceso a la lista de mesas

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
        mesas = new ArrayList<>();
        ordenesPendientes = new ConcurrentLinkedQueue<>();
        comidasListas = new ConcurrentLinkedQueue<>();
        lock = new ReentrantLock();
        ordenesDisponibles = lock.newCondition();
        comidasDisponibles = lock.newCondition();

        createGameEntities();
        FXGL.getGameWorld().addEntityFactory(new RestaurantFactory());
        spawnInitialEntities();

        // Iniciar el hilo de los chefs
        Thread hiloChefs = new Thread(this::runChefs);
        hiloChefs.setDaemon(true);
        hiloChefs.start();


        FXGL.run(() -> generarCliente(), Duration.seconds(2));

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
                Entity mesaEntity = FXGL.entityBuilder()
                        .at(300 + j * 150, 110 + i * 120)
                        .view(createImageView("/assets/textures/table.png", 70, 70))
                        .buildAndAttach();

                Mesa mesa = new Mesa(mesaEntity);
                mesas.add(mesa);



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
        spawnWaiters(2);
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
    private void generarClientesPoisson() {
        double lambda = 1; // Parámetro lambda para la distribución de Poisson (ajusta según sea necesario)


        while (true) {
            try {

                long tiempoEspera = (long) (-Math.log(1.0 - Math.random()) / lambda); // Poisson

                TimeUnit.SECONDS.sleep(tiempoEspera);
                SpawnData spawnData = new SpawnData(1210, 190); // Posición inicial del cliente

                FXGL.spawn("client", spawnData);


                // Agrega el cliente a la recepcionista
                Optional<Entity> receptionist = FXGL.getGameWorld().getEntitiesByType(EntityType.RECEPTIONIST).findFirst();

                receptionist.ifPresent(r -> r.getComponent(Receptionist.class).agregarCliente(
                        (Client) FXGL.getGameWorld().getEntitiesByType(EntityType.CLIENT).get(
                                FXGL.getGameWorld().getEntitiesByType(EntityType.CLIENT).size()-1)));




            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break; // Sale del bucle si el hilo es interrumpido
            }

        }
    }





    //Métodos para controlar el estado de las mesas (usados por Waiter, Client y Receptionist)

    public synchronized boolean isMesaDisponible(Entity mesa) {
        lockMesas.lock();
        try {
            return !mesa.getProperties().getBoolean("ocupada");
        } finally {
            lockMesas.unlock();
        }

    }

    public synchronized void ocuparMesa(Entity mesa, Client cliente) {
        lockMesas.lock();
        try {
            mesa.getProperties().setValue("ocupada", true);
            mesa.getProperties().setValue("cliente", cliente); // Asocia el cliente a la mesa
        } finally {
            lockMesas.unlock();
        }
    }


    public synchronized void liberarMesa(Entity mesa) {
        lockMesas.lock();
        try {
            mesa.getProperties().setValue("ocupada", false);
            mesa.getProperties().setValue("cliente", null); // Desasocia el cliente de la mesa

            Optional<Entity> receptionist = FXGL.getGameWorld().getEntitiesByType(EntityType.RECEPTIONIST).findFirst();
            receptionist.ifPresent(r -> r.getComponent(Receptionist.class).notificarMesaDisponible()); // Notificar a la recepcionista

        } finally {
            lockMesas.unlock();
        }

    }



    public Client getClienteEnMesa(Entity mesa){
        lockMesas.lock();

        try {

            return  (Client) mesa.getProperties().getValue("cliente");


        }finally {

            lockMesas.unlock();

        }


    }


}
