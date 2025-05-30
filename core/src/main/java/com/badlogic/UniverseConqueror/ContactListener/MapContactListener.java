package com.badlogic.UniverseConqueror.ContactListener;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.ECS.events.DamageTakenEvent;
import com.badlogic.UniverseConqueror.ECS.events.EndGameEvent;
import com.badlogic.UniverseConqueror.ECS.events.EventBus;
import com.badlogic.UniverseConqueror.ECS.systems.ItemCollectionSystem;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class MapContactListener implements ContactListener {
    private final ComponentMapper<KnockbackComponent> knockbackMapper = ComponentMapper.getFor(KnockbackComponent.class);

    private final Engine engine;
    private final ItemCollectionSystem itemCollectionSystem;
    private final java.util.function.Supplier<Integer> enemiesKilledSupplier;
    //   private HealthSystem healthSystem;
    private Runnable onEndLevelCallback;
    // Constructor to receive engine, itemCollectionSystem, and healthSystem
    public MapContactListener(Engine engine, ItemCollectionSystem itemCollectionSystem,
                              java.util.function.Supplier<Integer> enemiesKilledSupplier) {
        this.engine = engine;
        this.itemCollectionSystem = itemCollectionSystem;
        this.enemiesKilledSupplier = enemiesKilledSupplier;
    }
    public void setOnEndLevel(Runnable callback) {
        this.onEndLevelCallback = callback;
    }
    @Override
    public void beginContact(Contact contact) {
        Body bodyA = contact.getFixtureA().getBody();
        Body bodyB = contact.getFixtureB().getBody();

        Entity playerEntity = getPlayerEntity(bodyA, bodyB);
        boolean colA = isMapCollision(bodyA);
        boolean colB = isMapCollision(bodyB);
        if(colA==true||colB == true)
        {
            System.out.println("Collision Detected mapa"+ bodyA +"ou"+bodyB);
            if(colA==true) {

                 applyKnockbackIfEntity(bodyB,bodyA);
            }
            else{
                 applyKnockbackIfEntity(bodyA,bodyB);
            }
        }
        if (playerEntity == null) return;

        if (isPlayerAndSpaceshipCollision(bodyA, bodyB)) {
            int enemiesKilled = enemiesKilledSupplier.get();

            EventBus.get().notify(new EndGameEvent(playerEntity, enemiesKilled));
            return;
        }

        if (isPlayerAndItemCollision(bodyA, bodyB)) {
            Entity itemEntity = (Entity) bodyB.getUserData();
            collectItem(itemEntity, playerEntity);
            engine.removeEntity(itemEntity);
            return;
        }

        if (!isBulletCollision(bodyA, bodyB) && !isEnemyCollision(bodyA, bodyB)) {
            applyDamageToPlayer(playerEntity, 1);
        }
    }

//    private void applyKnockbackIfEntity(Body entityBody, Body mapBody) {
//        Entity entity = getEntity(entityBody);
//        if (entity == null) return;
//
//        if (entity.getComponent(KnockbackComponent.class) != null) return;
//
//        PositionComponent pos = entity.getComponent(PositionComponent.class);
//        BodyComponent bodyComp = entity.getComponent(BodyComponent.class);
//
//        if (pos == null || bodyComp == null) return;
//
//        Vector2 entityPos = pos.position;
//        Vector2 tilePos = mapBody.getPosition();
//
//        Vector2 direction = entityPos.cpy().sub(tilePos).nor();
//
////        // Empurra levemente o corpo antes de aplicar knockback
////        Vector2 displacement = direction.cpy().scl(0.5f); // desloca meio metro para fora
////        bodyComp.body.setTransform(bodyComp.body.getPosition().add(displacement), bodyComp.body.getAngle());
//
//        // Cria componente de knockback
//        KnockbackComponent knockback = new KnockbackComponent();
//        knockback.impulse.set(direction.scl(20f)); // impulso real do knockback
//        knockback.timeRemaining = 0.2f;
//        knockback.duration = 0.2f;
//        knockback.hasBeenApplied = false;
//
//        entity.add(knockback);
//
//        System.out.println("[DEBUG] Knockback aplicado à entidade: " + entity + " com direção: " + knockback.impulse);
//    }


//private void applyKnockbackIfEntity(Body entityBody, Body mapBody) {
//    Entity entity = getEntity(entityBody);
//    if (entity == null) return;
//
////    if (entity.getComponent(KnockbackComponent.class) != null) return;
//
//    PositionComponent pos = entity.getComponent(PositionComponent.class);
//    BodyComponent bodyComp = entity.getComponent(BodyComponent.class);
//
//    if (pos == null || bodyComp == null) return;
//
//    Vector2 entityPos = pos.position;
//    Vector2 tilePos = mapBody.getPosition();
//
//    Vector2 direction = entityPos.cpy().sub(tilePos).nor();
//
//    KnockbackComponent knockback = new KnockbackComponent();
//    knockback.impulse.set(direction.scl(10f)); // força da velocidade
//    knockback.timeRemaining = 0.2f;
//    knockback.duration = 0.2f;
//    knockback.hasBeenApplied = false;
//
//    entity.add(knockback);
//}

private void applyKnockbackIfEntity(Body entityBody, Body mapBody) {
    Entity entity = getEntity(entityBody);
    if (entity == null) return;

//    // ✅ Só aplica knockback se for um inimigo
//    if (entity.getComponent(EnemyComponent.class) == null) return;

    if (entity.getComponent(KnockbackComponent.class) != null) return;

    PositionComponent pos = entity.getComponent(PositionComponent.class);
    BodyComponent bodyComp = entity.getComponent(BodyComponent.class);

    if (pos == null || bodyComp == null) return;

    Vector2 entityPos = pos.position;
    Vector2 tilePos = mapBody.getPosition();

    Vector2 direction = entityPos.cpy().sub(tilePos).nor();

    if (entity.getComponent(KnockbackComponent.class) == null)
    {
        KnockbackComponent knockback = new KnockbackComponent();
        knockback.impulse = direction.scl(100f); // Ou qualquer força desejada
        knockback.timeRemaining = 0.3f;
        entity.add(knockback);
        System.out.println("[DEBUG] Knockback aplicado ao inimigo: " + entity + " com direção: " + direction);
    }


}



    private boolean isEnemyCollision(Body bodyA, Body bodyB) {
        Object dataA = bodyA.getUserData();
        Object dataB = bodyB.getUserData();

        if (dataA instanceof Entity && ((Entity) dataA).getComponent(com.badlogic.UniverseConqueror.ECS.components.EnemyComponent.class) != null) {
            return true;
        }
        if (dataB instanceof Entity && ((Entity) dataB).getComponent(com.badlogic.UniverseConqueror.ECS.components.EnemyComponent.class) != null) {
            return true;
        }
        return false;
    }

    private boolean isMapCollision(Body body) {
        for (Fixture fixture : body.getFixtureList()) {
            if ("map".equals(fixture.getUserData())) {
                return  true;
            }
        }
        return false;
    }

    private Entity getEntity(Body body) {
        Object userData = body.getUserData();
        return (userData instanceof Entity) ? (Entity) userData : null;
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
    private boolean isPlayerAndSpaceshipCollision(Body bodyA, Body bodyB) {
        Object dataA = bodyA.getUserData();
        Object dataB = bodyB.getUserData();

        if (!(dataA instanceof Entity) || !(dataB instanceof Entity)) {
            return false;
        }

        Entity entityA = (Entity) dataA;
        Entity entityB = (Entity) dataB;

        boolean isPlayerA = entityA.getComponent(PlayerComponent.class) != null;
        boolean isPlayerB = entityB.getComponent(PlayerComponent.class) != null;

        boolean isSpaceshipA = entityA.getComponent(com.badlogic.UniverseConqueror.ECS.components.EndLevelComponent.class) != null;
        boolean isSpaceshipB = entityB.getComponent(com.badlogic.UniverseConqueror.ECS.components.EndLevelComponent.class) != null;

        return (isPlayerA && isSpaceshipB) || (isPlayerB && isSpaceshipA);
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
    public void collectItem(Entity item, Entity player) {
        if (item == null || player == null) return;
        itemCollectionSystem.collectItem(item, player);
    }

    // Applies damage to the player
    private void applyDamageToPlayer(Entity playerEntity, int damageAmount) {
        try {
            if (playerEntity != null) {
                EventBus.get().notify(new DamageTakenEvent(playerEntity, null, damageAmount));
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

