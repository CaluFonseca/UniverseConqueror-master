package com.badlogic.UniverseConqueror.ECS.events;

import com.badlogic.ashley.core.Entity;

/// Evento disparado quando o poder de ataque de uma entidade é alterado
public class AttackPowerChangedEvent implements GameEvent {

    /// A entidade cujo poder de ataque foi alterado
    public final Entity entity;

    /// O novo valor do poder de ataque
    public final int newPower;

    /// Construtor que define a entidade afetada e o novo valor de poder de ataque
    public AttackPowerChangedEvent(Entity entity, int newPower) {
        this.entity = entity;
        this.newPower = newPower;
    }
}
