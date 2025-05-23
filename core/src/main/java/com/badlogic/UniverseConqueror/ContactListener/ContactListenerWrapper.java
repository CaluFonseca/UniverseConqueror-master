package com.badlogic.UniverseConqueror.ContactListener;

import com.badlogic.UniverseConqueror.ECS.entity.BulletFactory;
import com.badlogic.UniverseConqueror.ECS.systems.HealthSystem;
import com.badlogic.UniverseConqueror.ECS.systems.ItemCollectionSystem;
import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.physics.box2d.*;

public class ContactListenerWrapper implements ContactListener {

    public final MapContactListener mapContactListener;
    private final BulletContactListener bulletContactListener;
    private final World world;
    public ContactListenerWrapper(Engine engine, ItemCollectionSystem itemCollectionSystem, HealthSystem healthSystem,World world, BulletFactory bulletFactory) {
        this.world = world;
        this.mapContactListener = new MapContactListener(engine, itemCollectionSystem, healthSystem);
        this.bulletContactListener = new BulletContactListener(engine,world, bulletFactory);

    }

    @Override
    public void beginContact(Contact contact) {
        bulletContactListener.beginContact(contact);
        mapContactListener.beginContact(contact);
    }

    @Override
    public void endContact(Contact contact) {
        mapContactListener.endContact(contact);
        bulletContactListener.endContact(contact);
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        mapContactListener.preSolve(contact, oldManifold);
        bulletContactListener.preSolve(contact, oldManifold);
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        mapContactListener.postSolve(contact, impulse);
        bulletContactListener.postSolve(contact, impulse);
    }
}
