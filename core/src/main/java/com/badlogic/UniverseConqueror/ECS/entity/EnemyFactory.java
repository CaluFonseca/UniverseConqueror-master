package com.badlogic.UniverseConqueror.ECS.entity;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.Strategy.ChasePlayerStrategy;
import com.badlogic.UniverseConqueror.Strategy.EnemyStrategy;
import com.badlogic.UniverseConqueror.Strategy.PatrolStrategy;
import com.badlogic.UniverseConqueror.ECS.systems.EnemyAnimationLoader;
import com.badlogic.UniverseConqueror.Strategy.SwitchingStrategy;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class EnemyFactory {

    public static Entity createPatrollingEnemy(PooledEngine engine, World world, Vector2 start,
                                               AssetManager assetManager, Entity player, OrthographicCamera camera,
                                               Vector2... patrolPoints) {
        Entity enemy = engine.createEntity();

        enemy.add(new PositionComponent(start));

        BodyComponent bodyComponent = createEnemyBody(start, world);
        bodyComponent.body.setUserData(enemy);
        enemy.add(bodyComponent);
        enemy.add(new PhysicsComponent(bodyComponent.body));

        // Estratégias individuais
        EnemyStrategy patrol = new PatrolStrategy(patrolPoints);
        EnemyStrategy chase = new ChasePlayerStrategy(player, camera);
        EnemyStrategy smartStrategy = new SwitchingStrategy(player, patrol, chase, camera);

        enemy.add(new AIComponent(smartStrategy));

        TransformComponent transform = engine.createComponent(TransformComponent.class);
        transform.position.set(start.x, start.y, 0);
        enemy.add(transform);

        VelocityComponent velocity = engine.createComponent(VelocityComponent.class);
        enemy.add(velocity);

        HealthComponent health = engine.createComponent(HealthComponent.class);
        health.maxHealth = 100;
        health.currentHealth = 100;
        enemy.add(health);

        enemy.add(engine.createComponent(EnemyComponent.class));

        addAnimationComponents(enemy, engine, assetManager);

        return enemy;
    }

//    public static void debugEnemyState(Entity enemy) {
//        StateComponent state = enemy.getComponent(StateComponent.class);
//        HealthComponent health = enemy.getComponent(HealthComponent.class);
//
//        if (state != null && health != null) {
//            System.out.println("---------- ENEMY STATE DEBUG ----------");
//            System.out.println("Entity ID: " + enemy.hashCode());
//            System.out.println("State: " + state.get());
//            System.out.println("IsDead: " + health.isDead());
//            System.out.println("Health: " + health.currentHealth + "/" + health.maxHealth);
//       //     System.out.println("HasTakenDamage: " + health.hasTakenDamage);
//            System.out.println("---------------------------------------");
//        }
//    }
public static void changeState(Entity enemy, StateComponent.State newState) {
    StateComponent state = enemy.getComponent(StateComponent.class);
    if (state != null && state.get() != newState) {
        System.out.println("Enemy " + enemy.hashCode() + " changed from " + state.get() + " → " + newState);
        state.set(newState);
    }
}
    public static Entity createChasingEnemy(PooledEngine engine, World world, Vector2 start,
                                            Entity player, AssetManager assetManager) {
        Entity enemy = engine.createEntity();
        return enemy;
    }

    private static void addAnimationComponents(Entity enemy, PooledEngine engine, AssetManager assetManager) {
        EnemyAnimationLoader enemyLoader = new EnemyAnimationLoader(assetManager);
        ObjectMap<StateComponent.State, Animation<TextureRegion>> animations = enemyLoader.loadAnimations();

        AnimationComponent anim = engine.createComponent(AnimationComponent.class);
        anim.setAnimations(animations);

        StateComponent state = engine.createComponent(StateComponent.class);
     //   state.set(StateComponent.State.IDLE);

        enemy.add(anim);
        enemy.add(state);
    }

    private static BodyComponent createEnemyBody(Vector2 position, World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position);

        Body body = world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(50f, 65f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData("enemy");

        shape.dispose();

        BodyComponent bc = new BodyComponent();
        bc.body = body;


        return bc;
    }

    public static void free(Entity enemy, PooledEngine engine, Array<Entity> enemyPool) {
        PhysicsComponent physics = enemy.getComponent(PhysicsComponent.class);
        if (physics != null && physics.body != null) {
            physics.body.setActive(false);
            physics.body.setLinearVelocity(0, 0);
        }

        VelocityComponent velocity = enemy.getComponent(VelocityComponent.class);
        if (velocity != null) {
            velocity.velocity.setZero();
        }

        PositionComponent position = enemy.getComponent(PositionComponent.class);
        if (position != null) {
            position.position.set(-10000, -10000);
        }

        StateComponent state = enemy.getComponent(StateComponent.class);
        if (state != null) {
            state.set(StateComponent.State.IDLE); // ou outro estado de reset
            state.timeInState = 0;
        }

        HealthComponent health = enemy.getComponent(HealthComponent.class);
        if (health != null) {
            health.currentHealth = health.maxHealth;
            health.wasDamagedThisFrame = false;
            health.hurtCooldownTimer = 0;
            health.hurtDuration = 0;
        }
        EnemyComponent enemyComponent = engine.createComponent(EnemyComponent.class);
        enemyComponent.type = EnemyComponent.BehaviorType.PATROL;
        enemy.add(enemyComponent);

        // Remove da engine para evitar que continue em sistemas ativos
        engine.removeEntity(enemy);

        // Recoloca no pool
        enemyPool.add(enemy);
    }

    public static Entity createChasingEnemy(PooledEngine engine, World world, Vector2 position,
                                            AssetManager assetManager, Entity player, OrthographicCamera camera) {
        // Simplesmente passa start == end == position
        return createPatrollingEnemy(engine, world, position, assetManager, player, camera, position, position);
    }

}
