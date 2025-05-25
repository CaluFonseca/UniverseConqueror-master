package com.badlogic.UniverseConqueror.State;

import com.badlogic.gdx.math.Vector2;

public class SavedEnemyData {
    public Vector2 position;
    public Vector2 patrolStart;
    public Vector2 patrolEnd;
    public String type;

    public SavedEnemyData() {}

    public SavedEnemyData(Vector2 position, Vector2 patrolStart, Vector2 patrolEnd, String type) {
        this.position = position;
        this.patrolStart = patrolStart;
        this.patrolEnd = patrolEnd;
        this.type = type;
    }
}
