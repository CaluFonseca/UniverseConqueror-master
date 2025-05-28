package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.AnimationComponent;
import com.badlogic.UniverseConqueror.ECS.components.StateComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AnimationSystem extends IteratingSystem {
    private ComponentMapper<StateComponent> sm = ComponentMapper.getFor(StateComponent.class);
    private ComponentMapper<AnimationComponent> am = ComponentMapper.getFor(AnimationComponent.class);

    public AnimationSystem() {
        super(Family.all(AnimationComponent.class, StateComponent.class).get());
    }


    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        StateComponent state = sm.get(entity);
        AnimationComponent anim = am.get(entity);

        // Atualiza o tempo de estado e animação
        state.timeInState += deltaTime;
        anim.stateTime += deltaTime;

        Animation<TextureRegion> animation = anim.animations.get(state.currentState);
        if (animation != null) {
            anim.currentFrame = animation.getKeyFrame(anim.stateTime); // usa o modo configurado
        }
//        if (animation != null) {
//            // Define looping apenas para estados contínuos
//            boolean looping = (state.currentState == StateComponent.State.IDLE
//                || state.currentState == StateComponent.State.PATROL);
//
//            anim.currentFrame = animation.getKeyFrame(anim.stateTime, looping);
//
//            // DEBUG opcional
////            System.out.println("[AnimationSystem] Entity: " + entity.hashCode());
////            System.out.println(" - State: " + state.currentState);
////            System.out.println(" - TimeInState: " + state.timeInState);
////            System.out.println(" - FrameIndex: " + animation.getKeyFrameIndex(anim.stateTime));





    }


    public boolean isDeathAnimationFinished(Entity entity) {
        AnimationComponent anim = am.get(entity);
        StateComponent state = sm.get(entity);

        if (anim == null || state == null) return false;
        Animation<TextureRegion> deathAnim = anim.animations.get(StateComponent.State.DEATH);
        return state.currentState == StateComponent.State.DEATH &&
            deathAnim != null &&
            state.timeInState >= deathAnim.getAnimationDuration();
    }
}
