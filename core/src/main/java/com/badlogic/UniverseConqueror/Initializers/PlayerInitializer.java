package com.badlogic.UniverseConqueror.Initializers;

import com.badlogic.UniverseConqueror.ECS.entity.PlayerFactory;
import com.badlogic.UniverseConqueror.Pathfinding.MapGraphBuilder;
import com.badlogic.UniverseConqueror.Pathfinding.Node;
import com.badlogic.UniverseConqueror.Screens.GameScreen;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

public class PlayerInitializer {

    /// Inicializa o jogador no centro do mapa ou no ponto caminhável mais próximo
    public static Entity initializePlayer(GameScreen screen, MapGraphBuilder mapGraphBuilder) {
        /// Obtém as dimensões do grafo do mapa
        int mapWidth = mapGraphBuilder.getWidth();
        int mapHeight = mapGraphBuilder.getHeight();

        /// Tenta posicionar o jogador no centro do mapa
        Node spawnNode = mapGraphBuilder.nodes[mapWidth / 2][mapHeight / 2];

        /// Se o centro não for caminhável, procura por um nó caminhável próximo
        if (!spawnNode.walkable) {
            outer:
            for (int x = mapWidth / 2 - 2; x <= mapWidth / 2 + 2; x++) {
                for (int y = mapHeight / 2 - 2; y <= mapHeight / 2 + 2; y++) {
                    // Garante que está dentro dos limites do mapa
                    if (x >= 0 && y >= 0 && x < mapWidth && y < mapHeight) {
                        Node node = mapGraphBuilder.nodes[x][y];
                        if (node.walkable) {
                            spawnNode = node;
                            break outer; // Sai do loop duplo ao encontrar um ponto válido
                        }
                    }
                }
            }
        }

        /// Converte o nó encontrado para coordenadas no mundo
        Vector2 spawnPosition = mapGraphBuilder.toWorldPosition(spawnNode);

        /// Define as coordenadas centrais no ecrã principal (GameScreen)
        screen.centerX = spawnPosition.x + 5;
        screen.centerY = spawnPosition.y + 5;

        /// Cria a entidade do jogador e a adiciona na engine
        Entity player = PlayerFactory.createPlayer(screen.engine, spawnPosition, screen.world, screen.assetManager);
        screen.engine.addEntity(player);

        /// Retorna a entidade criada
        return player;
    }
}
