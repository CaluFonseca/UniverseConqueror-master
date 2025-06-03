package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.ECS.utils.ComponentMappers;
import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;


import java.util.function.Consumer;

public class EnemyCleanupSystem extends EntitySystem {

    private final PooledEngine engine;
    private final BodyRemovalSystem bodyRemovalSystem;
    private final AnimationSystem animationSystem;
    private final Consumer<Entity> onEnemyKilled;

    private ImmutableArray<Entity> enemies;

    // Construtor com dependências principais
    public EnemyCleanupSystem(PooledEngine engine,
                              BodyRemovalSystem bodyRemovalSystem,
                              AnimationSystem animationSystem,
                              Consumer<Entity> onEnemyKilled) {
        this.engine = engine;
        this.bodyRemovalSystem = bodyRemovalSystem;
        this.animationSystem = animationSystem;
        this.onEnemyKilled = onEnemyKilled;
    }

    // Define os inimigos relevantes ao adicionar o sistema
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
            HealthComponent health = ComponentMappers.health.get(enemy);
            StateComponent state = ComponentMappers.state.get(enemy);

            // Ignora se o inimigo ainda não morreu
            if (!health.isDead()) continue;

            // Verifica se a animação de morte já terminou
            if (state.get() == StateComponent.State.DEATH) {
                boolean finished = animationSystem.isDeathAnimationFinished(enemy);

                if (finished) {
                    // Marca corpo para destruição
                    PhysicsComponent pc = ComponentMappers.physics.get(enemy);
                    if (pc != null && pc.body != null) {
                        bodyRemovalSystem.markForRemoval(pc.body);
                        pc.body = null;
                    }

                    // Marca o componente de vida para sinalizar remoção
                    health.scheduledForRemoval = true;

                    // Remove a entidade da engine
                    engine.removeEntity(enemy);
                    enemy.removeAll(); // força limpeza total dos componentes

                    // Executa ação personalizada ao matar o inimigo, contador
                    onEnemyKilled.accept(enemy);
                }
            }
        }
    }
}
