package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.ECS.entity.EnemyFactory;
import com.badlogic.UniverseConqueror.ECS.events.*;
import com.badlogic.UniverseConqueror.ECS.utils.ComponentMappers;
import com.badlogic.UniverseConqueror.Interfaces.GameEvent;
import com.badlogic.UniverseConqueror.Interfaces.Observer;
import com.badlogic.ashley.core.*;

public class HealthSystem extends BaseIteratingSystem implements Observer {

    private Engine engine;

    public HealthSystem() {
        super(Family.all(HealthComponent.class, StateComponent.class).get());
    }


    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        this.engine = engine;
        EventBus.get().addObserver(this);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        HealthComponent health = ComponentMappers.health.get(entity);
        StateComponent state = ComponentMappers.state.get(entity);

        if (health == null || state == null) return;

        // Reduz timers de invulnerabilidade e animação HURT
        if (health.hurtCooldownTimer > 0f) {
            health.hurtCooldownTimer -= deltaTime;
        }
        if (health.hurtDuration > 0f) {
            health.hurtDuration -= deltaTime;
        }

        // Morte
        if (health.currentHealth <= 0 && state.get() != StateComponent.State.DEATH) {
            EnemyFactory.changeState(entity, StateComponent.State.DEATH);
            EventBus.get().notify(new DeathEvent(entity));
        }

        // Fim do HURT, volta para CHASE
        if (state.get() == StateComponent.State.HURT &&
            health.hurtCooldownTimer <= 0f &&
            health.hurtDuration <= 0f &&
            !health.isDead()) {

            EnemyFactory.changeState(entity, StateComponent.State.CHASE);
            EventBus.get().notify(new IdleEvent(entity));
        }
    }

    @Override
    public void onNotify(GameEvent event) {
        if (event instanceof DamageTakenEvent dmg) {
            Entity target = dmg.getTarget();
            HealthComponent health = ComponentMappers.health.get(target);

            if (health != null && health.hurtCooldownTimer <= 0f) {
                damage(target, dmg.getDamage());
                health.hurtCooldownTimer = 1.0f;
            }
        }
    }

    public void damage(Entity entity, int amount) {
        HealthComponent health = ComponentMappers.health.get(entity);
        StateComponent state = ComponentMappers.state.get(entity);

        if (health != null && health.hurtCooldownTimer <= 0f &&
            state != null &&
            state.get() != StateComponent.State.DEFENSE &&
            state.get() != StateComponent.State.DEFENSE_INJURED) {

            health.currentHealth = Math.max(0, health.currentHealth - amount);
            health.hurtCooldownTimer = 0.5f;
            health.hurtDuration = 0.5f;

            state.set(StateComponent.State.HURT);
            EventBus.get().notify(new HealthChangedEvent(entity, health.currentHealth));
        }
    }

    public void heal(Entity entity, int amount) {
        HealthComponent health = ComponentMappers.health.get(entity);
        if (health != null) {
            health.currentHealth = Math.min(health.maxHealth, health.currentHealth + amount);
            EventBus.get().notify(new HealthChangedEvent(entity, health.currentHealth));
        }
    }

    public int getCurrentHealth(Entity entity) {
        HealthComponent health = ComponentMappers.health.get(entity);
        return health != null ? health.currentHealth : 0;
    }

}
