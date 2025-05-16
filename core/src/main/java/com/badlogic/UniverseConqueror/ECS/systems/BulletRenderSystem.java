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

        // Inicializa os mappers
        positionMapper = ComponentMapper.getFor(PositionComponent.class);
        textureMapper = ComponentMapper.getFor(TextureComponent.class);
        velocityMapper = ComponentMapper.getFor(VelocityComponent.class);
    }

    @Override
    public void update(float deltaTime) {
        batch.begin(); // Inicia o SpriteBatch fora do loop de entidades

        super.update(deltaTime);  // Processa as entidades, uma por uma

        batch.end(); // Finaliza o SpriteBatch após o loop
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent position = positionMapper.get(entity);
        TextureComponent textureComponent = textureMapper.get(entity);
        VelocityComponent velocity = velocityMapper.get(entity);

        // Calcula o ângulo de rotação com base na direção do movimento
        float angle = velocity.velocity.angleDeg();

        // Verifica se a bala está indo para a direita ou para a esquerda
        boolean facingRight = false;  // Se a velocidade em X for positiva, a bala vai para a direita

        // Desenha a textura da bala na posição certa, com rotação e inversão se necessário
        batch.draw(
                textureComponent.texture,
                position.position.x, position.position.y,  // X, Y position
                textureComponent.texture.getWidth() / 2f, textureComponent.texture.getHeight() / 2f,  // Origin in the center
                textureComponent.texture.getWidth(), textureComponent.texture.getHeight(),  // Size of the texture
                1f, 1f,  // No scaling
                angle,  // Rotation angle (in radians)
                0, 0,  // Origin for rotation (using center of the texture)
                textureComponent.texture.getWidth(), textureComponent.texture.getHeight(),  // Size of the texture
                facingRight,  // Horizontal flip based on 'facingRight'
                false  // No vertical flip
        );

    }
}
