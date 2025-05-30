package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;

public class BulletRenderSystem extends IteratingSystem {

    /// Batch usado para desenhar as texturas no ecrã
    private SpriteBatch batch;

    /// Mappers para acesso rápido aos componentes necessários
    private ComponentMapper<PositionComponent> positionMapper;
    private ComponentMapper<TextureComponent> textureMapper;
    private ComponentMapper<VelocityComponent> velocityMapper;

    /// Construtor que define os componentes requeridos (bala com posição, textura e velocidade, mas não inimigo)
    public BulletRenderSystem(SpriteBatch batch) {
        super(Family.all(PositionComponent.class, TextureComponent.class, VelocityComponent.class, ProjectileComponent.class)
            .exclude(EnemyComponent.class)
            .get());

        this.batch = batch;
        positionMapper = ComponentMapper.getFor(PositionComponent.class);
        textureMapper = ComponentMapper.getFor(TextureComponent.class);
        velocityMapper = ComponentMapper.getFor(VelocityComponent.class);
    }

    /// Envolve a renderização das entidades entre begin() e end()
    @Override
    public void update(float deltaTime) {
        batch.begin();
        super.update(deltaTime);
        batch.end();
    }

    /// Renderiza cada bala com base em sua posição e direção
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent position = positionMapper.get(entity);
        TextureComponent textureComponent = textureMapper.get(entity);
        VelocityComponent velocity = velocityMapper.get(entity);

        /// Calcula o ângulo com base na direção da velocidade
        float angle = velocity.velocity.angleDeg();

        /// Define se a textura deve ser invertida horizontalmente
        boolean facingRight = false;

        /// Desenha a textura da bala com rotação, posição e escala
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
