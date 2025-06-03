package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.AnimationComponent;
import com.badlogic.UniverseConqueror.ECS.components.StateComponent;
import com.badlogic.UniverseConqueror.ECS.components.TransformComponent;
import com.badlogic.UniverseConqueror.ECS.utils.ComponentMappers;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class BaseAnimatedRenderSystem extends BaseRenderSystem {

    public BaseAnimatedRenderSystem(Family family, SpriteBatch batch, OrthographicCamera camera) {
        super(family, batch, camera);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        AnimationComponent anim = ComponentMappers.animation.get(entity);
        TransformComponent transform = ComponentMappers.transform.get(entity);
        StateComponent state = ComponentMappers.state.get(entity);

        if (anim == null || state == null || transform == null || anim.animations == null) return;

        Animation<TextureRegion> currentAnim = anim.animations.get(state.get());
        if (currentAnim == null) return;

        anim.stateTime += deltaTime;
        TextureRegion frame = currentAnim.getKeyFrame(anim.stateTime, true);
        anim.currentFrame = frame;

        float x = transform.position.x - frame.getRegionWidth() / 2f;
        float y = transform.position.y - frame.getRegionHeight() / 2f;

        batch.draw(frame, x, y);
    }
}
