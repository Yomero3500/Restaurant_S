package com.Restaurant.factories;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.Restaurant.models.Table;
import com.Restaurant.utils.ConstanstRestaurant;
import com.Restaurant.models.Chef;
import com.Restaurant.models.Waiter;
import com.Restaurant.models.Order;
import com.Restaurant.models.Customer;
import javafx.geometry.Point2D;
import java.util.ArrayList;
import java.util.List;
import com.Restaurant.models.Recepcionista;
import com.Restaurant.models.Food;

public class RestaurantFactory {

    private static List<Point2D> posicionesMesas = new ArrayList<>();
    private static List<Table> mesas = new ArrayList<>();
    private static List<Customer> comensales = new ArrayList<>();
    private static Recepcionista recepcionista;

    public static Entity crearFondo() {
        return FXGL.entityBuilder()
                .at(0, 0)
                .viewWithBBox(FXGL.texture("background.png"))
                .scale(1, 1)
                .buildAndAttach();
    }

    public static void crearMesas() {
        int startX = 200;
        int startY = 0;
        int id = 1; // ID único para cada mesa

        for (int fila = 0; fila < ConstanstRestaurant.MESAS_POR_FILA; fila++) {
            for (int columna = 0; columna < ConstanstRestaurant.MESAS_POR_COLUMNA; columna++) {
                double x = startX + (columna * ConstanstRestaurant.ESPACIO_ENTRE_X);
                double y = startY + (fila * ConstanstRestaurant.ESPACIO_ENTRE_Y);

                Table mesa = new Table(id++); // Crear mesa con un ID único
                mesa.crearMesa(x, y);
                mesas.add(mesa); // Guardar en la lista de mesas
                posicionesMesas.add(new Point2D(x, y));
            }
        }

    }

    public static void crearCocineros() {
        Chef chef1 = new Chef();
        Chef chef2 = new Chef();

        chef1.crearCocinero(-100, 100);
        chef2.crearCocinero(0, 100);

        chef1.cocinar();
        chef2.cocinar();
    }

    public static void crearMesero() {
        Waiter mesero = new Waiter();
        mesero.crearMesero(50, 320);
        mesero.iniciarServicio(posicionesMesas);
    }


    public static void crearRecepcionista() {
        if (mesas.isEmpty()) {
            throw new IllegalStateException("Las mesas deben estar creadas antes de crear el recepcionista");
        }

        recepcionista = new Recepcionista(mesas); // Pasar las mesas al constructor
        recepcionista.crearRecepcionista(800, 320);
    }

    public static void crearComensales() {
        if (recepcionista == null) {
            throw new IllegalStateException("El recepcionista debe estar creado antes de crear comensales");
        }

        double startX = 900;
        double startY = 320;

        for (int i = 0; i < 20; i++) {
            Customer comensal = new Customer(recepcionista); // Pasar el recepcionista al constructor
            Entity comensalEntity = comensal.crearComensal(startX, startY);

            comensales.add(comensal); // Guardar en la lista

            FXGL.runOnce(comensal::start, javafx.util.Duration.seconds(i * 0.5)); // Iniciar el hilo del comensal
        }
    }

    public static void crearComida() {
        double startX = -180;
        double startY = 210;

        for (int fila = 0; fila < 5; fila++) {
            double x = startX + (fila * ConstanstRestaurant.COMIDA_ESPACIO_ENTRE_X);
            Food comida = new Food();
            comida.crearComida(x, startY);
        }
    }

    public static void crearOrden() {

        double startX = -200;
        double startY = 50;

        for (int fila = 0; fila < 5; fila++) {
            double y = startY + (fila * ConstanstRestaurant.ORDEN_ESPACIO_ENTRE_Y);
            Order orden = new Order();
            orden.crearOrden(startX, y);
        }
    }
}