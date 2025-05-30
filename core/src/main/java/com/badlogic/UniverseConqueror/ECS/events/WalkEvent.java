package com.badlogic.UniverseConqueror.ECS.events;

import com.badlogic.UniverseConqueror.Interfaces.GameEvent;
import com.badlogic.ashley.core.Entity;

/// Evento disparado quando uma entidade inicia ou realiza a ação de caminhar
public class WalkEvent implements GameEvent {
    public final Entity entity;

    public WalkEvent(Entity entity) {
        this.entity = entity;
    }
}
