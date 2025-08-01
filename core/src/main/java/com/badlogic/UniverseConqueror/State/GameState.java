package com.badlogic.UniverseConqueror.State;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

// Classe que armazena o estado completo do jogo para guardar e restaurar
public class GameState {
    public Vector2 playerPosition;
    public int playerHealth;
    public int playerAttack;
    public float gameTime;
    public int collectedItemCount = 0;
    public ArrayList<SavedItemData> remainingItems = new ArrayList<>();
    public Vector2 spaceshipPosition;
    public Array<SavedEnemyData> enemies = new Array<>();
}
