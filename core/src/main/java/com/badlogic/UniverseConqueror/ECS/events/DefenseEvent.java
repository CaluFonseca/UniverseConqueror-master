package com.badlogic.UniverseConqueror.ECS.events;

import com.badlogic.UniverseConqueror.Interfaces.GameEvent;
import com.badlogic.ashley.core.Entity;

/// Evento que representa uma ação de defesa realizada por uma entidade
public class DefenseEvent implements GameEvent {

    /// Entidade que iniciou a defesa
    public final Entity entity;

    /// Construtor do evento de defesa
    /// @param entity Entidade que executou a ação de defesa
    public DefenseEvent(Entity entity) {
        this.entity = entity;
    }
}
