package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.PositionComponent;
import com.badlogic.UniverseConqueror.ECS.components.StateComponent;
import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.UniverseConqueror.ECS.components.AnimationComponent;
import com.badlogic.UniverseConqueror.ECS.components.TransformComponent;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class RenderSystem extends IteratingSystem {
    private final SpriteBatch batch;
    private final OrthographicCamera camera;

    private final ComponentMapper<TransformComponent> tm = ComponentMapper.getFor(TransformComponent.class);
    private final ComponentMapper<AnimationComponent> am = ComponentMapper.getFor(AnimationComponent.class);

    public RenderSystem(SpriteBatch batch, OrthographicCamera camera) {
        super(Family.all(TransformComponent.class, AnimationComponent.class).get());
        this.batch = batch;
        this.camera = camera;
    }

    @Override
    public void update(float deltaTime) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        super.update(deltaTime);
        batch.end();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        StateComponent state = entity.getComponent(StateComponent.class);
        AnimationComponent anim = entity.getComponent(AnimationComponent.class);
      //  if (state != null && state.get() == StateComponent.State.DEATH)return;
//        if (anim == null || anim.currentFrame == null) return;

        TransformComponent transform = tm.get(entity);
        AnimationComponent animation = am.get(entity);

        if (animation.currentFrame != null) {
            TextureRegion frame = animation.currentFrame;

            // Flip logic
            if (frame.isFlipX()) frame.flip(true, false);
            if (!animation.facingRight) frame.flip(true, false);

            float x = transform.position.x;
            float y = transform.position.y;

            batch.draw(frame, x - frame.getRegionWidth() / 2f, y - frame.getRegionHeight() / 2f);
        }
    }
}




