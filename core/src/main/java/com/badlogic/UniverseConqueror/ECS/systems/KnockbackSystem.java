package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.BodyComponent;
import com.badlogic.UniverseConqueror.ECS.components.KnockbackComponent;
import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;

public class KnockbackSystem extends IteratingSystem {

    /// Mapeadores de componentes para acesso rápido
    private final ComponentMapper<KnockbackComponent> km = ComponentMapper.getFor(KnockbackComponent.class);
    private final ComponentMapper<BodyComponent> bm = ComponentMapper.getFor(BodyComponent.class);

    /// Sistema processa todas as entidades que possuem Knockback e corpo físico
    public KnockbackSystem() {
        super(Family.all(KnockbackComponent.class, BodyComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        KnockbackComponent knockback = km.get(entity);
        BodyComponent body = bm.get(entity);

        /// Aplica o impulso de knockback uma única vez
        if (!knockback.hasBeenApplied) {
            body.body.applyLinearImpulse(knockback.impulse, body.body.getWorldCenter(), true);
            knockback.hasBeenApplied = true;
        }

        /// Reduz o tempo restante do knockback
        knockback.timeRemaining -= deltaTime;

        /// Quando o tempo do knockback acaba, remove o componente da entidade
        if (knockback.timeRemaining <= 0f) {
            entity.remove(KnockbackComponent.class);
        }
    }
}
