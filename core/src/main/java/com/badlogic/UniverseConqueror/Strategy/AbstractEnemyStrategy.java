package com.badlogic.UniverseConqueror.Strategy;

import com.badlogic.UniverseConqueror.Interfaces.EnemyStrategy;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

// Classe base abstrata para estratégias comuns de inimigos
public abstract class AbstractEnemyStrategy implements EnemyStrategy {
    protected final Vector2 direction = new Vector2(); // Direção do movimento
    // Retorna a direção atual
    @Override
    public Vector2 getDirection() {
        return direction;
    }

    // Calcula a direção entre duas posições
    protected void calculateDirection(Vector2 from, Vector2 to) {
        direction.set(to).sub(from).nor();
    }
}
