package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.BodyComponent;
import com.badlogic.UniverseConqueror.ECS.utils.ComponentMappers;
import com.badlogic.UniverseConqueror.ECS.components.KnockbackComponent;
import com.badlogic.UniverseConqueror.ECS.components.PhysicsComponent;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

/// Sistema responsável por aplicar efeitos de knockback (impulso) em entidades com física
public class KnockbackSystem extends BaseIteratingSystem {

    /// Construtor define a família de entidades com Knockback e Physics
    public KnockbackSystem() {
        super(Family.all(KnockbackComponent.class, PhysicsComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        KnockbackComponent knockback = ComponentMappers.knockback.get(entity);
        BodyComponent body = ComponentMappers.body.get(entity);

        /// Aplica o impulso de knockback se ainda não foi aplicado
        if (!knockback.hasBeenApplied) {
            body.body.setLinearVelocity(knockback.impulse); // Aplica como velocidade direta
            knockback.hasBeenApplied = true;
        }

        /// Diminui o tempo restante do efeito
        knockback.timeRemaining -= deltaTime;

        /// Quando o tempo expira, para o corpo e remove o componente
        if (knockback.timeRemaining <= 0f) {
            body.body.setLinearVelocity(0f, 0f);
            entity.remove(KnockbackComponent.class);
        }
    }
}
