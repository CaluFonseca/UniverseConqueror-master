package com.badlogic.UniverseConqueror.ContactListener;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;

public class BulletContactListener implements ContactListener {
    private final Engine engine;
    private final World world;

    // Constructor to initialize engine
    public BulletContactListener(Engine engine, World world) {
        this.engine = engine;
        this.world = world;
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

    // Checks if the body is a bullet by checking userData
    private boolean isBulletCollision(Body body) {
        for (Fixture fixture : body.getFixtureList()) {
            if ("bullet".equals(fixture.getUserData())) {
                return true;
            }
            if ("fireball".equals(fixture.getUserData())) {
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

    // Dispose the bullet by removing its entity and destroying its body in the Box2D world
    private void disposeBullet(Body bulletBody) {
        if (bulletBody.getUserData() instanceof Entity bulletEntity) {
            Gdx.app.postRunnable(() -> {
                world.destroyBody(bulletBody);
                engine.removeEntity(bulletEntity);
            });
        }
    }

    @Override
    public void endContact(Contact contact) {}

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {}

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}
}
