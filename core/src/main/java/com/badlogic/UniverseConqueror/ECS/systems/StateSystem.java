package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.HealthComponent;
import com.badlogic.UniverseConqueror.ECS.components.StateComponent;
import com.badlogic.UniverseConqueror.ECS.components.VelocityComponent;
import com.badlogic.UniverseConqueror.ECS.events.EventBus;
import com.badlogic.UniverseConqueror.ECS.events.IdleEvent;
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

        // Atualiza o tempo que está no estado atual
        state.timeInState += deltaTime;

        if (health == null || state == null) return;
        if (state.get() == StateComponent.State.HURT) {
            state.timeInState += deltaTime;

            if (state.timeInState >= health.hurtDuration) {
                state.set(StateComponent.State.IDLE); // ou outro estado
                state.timeInState = 0f;
            }
        }
        // Define se o personagem está ferido (menos de 25 de vida)
        boolean isInjured = health.currentHealth < 25;
        // Verifica se está em movimento baseado no vetor de velocidade (len2 evita sqrt para desempenho)
        boolean moving = velocity != null && Math.abs(velocity.velocity.len2()) > 0.1f;

        // Transições entre estados, incluindo ferido e não ferido
        switch (state.currentState) {
            case WALK:
                if (isInjured) {
                    // Passa para andar ferido se ferido
                    state.set(StateComponent.State.WALK_INJURED);
                } else if (!moving) {
                    // Se não estiver se movendo, vai para idle normal e notifica
                    state.set(StateComponent.State.IDLE);
                    EventBus.get().notify(new IdleEvent(entity));
                }
                break;

            case WALK_INJURED:
                if (!isInjured) {
                    // Volta a andar normal se não estiver mais ferido
                    state.set(StateComponent.State.WALK);
                } else if (!moving) {
                    // Se parou de se mover, vai para idle ferido
                    state.set(StateComponent.State.IDLE_INJURED);
                }
                break;

            case IDLE:
                if (isInjured) {
                    // Se ferido, muda para idle ferido
                    state.set(StateComponent.State.IDLE_INJURED);
                } else if (moving) {
                    // Se começou a se mover, muda para walk normal
                    state.set(StateComponent.State.WALK);
                }
                break;

            case IDLE_INJURED:
                if (!isInjured) {
                    // Se curou, volta para idle normal e notifica
                    state.set(StateComponent.State.IDLE);
                    EventBus.get().notify(new IdleEvent(entity));
                } else if (moving) {
                    // Se começar a se mover, muda para walk ferido
                    state.set(StateComponent.State.WALK_INJURED);
                }
                break;

            case DEFENSE:
                if (isInjured) {
                    // Se estiver defendendo e ferido, passa para defesa ferida
                    state.set(StateComponent.State.DEFENSE_INJURED);
                }
                break;

            case DEFENSE_INJURED:
                if (!isInjured) {
                    // Se não estiver mais ferido, volta para defesa normal
                    state.set(StateComponent.State.DEFENSE);
                }
                break;
        }
    }
}
