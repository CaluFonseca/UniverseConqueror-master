package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.HealthComponent;
import com.badlogic.UniverseConqueror.ECS.components.StateComponent;
import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.audio.Sound;

public class HealthSystem extends EntitySystem {
    private ImmutableArray<Entity> entities;
    private ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);
    private ComponentMapper<StateComponent> sm = ComponentMapper.getFor(StateComponent.class);

    private Sound deathSound, hurtSound;
    private HealthChangeListener healthChangeListener;
    private Engine engine;

    public interface HealthChangeListener {
        void onHealthChanged(int currentHealth);
    }

    public HealthSystem(Sound deathSound, Sound hurtSound, HealthChangeListener listener) {
        this.deathSound = deathSound;
        this.hurtSound = hurtSound;
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

            // Atualiza o cooldown de invulnerabilidade (hurtCooldownTimer)
            if (health.hurtCooldownTimer > 0f) {
                health.hurtCooldownTimer -= deltaTime;  // Reduz o cooldown
            }

            // Atualiza a duração da animação de "Hurt"
            if (health.hurtDuration > 0f) {
                health.hurtDuration -= deltaTime;  // Reduz a duração do efeito visual
            }

            // Se levou dano, muda para HURT
            if (health.wasDamagedThisFrame && !health.isDead() && health.hurtCooldownTimer <= 0f) {
                state.set(StateComponent.State.HURT);  // Altera o estado para HURT (animação de dor)
                if (hurtSound != null) {
                    hurtSound.play();  // Toca o som de dor
                }

                // Notifica mudança de saúde
                if (healthChangeListener != null) {
                    healthChangeListener.onHealthChanged(health.currentHealth);  // Notifica a mudança de saúde
                }

                // Reseta as variáveis de dano
                health.wasDamagedThisFrame = false;
                health.hurtCooldownTimer = 0.1f;  // 1 segundo de invulnerabilidade
                health.hurtDuration = 0.1f;  // Duração da animação de dor
            }

            // Lógica de morte
            if (health.currentHealth <= 0 && state.get() != StateComponent.State.DEATH) {
                state.set(StateComponent.State.DEATH);  // Muda para o estado de morte
                if (deathSound != null) {
                    deathSound.play();  // Toca o som de morte
                }
            }

            // Volta para IDLE quando a duração da animação de "HURT" acabar
            if (state.get() == StateComponent.State.HURT && health.hurtDuration <= 0f && health.hurtCooldownTimer <= 0f && !health.isDead()) {
                state.set(StateComponent.State.IDLE);  // Retorna ao estado IDLE
            }
        }
    }



    // Método para aplicar dano à saúde
    public void damage(Entity entity, int amount) {
        HealthComponent health = hm.get(entity);
        StateComponent state = sm.get(entity);
        if (health != null && health.hurtCooldownTimer <= 0f) {
            health.currentHealth = Math.max(0, health.currentHealth - amount);
            health.wasDamagedThisFrame = true;
            state.set(StateComponent.State.HURT);

            health.hurtCooldownTimer = 0.1f;  // 1 segundo de invulnerabilidade
            health.hurtDuration = 0.1f;

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
