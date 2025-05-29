package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class RenderSystem extends IteratingSystem {
    private final SpriteBatch batch; // Batch para desenhar sprites
    private final OrthographicCamera camera; // Câmera para projeção correta

    private final ComponentMapper<TransformComponent> tm = ComponentMapper.getFor(TransformComponent.class); // Mapper para TransformComponent
    private final ComponentMapper<AnimationComponent> am = ComponentMapper.getFor(AnimationComponent.class); // Mapper para AnimationComponent

    // Família que contém entidades com Transform e Animation, excluindo UFOs
    public RenderSystem(SpriteBatch batch, OrthographicCamera camera) {
        super(Family.all(TransformComponent.class, AnimationComponent.class).exclude(UfoComponent.class).get());
        this.batch = batch;
        this.camera = camera;
    }

    @Override
    public void update(float deltaTime) {
        batch.setProjectionMatrix(camera.combined); // Define matriz de projeção
        batch.begin();
        super.update(deltaTime); // Atualiza e processa entidades
        batch.end();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        StateComponent state = entity.getComponent(StateComponent.class);
        AnimationComponent anim = entity.getComponent(AnimationComponent.class);
        // Poderia pular renderização se o estado for DEATH, mas está comentado:
        // if (state != null && state.get() == StateComponent.State.DEATH) return;
        // if (anim == null || anim.currentFrame == null) return;

        TransformComponent transform = tm.get(entity);
        AnimationComponent animation = am.get(entity);

        if (animation.currentFrame != null) {
            TextureRegion frame = animation.currentFrame;

            // Lógica para flip horizontal da textura conforme direção do personagem
            if (frame.isFlipX()) frame.flip(true, false);
            if (!animation.facingRight) frame.flip(true, false);

            float x = transform.position.x;
            float y = transform.position.y;

            // Desenha o frame centrado na posição da entidade
            batch.draw(frame, x - frame.getRegionWidth() / 2f, y - frame.getRegionHeight() / 2f);
        }
    }
}
