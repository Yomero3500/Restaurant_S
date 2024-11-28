package com.Restaurant.models;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Optional;

public class Client extends Entity {

    private enum Estado {
        ESPERANDO_MESA, SENTADO, ESPERANDO_COMIDA, COMIENDO, TERMINADO
    }


    private Estado estado = Estado.ESPERANDO_MESA;
    private Waiter meseroAsignado;
    private Lock lock;
    private Condition comidaLista;


    public Client(SpawnData data) {

        Image clientImage = new Image("/assets/textures/customer.png");
        ImageView imageView = new ImageView(clientImage);
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);

        FXGL.entityBuilder(data)
                .view(imageView)
                .type(EntityType.CLIENT)
                .buildAndAttach();

        this.lock = new ReentrantLock();
        this.comidaLista = lock.newCondition();
    }

    public void asignarMesero(Waiter mesero){
        this.meseroAsignado = mesero;
        estado = Estado.SENTADO;

        //El cliente hace la orden
        Orden orden = new Orden(this, mesero);
        meseroAsignado.tomarOrden(orden);
    }

    public void comidaServida(Comida comida) {
        lock.lock();
        try {
            if (estado == Estado.ESPERANDO_COMIDA && comida.getCliente() == this) {
                estado = Estado.COMIENDO;
                this.comidaLista.signalAll();


                FXGL.runOnce(this::terminarDeComer, 5);
            }
        } finally {
            lock.unlock();
        }

    }

    private void terminarDeComer(){

        estado = Estado.TERMINADO;
        //Liberamos la mesa, el mesero tambien la debe liberar
        Optional<Entity> mesaOptional = FXGL.getGameWorld().getEntitiesByType(EntityType.MESA).findAny();
        if(mesaOptional.isPresent()){
            Mesa mesa = mesaOptional.get().getComponent(Mesa.class);
            mesa.liberarMesa();

        }

        //Nos retiramos del juego
        removeFromWorld();


    }

    public void esperarComida() {
        lock.lock();
        try {
            estado = Estado.ESPERANDO_COMIDA;

            while (estado == Estado.ESPERANDO_COMIDA){
                comidaLista.await();
            }

        }catch (InterruptedException e){
            e.printStackTrace();
        }

        finally {
            lock.unlock();
        }

    }

}