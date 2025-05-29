package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.AttackComponent;
import com.badlogic.UniverseConqueror.ECS.components.PlayerComponent;
import com.badlogic.UniverseConqueror.ECS.components.StateComponent;
import com.badlogic.UniverseConqueror.ECS.events.AttackEndedEvent;
import com.badlogic.UniverseConqueror.ECS.events.AttackPowerChangedEvent;
import com.badlogic.UniverseConqueror.ECS.events.EventBus;
import com.badlogic.UniverseConqueror.ECS.events.IdleEvent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

/// Sistema responsável por controlar o ciclo de ataque do jogador (e outros atacantes, se houver)
public class AttackSystem extends IteratingSystem {

    private final ComponentMapper<AttackComponent> am = ComponentMapper.getFor(AttackComponent.class);
    private final ComponentMapper<StateComponent> sm = ComponentMapper.getFor(StateComponent.class);

    /// Construtor define a família de entidades que devem possuir AttackComponent e StateComponent
    public AttackSystem() {
        super(Family.all(AttackComponent.class, StateComponent.class).get());
    }

    /// Atualiza cada entidade do sistema a cada frame
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        AttackComponent attack = am.get(entity);
        StateComponent state = sm.get(entity);

        // Atualiza o tempo desde o último ataque
        attack.timeSinceLastAttack += deltaTime;

        if (attack.isAttacking) {
            // Enquanto atacando, incrementa o tempo do ataque atual
            attack.attackTimer += deltaTime;

            // Verifica se o ataque terminou
            if (attack.attackTimer >= attack.attackDuration) {
                attack.isAttacking = false;
                attack.attackTimer = 0f;

                // Transição de estado: volta para IDLE se estava em ATTACK
                if (state.currentState == StateComponent.State.ATTACK) {
                    state.set(StateComponent.State.IDLE);
                    EventBus.get().notify(new IdleEvent(entity));
                }

                // Notifica o fim do ataque
                EventBus.get().notify(new AttackEndedEvent(entity));
            }
        } else {
            // Se não está atacando, pode executar lógica adicional se necessário
            if (attack.canAttack()) {
                // Lógica de ataque pode ser colocada aqui se for automático
            }
        }
    }

    /// Aumenta o poder de ataque restante do jogador e envia evento de atualização
    public void increaseAttackPower(int amount) {
        Entity playerEntity = getPlayerEntity();
        AttackComponent attack = playerEntity.getComponent(AttackComponent.class);

        if (attack != null) {
            attack.remainingAttackPower += amount;
            EventBus.get().notify(new AttackPowerChangedEvent(playerEntity, attack.remainingAttackPower));
        }
    }

    /// Define diretamente o poder de ataque restante e notifica observadores
    public void setRemainingAttackPower(int power) {
        Entity playerEntity = getPlayerEntity();
        AttackComponent attack = playerEntity.getComponent(AttackComponent.class);

        if (attack != null) {
            attack.remainingAttackPower = power;
            EventBus.get().notify(new AttackPowerChangedEvent(playerEntity, power));
        }
    }

    /// Retorna o poder de ataque restante atual do jogador
    public int getRemainingAttackPower() {
        Entity playerEntity = getPlayerEntity();
        AttackComponent attack = playerEntity.getComponent(AttackComponent.class);

        if (attack != null) {
            return attack.remainingAttackPower;
        }

        return 0;
    }

    /// Sobrescreve o método de setEngine caso precise capturar engine externamente (não usado aqui)
    public void setEngine(Engine engine) {
    }

    /// Recupera a entidade do jogador usando PlayerComponent
    private Entity getPlayerEntity() {
        try {
            ImmutableArray<Entity> entities = getEngine().getEntitiesFor(Family.all(PlayerComponent.class).get());
            if (entities != null && entities.size() > 0) {
                return entities.get(0); // Assume que há apenas um jogador
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
