package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.PositionComponent;
import com.badlogic.UniverseConqueror.ECS.components.TransformComponent;
import com.badlogic.UniverseConqueror.ECS.components.VelocityComponent;
import com.badlogic.UniverseConqueror.ECS.components.PhysicsComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public class BulletMovementSystem extends IteratingSystem {
    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
    private ComponentMapper<PhysicsComponent> phm = ComponentMapper.getFor(PhysicsComponent.class);

    public BulletMovementSystem() {
        super(Family.all(PositionComponent.class, VelocityComponent.class, PhysicsComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent position = pm.get(entity);
        PhysicsComponent physics = phm.get(entity);

        // Atualiza a posição da bala com base na física do Box2D
        position.position.set(physics.body.getPosition());  // Sincroniza a posição com o corpo Box2D
    }

    @Override
    public void update(float deltaTime) {
       // System.out.println("Updating Bullet Movement...");
        // Atualiza a posição das balas com base na sua velocidade
        for (Entity entity : getEngine().getEntitiesFor(Family.all(PositionComponent.class, VelocityComponent.class, TransformComponent.class).get())) {
            PositionComponent position = pm.get(entity);
            VelocityComponent velocity = vm.get(entity);

            // Atualiza a posição da bala com base na sua velocidade e no tempo
            position.position.mulAdd(velocity.velocity, deltaTime);
        }
    }
}
