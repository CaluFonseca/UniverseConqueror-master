package com.badlogic.UniverseConqueror.ECS.components;

import com.badlogic.UniverseConqueror.Strategy.EnemyStrategy;
import com.badlogic.ashley.core.Component;

public class AIComponent implements Component {
    public EnemyStrategy strategy;

    public AIComponent(EnemyStrategy strategy) {
        this.strategy = strategy;
    }
}
