package com.Restaurant.models;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.dsl.FXGL;
import javafx.geometry.Point2D;

public class Waiting {

    private Entity esperaEntity;
    private boolean esperando = true;
    private static final double OFFSET_X = 50; // Desplazamiento a la derecha
    private static final double OFFSET_Y = -40; // Desplazamiento hacia arriba

    public Entity crearEspera(Point2D posicionMesa) {
        double x = posicionMesa.getX() + OFFSET_X;
        double y = posicionMesa.getY() + OFFSET_Y;
        
        esperaEntity = FXGL.entityBuilder()
                .at(x, y)
                .viewWithBBox(FXGL.texture("wait.png"))
                .scale(.1, .1)
                .buildAndAttach();
        return esperaEntity;
    }

    public void eliminarEspera() {
        if (esperando && esperaEntity != null) {
            esperaEntity.removeFromWorld();
            esperando = false;
        }
    }

    public Entity getEsperaEntity() {
        return esperaEntity;
    }

}
