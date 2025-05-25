package com.badlogic.UniverseConqueror.Strategy;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

public class PatrolStrategy implements EnemyStrategy {
    private Vector2[] patrolPoints;
    private int currentPoint = 0;
    private float speed = 20f;
    private final Vector2 direction = new Vector2();

    public PatrolStrategy(Vector2... patrolPoints) {
        this.patrolPoints = patrolPoints;
    }

    @Override
    public void update(Entity enemy, float deltaTime) {
        PhysicsComponent pc = enemy.getComponent(PhysicsComponent.class);
        VelocityComponent velocity = enemy.getComponent(VelocityComponent.class);
        StateComponent state = enemy.getComponent(StateComponent.class);
        PositionComponent position = enemy.getComponent(PositionComponent.class);
        HealthComponent health = enemy.getComponent(HealthComponent.class);

        if (health != null && health.isDead()) return;
        if (pc == null || velocity == null) return;

        Vector2 target = patrolPoints[currentPoint];
        Vector2 pos = pc.body.getPosition();
        direction.set(target).sub(pos);

        if (direction.len() < 0.1f) {
            velocity.velocity.set(0, 0);
            direction.set(0, 0);
            currentPoint = (currentPoint + 1) % patrolPoints.length;
        } else {
            direction.nor().scl(speed);
            velocity.velocity.set(direction);

            if (state != null && state.get() != StateComponent.State.PATROL && state.currentState!=StateComponent.State.HURT) {
                state.set(StateComponent.State.PATROL);
            }
        }

        if (position != null) {
            position.position.set(pc.body.getPosition());
        }
    }

    @Override
    public Vector2 getDirection() {
        return direction;
    }
}
