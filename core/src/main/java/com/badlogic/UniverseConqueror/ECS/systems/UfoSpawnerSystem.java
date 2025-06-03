package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.Interfaces.Spawner;
import com.badlogic.ashley.core.EntitySystem;


public class UfoSpawnerSystem extends EntitySystem {

    private final Spawner<Void> ufoSpawner;
    private float spawnTimer = 0f;
    private final float spawnInterval = 10f;

    public UfoSpawnerSystem(Spawner<Void> ufoSpawner) {
        this.ufoSpawner = ufoSpawner;
    }

    @Override
    public void update(float deltaTime) {
        spawnTimer += deltaTime;
        if (spawnTimer >= spawnInterval) {
            spawnTimer = 0f;
            ufoSpawner.spawn();
        }
    }

    public void resetTimer() {
        this.spawnTimer = 0f;
    }
}
