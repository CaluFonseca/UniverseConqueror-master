package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.Utils.AssetPaths;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class CrosshairRenderSystem extends EntitySystem {

    private final SpriteBatch batch;
    private final OrthographicCamera camera;
    private final Texture crosshairTexture;

    private final float scale;
    private final float width, height;

    private final Vector2 crosshairPosition = new Vector2();
    private final Vector3 tempVector3 = new Vector3();

    public CrosshairRenderSystem(SpriteBatch batch, OrthographicCamera camera, AssetManager assetManager, float scale) {
        this.batch = batch;
        this.camera = camera;
        this.crosshairTexture = assetManager.get(AssetPaths.CROSSHAIR_TEXTURE, Texture.class);
        this.scale = scale;
        this.width = crosshairTexture.getWidth() * scale;
        this.height = crosshairTexture.getHeight() * scale;
    }

    @Override
    public void update(float deltaTime) {
        crosshairPosition.set(Gdx.input.getX(), Gdx.input.getY());
        camera.unproject(tempVector3.set(crosshairPosition.x, crosshairPosition.y, 0));
        crosshairPosition.set(tempVector3.x, tempVector3.y);

        batch.begin();
        batch.draw(
            crosshairTexture,
            crosshairPosition.x - width * 0.5f,
            crosshairPosition.y - height * 0.5f,
            width, height
        );
        batch.end();
    }
}
