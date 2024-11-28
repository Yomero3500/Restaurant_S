package com.Restaurant.models;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Waiter extends Entity {

    private enum Estado {
        ESPERANDO, CAMINANDO_A_MESA, TOMANDO_ORDEN, LLEVANDO_ORDEN_A_COCINA,
        ESPERANDO_COMIDA, LLEVANDO_COMIDA_A_MESA, REGRESANDO
    }

    private Estado estado = Estado.ESPERANDO;
    private Mesa mesaAsignada;
    private Point2D posicionInicial;
    private double velocidad = 100; // Pixeles por segundo

    private Queue<Orden> ordenesPendientes;
    private Queue<Comida> comidasListas;

    private Lock lock;
    private Condition comidaDisponible;
    private Orden ordenActual;


    public Waiter(SpawnData data) {
        Image waiterImage = new Image("/assets/textures/waiter.png"); // Ruta de la imagen
        ImageView imageView = new ImageView(waiterImage);
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);

        FXGL.entityBuilder(data)
                .view(imageView)
                .type(EntityType.WAITER)
                .buildAndAttach();


        posicionInicial = getPosition();
        ordenesPendientes = new ConcurrentLinkedQueue<>();
        comidasListas = new ConcurrentLinkedQueue<>();
        this.lock = new ReentrantLock();
        this.comidaDisponible = lock.newCondition();

        Thread hiloMesero = new Thread(this::run);
        hiloMesero.setDaemon(true);
        hiloMesero.start();


    }


    private void run() {
        while (true) {
            switch (estado) {
                case ESPERANDO:
                    buscarMesaDisponible();

                    break;
                case CAMINANDO_A_MESA:
                    moverAMesa();
                    break;

                case TOMANDO_ORDEN:

                    break;

                case LLEVANDO_ORDEN_A_COCINA:
                    llevarOrdenACocina();
                    break;
                case ESPERANDO_COMIDA:
                    esperarComida();
                    break;

                case LLEVANDO_COMIDA_A_MESA:
                    llevarComidaAMesa();
                    break;

                case REGRESANDO:
                    regresarAPosicionInicial();
                    break;
            }

            try {
                Thread.sleep(50); // Pequeña pausa para evitar un ciclo infinito
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

    }

    private void buscarMesaDisponible() {

        for(Entity entity: FXGL.getGameWorld().getEntitiesByType(EntityType.MESA)){

            Mesa mesa = entity.getComponent(Mesa.class);

            if(mesa.isDisponible() && mesa.getClienteEnMesa() != null){
                mesaAsignada = mesa;
                estado = Estado.CAMINANDO_A_MESA;
                return;
            }

        }




    }


    private void moverAMesa() {
        if(mesaAsignada != null && mesaAsignada.getEntity() != null) {
            Point2D posicionMesa = mesaAsignada.getEntity().getPosition();
            Point2D direccion = posicionMesa.subtract(getPosition()).normalize();

            translate(direccion.multiply(velocidad * FXGL.getGameTimer().getDeltaTime()));

            if (getPosition().distance(posicionMesa) < 5) {
                estado = Estado.TOMANDO_ORDEN;
                tomarOrden(ordenActual); // Mover la llamada a tomarOrden aquí
            }
        }


    }



    public void tomarOrden(Orden orden) {
        this.ordenActual = orden;


        if (estado == Estado.TOMANDO_ORDEN) {
            ordenesPendientes.offer(orden);
            estado = Estado.LLEVANDO_ORDEN_A_COCINA;

        }

    }



    private void llevarOrdenACocina(){
        Optional<Entity> chefOptional = FXGL.getGameWorld().getEntitiesByType(EntityType.CHEF).findAny();

        if(chefOptional.isPresent()){
            Chef chef = chefOptional.get().getComponent(Chef.class);
            Orden orden = ordenesPendientes.poll();
            if(orden != null){

                chef.agregarOrden(orden);
                orden.getCliente().esperarComida();
                estado = Estado.ESPERANDO_COMIDA;
            }
        }

    }


    private void esperarComida(){
        lock.lock();
        try{
            while (comidasListas.isEmpty()){
                comidaDisponible.await();
            }

            estado = Estado.LLEVANDO_COMIDA_A_MESA;

        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }


    }

    public void notificarComidaLista(){
        lock.lock();
        try{

            comidaDisponible.signalAll();

        }finally {
            lock.unlock();
        }

    }




    private void llevarComidaAMesa(){
        Comida comida = comidasListas.poll();//Sacamos del buffer
        if(comida != null && mesaAsignada != null && mesaAsignada.getClienteEnMesa() == comida.getCliente()){

            mesaAsignada.getClienteEnMesa().comidaServida(comida);
            estado = Estado.REGRESANDO;

        }




    }


    private void regresarAPosicionInicial(){

        Point2D direccion = posicionInicial.subtract(getPosition()).normalize();

        translate(direccion.multiply(velocidad * FXGL.getGameTimer().getDeltaTime()));


        if(getPosition().distance(posicionInicial) < 5){
            estado = Estado.ESPERANDO;
            mesaAsignada = null;//Liberamos la mesa
        }


    }






}