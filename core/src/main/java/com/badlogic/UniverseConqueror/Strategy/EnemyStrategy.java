/// Interface que define a estratégia de comportamento para inimigos.
/// Implementações concretas usarão esta interface para controlar o movimento e ações dos inimigos.

package com.badlogic.UniverseConqueror.Strategy;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

public interface EnemyStrategy {
    /// Atualiza o comportamento do inimigo a cada frame.
    /// @param enemy entidade inimiga a ser atualizada
    /// @param deltaTime tempo decorrido desde o último frame (em segundos)
    void update(Entity enemy, float deltaTime);

    /// Retorna a direção atual do movimento definida pela estratégia.
    /// @return um vetor normalizado representando a direção de movimento
    Vector2 getDirection();
}
