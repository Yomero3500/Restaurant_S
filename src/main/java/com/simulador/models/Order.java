package com.simulador.models;

public class Order {
    private final int id;
    private final int tableNumber;
    private final long preparationTime;
    private OrderStatus status;


    public Order(int id, int tableNumber) {
        this.id = id;
        this.tableNumber = tableNumber;
        this.status = OrderStatus.PENDING;
        this.preparationTime = (long) (Math.random() * 8000 + 3000); // Entre 3 y 8 segundos
    }

    public int getId() { return id; }
    public int getTableNumber() { return tableNumber; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public long getPreparationTime() { return preparationTime; }
}