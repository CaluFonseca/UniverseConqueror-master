package com.badlogic.UniverseConqueror.Initializers;

import com.badlogic.UniverseConqueror.ECS.entity.EnemyFactory;
import com.badlogic.UniverseConqueror.ECS.entity.SpaceshipFactory;
import com.badlogic.UniverseConqueror.Pathfinding.MapGraphBuilder;
import com.badlogic.UniverseConqueror.Pathfinding.Node;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class EnemyInitializer {

    private final PooledEngine engine;
    private final World world;
    private final AssetManager assetManager;
    private final Entity player;
    private final OrthographicCamera camera;
    private final MapGraphBuilder mapGraphBuilder;

    public EnemyInitializer(PooledEngine engine, World world, AssetManager assetManager,
                            Entity player, OrthographicCamera camera, MapGraphBuilder mapGraphBuilder) {
        this.engine = engine;
        this.world = world;
        this.assetManager = assetManager;
        this.player = player;
        this.camera = camera;
        this.mapGraphBuilder = mapGraphBuilder;
    }

    /// Inicializa a nave espacial (objetivo)
    public Node initializeSpaceship() {
        Node node = mapGraphBuilder.getRandomWalkableNode();
        Vector2 worldPos = mapGraphBuilder.toWorldPosition(node);

        SpaceshipFactory spaceshipFactory = new SpaceshipFactory(assetManager);
        spaceshipFactory.createSpaceship(worldPos, engine, world);

        return node;
    }

    /// Inicializa os inimigos à volta da nave espacial
    public void initializeEnemies(Node node) {
        // Deslocamentos para o primeiro anel (raio 1)
        int[][] firstRing = new int[][] {
            {-1,  1}, { 1,  1}, { 2,  0}, { 1, -1}, {-1, -1}
        };

        // Deslocamentos para o segundo anel (raio 2)
        int[][] secondRing = new int[][] {
            {-2,  2}, { 2,  2}, { 4,  0}, { 2, -2}, {-2, -2}
        };

        spawnEnemiesAround(node, firstRing);
        spawnEnemiesAround(node, secondRing);
    }

    private void spawnEnemiesAround(Node centerNode, int[][] offsets) {
        for (int[] offset : offsets) {
            Node patrolStartNode = mapGraphBuilder.findNearestWalkableOffset(centerNode, offset[0], offset[1]);
            Node patrolEndNode = mapGraphBuilder.findNearestWalkableOffset(centerNode, offset[0] * 2, offset[1] * 2);

            if (patrolStartNode != null && patrolEndNode != null) {
                Vector2 patrolStartWorld = mapGraphBuilder.toWorldPosition(patrolStartNode);
                Vector2 patrolEndWorld = mapGraphBuilder.toWorldPosition(patrolEndNode);

                Entity enemy = EnemyFactory.createPatrollingEnemy(
                    engine, world, patrolStartWorld, assetManager,
                    player, camera, patrolStartWorld, patrolEndWorld
                );

                engine.addEntity(enemy);
            }
        }
    }

}
