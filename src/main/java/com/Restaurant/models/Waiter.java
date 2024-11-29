package com.Restaurant.models;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.dsl.FXGL;
import static com.almasb.fxgl.dsl.FXGL.animationBuilder;
import javafx.geometry.Point2D;
import java.util.List;

public class Waiter {
    private Entity meseroEntity;
    private boolean entregando = false;
    private Point2D posicionInicial;
    private int mesaActual = 0;
    private static final double OFFSET_X = -50;
    
    // Constantes de tiempo
    private static final double TIEMPO_MOVIMIENTO = 3.0; // 3 segundos para moverse
    private static final double TIEMPO_ESPERA = 4.0; // 4 segundos tomando la orden

    private Point2D ajustarPosicionMesa(Point2D posicionMesa) {
        return new Point2D(posicionMesa.getX() + OFFSET_X, posicionMesa.getY());
    }

    public Entity crearMesero(double x, double y) {
        posicionInicial = new Point2D(x, y);
        meseroEntity = FXGL.entityBuilder()
                .at(x, y)
                .viewWithBBox(FXGL.texture("waiter.png"))
                .scale(.15, .15)
                .buildAndAttach();
        return meseroEntity;
    }

    public void iniciarServicio(List<Point2D> posicionesMesas) {
        if (meseroEntity == null || entregando) return;
        visitarSiguienteMesa(posicionesMesas);
    }

    private void visitarSiguienteMesa(List<Point2D> posicionesMesas) {
        if (mesaActual >= posicionesMesas.size()) {
            mesaActual = 0;
            return;
        }

        Point2D posicionMesa = posicionesMesas.get(mesaActual);
        Point2D posicionAjustada = ajustarPosicionMesa(posicionMesa);
        entregando = true;

        animationBuilder()
            .duration(javafx.util.Duration.seconds(TIEMPO_MOVIMIENTO))
            .translate(meseroEntity)
            .from(meseroEntity.getPosition())
            .to(posicionAjustada)
            .buildAndPlay();

        FXGL.runOnce(() -> {
            animationBuilder()
                .duration(javafx.util.Duration.seconds(TIEMPO_MOVIMIENTO))
                .translate(meseroEntity)
                .from(meseroEntity.getPosition())
                .to(posicionInicial)
                .buildAndPlay();

            entregando = false;
            mesaActual++;
            
            FXGL.runOnce(() -> visitarSiguienteMesa(posicionesMesas), 
                javafx.util.Duration.seconds(TIEMPO_MOVIMIENTO));
        }, javafx.util.Duration.seconds(TIEMPO_ESPERA));
    }
} 