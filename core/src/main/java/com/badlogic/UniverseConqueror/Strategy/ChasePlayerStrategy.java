package com.badlogic.UniverseConqueror.Strategy;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.ashley.core.Entity;

// Estratégia para perseguir o jogador
public class ChasePlayerStrategy extends AbstractEnemyStrategy {

    private final Entity target;
    private final float speed;
    private final OrthographicCamera camera;


    // Construtor da estratégia de perseguição ao jogador.
    public ChasePlayerStrategy(Entity target, OrthographicCamera camera, float speed) {
        this.target = target;
        this.camera = camera;
        this.speed = speed;
    }

    @Override
    public void update(Entity enemy, float deltaTime) {
        if (target == null) return;

        PhysicsComponent enemyPhysics = enemy.getComponent(PhysicsComponent.class);
        PhysicsComponent targetPhysics = target.getComponent(PhysicsComponent.class);
        VelocityComponent velocity = enemy.getComponent(VelocityComponent.class);
        StateComponent state = enemy.getComponent(StateComponent.class);
        HealthComponent health = enemy.getComponent(HealthComponent.class);

        // Verifica se algum componente necessário está ausente ou se o inimigo está morto
        if (enemyPhysics == null || targetPhysics == null || velocity == null || health == null || health.isDead())
            return;

        // Obtém a posição do inimigo e do alvo
        Vector2 enemyPos = enemyPhysics.body.getPosition();
        Vector2 targetPos = targetPhysics.body.getPosition();

        // Calcula a direção normalizada entre o inimigo e o alvo
        calculateDirection(enemyPos, targetPos);

        float distance = enemyPos.dst(targetPos);

        // Se a distância entre o inimigo e o alvo for menor que 100, o inimigo para de se mover e volta ao estado de patrulha
        if (distance < 100f) {
            velocity.velocity.setZero();
            if (state.currentState != StateComponent.State.HURT) {
                if (enemy.getComponent(UfoComponent.class) != null) {
                    if (state.currentState != StateComponent.State.CHASE && state.currentState != StateComponent.State.HURT) {
                        state.set(StateComponent.State.CHASE);
                    }
                } else {
                    if (state.currentState != StateComponent.State.PATROL && state.currentState != StateComponent.State.HURT) {
                        state.set(StateComponent.State.PATROL);
                    }
                }
            }
        } else {
            // Se o inimigo está distante o suficiente, continua se movendo em direção ao jogador
            velocity.velocity.set(direction.scl(speed));
            if (state.currentState != StateComponent.State.CHASE && state.currentState != StateComponent.State.HURT) {
                state.set(StateComponent.State.CHASE);
            }
        }
    }
}
