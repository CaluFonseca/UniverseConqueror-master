package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.ECS.utils.ComponentMappers;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

// Sistema responsável por renderizar projéteis (balas)
public class BulletRenderSystem extends BaseIteratingSystem {

    private final SpriteBatch batch;

    public BulletRenderSystem(SpriteBatch batch) {
        super(Family.all(PositionComponent.class, TextureComponent.class, VelocityComponent.class, ProjectileComponent.class)
            .exclude(EnemyComponent.class)
            .get());
        this.batch = batch;
    }

    @Override
    public void update(float deltaTime) {
        batch.begin();
        super.update(deltaTime);
        batch.end();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent position = ComponentMappers.position.get(entity);
        TextureComponent textureComponent = ComponentMappers.texture.get(entity);
        VelocityComponent velocity = ComponentMappers.velocity.get(entity);

        if (position == null || textureComponent == null || velocity == null) return;

        float angle = velocity.velocity.angleDeg();
        boolean facingRight = false;

        batch.draw(
            textureComponent.texture,
            position.position.x, position.position.y,
            textureComponent.texture.getWidth() / 2f, textureComponent.texture.getHeight() / 2f,
            textureComponent.texture.getWidth(), textureComponent.texture.getHeight(),
            1f, 1f,
            angle,
            0, 0,
            textureComponent.texture.getWidth(), textureComponent.texture.getHeight(),
            facingRight,
            false
        );
    }
}
