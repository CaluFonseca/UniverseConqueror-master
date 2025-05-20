package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.HealthComponent;
import com.badlogic.UniverseConqueror.ECS.components.StateComponent;
import com.badlogic.UniverseConqueror.ECS.components.VelocityComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public class StateSystem extends IteratingSystem {
    private ComponentMapper<StateComponent> sm = ComponentMapper.getFor(StateComponent.class);
    private ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);
    private ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
    public StateSystem() {
        super(Family.all(StateComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        StateComponent state = sm.get(entity);
        HealthComponent health = hm.get(entity);
        VelocityComponent velocity = vm.get(entity);

        state.timeInState += deltaTime;

        if (health == null || state == null) return;

        boolean isInjured = health.currentHealth < 25;
        boolean moving = velocity != null && Math.abs(velocity.velocity.len2()) > 0.1f;

        // Lógica de transição inteligente entre estados ferido/não ferido
        switch (state.currentState) {
            case WALK:
                if (isInjured) {
                    state.set(StateComponent.State.WALK_INJURED);
                } else if (!moving) {
                    state.set(StateComponent.State.IDLE);
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
        }
    }
}
