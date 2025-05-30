package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.AIComponent;
import com.badlogic.UniverseConqueror.ECS.components.KnockbackComponent;
import com.badlogic.UniverseConqueror.ECS.components.PositionComponent;
import com.badlogic.UniverseConqueror.ECS.components.VelocityComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

/// Sistema responsável por aplicar lógica de IA às entidades com AIComponent
public class AISystem extends IteratingSystem {

    /// Mappers para acesso rápido aos componentes
    private final ComponentMapper<AIComponent> aim = ComponentMapper.getFor(AIComponent.class);
    private final ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private final ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);

    /// Cria o sistema e define que ele processa entidades com AI, Posição e Velocidade
    public AISystem() {
        super(Family.all(AIComponent.class, PositionComponent.class, VelocityComponent.class).get());
    }

    /// Processa cada entidade com IA a cada frame
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        KnockbackComponent knockback = entity.getComponent(KnockbackComponent.class);
        if (knockback != null) return; // Ignora AI enquanto sofre knockback
        AIComponent ai = aim.get(entity);
        PositionComponent position = pm.get(entity);
        VelocityComponent velocity = vm.get(entity);

        /// Se a entidade tem estratégia de IA, aplica-a e atualiza a direção
        if (ai.strategy != null && position != null && velocity != null) {
            ai.strategy.update(entity, deltaTime);  /// Atualiza a lógica da IA
            velocity.velocity.set(ai.strategy.getDirection());  /// Aplica a direção calculada pela IA
        }
    }
}
