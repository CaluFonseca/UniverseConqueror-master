package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.Audio.SoundManager;
import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.ECS.events.*;
import com.badlogic.UniverseConqueror.ECS.utils.ComponentMappers;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

/// Sistema responsável por tocar sons e notificar eventos com base em mudanças de estado
public class StateSoundSystem extends BaseIteratingSystem {

    private final OrthographicCamera camera;

    /// Construtor recebe a câmera para verificar visibilidade de inimigos
    public StateSoundSystem(OrthographicCamera camera) {
        super(Family.all(StateComponent.class).get());
        this.camera = camera;
    }

    /// Processa cada entidade a cada frame
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        StateComponent state = ComponentMappers.state.get(entity);
        boolean isEnemy = ComponentMappers.enemy.has(entity);
        EnemyComponent enemyComponent = ComponentMappers.enemy.get(entity);
        boolean isUfo = isEnemy && enemyComponent.type == EnemyComponent.BehaviorType.UFO;

        /// Atualiza tempo de permanência no estado atual
        state.timeInState += deltaTime;

        /// Interrompe som de fastmove se o estado não for mais FAST_MOVE e não for inimigo
        if (state.currentState != StateComponent.State.FAST_MOVE && !isEnemy && !isUfo) {
            SoundManager.getInstance().stop("fastmove");
        }

        /// Detecta mudança de estado e dispara eventos/sons
        if (state.currentState != state.previousState) {
            handleStateChange(entity, state, isEnemy, isUfo);
            state.previousState = state.currentState;
            state.timeInState = 0f;
        }

        /// Som e eventos contínuos baseados em estado atual
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

    /// Lida com mudanças de estado únicas e emite sons/eventos correspondentes
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

    /// Sons e eventos cíclicos enquanto o jogador anda ou está ferido
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

    /// Sons ambientes de inimigos baseados no estado atual, se estiverem visíveis na câmera
    private void handleEnemyAmbientSound(Entity entity, StateComponent state, boolean isUfo) {
        String ambientKey = switch (state.currentState) {
            case CHASE -> isUfo ? "chaseUfo" : "chaseAlien";
            case PATROL -> "patrolAlien";
            default -> null;
        };

        SoundManager soundManager = SoundManager.getInstance();

        if (ambientKey != null) {
            if (!soundManager.isLooping(ambientKey)) {
                soundManager.loopUnique(entity, ambientKey);
            } else if (!ambientKey.equals(soundManager.getCurrentLoopKey(entity))) {
                soundManager.stopLoopForEntity(entity);
            }
        } else {
            soundManager.stopLoopForEntity(entity);
        }
    }

    /// Verifica se a entidade está dentro da área visível pela câmera
    private boolean isInCameraView(Entity entity) {
        if (!ComponentMappers.position.has(entity)) return false;
        Vector2 position = ComponentMappers.position.get(entity).position;

        float halfW = camera.viewportWidth * 0.5f * camera.zoom;
        float halfH = camera.viewportHeight * 0.5f * camera.zoom;

        return position.x >= camera.position.x - halfW && position.x <= camera.position.x + halfW &&
            position.y >= camera.position.y - halfH && position.y <= camera.position.y + halfH;
    }
}
