package com.badlogic.UniverseConqueror.ECS.events;

import com.badlogic.UniverseConqueror.Interfaces.GameEvent;
import com.badlogic.ashley.core.Entity;

/// Evento que representa quando uma entidade sofre dano
public class DamageTakenEvent implements GameEvent {

    /// Entidade que recebeu o dano
    private final Entity target;

    /// Entidade que causou o dano
    private final Entity source;

    /// Quantidade de dano sofrido
    private final int damage;

    /// Construtor do evento de dano
    /// @param target Entidade que sofreu o dano
    /// @param source Entidade que causou o dano
    /// @param damage Quantidade de dano infligido
    public DamageTakenEvent(Entity target, Entity source, int damage) {
        this.target = target;
        this.source = source;
        this.damage = damage;
    }

    /// Retorna a entidade que sofreu o dano
    public Entity getTarget() {
        return target;
    }

    /// Retorna a entidade que causou o dano
    public Entity getSource() {
        return source;
    }

    /// Retorna a quantidade de dano
    public int getDamage() {
        return damage;
    }
}
