/// Estratégia de patrulha para inimigos.
/// O inimigo se move entre pontos definidos em sequência, formando um caminho de patrulha.

package com.badlogic.UniverseConqueror.Strategy;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.Interfaces.EnemyStrategy;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

public class PatrolStrategy implements EnemyStrategy {

    /// Pontos que o inimigo deve patrulhar.
    private Vector2[] patrolPoints;

    /// Índice do ponto atual no caminho de patrulha.
    private int currentPoint = 0;

    /// Velocidade de movimento do inimigo durante a patrulha.
    private float speed = 20f;

    /// Direção atual do movimento.
    private final Vector2 direction = new Vector2();

    /// Construtor que recebe os pontos de patrulha.
    /// @param patrolPoints sequência de pontos a serem patrulhados
    public PatrolStrategy(Vector2... patrolPoints) {
        this.patrolPoints = patrolPoints;
    }

    /// Atualiza o comportamento de patrulha do inimigo.
    /// @param enemy a entidade inimiga a ser atualizada
    /// @param deltaTime tempo decorrido desde o último frame (em segundos)
    @Override
    public void update(Entity enemy, float deltaTime) {

        PhysicsComponent pc = enemy.getComponent(PhysicsComponent.class);
        VelocityComponent velocity = enemy.getComponent(VelocityComponent.class);
        StateComponent state = enemy.getComponent(StateComponent.class);
        PositionComponent position = enemy.getComponent(PositionComponent.class);
        HealthComponent health = enemy.getComponent(HealthComponent.class);

        /// Ignora atualização se o inimigo estiver morto ou sem componentes necessários.
        if (health != null && health.isDead()) return;
        if (pc == null || velocity == null) return;

        /// Obtém o ponto de destino atual e calcula a direção até ele.
        Vector2 target = patrolPoints[currentPoint];
        Vector2 pos = pc.body.getPosition();
        direction.set(target).sub(pos);

        /// Se o inimigo chegou próximo ao ponto, muda para o próximo.
        if (direction.len() < 0.1f) {
            velocity.velocity.set(0, 0);
            direction.set(0, 0);
            currentPoint = (currentPoint + 1) % patrolPoints.length;
        } else {
            /// Move na direção do ponto de patrulha com velocidade ajustada.
            direction.nor().scl(speed);
            velocity.velocity.set(direction);

            /// Atualiza o estado para PATROL, se aplicável.
            if (state != null && state.get() != StateComponent.State.PATROL && state.currentState != StateComponent.State.HURT) {
                state.set(StateComponent.State.PATROL);
            }
        }

        /// Atualiza a posição lógica da entidade com base na física.
        if (position != null) {
            position.position.set(pc.body.getPosition());
        }
    }

    /// Retorna a direção atual do movimento.
    /// @return vetor normalizado representando a direção
    @Override
    public Vector2 getDirection() {
        return direction;
    }
}
