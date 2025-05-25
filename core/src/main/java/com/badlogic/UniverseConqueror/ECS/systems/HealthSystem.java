package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.HealthComponent;
import com.badlogic.UniverseConqueror.ECS.components.StateComponent;
import com.badlogic.UniverseConqueror.ECS.entity.EnemyFactory;
import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.audio.Sound;

public class HealthSystem extends EntitySystem {
    private ImmutableArray<Entity> entities;
    private ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);
    private ComponentMapper<StateComponent> sm = ComponentMapper.getFor(StateComponent.class);

    private HealthChangeListener healthChangeListener;
    private Engine engine;

    public interface HealthChangeListener {
        void onHealthChanged(int currentHealth);
    }

    public HealthSystem( HealthChangeListener listener) {
        this.healthChangeListener = listener;
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(HealthComponent.class, StateComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        for (Entity entity : entities) {
            HealthComponent health = hm.get(entity);
            StateComponent state = sm.get(entity);

            if (health.hurtCooldownTimer > 0f) {
                health.hurtCooldownTimer -= deltaTime;
            }

            // Atualiza a duração da animação de "Hurt"
            if (health.hurtDuration > 0f) {
                health.hurtDuration -= deltaTime;
            }

            // Se levou dano, muda para HURT
            if (health.wasDamagedThisFrame && !health.isDead() && health.hurtCooldownTimer <= 0f) {
                state.set(StateComponent.State.HURT);  // Altera o estado para HURT (animação de dor)

                // Notifica mudança de saúde
                if (healthChangeListener != null) {
                    healthChangeListener.onHealthChanged(health.currentHealth);  // Notifica a mudança de saúde
                }

                health.wasDamagedThisFrame = false;
                health.hurtCooldownTimer = 0.1f;  // 1 segundo de invulnerabilidade
                health.hurtDuration = 0.1f;  // Duração da animação de dor
            }

            // Lógica de morte
            if (health.currentHealth <= 0 && state.get() != StateComponent.State.DEATH) {
                state.set(StateComponent.State.DEATH);
                EnemyFactory.changeState(entity, StateComponent.State.DEATH);

            }

            if (state.get() == StateComponent.State.HURT && health.hurtDuration <= 0f && health.hurtCooldownTimer <= 0f && !health.isDead()) {
                state.set(StateComponent.State.IDLE);
                EnemyFactory.changeState(entity, StateComponent.State.IDLE);
            }
        }
    }

    // Método para aplicar dano à saúde
    public void damage(Entity entity, int amount) {
        HealthComponent health = hm.get(entity);
        StateComponent state = sm.get(entity);
        if (health != null && health.hurtCooldownTimer <= 0f &&
            (StateComponent.State.DEFENSE != state.get() && StateComponent.State.DEFENSE_INJURED != state.get())) {
            health.currentHealth = Math.max(0, health.currentHealth - amount);
            health.wasDamagedThisFrame = true;
            state.set(StateComponent.State.HURT);

            health.hurtCooldownTimer = 0.1f;  // 1 segundo de invulnerabilidade
            health.hurtDuration = 0.1f;
            //System.out.println("[HealthSystem] State changed to: " + state.get());
        }
    }

    // Método para curar a saúde
    public void heal(Entity entity, int amount) {
        HealthComponent health = hm.get(entity);
        if (health != null) {
            health.currentHealth = Math.min(health.maxHealth, health.currentHealth + amount);
            if (healthChangeListener != null) {
                healthChangeListener.onHealthChanged(health.currentHealth);  // Notifica mudança de saúde
            }
        }
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }
}
