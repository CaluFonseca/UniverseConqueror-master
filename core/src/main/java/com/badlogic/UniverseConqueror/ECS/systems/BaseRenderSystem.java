package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class BaseRenderSystem extends IteratingSystem {

    protected final SpriteBatch batch;
    protected final OrthographicCamera camera;

    public BaseRenderSystem(Family family, SpriteBatch batch, OrthographicCamera camera) {
        super(family);
        this.batch = batch;
        this.camera = camera;
    }

    @Override
    public void update(float deltaTime) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        super.update(deltaTime);
        batch.end();
    }
}
