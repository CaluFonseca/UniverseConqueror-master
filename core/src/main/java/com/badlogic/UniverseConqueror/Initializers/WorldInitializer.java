package com.badlogic.UniverseConqueror.Initializers;

import com.badlogic.UniverseConqueror.Screens.GameScreen;
import com.badlogic.UniverseConqueror.Utils.MapCollisionHandler;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

public class WorldInitializer {

    private final GameScreen screen;

    public WorldInitializer(GameScreen screen) {
        this.screen = screen;
    }

    /// Inicializa o mundo físico e o mapa com colisões
    public void initializeWorld() {
        screen.world = new World(new Vector2(0, -9.8f), true);
        screen.debugRenderer = new Box2DDebugRenderer();

        screen.map = new TmxMapLoader().load("mapa.tmx");
        screen.mapRenderer = new IsometricTiledMapRenderer(screen.map);

        screen.collisionHandler = new MapCollisionHandler(screen.map, "Collisions", "Jumpable");
        screen.collisionHandler.createBox2DBodies(screen.world);

        screen.shapeRenderer = new ShapeRenderer();
    }
}
