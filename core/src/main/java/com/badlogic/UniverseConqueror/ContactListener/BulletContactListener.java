package com.badlogic.UniverseConqueror.ContactListener;

import com.badlogic.UniverseConqueror.ECS.components.PhysicsComponent;
import com.badlogic.UniverseConqueror.ECS.entity.BulletFactory;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;

public class BulletContactListener implements ContactListener {
    private final Engine engine;
    private final World world;
    private final BulletFactory bulletFactory;

    public BulletContactListener(Engine engine, World world, BulletFactory bulletFactory) {
        this.engine = engine;
        this.world = world;
        this.bulletFactory = bulletFactory;
    }

    @Override
    public void beginContact(Contact contact) {
        Body bodyA = contact.getFixtureA().getBody();
        Body bodyB = contact.getFixtureB().getBody();

        if ((isBulletCollision(bodyA) && isMapCollision(bodyB)) || (isBulletCollision(bodyB) && isMapCollision(bodyA))) {
            Body bulletBody = isBulletCollision(bodyA) ? bodyA : bodyB;
            disposeBullet(bulletBody);
        }
    }

    private boolean isBulletCollision(Body body) {
        for (Fixture fixture : body.getFixtureList()) {
            Object data = fixture.getUserData();
            if ("bullet".equals(data) || "fireball".equals(data)) {
                return true;
            }
        }
        return false;
    }

    private boolean isMapCollision(Body body) {
        for (Fixture fixture : body.getFixtureList()) {
            if ("map".equals(fixture.getUserData())) {
                return true;
            }
        }
        return false;
    }

    private void disposeBullet(Body bulletBody) {
        if (bulletBody.getUserData() instanceof Entity bulletEntity) {
            Gdx.app.postRunnable(() -> {
                bulletFactory.free(bulletEntity);
                engine.removeEntity(bulletEntity);
            });
        }
    }

    @Override public void endContact(Contact contact) {}
    @Override public void preSolve(Contact contact, Manifold oldManifold) {}
    @Override public void postSolve(Contact contact, ContactImpulse impulse) {}
}
