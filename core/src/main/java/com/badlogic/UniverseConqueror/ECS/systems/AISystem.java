package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.ECS.utils.ComponentMappers;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

/// Sistema responsável por aplicar lógica de IA às entidades com AIComponent
public class AISystem extends BaseIteratingSystem {

    /// Cria o sistema e define que ele processa entidades com AI, Posição e Velocidade
    public AISystem() {
        super(Family.all(AIComponent.class, PositionComponent.class, VelocityComponent.class).get());
    }

    /// Processa cada entidade com IA a cada frame
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        KnockbackComponent knockback = ComponentMappers.knockback.get(entity);
        if (knockback != null) return; // Ignora AI enquanto sofre knockback

        AIComponent ai = ComponentMappers.ai.get(entity);
        PositionComponent position = ComponentMappers.position.get(entity);
        VelocityComponent velocity = ComponentMappers.velocity.get(entity);

        /// Se a entidade tem estratégia de IA, aplica-a e atualiza a direção
        if (ai.strategy != null && position != null && velocity != null) {
            ai.strategy.update(entity, deltaTime);  /// Atualiza a lógica da IA
            velocity.velocity.set(ai.strategy.getDirection());  /// Aplica a direção calculada pela IA
        }
    }
}
