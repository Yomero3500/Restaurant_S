package com.simulador.entidades;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.simulador.modelos.Order;
import com.simulador.modelos.monitores.MonitorOrder;
import com.simulador.modelos.monitores.MonitorCustomer;
import javafx.geometry.Point2D;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Queue;
import java.util.LinkedList;
import java.util.List;
import com.simulador.controllers.WaiterController;
import com.simulador.controllers.KitchenController;
import com.simulador.controllers.RestaurantController;

public class Waiter extends Component {
    private static final AtomicInteger orderIdGenerator = new AtomicInteger(0);
    private final Object taskLock = new Object();
    private final Object stateLock = new Object();

    private final MonitorOrder orderQueueMonitor;
    private final MonitorCustomer customerQueueMonitor;
    private final Point2D restPosition;
    private Point2D targetPosition;
    private boolean isMoving = false;
    private boolean isBusy = false;
    private WaiterState state = WaiterState.RESTING;
    private final Queue<Task> taskQueue = new LinkedList<>();
    private static final double SPEED = WaiterController.WAITER_SPEED;
    private final List<Entity> tables;

    public enum WaiterState {
        RESTING,
        MOVING_TO_TABLE,
        TAKING_ORDER,
        MOVING_TO_KITCHEN,
        DELIVERING_ORDER,
        RETURNING_TO_REST
    }

    private static class Task {
        final WaiterState targetState;
        final Point2D targetPos;
        final Runnable onComplete;

        Task(WaiterState state, Point2D pos, Runnable onComplete) {
            this.targetState = state;
            this.targetPos = pos;
            this.onComplete = onComplete;
        }
    }

    public Waiter(int id, MonitorOrder orderQueueMonitor, MonitorCustomer customerQueueMonitor,
                  Point2D restPosition, List<Entity> tables) {
        this.orderQueueMonitor = orderQueueMonitor;
        this.customerQueueMonitor = customerQueueMonitor;
        this.restPosition = restPosition;
        this.tables = tables;
    }

    @Override
    public void onAdded() {
        entity.setPosition(restPosition);
        startWaiterBehavior();
    }

    @Override
    public void onUpdate(double tpf) {
        if (isMoving && targetPosition != null) {
            Point2D currentPos = entity.getPosition();
            Point2D direction = targetPosition.subtract(currentPos);

            double distance = direction.magnitude();
            if (distance < SPEED * tpf) {
                entity.setPosition(targetPosition);
                System.out.println("Mesero alcanzó la posición objetivo: " + targetPosition);
                onTargetReached();

            } else {
                direction = direction.normalize().multiply(SPEED * tpf);
                entity.translate(direction.getX(), direction.getY());
                System.out.println("Mesero avanzando: nueva posición = " + entity.getPosition());
            }
        }
    }

    private void startWaiterBehavior() {
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    synchronized (stateLock) {
                        if (!isBusy) {
                            if (!checkAndDeliverReadyOrders()) {
                                if (customerQueueMonitor.hasWaitingCustomers()) {
                                    MonitorCustomer.CustomerRequest request =
                                            customerQueueMonitor.getNextCustomer();
                                    if (request != null) {
                                        serveCustomer(request.customer, request.tableNumber);
                                    }
                                } else if (state != WaiterState.RESTING && !isMoving && taskQueue.isEmpty()) {
                                    moveToRest();
                                }
                            }
                        }
                        stateLock.wait(500);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();
    }

    private void serveCustomer(Customer customer, int tableNumber) {
        synchronized (stateLock) {
            isBusy = true;
            System.out.println("Sirviendo al cliente: " + customer + " en la mesa " + tableNumber);

            addTask(new Task(
                    WaiterState.MOVING_TO_TABLE,
                    calculateTablePosition(tableNumber),
                    () -> takeOrderFromCustomer(tableNumber)
            ));
        }
    }


    private void addTask(Task task) {
        synchronized (taskLock) {
            taskQueue.add(task);
            System.out.println("Nueva tarea añadida: estado = " + task.targetState + ", posición = " + task.targetPos);

            if (!isMoving) {
                startNextTask();
            }
        }
    }


    private void startNextTask() {
        synchronized (taskLock) {
            if (!taskQueue.isEmpty() && !isMoving) {
                Task task = taskQueue.peek();
                if (task != null) {
                    state = task.targetState;
                    targetPosition = task.targetPos;
                    isMoving = true;
                }
            }
        }
    }

    private void onTargetReached() {
        synchronized (taskLock) {
            isMoving = false;
            Task currentTask = taskQueue.poll();
            if (currentTask != null) {
                currentTask.onComplete.run();
            }
            if (!taskQueue.isEmpty()) {
                startNextTask();
            }
        }
    }

    private void takeOrderFromCustomer(int tableNumber) {
        Order orden = new Order(orderIdGenerator.incrementAndGet(), tableNumber);

        Point2D kitchenPos = new Point2D(KitchenController.KITCHEN_X - 50, KitchenController.KITCHEN_Y);
        addTask(new Task(
                WaiterState.MOVING_TO_KITCHEN,
                kitchenPos,
                () -> {
                    orderQueueMonitor.addOrder(orden);
                    resetState();
                }
        ));
    }

    private void deliverOrder(Order order) {
        synchronized (stateLock) {
            isBusy = true;
            int tableNumber = order.getTableNumber();

            Point2D kitchenPos = new Point2D(KitchenController.KITCHEN_X - 50, KitchenController.KITCHEN_Y);
            addTask(new Task(
                    WaiterState.MOVING_TO_KITCHEN,
                    kitchenPos,
                    () -> addTask(new Task(
                            WaiterState.DELIVERING_ORDER,
                            calculateTablePosition(tableNumber),
                            () -> {
                                for (Entity tableEntity : tables) {
                                    Table table = tableEntity.getComponent(Table.class);
                                    if (table != null && table.getNumber() == tableNumber) {
                                        Customer customer = table.getCurrentCustomer();
                                        if (customer != null) {
                                            customer.startEating();
                                        }
                                        break;
                                    }
                                }
                                resetState();
                            }
                    ))
            ));
        }
    }

    private boolean checkAndDeliverReadyOrders() {
        try {
            synchronized (stateLock) {
                if (!isBusy) {
                    for (int i = 0; i < RestaurantController.TOTAL_TABLES; i++) {
                        Order readyOrder = orderQueueMonitor.checkReadyOrder(i);
                        if (readyOrder != null) {
                            deliverOrder(readyOrder);
                            return true;
                        }
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return false;
    }

    private void moveToRest() {
        synchronized (taskLock) {
            if (!isMoving && taskQueue.isEmpty()) {
                addTask(new Task(
                        WaiterState.RETURNING_TO_REST,
                        restPosition,
                        () -> state = WaiterState.RESTING
                ));
            }
        }
    }

    private Point2D calculateTablePosition(int tableNumber) {
        int row = tableNumber / 5;
        int col = tableNumber % 5;
        return new Point2D(
                300 + col * (RestaurantController.SPRITE_SIZE * 2),
                100 + row * (RestaurantController.SPRITE_SIZE * 2)
        );
    }

    private void resetState() {
        synchronized (stateLock) {
            isBusy = false;
        }
    }
}