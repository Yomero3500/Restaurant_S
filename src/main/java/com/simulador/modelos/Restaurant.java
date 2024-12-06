package com.simulador.modelos;

import com.simulador.Observer.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class Restaurant {
    public static final int TOTAL_TABLES = 10;
    private final boolean[] tables;
    private final ReentrantLock lock;
    private final Condition tableAvailable;
    private final List<Observer> observers;

    public Restaurant() {
        tables = new boolean[TOTAL_TABLES];
        lock = new ReentrantLock();
        tableAvailable = lock.newCondition();
        observers = new ArrayList<>();
    }

    public void addObserver(Observer observer) {
        lock.lock();
        try {
            observers.add(observer);
        } finally {
            lock.unlock();
        }
    }

    public void removeObserver(Observer observer) {
        lock.lock();
        try {
            observers.remove(observer);
        } finally {
            lock.unlock();
        }
    }

    private void notifyObservers() {
        lock.lock();
        try {
            for (Observer observer : observers) {
                observer.onTableAvailable();
            }
        } finally {
            lock.unlock();
        }
    }

    public int findAvailableTable() {
        lock.lock();
        try {
            for (int i = 0; i < tables.length; i++) {
                if (!tables[i]) {
                    return i;
                }
            }
            return -1;
        } finally {
            lock.unlock();
        }
    }

    public void occupyTable(int tableNumber) {
        lock.lock();
        try {
            tables[tableNumber] = true;
        } finally {
            lock.unlock();
        }
    }

    public void releaseTable(int tableNumber) {
        lock.lock();
        try {
            tables[tableNumber] = false;
            notifyObservers(); // Notifica cuando una mesa se libera
            tableAvailable.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void waitForAvailableTable() throws InterruptedException {
        lock.lock();
        try {
            while (findAvailableTable() == -1) {
                tableAvailable.await();
            }
        } finally {
            lock.unlock();
        }
    }
}
