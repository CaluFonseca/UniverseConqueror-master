package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.BodyComponent;
import com.badlogic.UniverseConqueror.ECS.utils.ComponentMappers;
import com.badlogic.UniverseConqueror.ECS.components.KnockbackComponent;
import com.badlogic.UniverseConqueror.ECS.components.PhysicsComponent;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

// Sistema responsável por aplicar efeitos de knockback (impulso) em entidades com física
public class KnockbackSystem extends BaseIteratingSystem {

    public KnockbackSystem() {
        super(Family.all(KnockbackComponent.class, PhysicsComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        KnockbackComponent knockback = ComponentMappers.knockback.get(entity);
        BodyComponent body = ComponentMappers.body.get(entity);

        if (!knockback.hasBeenApplied) {
            body.body.setLinearVelocity(knockback.impulse);
            knockback.hasBeenApplied = true;
        }

        knockback.timeRemaining -= deltaTime;

        if (knockback.timeRemaining <= 0f) {
            body.body.setLinearVelocity(0f, 0f);
            entity.remove(KnockbackComponent.class);
        }
    }
}
