package com.badlogic.UniverseConqueror.State;

import com.badlogic.gdx.math.Vector2;

public class SavedEnemyData {
    public Vector2 position;
    public Vector2 patrolStart;
    public Vector2 patrolEnd;
    public String type;

    public Vector2 target;
    public SavedEnemyData() {}

    // Construtor completo para criar um objeto SavedEnemyData com todos os dados necessários
    public SavedEnemyData(Vector2 position, Vector2 patrolStart, Vector2 patrolEnd, String type, Vector2 target) {
        this.position = position;
        this.patrolStart = patrolStart;
        this.patrolEnd = patrolEnd;
        this.type = type;
        this.target = target;
    }
}
