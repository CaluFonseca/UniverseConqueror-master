package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.ParticleComponent;
import com.badlogic.UniverseConqueror.ECS.components.PositionComponent;
import com.badlogic.UniverseConqueror.ECS.components.VelocityComponent;
import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;

public class ParticleSystem extends EntitySystem {

    private final SpriteBatch batch;
    private final OrthographicCamera camera;

    private final ComponentMapper<ParticleComponent> pm = ComponentMapper.getFor(ParticleComponent.class);
    private final ComponentMapper<PositionComponent> posm = ComponentMapper.getFor(PositionComponent.class);
    private final ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
    private ImmutableArray<Entity> entities;

    public ParticleSystem(SpriteBatch batch, OrthographicCamera camera) {
        this.batch = batch;
        this.camera = camera;
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(ParticleComponent.class, PositionComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        for (Entity entity : entities) {
            ParticleComponent particle = pm.get(entity);
            PositionComponent position = posm.get(entity);
            VelocityComponent velocity = vm.has(entity) ? vm.get(entity) : null;

            if (velocity != null && particle.effect != null) {
                float angle = velocity.velocity.angleDeg() + 180f; // direção inversa da bala

                // Aplica o ângulo a todos os emissores
                for (var emitter : particle.effect.getEmitters()) {
                    emitter.getAngle().setHigh(angle + 40f);
                    emitter.getAngle().setLow(angle - 40f);
                    // Removido o flip para evitar artefatos com additive blending
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

        batch.end();
    }

}
