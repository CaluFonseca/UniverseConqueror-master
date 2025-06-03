package com.badlogic.UniverseConqueror.ECS.events;

import com.badlogic.UniverseConqueror.Interfaces.GameEvent;
import com.badlogic.ashley.core.Entity;

// Evento disparado quando uma entidade realiza um salto
public class JumpEvent implements GameEvent {

    // Entidade que executou o salto
    public final Entity jumper;

    // Construtor do evento de salto
    public JumpEvent(Entity jumper) {
        this.jumper = jumper;
    }
}
