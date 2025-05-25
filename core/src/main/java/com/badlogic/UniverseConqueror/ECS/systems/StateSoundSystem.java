package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.Audio.SoundManager;
import com.badlogic.UniverseConqueror.ECS.components.EnemyComponent;
import com.badlogic.UniverseConqueror.ECS.components.StateComponent;
import com.badlogic.UniverseConqueror.ECS.components.PositionComponent;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;

public class StateSoundSystem extends IteratingSystem {

    private final ComponentMapper<StateComponent> sm = ComponentMapper.getFor(StateComponent.class);
    private final ComponentMapper<EnemyComponent> em = ComponentMapper.getFor(EnemyComponent.class);
    private final ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);

    private final OrthographicCamera camera;

    public StateSoundSystem(OrthographicCamera camera) {
        super(Family.all(StateComponent.class).get());
        this.camera = camera;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        StateComponent state = sm.get(entity);
        boolean isEnemy = em.has(entity);

        state.timeInState += deltaTime;

        if (state.currentState != StateComponent.State.FAST_MOVE && !isEnemy) {
            SoundManager.getInstance().stop("fastmove");
        }

        if (state.currentState != state.previousState) {
            handleStateChange(entity, state, isEnemy);
            state.previousState = state.currentState;
            state.timeInState = 0f;
        }

        if (!isEnemy) {
            handlePlayerLoopedSounds(state);
        }  else {
        if (isInCameraView(entity)) {
            handleEnemyAmbientSound(entity, state);
        } else {
            SoundManager.getInstance().stopLoopForEntity(entity);
        }
    }
    }

    private void handleStateChange(Entity entity, StateComponent state, boolean isEnemy) {
        switch (state.currentState) {
            case JUMP -> SoundManager.getInstance().play("jump");
            case ATTACK -> SoundManager.getInstance().play("attack");
            case FAST_MOVE -> SoundManager.getInstance().loop("fastmove");
            case HURT -> SoundManager.getInstance().play(isEnemy ? "hurtAlien" : "hurt");
            case DEATH -> SoundManager.getInstance().play(isEnemy ? "deathAlien" : "death");
        }
    }

    private void handlePlayerLoopedSounds(StateComponent state) {
        switch (state.currentState) {
            case WALK -> {
                if (state.timeInState >= 0.4f) {
                    SoundManager.getInstance().play("walk");
                    state.timeInState = 0f;
                }
            }
            case WALK_INJURED -> {
                if (state.timeInState >= 0.6f) {
                    SoundManager.getInstance().play("walk_injured");
                    state.timeInState = 0f;
                }
            }
        }
    }

    private void handleEnemyAmbientSound(Entity entity, StateComponent state) {
        String ambientKey = switch (state.currentState) {
            case CHASE -> "chaseAlien";
            case PATROL -> "patrolAlien";
            default -> null;
        };

        SoundManager soundManager = SoundManager.getInstance();

        if (ambientKey != null) {
            // Só toca se não estiver tocando este som para nenhuma outra entidade
            if (!soundManager.isLooping(ambientKey)) {
                soundManager.loopUnique(entity, ambientKey);
            } else if (!ambientKey.equals(soundManager.getCurrentLoopKey(entity))) {
                soundManager.stopLoopForEntity(entity); // para som antigo errado
            }
        } else {
            soundManager.stopLoopForEntity(entity);
        }
    }


    private boolean isInCameraView(Entity entity) {
        if (!pm.has(entity)) return false;
        Vector2 position = pm.get(entity).position;
        float halfW = camera.viewportWidth * 0.5f * camera.zoom;
        float halfH = camera.viewportHeight * 0.5f * camera.zoom;
        return position.x >= camera.position.x - halfW && position.x <= camera.position.x + halfW &&
            position.y >= camera.position.y - halfH && position.y <= camera.position.y + halfH;
    }
}
