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

        EnemyComponent enemyComponent = engine.createComponent(EnemyComponent.class);
        enemyComponent.type = EnemyComponent.BehaviorType.PATROL;
        enemy.add(enemyComponent);

        addAnimationComponents(enemy, engine, assetManager, "default", StateComponent.State.PATROL);

        return enemy;
    }

    public static Entity createChasingEnemy(PooledEngine engine, World world, Vector2 position,
                                            AssetManager assetManager, Entity player, OrthographicCamera camera) {
        return createPatrollingEnemy(engine, world, position, assetManager, player, camera, position, position);
    }

    public static Entity createUfoEnemy(PooledEngine engine, World world, Vector2 position,
                                        AssetManager assetManager, Entity player, OrthographicCamera camera) {

        Entity enemy = engine.createEntity();

        // Load animations first
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

        // Now it's safe to add the rest
        enemy.add(new PositionComponent(position));
        enemy.add(new AIComponent(new ChasePlayerStrategy(player, camera)));
        enemy.add(new VelocityComponent());
        enemy.add(new TransformComponent());

        HealthComponent health = engine.createComponent(HealthComponent.class);
        health.maxHealth = 120;
        health.currentHealth = 120;
        enemy.add(health);

        EnemyComponent enemyComponent = engine.createComponent(EnemyComponent.class);
        enemyComponent.type = EnemyComponent.BehaviorType.UFO;
        enemy.add(enemyComponent);

        // ✅ Add Box2D body only after confirming animations are valid
        BodyComponent bodyComponent = createEnemyBody(position, world);
        bodyComponent.body.setUserData(enemy);
        enemy.add(bodyComponent);
        enemy.add(new PhysicsComponent(bodyComponent.body));

        return enemy;
    }


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

        //System.out.println("[EnemyFactory] " + enemyType + " criado com estado inicial: " + chosenState);

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

        body.createFixture(fixtureDef).setUserData("enemy");

        shape.dispose();

        BodyComponent bc = new BodyComponent();
        bc.body = body;
        return bc;
    }

    public static void changeState(Entity enemy, StateComponent.State newState) {
        StateComponent state = enemy.getComponent(StateComponent.class);
        if (state != null && state.get() != newState) {
            //System.out.println("Enemy " + enemy.hashCode() + " changed from " + state.get() + " → " + newState);
            state.set(newState);
        }
    }
}
