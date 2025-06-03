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

    @Override
    public Void spawn() {
        System.out.println("Starting spawn process...");

        int[][] offsets = {
            {-1, 1}, {1, 1}, {2, 0}, {1, -1}, {-1, -1}, {-2, 0}
        };

        for (int[] offset : offsets) {
            System.out.println("Processing offset: [" + offset[0] + ", " + offset[1] + "]");

            Node patrolStart = mapGraphBuilder.findNearestWalkableOffset(spaceshipNode, offset[0], offset[1]);
            Node patrolEnd = mapGraphBuilder.findNearestWalkableOffset(spaceshipNode, offset[0] * 2, offset[1] * 2);

            if (patrolStart == null || patrolEnd == null) {
                System.out.println("One or both patrol nodes are null for offset [" + offset[0] + ", " + offset[1] + "]");
                continue;
            }

            System.out.println("Patrol start node: " + patrolStart + ", Patrol end node: " + patrolEnd);

            Vector2 startWorld = mapGraphBuilder.toWorldPosition(patrolStart);
            Vector2 endWorld = mapGraphBuilder.toWorldPosition(patrolEnd);

            System.out.println("Start world position: " + startWorld + ", End world position: " + endWorld);

            // Debug if the world positions are valid.
            if (startWorld == null || endWorld == null) {
                System.out.println("Invalid world positions for patrol start and end.");
                continue;
            }

            // Criação do inimigo
            Entity enemy = EnemyFactory.createPatrollingEnemy(
                engine, world, startWorld, assetManager, player, camera, startWorld, endWorld
            );
            if (enemy == null) {
                System.out.println("Failed to create enemy.");
            } else {
                System.out.println("Enemy created and added to the engine.");
                engine.addEntity(enemy);
            }
        }

        System.out.println("Spawn process completed.");
        return null;
    }
}
