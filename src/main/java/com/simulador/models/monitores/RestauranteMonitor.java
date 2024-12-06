package com.simulador.models.monitores;

public class RestauranteMonitor {
    public static final int TOTAL_TABLES = 10;
    private final boolean[] tables;

    public RestauranteMonitor() {
        tables = new boolean[TOTAL_TABLES];
    }

    public synchronized int findAvailableTable() {
        for (int i = 0; i < tables.length; i++) {
            if (!tables[i]) {
                return i;
            }
        }
        return -1;
    }

    public synchronized void occupyTable(int tableNumber) {
        tables[tableNumber] = true;
    }

    public synchronized void releaseTable(int tableNumber) {
        tables[tableNumber] = false;
        notifyAll(); // Notificar a todos los threads que esperan una mesa disponible.
    }

    public synchronized void waitForAvailableTable() throws InterruptedException {
        while (findAvailableTable() == -1) {
            wait(); // Esperar hasta que haya una mesa disponible.
        }
    }
}
