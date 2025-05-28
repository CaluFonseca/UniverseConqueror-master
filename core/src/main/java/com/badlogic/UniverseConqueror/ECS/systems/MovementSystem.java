package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.PhysicsComponent;
import com.badlogic.UniverseConqueror.ECS.components.TransformComponent;
import com.badlogic.UniverseConqueror.ECS.components.VelocityComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public class MovementSystem extends IteratingSystem {
    private ComponentMapper<PhysicsComponent> phm = ComponentMapper.getFor(PhysicsComponent.class);
    private ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
    private ComponentMapper<TransformComponent> tm = ComponentMapper.getFor(TransformComponent.class);

    public MovementSystem() {
        super(Family.all(PhysicsComponent.class, VelocityComponent.class, TransformComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PhysicsComponent physics = phm.get(entity);
        VelocityComponent velocity = vm.get(entity);
        TransformComponent transform = tm.get(entity);

        if (physics.body != null && velocity != null) {
            // Aplica a velocidade no corpo de Box2D
            physics.body.setLinearVelocity(velocity.velocity);
//            System.out.println("Processing entity: " + entity);
//            // DEBUG: informações antes e depois
//            System.out.println("[MovementSystem] ---");
//            System.out.println("Entity ID: " + entity.hashCode());
//            System.out.println("Velocity: " + velocity.velocity);
//            System.out.println("Body position (before): " + physics.body.getPosition());
//            System.out.println("Transform position (before): " + transform.position);

            // Atualiza o TransformComponent
            transform.position.set(
                physics.body.getPosition().x,
                physics.body.getPosition().y,
                transform.position.z
            );

           // System.out.println("Transform position (after): " + transform.position);
        } else {
            //System.out.println("[MovementSystem] Missing components for entity: " + entity.hashCode());
        }
    }

}
