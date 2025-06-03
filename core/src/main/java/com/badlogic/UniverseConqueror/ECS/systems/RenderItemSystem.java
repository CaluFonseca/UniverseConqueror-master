package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.ECS.utils.ComponentMappers;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class RenderItemSystem extends BaseRenderSystem {

    public RenderItemSystem(SpriteBatch batch, OrthographicCamera camera) {
        super(Family.all(TextureComponent.class, TransformComponent.class, ItemComponent.class)
            .exclude(UfoComponent.class).get(), batch, camera);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TextureComponent texture = ComponentMappers.texture.get(entity);
        TransformComponent transform = ComponentMappers.transform.get(entity);

        if (texture.texture != null) {
            batch.draw(texture.texture, transform.position.x, transform.position.y);
        }
    }
}
