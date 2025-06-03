package com.badlogic.UniverseConqueror.Strategy;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

public class PatrolStrategy extends AbstractEnemyStrategy {

    /// Pontos que o inimigo deve patrulhar.
    private final Vector2[] patrolPoints;

    /// Índice do ponto atual no caminho de patrulha.
    private int currentPoint = 0;

    /// Velocidade de movimento do inimigo durante a patrulha.
    private final float speed = 20f;

    /// Construtor que recebe os pontos de patrulha.
    /// @param patrolPoints sequência de pontos a serem patrulhados
    public PatrolStrategy(Vector2... patrolPoints) {
        this.patrolPoints = patrolPoints;
    }

    /// Atualiza o comportamento de patrulha do inimigo.
    /// @param enemy a entidade inimiga a ser atualizada
    /// @param deltaTime tempo decorrido desde o último frame (em segundos)
    @Override
    public void update(Entity enemy, float deltaTime) {
        PhysicsComponent pc = enemy.getComponent(PhysicsComponent.class);
        VelocityComponent velocity = enemy.getComponent(VelocityComponent.class);
        StateComponent state = enemy.getComponent(StateComponent.class);
        HealthComponent health = enemy.getComponent(HealthComponent.class);

        if (health != null && health.isDead()) return;
        if (pc == null || velocity == null) return;

        Vector2 target = patrolPoints[currentPoint];
        Vector2 pos = pc.body.getPosition();

        // Direção até o ponto de patrulha
        Vector2 rawDir = target.cpy().sub(pos);

        // Verifica se o inimigo chegou ao ponto de patrulha
        if (rawDir.len2() < 2f) {
            velocity.velocity.setZero();
            currentPoint = (currentPoint + 1) % patrolPoints.length;
            pc.body.setLinearVelocity(0, 0);
            return;
        }

        // Movimento e orientação
        direction.set(rawDir).nor().scl(speed);
        velocity.velocity.set(direction);
        pc.body.setLinearVelocity(velocity.velocity);

        // Estado de patrulha
        if (state != null && state.get() != StateComponent.State.PATROL && state.currentState != StateComponent.State.HURT) {
            state.set(StateComponent.State.PATROL);
        }

    }
}
