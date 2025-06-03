package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.ECS.events.*;
import com.badlogic.UniverseConqueror.ECS.utils.ComponentMappers;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

// Sistema responsável por controlar o ciclo de ataque do jogador
public class AttackSystem extends BaseIteratingSystem {

    public AttackSystem() {
        super(Family.all(AttackComponent.class, StateComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        AttackComponent attack = ComponentMappers.attack.get(entity);
        StateComponent state = ComponentMappers.state.get(entity);

        // Atualiza o tempo desde o último ataque
        attack.timeSinceLastAttack += deltaTime;

        if (attack.isAttacking) {
            attack.attackTimer += deltaTime;

            if (attack.attackTimer >= attack.attackDuration) {
                attack.isAttacking = false;
                attack.attackTimer = 0f;

                if (state.currentState == StateComponent.State.ATTACK) {
                    state.set(StateComponent.State.IDLE);
                    EventBus.get().notify(new IdleEvent(entity));
                }

                EventBus.get().notify(new AttackEndedEvent(entity));
            }
        }
    }

    public void increaseAttackPower(int amount) {
        Entity playerEntity = getPlayerEntity();
        if (playerEntity == null) return;

        AttackComponent attack = ComponentMappers.attack.get(playerEntity);
        if (attack != null) {
            attack.remainingAttackPower += amount;
            EventBus.get().notify(new AttackPowerChangedEvent(playerEntity, attack.remainingAttackPower));
        }
    }

    public void setRemainingAttackPower(int power) {
        Entity playerEntity = getPlayerEntity();
        if (playerEntity == null) return;

        AttackComponent attack = ComponentMappers.attack.get(playerEntity);
        if (attack != null) {
            attack.remainingAttackPower = power;
            EventBus.get().notify(new AttackPowerChangedEvent(playerEntity, power));
        }
    }

    public int getRemainingAttackPower() {
        Entity playerEntity = getPlayerEntity();
        if (playerEntity == null) return 0;

        AttackComponent attack = ComponentMappers.attack.get(playerEntity);
        return (attack != null) ? attack.remainingAttackPower : 0;
    }


    public void setEngine(Engine engine) {
    }

    private Entity getPlayerEntity() {
        try {
            ImmutableArray<Entity> entities = getEngine().getEntitiesFor(Family.all(PlayerComponent.class).get());
            if (entities != null && entities.size() > 0) {
                return entities.first(); // Assume que há apenas um jogador
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
