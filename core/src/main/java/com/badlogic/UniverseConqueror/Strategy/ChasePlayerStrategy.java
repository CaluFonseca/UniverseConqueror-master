package com.badlogic.UniverseConqueror.Strategy;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

public class ChasePlayerStrategy implements EnemyStrategy {

    private final Entity target;
    private final float speed = 30.0f;
    private final Vector2 direction = new Vector2();
    private final OrthographicCamera camera;

    public ChasePlayerStrategy(Entity target, OrthographicCamera camera) {
        this.target = target;
        this.camera = camera;
    }

    @Override
    public void update(Entity enemy, float deltaTime) {
        if (target == null) return;

        PhysicsComponent enemyPhysics = enemy.getComponent(PhysicsComponent.class);
        PhysicsComponent targetPhysics = target.getComponent(PhysicsComponent.class);
        VelocityComponent velocity = enemy.getComponent(VelocityComponent.class);
        StateComponent state = enemy.getComponent(StateComponent.class);
        HealthComponent health = enemy.getComponent(HealthComponent.class);

        if (enemyPhysics == null || targetPhysics == null || velocity == null || health == null || health.isDead()) return;

        Vector2 enemyPos = enemyPhysics.body.getPosition();
        Vector2 targetPos = targetPhysics.body.getPosition();

        direction.set(targetPos).sub(enemyPos);
        AnimationComponent animation = enemy.getComponent(AnimationComponent.class);
        if (animation != null) {
            animation.facingRight = direction.x <= 0;
        }

        float distance = direction.len();

        if (distance < 100f) {
            // Distancia Para ativar o chase
            velocity.velocity.setZero();
            if(state.currentState!=StateComponent.State.HURT){
            state.set(StateComponent.State.PATROL);
            }
        } else {
            direction.nor().scl(speed);
            velocity.velocity.set(direction);
            if(state.currentState!=StateComponent.State.HURT) {
                state.set(StateComponent.State.CHASE);
            }
        }
    }

    @Override
    public Vector2 getDirection() {
        return direction;
    }
}
