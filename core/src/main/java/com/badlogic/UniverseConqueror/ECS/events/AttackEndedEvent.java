package com.badlogic.UniverseConqueror.ECS.events;

import com.badlogic.UniverseConqueror.Interfaces.GameEvent;
import com.badlogic.ashley.core.Entity;

// Evento disparado quando o ataque de uma entidade termina
public class AttackEndedEvent implements GameEvent {

    // A entidade que finalizou o ataque
    public final Entity entity;

    // Construtor que recebe a entidade associada ao fim do ataque
    public AttackEndedEvent(Entity entity) {
        this.entity = entity;
    }
}
