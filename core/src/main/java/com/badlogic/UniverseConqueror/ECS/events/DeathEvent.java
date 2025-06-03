package com.badlogic.UniverseConqueror.ECS.events;

import com.badlogic.UniverseConqueror.Interfaces.GameEvent;
import com.badlogic.ashley.core.Entity;

// Evento que representa a morte de uma entidade
public class DeathEvent implements GameEvent {

    // Entidade que morreu
    public final Entity entity;

    // Construtor do evento de morte
    // @param entity Entidade que morreu
    public DeathEvent(Entity entity) {
        this.entity = entity;
    }
}
