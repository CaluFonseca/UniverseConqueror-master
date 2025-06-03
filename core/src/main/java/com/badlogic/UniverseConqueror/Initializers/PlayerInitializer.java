package com.badlogic.UniverseConqueror.Initializers;

import com.badlogic.UniverseConqueror.Context.GameContext;
import com.badlogic.UniverseConqueror.ECS.entity.PlayerFactory;
import com.badlogic.UniverseConqueror.Pathfinding.MapGraphBuilder;
import com.badlogic.UniverseConqueror.Pathfinding.Node;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

/**
 * A classe `PlayerInitializer` é responsável por inicializar o jogador no jogo.
 * Usa o `MapGraphBuilder` para encontrar uma posição viável no mapa para o jogador
 * e cria a entidade do jogador utilizando a `PlayerFactory`.
 */
public class PlayerInitializer extends AbstractInitializer {

    /**
     * Construtor da classe `PlayerInitializer`. Inicializa o contexto do jogo.
     *
     * @param context O contexto do jogo, necessário para aceder sistemas e recursos.
     */
    public PlayerInitializer(GameContext context) {
        super(context);
    }

    @Override
    public void initialize() {
        MapGraphBuilder mapGraphBuilder = context.getMapGraphBuilder();
        int mapWidth = mapGraphBuilder.getWidth();
        int mapHeight = mapGraphBuilder.getHeight();

        // Inicializa a posição de spawn do jogador no centro do mapa
        Node spawnNode = mapGraphBuilder.nodes[mapWidth / 2][mapHeight / 2];

        // Verifica se o nó de spawn é caminhável. Caso contrário, procura um nó próximo que seja caminhável.
        if (!spawnNode.walkable) {
            outer:
            for (int x = mapWidth / 2 - 2; x <= mapWidth / 2 + 2; x++) {
                for (int y = mapHeight / 2 - 2; y <= mapHeight / 2 + 2; y++) {
                    if (x >= 0 && y >= 0 && x < mapWidth && y < mapHeight) {
                        Node node = mapGraphBuilder.nodes[x][y];
                        if (node.walkable) {
                            spawnNode = node;
                            break outer;
                        }
                    }
                }
            }
        }

        // Converte a posição do nó para a posição no mundo
        Vector2 spawnPosition = mapGraphBuilder.toWorldPosition(spawnNode);

        // Define o centro do mapa para ser a posição do jogador
        context.getWorldContext().setCenterX(spawnPosition.x + 5);
        context.getWorldContext().setCenterY(spawnPosition.y + 5);

        // Cria a entidade do jogador utilizando a `PlayerFactory`
        Entity player = PlayerFactory.createPlayer(
            context.getEngine(),
            spawnPosition,
            context.getWorldContext().getWorld(),
            context.getAssetManager()
        );

        // Adiciona a entidade do jogador à engine do ECS
        context.getEngine().addEntity(player);
        context.setPlayer(player);
    }
}
