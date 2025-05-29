package com.badlogic.UniverseConqueror.ContactListener;

import com.badlogic.UniverseConqueror.ECS.entity.BulletFactory;
import com.badlogic.UniverseConqueror.ECS.systems.ItemCollectionSystem;
import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.physics.box2d.*;

public class ContactListenerWrapper implements ContactListener {

    /// Listener responsável por colisões com o mapa (jogador, itens, inimigos)
    public final MapContactListener mapContactListener;

    /// Listener para colisões de projéteis (balas, fireballs)
    private final BulletContactListener bulletContactListener;

    /// Listener para colisões entre inimigos e outros corpos
    private final EnemyContactListener enemyListener;

    /// Contador de inimigos mortos, passado para MapContactListener via lambda
    private int enemiesKilledCount;

    public ContactListenerWrapper(Engine engine, ItemCollectionSystem itemCollectionSystem, BulletFactory bulletFactory) {
        this.mapContactListener = new MapContactListener(engine, itemCollectionSystem, () -> this.enemiesKilledCount);
        this.bulletContactListener = new BulletContactListener(bulletFactory);
        this.enemyListener = new EnemyContactListener();
    }

    @Override
    public void beginContact(Contact contact) {
        bulletContactListener.beginContact(contact);
        mapContactListener.beginContact(contact);
        enemyListener.beginContact(contact);
    }

    @Override
    public void endContact(Contact contact) {
        mapContactListener.endContact(contact);
        bulletContactListener.endContact(contact);
        enemyListener.endContact(contact);
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        mapContactListener.preSolve(contact, oldManifold);
        bulletContactListener.preSolve(contact, oldManifold);
        enemyListener.preSolve(contact, oldManifold);
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        mapContactListener.postSolve(contact, impulse);
        bulletContactListener.postSolve(contact, impulse);
        enemyListener.postSolve(contact, impulse);
    }
}
