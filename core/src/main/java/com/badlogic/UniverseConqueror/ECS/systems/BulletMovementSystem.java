package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.ECS.utils.ComponentMappers;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

// Sistema que move projéteis com base na física Box2D
public class BulletMovementSystem extends BaseIteratingSystem {

    public BulletMovementSystem() {
        super(Family.all(PositionComponent.class, VelocityComponent.class, PhysicsComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        VelocityComponent velocity = ComponentMappers.velocity.get(entity);
        PhysicsComponent physics = ComponentMappers.physics.get(entity);
        PositionComponent position = ComponentMappers.position.get(entity);

        if (physics.body != null && physics.body.isActive()) {
            physics.body.setLinearVelocity(velocity.velocity);
            position.position.set(physics.body.getPosition());
        }
    }
}
