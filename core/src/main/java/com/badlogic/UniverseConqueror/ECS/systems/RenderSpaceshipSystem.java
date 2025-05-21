package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.TextureComponent;
import com.badlogic.UniverseConqueror.ECS.components.TransformComponent;
import com.badlogic.UniverseConqueror.ECS.components.EndLevelComponent;
import com.badlogic.ashley.core.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class RenderSpaceshipSystem extends EntitySystem {
    private final SpriteBatch batch;
    private final OrthographicCamera camera;

    private final ComponentMapper<TextureComponent> tm = ComponentMapper.getFor(TextureComponent.class);
    private final ComponentMapper<TransformComponent> trm = ComponentMapper.getFor(TransformComponent.class);

    // Só renderiza entidades com Texture + Transform + EndLevel
    private final Family spaceshipFamily = Family.all(TextureComponent.class, TransformComponent.class, EndLevelComponent.class).get();

    public RenderSpaceshipSystem(SpriteBatch batch, OrthographicCamera camera) {
        this.batch = batch;
        this.camera = camera;
    }

    @Override
    public void update(float deltaTime) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        for (Entity entity : getEngine().getEntitiesFor(spaceshipFamily)) {
            TextureComponent texture = tm.get(entity);
            TransformComponent transform = trm.get(entity);

            if (texture.texture != null) {
                batch.draw(
                    texture.texture,
                    transform.position.x - texture.texture.getWidth() / 2f,
                    transform.position.y - texture.texture.getHeight() / 2f
                );
            }
        }

        batch.end();
    }
}
