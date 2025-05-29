package com.badlogic.UniverseConqueror.State;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

/// Classe que armazena o estado completo do jogo para salvar e restaurar
public class GameState {
    /// Posição atual do jogador no mundo
    public Vector2 playerPosition;

    /// Vida atual do jogador
    public int playerHealth;

    /// Poder de ataque atual do jogador
    public int playerAttack;

    /// Tempo total de jogo transcorrido
    public float gameTime;

    /// Quantidade de itens coletados pelo jogador
    public int collectedItemCount = 0;

    /// Lista dos dados dos itens restantes no mundo (não coletados)
    public ArrayList<SavedItemData> remainingItems = new ArrayList<>();

    /// Posição da nave espacial (objetivo do nível)
    public Vector2 spaceshipPosition;

    /// Lista dos dados dos inimigos existentes no jogo
    public Array<SavedEnemyData> enemies = new Array<>();
}
