package com.badlogic.UniverseConqueror.Initializers;

import com.badlogic.UniverseConqueror.Context.GameContext;
import com.badlogic.UniverseConqueror.ECS.entity.PlayerFactory;
import com.badlogic.UniverseConqueror.Pathfinding.MapGraphBuilder;
import com.badlogic.UniverseConqueror.Pathfinding.Node;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

/**
 * A classe `PlayerInitializer` é responsável por inicializar o jogador no jogo.
 * Ela usa o `MapGraphBuilder` para encontrar uma posição viável no mapa para o jogador
 * e cria a entidade do jogador utilizando a `PlayerFactory`.
 */
public class PlayerInitializer extends AbstractInitializer {

    /**
     * Construtor da classe `PlayerInitializer`. Inicializa o contexto do jogo.
     *
     * @param context O contexto do jogo, necessário para acessar sistemas e recursos.
     */
    public PlayerInitializer(GameContext context) {
        super(context);  /// Chama o construtor da classe pai para inicializar o contexto
    }

    @Override
    public void initialize() {
        // Acessa o MapGraphBuilder a partir do contexto
        MapGraphBuilder mapGraphBuilder = context.getMapGraphBuilder();  /// Obtém o construtor do mapa do contexto
        int mapWidth = mapGraphBuilder.getWidth();  /// Obtém a largura do mapa
        int mapHeight = mapGraphBuilder.getHeight();  /// Obtém a altura do mapa

        // Inicializa a posição de spawn do jogador no centro do mapa
        Node spawnNode = mapGraphBuilder.nodes[mapWidth / 2][mapHeight / 2];

        // Verifica se o nó de spawn é caminhável. Caso contrário, procura um nó próximo que seja caminhável.
        if (!spawnNode.walkable) {
            outer:  /// Rótulo para sair do laço de busca
            for (int x = mapWidth / 2 - 2; x <= mapWidth / 2 + 2; x++) {  /// Percorre uma área ao redor do centro
                for (int y = mapHeight / 2 - 2; y <= mapHeight / 2 + 2; y++) {
                    if (x >= 0 && y >= 0 && x < mapWidth && y < mapHeight) {  /// Verifica se as coordenadas estão dentro do mapa
                        Node node = mapGraphBuilder.nodes[x][y];  /// Obtém o nó da posição
                        if (node.walkable) {  /// Se o nó for caminhável
                            spawnNode = node;  /// Define esse nó como o local de spawn
                            break outer;  /// Sai do laço de busca
                        }
                    }
                }
            }
        }

        // Converte a posição do nó para a posição no mundo
        Vector2 spawnPosition = mapGraphBuilder.toWorldPosition(spawnNode);

        // Define o centro do mapa para ser a posição do jogador
        context.getWorldContext().setCenterX(spawnPosition.x + 5);  /// Ajusta o centro do mapa para a posição do jogador
        context.getWorldContext().setCenterY(spawnPosition.y + 5);  /// Ajusta o centro do mapa para a posição do jogador

        // Cria a entidade do jogador utilizando a `PlayerFactory`
        Entity player = PlayerFactory.createPlayer(
            context.getEngine(),  /// Passa a engine do ECS
            spawnPosition,  /// Posição do jogador no mapa
            context.getWorldContext().getWorld(),  /// Mundo físico do jogo
            context.getAssetManager()  /// AssetManager para carregar recursos do jogador
        );

        // Adiciona a entidade do jogador à engine do ECS
        context.getEngine().addEntity(player);
        context.setPlayer(player);  /// Define a entidade do jogador no contexto do jogo
    }
}
