package com.badlogic.UniverseConqueror.Spawner;

import com.badlogic.UniverseConqueror.Interfaces.Spawner;
import com.badlogic.UniverseConqueror.ECS.entity.ItemFactory;
import com.badlogic.UniverseConqueror.ECS.systems.RenderItemSystem;
import com.badlogic.UniverseConqueror.Pathfinding.MapGraphBuilder;
import com.badlogic.UniverseConqueror.Pathfinding.Node;
import com.badlogic.UniverseConqueror.Utils.AssetPaths;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class ItemSpawner implements Spawner<Void> {

    private final PooledEngine engine;
    private final World world;
    private final AssetManager assetManager;
    private final OrthographicCamera camera;
    private final MapGraphBuilder mapGraphBuilder;

    public ItemSpawner(PooledEngine engine, World world, AssetManager assetManager,
                       OrthographicCamera camera, MapGraphBuilder mapGraphBuilder) {
        this.engine = engine;
        this.world = world;
        this.assetManager = assetManager;
        this.camera = camera;
        this.mapGraphBuilder = mapGraphBuilder;
    }

    @Override
    public Void spawn() {
        for (int i = 0; i < 10; i++) {
            engine.addEntity(createItem("Vida", AssetPaths.ITEM_VIDA).createEntity(engine, world));
            engine.addEntity(createItem("Ataque", AssetPaths.ITEM_ATAQUE).createEntity(engine, world));
            engine.addEntity(createItem("SuperAtaque", AssetPaths.ITEM_SUPER_ATAQUE).createEntity(engine, world));
        }

        SpriteBatch batchItem = new SpriteBatch();
        engine.addSystem(new RenderItemSystem(batchItem, camera));
        return null;
    }

    private ItemFactory createItem(String type, String assetPath) {
        Node node = mapGraphBuilder.getRandomWalkableNode();
        Vector2 worldPos = mapGraphBuilder.toWorldPosition(node);
        return new ItemFactory(type, worldPos.x, worldPos.y, assetPath, assetManager);
    }
}
