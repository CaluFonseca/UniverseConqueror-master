package com.badlogic.UniverseConqueror.ECS.events;

import com.badlogic.UniverseConqueror.Interfaces.GameEvent;
import com.badlogic.ashley.core.Entity;

// Evento disparado quando uma entidade ativa o movimento rápido (FAST_MOVE)
public class FastMoveEvent implements GameEvent {

    // Entidade que iniciou o movimento rápido
    public final Entity entity;

    // Construtor do evento de movimento rápido
    public FastMoveEvent(Entity entity) {
        this.entity = entity;
    }
}
