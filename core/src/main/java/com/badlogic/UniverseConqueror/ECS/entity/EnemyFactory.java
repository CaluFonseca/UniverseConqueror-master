package com.badlogic.UniverseConqueror.ECS.entity;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.Strategy.*;
import com.badlogic.UniverseConqueror.ECS.systems.EnemyAnimationLoader;
import com.badlogic.ashley.core.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.ObjectMap;

public class EnemyFactory {

    /// Cria um inimigo que patrulha entre pontos e persegue o jogador ao detectá-lo
    public static Entity createPatrollingEnemy(PooledEngine engine, World world, Vector2 start,
                                               AssetManager assetManager, Entity player, OrthographicCamera camera,
                                               Vector2... patrolPoints) {

        Entity enemy = engine.createEntity();

        BodyComponent bodyComponent = createEnemyBody(start, world);
        bodyComponent.body.setUserData(enemy);
        enemy.add(bodyComponent);
        enemy.add(new PhysicsComponent(bodyComponent.body));
        enemy.add(new PositionComponent(start));

        EnemyStrategy patrol = new PatrolStrategy(patrolPoints);
        EnemyStrategy chase = new ChasePlayerStrategy(player, camera,60f);
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

        EnemyComponent enemyComponent = engine.createComponent(EnemyComponent.class);
        enemyComponent.type = EnemyComponent.BehaviorType.PATROL;
        enemy.add(enemyComponent);

        addAnimationComponents(enemy, engine, assetManager, "default", StateComponent.State.PATROL);

        return enemy;
    }

    /// Cria um inimigo que apenas persegue o jogador
    public static Entity createChasingEnemy(PooledEngine engine, World world, Vector2 position,
                                            AssetManager assetManager, Entity player, OrthographicCamera camera) {
        return createPatrollingEnemy(engine, world, position, assetManager, player, camera, position, position);
    }

    /// Cria um inimigo do tipo UFO com animações específicas
    public static Entity createUfoEnemy(PooledEngine engine, World world, Vector2 position,
                                        AssetManager assetManager, Entity player, OrthographicCamera camera) {

        Entity enemy =  new Entity();
        engine.removeEntity(enemy);
        enemy.removeAll();
        resetUfoEnemy(enemy, position, assetManager, player, camera, engine);

        EnemyAnimationLoader loader = new EnemyAnimationLoader(assetManager);
        ObjectMap<StateComponent.State, Animation<TextureRegion>> animations = loader.loadAnimations("ufo");

        if (animations == null || animations.get(StateComponent.State.CHASE) == null) {
            System.err.println("[ERROR] UFO animation failed to load. Aborting entity creation.");
            return null;
        }

        AnimationComponent anim = engine.createComponent(AnimationComponent.class);
        anim.setAnimations(animations);
        anim.stateTime = 0f;
        anim.currentFrame = animations.get(StateComponent.State.CHASE).getKeyFrame(0);

        StateComponent state = engine.createComponent(StateComponent.class);
        state.set(StateComponent.State.CHASE);

        enemy.add(anim);
        enemy.add(state);
        enemy.add(new UfoComponent());
        enemy.add(new PositionComponent(position));
        enemy.add(new AIComponent(new ChasePlayerStrategy(player, camera,100f)));
        enemy.add(new VelocityComponent());
        enemy.add(new TransformComponent());

        HealthComponent health = engine.createComponent(HealthComponent.class);
        health.maxHealth = 120;
        health.currentHealth = 120;
        enemy.add(health);

        EnemyComponent enemyComponent = engine.createComponent(EnemyComponent.class);
        enemyComponent.type = EnemyComponent.BehaviorType.UFO;
        enemy.add(enemyComponent);

        BodyComponent bodyComponent = createEnemyBody(position, world);
        bodyComponent.body.setUserData(enemy);
        enemy.add(bodyComponent);
        enemy.add(new PhysicsComponent(bodyComponent.body));

        return enemy;
    }

