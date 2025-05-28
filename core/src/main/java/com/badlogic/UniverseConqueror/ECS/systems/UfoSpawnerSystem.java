package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.AnimationComponent;
import com.badlogic.UniverseConqueror.ECS.components.StateComponent;
import com.badlogic.UniverseConqueror.ECS.entity.EnemyFactory;
import com.badlogic.UniverseConqueror.Pathfinding.MapGraphBuilder;
import com.badlogic.UniverseConqueror.Pathfinding.Node;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class UfoSpawnerSystem extends EntitySystem {

    private final PooledEngine engine;
    private final World world;
    private final AssetManager assetManager;
    private final MapGraphBuilder mapGraphBuilder;
    private final Entity player;
    private final OrthographicCamera camera;

    private float spawnTimer = 0f;
    private final float spawnInterval = 5f; // segundos entre spawns

    public UfoSpawnerSystem(PooledEngine engine, World world, AssetManager assetManager,
                            MapGraphBuilder mapGraphBuilder, Entity player, OrthographicCamera camera) {
        this.engine = engine;
        this.world = world;
        this.assetManager = assetManager;
        this.mapGraphBuilder = mapGraphBuilder;
        this.player = player;
        this.camera = camera;
    }

    @Override
    public void update(float deltaTime) {
        spawnTimer += deltaTime;
        if (spawnTimer >= spawnInterval) {
            spawnTimer = 0f;

            Node node = getRandomWalkableNode();
            if (node != null) {
                Vector2 worldPos = mapGraphBuilder.toWorldPosition(node);
                if (worldPos != null) {
                    // 🟢 UFO com pooling: entidade pode vir "reciclada" do pool
                    Entity ufoEnemy = EnemyFactory.createUfoEnemy(engine, world, worldPos, assetManager, player, camera);

                    AnimationComponent anim = ufoEnemy.getComponent(AnimationComponent.class);
                    StateComponent state = ufoEnemy.getComponent(StateComponent.class);

                    if (ufoEnemy != null){
                        engine.addEntity(ufoEnemy);
                    } else {
                        System.err.println("[ERRO] UFO criado incompleto! Falta Animation ou State!");
                    }

                }
            }
        }
    }

    private Node getRandomWalkableNode() {
        return mapGraphBuilder.getRandomWalkableNode();
    }
}
