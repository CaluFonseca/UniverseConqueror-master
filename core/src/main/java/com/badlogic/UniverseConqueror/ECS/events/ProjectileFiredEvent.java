package com.badlogic.UniverseConqueror.ECS.events;

import com.badlogic.UniverseConqueror.Interfaces.GameEvent;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

public class ProjectileFiredEvent implements GameEvent {
    public final Entity attacker;
    public final Vector2 target;
    public final boolean isFireball;

    public ProjectileFiredEvent(Entity attacker, Vector2 target, boolean isFireball) {
        this.attacker = attacker;
        this.target = target;
        this.isFireball = isFireball;
    }
}
