package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.physics.box2d.Body;

import java.util.function.Consumer;

public class EnemyCleanupSystem extends EntitySystem {

    private final ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);
    private final ComponentMapper<StateComponent> sm = ComponentMapper.getFor(StateComponent.class);
    private final ComponentMapper<PhysicsComponent> pm = ComponentMapper.getFor(PhysicsComponent.class);

    private final PooledEngine engine;
    private final BodyRemovalSystem bodyRemovalSystem;
    private final AnimationSystem animationSystem;
    private final Consumer<Entity> onEnemyKilled;

    private ImmutableArray<Entity> enemies;

    public EnemyCleanupSystem(PooledEngine engine,
                              BodyRemovalSystem bodyRemovalSystem,
                              AnimationSystem animationSystem,
                              Consumer<Entity> onEnemyKilled) {
        this.engine = engine;
        this.bodyRemovalSystem = bodyRemovalSystem;
        this.animationSystem = animationSystem;
        this.onEnemyKilled = onEnemyKilled;
    }

    @Override
    public void addedToEngine(Engine engine) {
        enemies = engine.getEntitiesFor(Family.all(
            HealthComponent.class,
            StateComponent.class,
            EnemyComponent.class
        ).get());
    }

    @Override
    public void update(float deltaTime) {
        for (Entity enemy : enemies) {
            HealthComponent health = hm.get(enemy);
            StateComponent state = sm.get(enemy);

            if (health.isDead()
                && state.get() == StateComponent.State.DEATH
                && animationSystem.isDeathAnimationFinished(enemy)) {

                PhysicsComponent pc = pm.get(enemy);
                if (pc != null && pc.body != null) {
                    bodyRemovalSystem.markForRemoval(pc.body);
                    pc.body = null; // 🔒 limpa referência ao corpo
                }

                health.scheduledForRemoval = true; // opcional, rastreia entidades a remover

                engine.removeEntity(enemy);
                onEnemyKilled.accept(enemy);

                //System.out.println("[EnemyCleanup] Inimigo removido: " + enemy.hashCode());
            }
        }
    }

}

