package com.badlogic.UniverseConqueror.State;

import com.badlogic.gdx.math.Vector2;

public class SavedEnemyData {
    /// Posição atual do inimigo no mundo (coordenadas x,y)
    public Vector2 position;
    /// Ponto inicial do caminho de patrulha do inimigo
    public Vector2 patrolStart;
    /// Ponto final do caminho de patrulha do inimigo
    public Vector2 patrolEnd;
    /// Tipo do inimigo (ex: "chase", "patrol", etc), usado para recriar o comportamento correto
    public String type;

    /// Construtor vazio necessário para serialização/deserialização JSON
    public SavedEnemyData() {}

    /// Construtor completo para criar um objeto SavedEnemyData com todos os dados necessários
    public SavedEnemyData(Vector2 position, Vector2 patrolStart, Vector2 patrolEnd, String type) {
        this.position = position;
        this.patrolStart = patrolStart;
        this.patrolEnd = patrolEnd;
        this.type = type;
    }
}
