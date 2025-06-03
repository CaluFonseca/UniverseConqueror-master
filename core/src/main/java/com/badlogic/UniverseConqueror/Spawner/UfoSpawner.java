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
//Spawn UFO
public class UfoSpawner implements Spawner<Void> {

    private final PooledEngine engine;
    private final World world;
    private final AssetManager assetManager;
    private final Entity player;
    private final OrthographicCamera camera;
    private final MapGraphBuilder mapGraphBuilder;

    public UfoSpawner(PooledEngine engine, World world, AssetManager assetManager,
                      Entity player, OrthographicCamera camera,
                      MapGraphBuilder mapGraphBuilder) {
        this.engine = engine;
        this.world = world;
        this.assetManager = assetManager;
        this.player = player;
        this.camera = camera;
        this.mapGraphBuilder = mapGraphBuilder;
    }
/**
 * Método responsável por gerar os inimigos do tipo "UFO".
 * Cria três inimigos em posições aleatórias válidas no mapa.
 **/
    @Override
    public Void spawn() {
        for (int i = 0; i < 3; i++) {
            Node node = mapGraphBuilder.getRandomWalkableNode();
            if (node != null) {
                Vector2 position = mapGraphBuilder.toWorldPosition(node);
                Entity ufo = EnemyFactory.createUfoEnemy(engine, world, position, assetManager, player, camera);
                if (ufo != null) {
                    engine.addEntity(ufo);
                }
            }
        }
        return null;
    }
}
