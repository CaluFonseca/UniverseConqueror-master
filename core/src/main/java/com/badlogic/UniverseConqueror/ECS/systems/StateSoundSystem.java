package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.Audio.SoundManager;
import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.ECS.events.*;
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
        EnemyComponent enemyComponent = em.get(entity);
        boolean isUfo = isEnemy && enemyComponent.type == EnemyComponent.BehaviorType.UFO;

        // Incrementa o tempo na animação/estado atual
        state.timeInState += deltaTime;

        // Para som de fastmove quando não está no estado, e não é inimigo ou UFO
        if (state.currentState != StateComponent.State.FAST_MOVE && !isEnemy && !isUfo) {
            SoundManager.getInstance().stop("fastmove");
        }

        // Detecta mudança de estado para disparar sons e eventos
        if (state.currentState != state.previousState) {
            handleStateChange(entity, state, isEnemy, isUfo);
            state.previousState = state.currentState;
            state.timeInState = 0f;
        }

        if (!isEnemy) {
            handlePlayerLoopedSounds(entity, state);
        } else {
            if (isInCameraView(entity)) {
                handleEnemyAmbientSound(entity, state, isUfo);
            } else {
                SoundManager.getInstance().stopLoopForEntity(entity);
            }
        }
    }

    private void handleStateChange(Entity entity, StateComponent state, boolean isEnemy, boolean isUfo) {
        switch (state.currentState) {
            case JUMP -> EventBus.get().notify(new JumpEvent(entity));
            case ATTACK -> EventBus.get().notify(new AttackStartedEvent(entity, false));
            case FAST_MOVE -> EventBus.get().notify(new FastMoveEvent(entity));
            case HURT -> SoundManager.getInstance().play(
                isUfo ? "hurtUfo" : (isEnemy ? "hurtAlien" : "hurt")
            );
            case DEATH -> SoundManager.getInstance().play(
                isUfo ? "deathUfo" : (isEnemy ? "deathAlien" : "death")
            );
        }
    }

    private void handlePlayerLoopedSounds(Entity entity, StateComponent state) {
        switch (state.currentState) {
            case WALK -> {
                if (state.timeInState >= 0.4f) {
                    EventBus.get().notify(new WalkEvent(entity));
                    state.timeInState = 0f;
                }
            }
            case WALK_INJURED -> {
                if (state.timeInState >= 0.6f) {
                    EventBus.get().notify(new WalkEvent(entity));
                    state.timeInState = 0f;
                }
            }
        }
    }

    private void handleEnemyAmbientSound(Entity entity, StateComponent state, boolean isUfo) {
        String ambientKey = switch (state.currentState) {
            case CHASE -> isUfo ? "chaseUfo" : "chaseAlien";
            case PATROL -> "patrolAlien"; // UFOs não patrulham
            default -> null;
        };

        SoundManager soundManager = SoundManager.getInstance();

        if (ambientKey != null) {
            // Toca som em loop único por entidade, evita duplicação
            if (!soundManager.isLooping(ambientKey)) {
                soundManager.loopUnique(entity, ambientKey);
            } else if (!ambientKey.equals(soundManager.getCurrentLoopKey(entity))) {
                soundManager.stopLoopForEntity(entity); // para som antigo incorreto
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