    /// Reinicializa todos os componentes do inimigo UFO
    public static void resetUfoEnemy(Entity enemy, Vector2 position, AssetManager assetManager,
                                     Entity player, OrthographicCamera camera, PooledEngine engine) {

        EnemyAnimationLoader loader = new EnemyAnimationLoader(assetManager);
        ObjectMap<StateComponent.State, Animation<TextureRegion>> animations = loader.loadAnimations("ufo");

        AnimationComponent anim = enemy.getComponent(AnimationComponent.class);
        if (anim == null) {
            anim = engine.createComponent(AnimationComponent.class);
            enemy.add(anim);
        }
        anim.setAnimations(animations);
        anim.stateTime = 0f;
        anim.currentFrame = animations.get(StateComponent.State.CHASE).getKeyFrame(0f);

        StateComponent state = enemy.getComponent(StateComponent.class);
        if (state == null) {
            state = engine.createComponent(StateComponent.class);
            enemy.add(state);
        }
        state.set(StateComponent.State.CHASE);
        state.timeInState = 0f;

        PositionComponent pos = enemy.getComponent(PositionComponent.class);
        if (pos == null) {
            pos = new PositionComponent(position);
            enemy.add(pos);
        } else {
            pos.position.set(position);
        }

        HealthComponent health = enemy.getComponent(HealthComponent.class);
        if (health == null) {
            health = engine.createComponent(HealthComponent.class);
            enemy.add(health);
        }
        health.maxHealth = 120;
        health.currentHealth = 120;
        health.hurtCooldownTimer = 0;
        health.hurtDuration = 0;
        health.scheduledForRemoval = false;

        AIComponent ai = enemy.getComponent(AIComponent.class);
        if (ai == null) {
            ai = new AIComponent(new ChasePlayerStrategy(player, camera,100f));
            enemy.add(ai);
        } else {
            ai.strategy = new ChasePlayerStrategy(player, camera,100f);
        }

        if (enemy.getComponent(VelocityComponent.class) == null) {
            enemy.add(new VelocityComponent());
        }

        if (enemy.getComponent(TransformComponent.class) == null) {
            enemy.add(new TransformComponent());
        }

        if (enemy.getComponent(UfoComponent.class) == null) {
            enemy.add(new UfoComponent());
        }

        EnemyComponent enemyComponent = enemy.getComponent(EnemyComponent.class);
        if (enemyComponent == null) {
            enemyComponent = engine.createComponent(EnemyComponent.class);
            enemy.add(enemyComponent);
        }
        enemyComponent.type = EnemyComponent.BehaviorType.UFO;

        if (!anim.animations.containsKey(state.get())) {
            // System.err.println("[ERRO] UFO resetado com estado inv\u00e1lido: " + state.get());
        }
    }

    /// Adiciona anima\u00e7\u00f5es e estado inicial ao inimigo
    private static void addAnimationComponents(Entity enemy, PooledEngine engine,
                                               AssetManager assetManager, String enemyType,
                                               StateComponent.State ignoredInitialState) {
        EnemyAnimationLoader enemyLoader = new EnemyAnimationLoader(assetManager);
        ObjectMap<StateComponent.State, Animation<TextureRegion>> animations = enemyLoader.loadAnimations(enemyType);

        AnimationComponent anim = engine.createComponent(AnimationComponent.class);
        anim.setAnimations(animations);
        anim.stateTime = 0f;

        StateComponent state = engine.createComponent(StateComponent.class);

        StateComponent.State chosenState = "ufo".equals(enemyType)
            ? StateComponent.State.CHASE
            : StateComponent.State.PATROL;

        state.set(chosenState);
        anim.currentFrame = animations.get(chosenState).getKeyFrame(0);

        enemy.add(anim);
        enemy.add(state);
    }

    /// Cria corpo Box2D padr\u00e3o para inimigos
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

        body.createFixture(fixtureDef).setUserData("enemy");
        shape.dispose();

        BodyComponent bc = new BodyComponent();
        bc.body = body;
        return bc;
    }

    /// Altera o estado e reinicia a anima\u00e7\u00e3o do inimigo
    public static void changeState(Entity enemy, StateComponent.State newState) {
        StateComponent state = enemy.getComponent(StateComponent.class);
        if (state != null && state.get() != newState) {
            state.set(newState);
            state.timeInState = 0f;

            AnimationComponent anim = enemy.getComponent(AnimationComponent.class);
            if (anim != null) {
                anim.stateTime = 0f;
            }
        }
    }
}
