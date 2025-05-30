package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.BodyComponent;
import com.badlogic.UniverseConqueror.ECS.components.KnockbackComponent;
import com.badlogic.UniverseConqueror.ECS.components.PhysicsComponent;
import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;

public class KnockbackSystem extends IteratingSystem {

    /// Mapeadores de componentes para acesso rápido
    private final ComponentMapper<KnockbackComponent> km = ComponentMapper.getFor(KnockbackComponent.class);
    private final ComponentMapper<BodyComponent> bm = ComponentMapper.getFor(BodyComponent.class);
    private final ComponentMapper<PhysicsComponent> pm = ComponentMapper.getFor(PhysicsComponent.class);

    /// Sistema processa todas as entidades que possuem Knockback e corpo físico
    public KnockbackSystem() {
        super(Family.all(KnockbackComponent.class, PhysicsComponent.class).get());
    }

    @Override
    protected void processEntity (Entity entity,float deltaTime){
            KnockbackComponent knockback = km.get(entity);
            BodyComponent body = bm.get(entity);

            if (!knockback.hasBeenApplied) {
                body.body.setLinearVelocity(knockback.impulse); // Aplica velocidade diretamente
                knockback.hasBeenApplied = true;
            }

            knockback.timeRemaining -= deltaTime;

            if (knockback.timeRemaining <= 0f) {
                body.body.setLinearVelocity(0f, 0f); // Para o movimento após o tempo
                entity.remove(KnockbackComponent.class);
            }
        }
    }

