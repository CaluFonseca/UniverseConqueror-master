package com.badlogic.UniverseConqueror.ECS.components;

import com.badlogic.UniverseConqueror.Interfaces.EnemyStrategy;
import com.badlogic.ashley.core.Component;

public class AIComponent implements Component {
    public EnemyStrategy strategy;
    public long lastPathfindingTime = 0;
    public AIComponent(EnemyStrategy strategy) {
        this.strategy = strategy;
    }
}
