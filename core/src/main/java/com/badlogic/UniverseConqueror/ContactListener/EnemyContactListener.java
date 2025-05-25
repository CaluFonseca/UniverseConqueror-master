package com.badlogic.UniverseConqueror.ContactListener;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.ECS.systems.HealthSystem;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.*;

public class EnemyContactListener {
    private final Engine engine;
    private final HealthSystem healthSystem;

    public EnemyContactListener(Engine engine, HealthSystem healthSystem) {
        this.engine = engine;
        this.healthSystem = healthSystem;
    }

    public void beginContact(Contact contact) {
        Entity a = getEntity(contact.getFixtureA());
        Entity b = getEntity(contact.getFixtureB());

        if (a == null || b == null) return;

        if (isEnemy(a) && isPlayer(b)) {
            applyDamageToPlayer(b, a);
        } else if (isEnemy(b) && isPlayer(a)) {
            applyDamageToPlayer(a, b);
        }
    }

    public void endContact(Contact contact) {}
    public void preSolve(Contact contact, Manifold oldManifold) {}
    public void postSolve(Contact contact, ContactImpulse impulse) {}

    private Entity getEntity(Fixture fixture) {
        Object userData = fixture.getBody().getUserData();
        return userData instanceof Entity ? (Entity) userData : null;
    }

    private boolean isEnemy(Entity entity) {
        return entity.getComponent(EnemyComponent.class) != null;
    }

    private boolean isPlayer(Entity entity) {
        return entity.getComponent(PlayerComponent.class) != null;
    }

    private void applyDamageToPlayer(Entity player, Entity enemy) {
        healthSystem.damage(player, 10);
    }
}
