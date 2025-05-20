package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.TextureComponent;
import com.badlogic.UniverseConqueror.ECS.components.PositionComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.UniverseConqueror.ECS.components.VelocityComponent;

public class BulletRenderSystem extends IteratingSystem {
    private SpriteBatch batch;

    private ComponentMapper<PositionComponent> positionMapper;
    private ComponentMapper<TextureComponent> textureMapper;
    private ComponentMapper<VelocityComponent> velocityMapper;

    public BulletRenderSystem(SpriteBatch batch) {
        super(Family.all(PositionComponent.class, TextureComponent.class, VelocityComponent.class).get());
        this.batch = batch;
        positionMapper = ComponentMapper.getFor(PositionComponent.class);
        textureMapper = ComponentMapper.getFor(TextureComponent.class);
        velocityMapper = ComponentMapper.getFor(VelocityComponent.class);
    }

    @Override
    public void update(float deltaTime) {
        batch.begin();
        super.update(deltaTime);
        batch.end();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent position = positionMapper.get(entity);
        TextureComponent textureComponent = textureMapper.get(entity);
        VelocityComponent velocity = velocityMapper.get(entity);

        // Calcula o ângulo de rotação com base na direção do movimento
        float angle = velocity.velocity.angleDeg();
        boolean facingRight = false;

        // Desenha a textura da bala na posição certa, com rotação e inversão
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
