package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.utils.ComponentMappers;
import com.badlogic.UniverseConqueror.ECS.components.HealthComponent;
import com.badlogic.UniverseConqueror.ECS.components.StateComponent;
import com.badlogic.UniverseConqueror.ECS.components.VelocityComponent;
import com.badlogic.UniverseConqueror.ECS.events.EventBus;
import com.badlogic.UniverseConqueror.ECS.events.IdleEvent;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

// Sistema responsável por gerir transições de estado com base em saúde e movimento
public class StateSystem extends BaseIteratingSystem {

    public StateSystem() {
        super(Family.all(StateComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        StateComponent state = ComponentMappers.state.get(entity);
        HealthComponent health = ComponentMappers.health.get(entity);
        VelocityComponent velocity = ComponentMappers.velocity.get(entity);

        // Atualiza o tempo acumulado no estado atual
        state.timeInState += deltaTime;

        if (health == null || state == null) return;

        // Se está no estado HURT, verifica se deve sair dele
        if (state.get() == StateComponent.State.HURT) {
            if (state.timeInState >= health.hurtDuration) {
                state.set(StateComponent.State.IDLE);
                state.timeInState = 0f;
            }
            return;
        }

        boolean isInjured = health.currentHealth < 25f;
        boolean moving = velocity != null && velocity.velocity.len2() > 0.1f;

        // Transições de estado com base na saúde e no movimento
        switch (state.currentState) {
            case WALK:
                if (isInjured) {
                    state.set(StateComponent.State.WALK_INJURED);
                } else if (!moving) {
                    state.set(StateComponent.State.IDLE);
                    EventBus.get().notify(new IdleEvent(entity));
                }
                break;

            case WALK_INJURED:
                if (!isInjured) {
                    state.set(StateComponent.State.WALK);
                } else if (!moving) {
                    state.set(StateComponent.State.IDLE_INJURED);
                }
                break;

            case IDLE:
                if (isInjured) {
                    state.set(StateComponent.State.IDLE_INJURED);
                } else if (moving) {
                    state.set(StateComponent.State.WALK);
                }
                break;

            case IDLE_INJURED:
                if (!isInjured) {
                    state.set(StateComponent.State.IDLE);
                    EventBus.get().notify(new IdleEvent(entity));
                } else if (moving) {
                    state.set(StateComponent.State.WALK_INJURED);
                }
                break;

            case DEFENSE:
                if (isInjured) {
                    state.set(StateComponent.State.DEFENSE_INJURED);
                }
                break;

            case DEFENSE_INJURED:
                if (!isInjured) {
                    state.set(StateComponent.State.DEFENSE);
                }
                break;

            default:
                break;
        }
    }
}
