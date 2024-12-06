package com.simulador.app;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.ViewComponent;
import com.simulador.controllers.*;
import com.simulador.entidades.*;
import com.simulador.modelos.ComensalesStats;
import com.simulador.modelos.monitores.ComensalesMonitor;
import com.simulador.modelos.monitores.OrdenMonitor;
import com.simulador.modelos.Restaurante;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.simulador.utils.PoissonDistribution;

import static com.almasb.fxgl.dsl.FXGL.*;

public class Main extends GameApplication {
    private Restaurante restaurantMonitor;
    private OrdenMonitor orderQueueMonitor;
    private ComensalesMonitor customerQueueMonitor;
    private ComensalesStats customerStats;
    private final List<Entity> tables = new ArrayList<>();
    private ScheduledExecutorService customerSpawner;
    private int customerIdCounter = 0;
    private PoissonDistribution poissonDistribution;
    private Entity receptionistEntity;
    private List<Thread> cookThreads = new ArrayList<>();

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(Juego.WINDOW_WIDTH);
        settings.setHeight(Juego.WINDOW_HEIGHT);
        settings.setTitle("Restaurante");
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new ComensalContro());
        getGameWorld().addEntityFactory(new MeseroContro());
        getGameWorld().addEntityFactory(new CocineroContro());
        getGameWorld().addEntityFactory(new RecepcionistaContro());
        getGameWorld().addEntityFactory(new CocinaContro());
        getGameWorld().addEntityFactory(new Juego());

        Entity background = createBackgroundEntity();
        getGameWorld().addEntity(background);

        initializeComponents();
        initializeUI();
        initializeGameElements();
        startCustomerGenerator();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (customerSpawner != null) {
                customerSpawner.shutdownNow();
            }
            for (Thread cookThread : cookThreads) {
                cookThread.interrupt();
            }
        }));
    }


    class BackgroundComponent extends Component {

        private AnchorPane root;

        public BackgroundComponent(AnchorPane root) {
            this.root = root;
        }

        @Override
        public void onAdded() {
            ViewComponent viewComponent = getEntity().getViewComponent();
            viewComponent.addChild(root);
        }
    }

    private Entity createBackgroundEntity() {
        Image backgroundImage = new Image(getClass().getResourceAsStream("/img/Fondo/piso-g.jpeg"));
        ImageView backgroundImageView = new ImageView(backgroundImage);
        backgroundImageView.setFitWidth(1050);
        backgroundImageView.setFitHeight(1000);
        backgroundImageView.setPreserveRatio(false);

        AnchorPane root = new AnchorPane();
        AnchorPane.setTopAnchor(backgroundImageView, -250.0);
        AnchorPane.setLeftAnchor(backgroundImageView, 0.0);
        root.getChildren().add(backgroundImageView);

        // crea una entidad con el AnchorPane como componente
        Entity backgroundEntity = new Entity();
        backgroundEntity.addComponent(new BackgroundComponent(root));

        return backgroundEntity;
    }


    private void initializeComponents() {
        customerStats = new ComensalesStats();
        restaurantMonitor = new Restaurante();
        orderQueueMonitor = new OrdenMonitor();
        customerQueueMonitor = new ComensalesMonitor();
        poissonDistribution = new PoissonDistribution(0.2); // Ajusta este valor para cambiar la frecuencia de llegada
    }

    private void initializeUI() {
        VBox statsBox = createStatsBox();
        getGameScene().addUINode(statsBox);
    }

    private VBox createStatsBox() {
        VBox stats = new VBox(10);
        stats.setStyle("-fx-background-color: rgba(255, 255, 255, 0.8); -fx-padding: 10; -fx-background-radius: 5;");
        stats.setTranslateX(10);
        stats.setTranslateY(10);

        String labelStyle = "-fx-font-size: 14px; -fx-font-weight: bold;";

        Label waitingTableLabel = createStatsLabel(
                customerStats.customersWaitingTableProperty(),
                "Esperando mesa: %d",
                labelStyle
        );
        return stats;
    }

    private Label createStatsLabel(javafx.beans.property.IntegerProperty property, String format, String style) {
        Label label = new Label();
        label.setTextFill(Color.BLACK);
        label.textProperty().bind(property.asString(format));
        label.setStyle(style);
        return label;
    }

    private void initializeGameElements() {
        initializeKitchen();
        initializeTables();
        initializeReceptionist();
        initializeWaiters();
        initializeCooks();
    }

    private void initializeKitchen() {
        getGameWorld().spawn("kitchen",
                new SpawnData(CocinaContro.KITCHEN_X, CocinaContro.KITCHEN_Y)
        );
    }

    private void initializeTables() {
        for (int i = 0; i < Juego.TOTAL_TABLES; i++) {
            int row = i / 5;
            int col = i % 5;
            double x = 200 + col * (Juego.SPRITE_SIZE * 2);
            double y = 3 + row * (Juego.SPRITE_SIZE * 2);

            SpawnData data = new SpawnData(x, y);
            data.put("tableNumber", i);
            Entity table = getGameWorld().spawn("table", data);
            tables.add(table);
        }
    }


    private void initializeReceptionist() {
        Point2D receptionistPos = new Point2D(RecepcionistaContro.RECEPTIONIST_X, RecepcionistaContro.RECEPTIONIST_Y);
        Recepcionista receptionistComponent = new Recepcionista(
                restaurantMonitor,
                receptionistPos,
                customerStats
        );

        SpawnData data = new SpawnData(receptionistPos.getX(), receptionistPos.getY());
        data.put("receptionistComponent", receptionistComponent);
        receptionistEntity = getGameWorld().spawn("receptionist", data);
    }

    private void initializeWaiters() {
        for (int i = 0; i < MeseroContro.TOTAL_WAITERS; i++) {
            Point2D startPos = new Point2D(
                    MeseroContro.WAITER_X + Juego.SPRITE_SIZE,
                    MeseroContro.WAITER_Y - ((i + 1) * Juego.SPRITE_SIZE * 1.5)
            );

            Mesero waiter = new Mesero(
                    i,
                    orderQueueMonitor,
                    customerQueueMonitor,
                    startPos,
                    tables
            );

            SpawnData data = new SpawnData(startPos.getX(), startPos.getY());
            data.put("waiterComponent", waiter);
            getGameWorld().spawn("waiter", data);
        }
    }

    private void initializeCooks() {
        double kitchenY = CocinaContro.KITCHEN_Y;
        for (int i = 0; i < CocineroContro.TOTAL_COOKS; i++) {
            Cocinero cook = new Cocinero(i, orderQueueMonitor);
            SpawnData data = new SpawnData(
                    CocinaContro.KITCHEN_X,
                    kitchenY + (i * Juego.SPRITE_SIZE)
            );
            data.put("cookComponent", cook);
            Entity cookEntity = getGameWorld().spawn("cook", data);

            Thread cookThread = new Thread(cook);
            cookThreads.add(cookThread);
            cookThread.start();
        }
    }

    private void startCustomerGenerator() {
        customerSpawner = Executors.newSingleThreadScheduledExecutor();
        customerSpawner.scheduleAtFixedRate(() -> {
            try {
                generateNewCustomers();
            } catch (Exception e) {
                System.err.println("Error en generador de clientes: " + e.getMessage());
            }
        }, 0, 3, TimeUnit.SECONDS);
    }

    private void generateNewCustomers() {
        int numCustomers = poissonDistribution.nextInt();
        for (int i = 0; i < numCustomers; i++) {
            final int currentId = customerIdCounter++;
            runOnce(() -> spawnCustomer(currentId),
                    Duration.seconds(poissonDistribution.nextArrivalTime())
            );
        }
    }

    private void spawnCustomer(int id) {
        Comensal customer = new Comensal(
                id,
                restaurantMonitor,
                orderQueueMonitor,
                customerQueueMonitor,
                customerStats,
                tables
        );
        SpawnData data = new SpawnData(Juego.ENTRANCE_X, Juego.ENTRANCE_Y);
        data.put("customerComponent", customer);
        getGameWorld().spawn("customer", data);
    }

    @Override
    protected void onUpdate(double tpf) {

    }

    private void stopCookThreads() {
        for (Thread cookThread : cookThreads) {
            cookThread.interrupt();
            try {
                cookThread.join(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        cookThreads.clear();
    }

    private void stopCustomerGenerator() {
        if (customerSpawner != null) {
            customerSpawner.shutdown();
            try {
                if (!customerSpawner.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                    customerSpawner.shutdownNow();
                }
            } catch (InterruptedException e) {
                customerSpawner.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}