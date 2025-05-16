package com.badlogic.UniverseConqueror.ContactListener;

import com.badlogic.UniverseConqueror.ECS.components.PlayerComponent;
import com.badlogic.UniverseConqueror.ECS.systems.HealthSystem;
import com.badlogic.UniverseConqueror.ECS.systems.ItemCollectionSystem;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.*;

public class MapContactListener implements ContactListener {

    private final Engine engine;
    private final ItemCollectionSystem itemCollectionSystem;
    private HealthSystem healthSystem;

    // Constructor to receive engine, itemCollectionSystem, and healthSystem
    public MapContactListener(Engine engine, ItemCollectionSystem itemCollectionSystem, HealthSystem healthSystem) {
        this.engine = engine;
        this.itemCollectionSystem = itemCollectionSystem;
        this.healthSystem = healthSystem;
    }

    @Override
    public void beginContact(Contact contact) {
        Body bodyA = contact.getFixtureA().getBody();
        Body bodyB = contact.getFixtureB().getBody();

        // Get the player entity from the collision
        Entity playerEntity = getPlayerEntity(bodyA, bodyB);

        if (playerEntity != null) {
            // Check if the collision is with an item
            if (isPlayerAndItemCollision(bodyA, bodyB)) {
                collectItem(bodyA, bodyB, playerEntity);
            } else if (!isBulletCollision(bodyA, bodyB)) {
                applyDamageToPlayer(playerEntity, 1);
            }
        }
    }

    // Retrieves the player entity from the collision
    private Entity getPlayerEntity(Body bodyA, Body bodyB) {
        Object userDataA = bodyA.getUserData();
        Object userDataB = bodyB.getUserData();

        if (userDataA instanceof Entity) {
            Entity entityA = (Entity) userDataA;
            if (entityA.getComponent(PlayerComponent.class) != null) {
                return entityA;
            }
        }

        if (userDataB instanceof Entity) {
            Entity entityB = (Entity) userDataB;
            if (entityB.getComponent(PlayerComponent.class) != null) {
                return entityB;
            }
        }

        return null;
    }

    // Checks if the collision is between the player and an item
    private boolean isPlayerAndItemCollision(Body bodyA, Body bodyB) {
        Fixture playerFixture = bodyA.getFixtureList().get(0);
        Fixture itemFixture = bodyB.getFixtureList().get(0);

        if ("item".equals(itemFixture.getUserData())) {
            return true;
        }
        if ("bullet".equals(itemFixture.getUserData()) || "fireball".equals(itemFixture.getUserData())) {
            return false;
        }

        return false;
    }

    private boolean isBulletCollision(Body bodyA, Body bodyB) {
        Fixture playerFixture = bodyA.getFixtureList().get(0);
        Fixture itemFixture = bodyB.getFixtureList().get(0);
        return "bullet".equals(itemFixture.getUserData()) || "fireball".equals(itemFixture.getUserData());
    }

    private void disposeBullet(Body bulletBody) {
        if (bulletBody.getUserData() instanceof Entity) {
            Entity bulletEntity = (Entity) bulletBody.getUserData();
            bulletBody.getWorld().destroyBody(bulletBody);
            engine.removeEntity(bulletEntity);
        }
    }

    // Item collection method, including healing the player if it's a "Vida" item
    private void collectItem(Body bodyA, Body bodyB, Entity playerEntity) {
        Fixture itemFixture = bodyB.getFixtureList().get(0);

        if ("item".equals(itemFixture.getUserData())) {
            Entity itemEntity = (Entity) itemFixture.getBody().getUserData();
            if (itemEntity != null) {
                itemCollectionSystem.collectItem(itemEntity,playerEntity);
                engine.removeEntity(itemEntity);
            }
        }
    }

    // Applies damage to the player
    private void applyDamageToPlayer(Entity playerEntity, int damageAmount) {
        try {
            if (playerEntity != null) {
                healthSystem.damage(playerEntity, damageAmount);
            }
        } catch (Exception e) {
            System.err.println("Error applying damage to the player: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void endContact(Contact contact) {}

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {}

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}
}
