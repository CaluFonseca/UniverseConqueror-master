package com.badlogic.UniverseConqueror.Context;

import com.badlogic.UniverseConqueror.Utils.MapCollisionHandler;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.maps.tiled.TiledMap;

public class WorldContext {
    private float centerX;
    private float centerY;
    private World world;
    private Box2DDebugRenderer debugRenderer;
    private TiledMap map;
    private IsometricTiledMapRenderer mapRenderer;
    private MapCollisionHandler collisionHandler;
    private ShapeRenderer shapeRenderer;

    public float getCenterX() {
        return centerX;
    }

    public void setCenterX(float centerX) {
        this.centerX = centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public void setCenterY(float centerY) {
        this.centerY = centerY;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public void setDebugRenderer(Box2DDebugRenderer debugRenderer) {
        this.debugRenderer = debugRenderer;
    }

    public Box2DDebugRenderer getDebugRenderer() {
        return debugRenderer;
    }

    public void setMap(TiledMap map) {
        this.map = map;
    }

    public TiledMap getMap() {
        return map;
    }

    public IsometricTiledMapRenderer getMapRenderer() {
        return mapRenderer;
    }

    public void setMapRenderer(IsometricTiledMapRenderer mapRenderer) {
        this.mapRenderer = mapRenderer;
    }


    public MapCollisionHandler getCollisionHandler() {
        return collisionHandler;
    }

    public void setCollisionHandler(MapCollisionHandler collisionHandler) {
        this.collisionHandler = collisionHandler;
    }

    public ShapeRenderer getShapeRenderer() {
        return shapeRenderer;
    }

    public void setShapeRenderer(ShapeRenderer shapeRenderer) {
        this.shapeRenderer = shapeRenderer;
    }
}
