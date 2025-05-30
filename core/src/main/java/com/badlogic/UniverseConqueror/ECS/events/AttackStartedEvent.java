package com.badlogic.UniverseConqueror.ECS.events;

import com.badlogic.UniverseConqueror.Interfaces.GameEvent;
import com.badlogic.ashley.core.Entity;

/// Evento disparado quando uma entidade inicia um ataque
public class AttackStartedEvent implements GameEvent {

    /// A entidade que iniciou o ataque
    public final Entity attacker;

    /// Indica se o ataque é um super ataque
    public final boolean isSuperAttack;

    /// Construtor do evento de ataque iniciado
    /// @param attacker A entidade que está atacando
    /// @param isSuperAttack True se for um super ataque, false caso contrário
    public AttackStartedEvent(Entity attacker, boolean isSuperAttack) {
        this.attacker = attacker;
        this.isSuperAttack = isSuperAttack;
    }
}
