package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.Audio.SoundManager;
import com.badlogic.UniverseConqueror.ECS.components.StateComponent;
import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;

public class StateSoundSystem extends IteratingSystem {

    private final ComponentMapper<StateComponent> sm = ComponentMapper.getFor(StateComponent.class);

    public StateSoundSystem() {
        super(Family.all(StateComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        StateComponent state = sm.get(entity);

        if (state.previousState != StateComponent.State.FAST_MOVE) {
            SoundManager.getInstance().stop("fastmove");
        }
        // Só toca uma vez ao mudar de estado
        if (state.currentState != state.previousState) {
            switch (state.currentState) {
                case JUMP -> SoundManager.getInstance().play("jump");
                case ATTACK -> SoundManager.getInstance().play("attack");
                case FAST_MOVE -> SoundManager.getInstance().loop("fastmove");
                case HURT -> SoundManager.getInstance().play("hurt");
                case DEATH -> SoundManager.getInstance().play("death");
            }

            state.previousState = state.currentState;
            state.timeInState = 0f;
        }

        // Repetição de passos enquanto anda
        if (state.currentState == StateComponent.State.WALK && state.timeInState >= 0.4f) {
            SoundManager.getInstance().play("walk");
            state.timeInState = 0f;
        }

        if (state.currentState == StateComponent.State.WALK_INJURED && state.timeInState >= 0.6f) {
            SoundManager.getInstance().play("walk_injured");
            state.timeInState = 0f;
        }
    }

}
