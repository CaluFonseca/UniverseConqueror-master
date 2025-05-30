package com.badlogic.UniverseConqueror.ECS.systems;

// Importações dos componentes, fábrica de inimigos e eventos do sistema
import com.badlogic.UniverseConqueror.ECS.components.HealthComponent;
import com.badlogic.UniverseConqueror.ECS.components.StateComponent;
import com.badlogic.UniverseConqueror.ECS.entity.EnemyFactory;
import com.badlogic.UniverseConqueror.ECS.events.*;
import com.badlogic.UniverseConqueror.Interfaces.GameEvent;
import com.badlogic.UniverseConqueror.Interfaces.Observer;
import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;

// Sistema que gerencia a vida das entidades e responde a eventos de dano
public class HealthSystem extends EntitySystem implements Observer {

    private ImmutableArray<Entity> entities; // Entidades com vida e estado
    private final ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);
    private final ComponentMapper<StateComponent> sm = ComponentMapper.getFor(StateComponent.class);
    private Engine engine;

    // Adicionado à engine: captura entidades e registra no EventBus
    @Override
    public void addedToEngine(Engine engine) {
        this.engine = engine;
        entities = engine.getEntitiesFor(Family.all(HealthComponent.class, StateComponent.class).get());
        EventBus.get().addObserver(this); // Inscreve-se para receber eventos do tipo GameEvent
    }

    @Override
    public void update(float deltaTime) {
        for (Entity entity : entities) {
            HealthComponent health = hm.get(entity);
            StateComponent state = sm.get(entity);

            // Reduz os timers de cooldown e duração da animação HURT
            if (health.hurtCooldownTimer > 0f) {
                health.hurtCooldownTimer -= deltaTime;
            }
            if (health.hurtDuration > 0f) {
                health.hurtDuration -= deltaTime;
            }

            // Se a vida acabou e o estado ainda não é DEATH, muda o estado
            if (health.currentHealth <= 0 && state.get() != StateComponent.State.DEATH) {
                EnemyFactory.changeState(entity, StateComponent.State.DEATH); // Usa factory para transição segura
                EventBus.get().notify(new DeathEvent(entity)); // Notifica que a entidade morreu
            }

            // Se terminou o estado HURT, retorna para CHASE se ainda está vivo
            if (state.get() == StateComponent.State.HURT &&
                health.hurtDuration <= 0f &&
                health.hurtCooldownTimer <= 0f &&
                !health.isDead()) {

                EnemyFactory.changeState(entity, StateComponent.State.CHASE);
                EventBus.get().notify(new IdleEvent(entity)); // Pode ser usado para atualizar HUD ou lógicas de AI
            }
        }
    }

    // Método chamado quando um evento é disparado no EventBus
    @Override
    public void onNotify(GameEvent event) {
        if (event instanceof DamageTakenEvent dmg) {
            Entity target = dmg.getTarget();
            HealthComponent health = hm.get(target);

            // Verifica cooldown antes de aplicar o dano
            if (health != null && health.hurtCooldownTimer <= 0f) {
                damage(target, dmg.getDamage());
                health.hurtCooldownTimer = 1.0f; // Pequeno tempo de invulnerabilidade
            }
        }
    }

    // Aplica dano à entidade e inicia o estado HURT
    public void damage(Entity entity, int amount) {
        HealthComponent health = hm.get(entity);
        StateComponent state = sm.get(entity);

        if (health != null && health.hurtCooldownTimer <= 0f &&
            state != null &&
            state.get() != StateComponent.State.DEFENSE &&
            state.get() != StateComponent.State.DEFENSE_INJURED) {

            // Diminui vida, inicia timers de HURT
            health.currentHealth = Math.max(0, health.currentHealth - amount);
            health.hurtCooldownTimer = 0.5f;
            health.hurtDuration = 0.5f;

            state.set(StateComponent.State.HURT); // Atualiza estado
            EventBus.get().notify(new HealthChangedEvent(entity, health.currentHealth)); // Atualiza HUD
        }
    }

    // Cura a entidade
    public void heal(Entity entity, int amount) {
        HealthComponent health = hm.get(entity);
        if (health != null) {
            health.currentHealth = Math.min(health.maxHealth, health.currentHealth + amount);
            EventBus.get().notify(new HealthChangedEvent(entity, health.currentHealth)); // Atualiza HUD
        }
    }

    // Getter de vida atual
    public int getCurrentHealth(Entity entity) {
        HealthComponent health = hm.get(entity);
        return health != null ? health.currentHealth : 0;
    }

    // Setter opcional para engine (caso precise setar externamente)
    public void setEngine(Engine engine) {
        this.engine = engine;
    }
}
