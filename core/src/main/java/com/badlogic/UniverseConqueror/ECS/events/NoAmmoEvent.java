package com.badlogic.UniverseConqueror.ECS.events;

import com.badlogic.ashley.core.Entity;

/// Evento disparado quando a entidade tenta atacar sem munição
public class NoAmmoEvent implements GameEvent {

    /// Entidade que ficou sem munição
    public final Entity entity;

    /// Construtor do evento NoAmmo
    public NoAmmoEvent(Entity entity) {
        this.entity = entity;
    }
}
