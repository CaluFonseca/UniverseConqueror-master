package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.AnimationComponent;
import com.badlogic.UniverseConqueror.ECS.components.StateComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/// Sistema responsável por atualizar as animações de entidades com base no estado atual
public class AnimationSystem extends IteratingSystem {

    private final ComponentMapper<StateComponent> sm = ComponentMapper.getFor(StateComponent.class);
    private final ComponentMapper<AnimationComponent> am = ComponentMapper.getFor(AnimationComponent.class);

    /// Construtor define o sistema para processar entidades que possuem AnimationComponent e StateComponent
    public AnimationSystem() {
        super(Family.all(AnimationComponent.class, StateComponent.class).get());
    }

    /// Atualiza o frame da animação com base no tempo e estado atual da entidade
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        StateComponent state = sm.get(entity);
        AnimationComponent anim = am.get(entity);

        // Atualiza os tempos acumulados
        state.timeInState += deltaTime;
        anim.stateTime += deltaTime;

        // Obtém a animação correspondente ao estado atual
        Animation<TextureRegion> animation = anim.animations.get(state.currentState);

        if (animation != null) {
            // Atualiza o frame atual da animação
            TextureRegion frame = animation.getKeyFrame(anim.stateTime);
            if (frame != null) {
                anim.currentFrame = frame;
            }
        }
    }

    /// Verifica se a animação de morte foi completamente reproduzida para a entidade
    public boolean isDeathAnimationFinished(Entity entity) {
        AnimationComponent anim = am.get(entity);
        StateComponent state = sm.get(entity);

        // Verifica se os componentes necessários existem
        if (anim == null || state == null) {
            return false;
        }

        // Verifica se o estado atual é DEATH
        if (state.get() != StateComponent.State.DEATH) {
            return false;
        }

        // Obtém a animação do estado DEATH
        Animation<TextureRegion> deathAnim = anim.animations.get(StateComponent.State.DEATH);

        if (deathAnim == null) {
            return false;
        }

        // Verifica se o tempo decorrido no estado DEATH excedeu a duração total da animação
        return state.timeInState >= deathAnim.getAnimationDuration();
    }
}
