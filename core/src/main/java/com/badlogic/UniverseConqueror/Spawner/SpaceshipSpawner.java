package com.badlogic.UniverseConqueror.Spawner;

import com.badlogic.UniverseConqueror.ECS.entity.SpaceshipFactory;
import com.badlogic.UniverseConqueror.Interfaces.Spawner;
import com.badlogic.UniverseConqueror.Pathfinding.MapGraphBuilder;
import com.badlogic.UniverseConqueror.Pathfinding.Node;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class SpaceshipSpawner implements Spawner<Node> {

    private final PooledEngine engine;
    private final World world;
    private final AssetManager assetManager;
    private final MapGraphBuilder mapGraphBuilder;

    public SpaceshipSpawner(PooledEngine engine, World world, AssetManager assetManager, MapGraphBuilder mapGraphBuilder) {
        this.engine = engine;
        this.world = world;
        this.assetManager = assetManager;
        this.mapGraphBuilder = mapGraphBuilder;
    }

    @Override
    public Node spawn() {
        Node node = mapGraphBuilder.getRandomWalkableNode();
        Vector2 position = mapGraphBuilder.toWorldPosition(node);
        new SpaceshipFactory(assetManager).createSpaceship(position, engine, world);
        return node;
    }
}
