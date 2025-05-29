/// Estratégia que alterna dinamicamente entre patrulha e perseguição.
/// O inimigo persegue o jogador se estiver próximo e visível, caso contrário patrulha.

package com.badlogic.UniverseConqueror.Strategy;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

public class SwitchingStrategy implements EnemyStrategy {

    /// Estratégia de patrulha utilizada quando o jogador não está próximo.
    private final EnemyStrategy patrolStrategy;

    /// Estratégia de perseguição utilizada quando o jogador está próximo e visível.
    private final EnemyStrategy chaseStrategy;

    /// Entidade-alvo (geralmente o jogador).
    private final Entity target;

    /// Câmera usada para verificar visibilidade do jogador.
    private final OrthographicCamera camera;

    /// Distância máxima para ativar a perseguição.
    private final float triggerDistance = 400f;

    /// Estratégia atual em execução.
    private EnemyStrategy currentStrategy;

    /// Mapper para acessar rapidamente o componente de estado.
    private final ComponentMapper<StateComponent> sm = ComponentMapper.getFor(StateComponent.class);

    /// Construtor que define o alvo, estratégias e câmera.
    /// @param target entidade alvo (jogador)
    /// @param patrolStrategy estratégia de patrulha
    /// @param chaseStrategy estratégia de perseguição
    /// @param camera câmera para checar visibilidade
    public SwitchingStrategy(Entity target, EnemyStrategy patrolStrategy, EnemyStrategy chaseStrategy, OrthographicCamera camera) {
        this.target = target;
        this.patrolStrategy = patrolStrategy;
        this.chaseStrategy = chaseStrategy;
        this.camera = camera;
        this.currentStrategy = patrolStrategy;
    }

    /// Verifica se uma posição está visível na câmera.
    /// @param worldPos posição a verificar
    /// @return true se estiver dentro dos limites da câmera
    private boolean isInCameraView(Vector2 worldPos) {
        float camX = camera.position.x;
        float camY = camera.position.y;
        float halfW = camera.viewportWidth * 0.5f * camera.zoom;
        float halfH = camera.viewportHeight * 0.5f * camera.zoom;

        return worldPos.x >= camX - halfW && worldPos.x <= camX + halfW &&
            worldPos.y >= camY - halfH && worldPos.y <= camY + halfH;
    }

    /// Atualiza a estratégia do inimigo com base na visibilidade e distância do jogador.
    /// @param enemy entidade inimiga
    /// @param deltaTime tempo desde o último frame
    @Override
    public void update(Entity enemy, float deltaTime) {
        if (target == null || camera == null || enemy == null) return;

        PhysicsComponent enemyPhysics = enemy.getComponent(PhysicsComponent.class);
        PhysicsComponent targetPhysics = target.getComponent(PhysicsComponent.class);
        HealthComponent health = enemy.getComponent(HealthComponent.class);
        StateComponent state = sm.get(enemy);

        if (enemyPhysics == null || targetPhysics == null || health == null || state == null || health.isDead()) return;

        Vector2 enemyPos = enemyPhysics.body.getPosition();
        Vector2 playerPos = targetPhysics.body.getPosition();

        boolean playerVisible = isInCameraView(playerPos);
        float distance = enemyPos.dst(playerPos);

        /// Bloqueia troca de estratégia se estiver morto.
        if (state.get() == StateComponent.State.DEATH) {
            currentStrategy.update(enemy, deltaTime);
            return;
        }

        /// Alterna para perseguição se o jogador estiver visível e próximo.
        if (playerVisible && distance <= triggerDistance) {
            currentStrategy = chaseStrategy;
        } else {
            currentStrategy = patrolStrategy;
        }

        /// Executa a estratégia atual.
        currentStrategy.update(enemy, deltaTime);
    }

    /// Retorna a direção atual da estratégia ativa.
    /// @return vetor de direção
    @Override
    public Vector2 getDirection() {
        return currentStrategy.getDirection();
    }
}
