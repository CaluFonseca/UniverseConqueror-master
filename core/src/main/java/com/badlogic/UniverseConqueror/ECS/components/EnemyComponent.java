package com.badlogic.UniverseConqueror.ECS.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class EnemyComponent implements Component {
    public enum BehaviorType {
        CHASE,
        PATROL,
        IDLE
    }
    public Vector2 patrolStart = null;
    public Vector2 patrolEnd = null;
    public BehaviorType type = BehaviorType.PATROL;
}
