package com.badlogic.UniverseConqueror.Strategy;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

public interface EnemyStrategy {
    void update(Entity enemy, float deltaTime);
    Vector2 getDirection();
}
