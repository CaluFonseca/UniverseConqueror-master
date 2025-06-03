package com.badlogic.UniverseConqueror.Spawner;

import com.badlogic.UniverseConqueror.ECS.entity.EnemyFactory;
import com.badlogic.UniverseConqueror.Interfaces.Spawner;
import com.badlogic.UniverseConqueror.Pathfinding.MapGraphBuilder;
import com.badlogic.UniverseConqueror.Pathfinding.Node;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class SpaceshipEnemySpawner implements Spawner<Void> {
    private final PooledEngine engine;
    private final World world;
    private final AssetManager assetManager;
    private final Entity player;
    private final OrthographicCamera camera;
    private final MapGraphBuilder mapGraphBuilder;
    private final Node spaceshipNode;

    //Spawn de inimigos
    public SpaceshipEnemySpawner(PooledEngine engine, World world, AssetManager assetManager,
                                 Entity player, OrthographicCamera camera,
                                 MapGraphBuilder mapGraphBuilder, Node spaceshipNode) {
        this.engine = engine;
        this.world = world;
        this.assetManager = assetManager;
        this.player = player;
        this.camera = camera;
        this.mapGraphBuilder = mapGraphBuilder;
        this.spaceshipNode = spaceshipNode;
    }

    //Método que realiza o spawn dos inimigos.
    @Override
    public Void spawn() {
        System.out.println("Starting spawn process...");

        int[][] offsets = {
            {-1, 1}, {1, 1}, {2, 0}, {1, -1}, {-1, -1}, {-2, 0}
        };

        for (int[] offset : offsets) {
            Node patrolStart = mapGraphBuilder.findNearestWalkableOffset(spaceshipNode, offset[0], offset[1]);
            Node patrolEnd = mapGraphBuilder.findNearestWalkableOffset(spaceshipNode, offset[0] * 2, offset[1] * 2);

            if (patrolStart == null || patrolEnd == null) {
                continue;
            }

            Vector2 startWorld = mapGraphBuilder.toWorldPosition(patrolStart);
            Vector2 endWorld = mapGraphBuilder.toWorldPosition(patrolEnd);

            if (startWorld == null || endWorld == null) {
                continue;
            }

            // Criação do inimigo
            Entity enemy = EnemyFactory.createPatrollingEnemy(
                engine, world, startWorld, assetManager, player, camera, startWorld, endWorld
            );
                engine.addEntity(enemy);
        }
        return null;
    }
}
