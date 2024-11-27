package com.Restaurant.factories;

import com.Restaurant.models.*;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;

public class RestaurantFactory implements EntityFactory {
    @Spawns("client")
    public Entity newClient(SpawnData data) {
        return new Client(data);
    }

    @Spawns("waiter")
    public Entity newWaiter(SpawnData data) {
        return new Waiter(data);
    }

    @Spawns("chef")
    public Entity newChef(SpawnData data) {
        return new Chef(data);
    }

    @Spawns("receptionist")
    public Entity newReceptionist(SpawnData data) {
        return new Receptionist(data);
    }
}
