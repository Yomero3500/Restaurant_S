package com.Restaurant.models;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue; // Import para ConcurrentLinkedQueue
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Chef extends Entity {

    private Queue<Orden> bufferOrdenes;
    private Queue<Comida> bufferComidas;
    private Lock lock;
    private Condition ordenesDisponibles;


    public Chef(SpawnData data) {

        Image chefImage = new Image("/assets/textures/chef.png");
        ImageView imageView = new ImageView(chefImage);
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);

        FXGL.entityBuilder(data)
                .view(imageView)
                .type(EntityType.CHEF)
                .buildAndAttach();


        this.bufferOrdenes = new ConcurrentLinkedQueue<>();  // Usa ConcurrentLinkedQueue
        this.bufferComidas = new ConcurrentLinkedQueue<>();// Usa ConcurrentLinkedQueue
        this.lock = new ReentrantLock();
        this.ordenesDisponibles = lock.newCondition();


        // Inicia el hilo del chef
        Thread hiloChef = new Thread(this::run);
        hiloChef.setDaemon(true); // Para que el hilo termine cuando la app cierra
        hiloChef.start();
    }

    private void run() {
        while (true) {
            lock.lock();
            try {
                while (bufferOrdenes.isEmpty()) {
                    ordenesDisponibles.await(); // Espera a que haya órdenes
                }

                Orden orden = bufferOrdenes.poll();
                if (orden != null) {
                    prepararComida(orden);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Manejo de interrupciones
                break; // Sale del bucle si el hilo es interrumpido
            } finally {

                lock.unlock();
            }


        }
    }



    private void prepararComida(Orden orden) {
        try {
            Thread.sleep(3000); // Simula el tiempo de preparación

            Comida comida = new Comida(orden.getCliente(), orden.getMesero());

            lock.lock();
            try {


                bufferComidas.offer(comida);
                FXGL.getGameWorld().getEntitiesByType(EntityType.WAITER).forEach(waiter -> {
                    waiter.getComponent(Waiter.class).notificarComidaLista();

                });

            }
            finally {
                lock.unlock();
            }



        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    public void agregarOrden(Orden orden) {
        lock.lock();
        try {
            bufferOrdenes.offer(orden);
            ordenesDisponibles.signalAll();  // Notifica a los chefs
        } finally {
            lock.unlock();
        }
    }




}