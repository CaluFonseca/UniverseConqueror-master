package com.badlogic.UniverseConqueror.Initializers;

import com.badlogic.UniverseConqueror.Context.GameContext;
import com.badlogic.UniverseConqueror.Interfaces.Spawner;
import com.badlogic.UniverseConqueror.Pathfinding.MapGraphBuilder;
import com.badlogic.UniverseConqueror.Pathfinding.Node;
import com.badlogic.UniverseConqueror.Spawner.SpaceshipEnemySpawner;
import com.badlogic.UniverseConqueror.Spawner.SpaceshipSpawner;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;

public class EnemyInitializer extends AbstractInitializer {

    private final PooledEngine engine;
    private final World world;
    private final AssetManager assetManager;
    private final Entity player;
    private final OrthographicCamera camera;
    private final MapGraphBuilder mapGraphBuilder;

    private Spawner<Void> spaceshipEnemySpawner;

    public EnemyInitializer(GameContext context) {
        super(context);
        this.engine = context.getEngine();
        this.world = context.getWorldContext().getWorld();
        this.assetManager = context.getAssetManager();
        this.player = context.getPlayer();
        this.camera = context.getCamera();
        this.mapGraphBuilder = context.getMapGraphBuilder();
    }

    // Método implementado da classe AbstractInitializer
    @Override
    public void initialize() {
        // Inicializa a nave e retorna o nó onde ela está
        Node spaceshipNode = initializeSpaceship();
        // Spawna os inimigos ao redor da nave
        initializeEnemies(spaceshipNode);
    }

    // Cria a nave e retorna o nó onde ela está
    public Node initializeSpaceship() {
        Spawner<Node> spaceshipSpawner = new SpaceshipSpawner(engine, world, assetManager, mapGraphBuilder);
        return spaceshipSpawner.spawn();
    }

    // Spawna os inimigos ao redor da nave
    public void initializeEnemies(Node spaceshipNode) {
        spaceshipEnemySpawner = new SpaceshipEnemySpawner(
            engine, world, assetManager, player, camera, mapGraphBuilder, spaceshipNode
        );
        spaceshipEnemySpawner.spawn();
    }
}
