package com.Restaurant.models;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Receptionist extends Entity {

    private Queue<Client> clientesEnEspera;
    private Lock lock;
    private Condition mesaDisponible;

    public Receptionist(SpawnData data) {
        // Cargar la imagen
        Image receptionistImage = new Image("/assets/textures/receptionist.png"); // Ruta de la imagen

        // Crear la vista de la entidad usando ImageView
        ImageView imageView = new ImageView(receptionistImage);

        // Configurar la escala de la imagen si es necesario
        imageView.setFitWidth(50); // Ajusta el ancho según sea necesario
        imageView.setFitHeight(50); // Ajusta la altura según sea necesario

        // Construir y adjuntar la entidad con la imagen
        FXGL.entityBuilder(data)
                .view(imageView) // Usar ImageView en lugar de Circle
                .type(EntityType.RECEPTIONIST)
                .buildAndAttach();

        clientesEnEspera = new ConcurrentLinkedQueue<>();
        lock = new ReentrantLock();
        mesaDisponible = lock.newCondition();

        Thread hiloRecepcionista = new Thread(this::run);
        hiloRecepcionista.setDaemon(true);
        hiloRecepcionista.start();


    }

    private void run(){
        while (true){
            lock.lock();
            try {
                while (clientesEnEspera.isEmpty()) {
                    mesaDisponible.await();
                }

                Client cliente = clientesEnEspera.poll();
                if(cliente != null){
                    asignarMesa(cliente);
                }

            }catch (InterruptedException e){

                Thread.currentThread().interrupt();
                break;
            }
            finally {
                lock.unlock();
            }

        }
    }

    private void asignarMesa(Client cliente) {
        // Lógica para asignar una mesa al cliente

        for (Entity entity : FXGL.getGameWorld().getEntitiesByType(EntityType.MESA)) {
            Mesa mesa = entity.getComponent(Mesa.class);
            if (mesa.isDisponible()) {

                mesa.asignarCliente(cliente);

                Optional<Entity> waiterOptional = FXGL.getGameWorld().getEntitiesByType(EntityType.WAITER).findAny();
                if(waiterOptional.isPresent()){
                    Waiter waiter = waiterOptional.get().getComponent(Waiter.class);
                    cliente.asignarMesero(waiter);

                }

                return; // Salir del bucle una vez que se asigna una mesa

            }
        }
        //Si no hay mesas, el cliente debe esperar
        clientesEnEspera.offer(cliente);

    }
    public void agregarCliente(Client cliente) {
        lock.lock();

        try{
            clientesEnEspera.offer(cliente);
            mesaDisponible.signalAll();
        }
        finally {
            lock.unlock();
        }


    }
}