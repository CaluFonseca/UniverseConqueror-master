package com.badlogic.UniverseConqueror.Strategy;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

public class PatrolStrategy extends AbstractEnemyStrategy {
    private final Vector2[] patrolPoints;
    private int currentPoint = 0;
    private final float speed = 20f;

    // Construtor que recebe os pontos de patrulha.
    public PatrolStrategy(Vector2... patrolPoints) {
        this.patrolPoints = patrolPoints;
    }

    // Atualiza o comportamento de patrulha do inimigo.
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
