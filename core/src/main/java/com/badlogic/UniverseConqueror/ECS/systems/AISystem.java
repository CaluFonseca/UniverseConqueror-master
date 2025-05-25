package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.AIComponent;
import com.badlogic.UniverseConqueror.ECS.components.PositionComponent;
import com.badlogic.UniverseConqueror.ECS.components.VelocityComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public class AISystem extends IteratingSystem {
    private final ComponentMapper<AIComponent> aim = ComponentMapper.getFor(AIComponent.class);
    private final ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private final ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);

    public AISystem() {
        super(Family.all(AIComponent.class, PositionComponent.class, VelocityComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        AIComponent ai = aim.get(entity);
        PositionComponent position = pm.get(entity);
        VelocityComponent velocity = vm.get(entity);

        if (ai.strategy != null && position != null && velocity != null) {
            ai.strategy.update(entity, deltaTime);
            velocity.velocity.set(ai.strategy.getDirection()); // <- isto estava a faltar!
        }
    }
}

