package com.badlogic.UniverseConqueror.ECS.events;

import com.badlogic.ashley.core.Entity;

/// Evento que indica uma mudança nos pontos de vida (HP) de uma entidade
public class HealthChangedEvent implements GameEvent {
    /// Entidade que teve a saúde alterada
    public final Entity entity;

    /// Valor atual da saúde após a alteração
    public final int currentHealth;

    /// Construtor do evento
    public HealthChangedEvent(Entity entity, int currentHealth) {
        this.entity = entity;
        this.currentHealth = currentHealth;
    }
}
