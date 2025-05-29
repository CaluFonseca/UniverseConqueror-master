package com.badlogic.UniverseConqueror.ECS.events;

import com.badlogic.ashley.core.Entity;

/// Evento que indica que uma entidade entrou no estado de inatividade (IDLE)
public class IdleEvent implements GameEvent {
    /// Entidade que está agora em estado IDLE
    public final Entity entity;

    /// Construtor do evento Idle
    public IdleEvent(Entity entity) {
        this.entity = entity;
    }
}
