package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.ECS.utils.ComponentMappers;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

// Sistema que move entidades com base na velocidade e atualiza posição lógica
public class MovementSystem extends BaseIteratingSystem {

    public MovementSystem() {
        super(Family.all(PhysicsComponent.class, VelocityComponent.class, TransformComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PhysicsComponent physics = ComponentMappers.physics.get(entity);
        VelocityComponent velocity = ComponentMappers.velocity.get(entity);
        TransformComponent transform = ComponentMappers.transform.get(entity);
        AnimationComponent animation = ComponentMappers.animation.get(entity);

        if (physics.body != null && velocity != null) {

            physics.body.setLinearVelocity(velocity.velocity);


            if (animation != null) {
                float vx = velocity.velocity.x;
                if (vx > 0.01f) {
                    animation.facingRight = true;
                } else if (vx < -0.01f) {
                    animation.facingRight = false;
                }
            }


            transform.position.set(
                physics.body.getPosition().x,
                physics.body.getPosition().y,
                transform.position.z
            );
        }
    }
}
