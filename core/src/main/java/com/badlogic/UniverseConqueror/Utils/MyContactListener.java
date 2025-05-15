package com.badlogic.UniverseConqueror.Utils;

import com.badlogic.UniverseConqueror.ECS.components.HealthComponent;
import com.badlogic.UniverseConqueror.ECS.components.ItemComponent;
import com.badlogic.UniverseConqueror.ECS.components.PlayerComponent;
import com.badlogic.UniverseConqueror.ECS.systems.HealthSystem;
import com.badlogic.UniverseConqueror.ECS.systems.ItemCollectionSystem;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.*;

public class MyContactListener implements ContactListener {

    private final Engine engine;
    private final ItemCollectionSystem itemCollectionSystem; // Item collection system
    private HealthSystem healthSystem;

    // Constructor to receive engine, itemCollectionSystem, and healthSystem
    public MyContactListener(Engine engine, ItemCollectionSystem itemCollectionSystem, HealthSystem healthSystem) {
        this.engine = engine;
        this.itemCollectionSystem = itemCollectionSystem; // Initializes itemCollectionSystem
        this.healthSystem = healthSystem;  // Initializes healthSystem
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
                // Collect item and heal the player if it's a healing item
                collectItem(bodyA, bodyB, playerEntity);
            } else {
                // If not an item, apply damage to the player
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

        return null; // Returns null if no player entity found
    }

    // Checks if the collision is between the player and an item
    private boolean isPlayerAndItemCollision(Body bodyA, Body bodyB) {
        Fixture playerFixture = bodyA.getFixtureList().get(0);
        Fixture itemFixture = bodyB.getFixtureList().get(0);

        // Item collision is identified by the string "item" in the fixture's userData
        return ("item".equals(itemFixture.getUserData()));
    }

    // Item collection method, including healing the player if it's a "Vida" item
    private void collectItem(Body bodyA, Body bodyB, Entity playerEntity) {
        Fixture itemFixture = bodyB.getFixtureList().get(0);

        // Check if the userData of the item fixture is "item" and collect
        if ("item".equals(itemFixture.getUserData())) {
            Entity itemEntity = (Entity) itemFixture.getBody().getUserData();
            if (itemEntity != null) {
                // Call the ItemCollectionSystem method to collect the item
                itemCollectionSystem.collectItem(itemEntity,playerEntity);

                // Remove the item from ECS
                engine.removeEntity(itemEntity);

                // If the collected item is "Vida", heal the player
//                ItemComponent item = itemEntity.getComponent(ItemComponent.class);
//                if ("Vida".equals(item.name) && playerEntity != null) {
//                    // Heal the player with a specific amount (example: 20)
//                    healthSystem.heal(playerEntity, 20);  // Heal the player by 20 health points
//                }
            }
        }
    }

    // Applies damage to the player
    private void applyDamageToPlayer(Entity playerEntity, int damageAmount) {
        try {
            if (playerEntity != null) {
                // Use the HealthSystem to apply damage
                healthSystem.damage(playerEntity, damageAmount);  // Applies damage to the player
            }
        } catch (Exception e) {
            // Catch the exception and print the error message
            System.err.println("Error applying damage to the player: " + e.getMessage());
            e.printStackTrace();  // Optional: Print the stack trace for debugging
        }
    }

    @Override
    public void endContact(Contact contact) {}

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {}

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}
}
