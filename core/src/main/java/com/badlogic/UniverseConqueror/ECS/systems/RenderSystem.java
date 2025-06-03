package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.ECS.utils.ComponentMappers;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import static com.badlogic.UniverseConqueror.Utils.Constants.MIN_FLIP_THRESHOLD;

// Sistema que renderiza entidades com animações, exceto UFOs.
public class RenderSystem extends BaseRenderSystem {


    public RenderSystem(SpriteBatch batch, OrthographicCamera camera) {
        super(
            Family.all(TransformComponent.class, AnimationComponent.class, BodyComponent.class)
                .exclude(UfoComponent.class).get(),
            batch,
            camera
        );
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        AnimationComponent anim = ComponentMappers.animation.get(entity);
        TransformComponent transform = ComponentMappers.transform.get(entity);
        BodyComponent body = ComponentMappers.body.get(entity);

        if (anim == null || transform == null || body == null || anim.currentFrame == null) return;

        // Detecta direção (AI ou física)
        AIComponent ai = entity.getComponent(AIComponent.class);
        if (ai != null && ai.strategy != null) {
            Vector2 dir = ai.strategy.getDirection();
            if (Math.abs(dir.x) > MIN_FLIP_THRESHOLD) {
                anim.facingRight = dir.x < 0;
            }
        } else {
            float velocityX = body.body.getLinearVelocity().x;
            if (Math.abs(velocityX) > MIN_FLIP_THRESHOLD) {
                anim.facingRight = velocityX > 0;
            }
        }

        // Desenha o frame
        TextureRegion frame = anim.currentFrame;
        float x = transform.position.x;
        float y = transform.position.y;
        float w = frame.getRegionWidth();
        float h = frame.getRegionHeight();

        float drawX = anim.facingRight ? x - w / 2f : x + w / 2f;
        float drawW = anim.facingRight ? w : -w;

        batch.draw(frame, drawX, y - h / 2f, drawW, h);
    }

}
