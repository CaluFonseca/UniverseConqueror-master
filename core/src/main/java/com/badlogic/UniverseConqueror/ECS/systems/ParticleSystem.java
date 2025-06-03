package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.utils.ComponentMappers;
import com.badlogic.UniverseConqueror.ECS.components.ParticleComponent;
import com.badlogic.UniverseConqueror.ECS.components.PositionComponent;
import com.badlogic.UniverseConqueror.ECS.components.VelocityComponent;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

// Sistema responsável por renderizar efeitos de partículas associados a entidades
public class ParticleSystem extends BaseIteratingSystem {

    private final SpriteBatch batch;
    private final OrthographicCamera camera;


    public ParticleSystem(SpriteBatch batch, OrthographicCamera camera) {
        super(Family.all(ParticleComponent.class, PositionComponent.class).get());
        this.batch = batch;
        this.camera = camera;
    }

    // Inicia o batch antes de processar entidades
    @Override
    public void update(float deltaTime) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        super.update(deltaTime);
        batch.end();
    }

    // Processa uma única entidade com partículas
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ParticleComponent particle = ComponentMappers.particle.get(entity);
        PositionComponent position = ComponentMappers.position.get(entity);
        VelocityComponent velocity = ComponentMappers.velocity.has(entity) ? ComponentMappers.velocity.get(entity) : null;


        if (velocity != null && particle.effect != null) {
            float angle = velocity.velocity.angleDeg() + 180f;

            for (var emitter : particle.effect.getEmitters()) {
                emitter.getAngle().setHigh(angle + 40f);
                emitter.getAngle().setLow(angle - 40f);
                emitter.getVelocity().setLow(100f);
                emitter.getVelocity().setHigh(300f);
            }
        }


        if (particle.effect != null) {
            float px = position.position.x + particle.offset.x;
            float py = position.position.y + particle.offset.y;
            particle.effect.setPosition(px, py);
            particle.effect.draw(batch, deltaTime);
        }
    }
}
