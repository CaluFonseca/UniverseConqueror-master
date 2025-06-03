package com.badlogic.UniverseConqueror.Initializers;

import com.badlogic.UniverseConqueror.Screens.GameScreen;
import com.badlogic.UniverseConqueror.Context.GameContext;
import com.badlogic.UniverseConqueror.Utils.MapCollisionHandler;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

/**
 * A classe `WorldInitializer` é responsável por inicializar o mundo físico e o mapa com colisões.
 * Ela configura o sistema físico (Box2D), o carregamento do mapa isométrico e a manipulação de colisões.
 */
public class WorldInitializer extends AbstractInitializer {

    GameScreen screen;  /// Referência ao ecrã principal do jogo (GameScreen)

    /**
     * Construtor da classe `WorldInitializer`.
     *
     * @param context O contexto do jogo, necessário para acessar sistemas e recursos.
     * @param screen A tela do jogo, utilizada para modificar ou configurar o ecrã principal.
     */
    public WorldInitializer(GameContext context, GameScreen screen) {
        super(context);  // Chama o construtor da classe base AbstractInitializer
        this.screen = screen;  // Inicializa a referência ao ecrã
    }

    @Override
    public void initialize() {
        // Chama o método para inicializar o mundo físico e o mapa com colisões
        initializeWorld();
    }

    /**
     * Inicializa o mundo físico do Box2D, o mapa isométrico e os sistemas de colisões.
     */
    public void initializeWorld() {
        // Cria o mundo físico com a gravidade definida como (0, -9.8f) para simular a gravidade
        World world = new World(new Vector2(0, -9.8f), true);  /// Cria o mundo com a gravidade padrão
        context.getWorldContext().setWorld(world);  /// Define o mundo físico no contexto do jogo

        // Cria um renderizador de debug para o Box2D para visualizar as colisões
        Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();
        context.getWorldContext().setDebugRenderer(debugRenderer);  /// Define o renderizador de debug no contexto do jogo

        // Carrega o mapa isométrico a partir de um arquivo Tiled (.tmx)
        TiledMap map = new TmxMapLoader().load("mapa.tmx");  /// Carrega o mapa do arquivo
        context.getWorldContext().setMap(map);  /// Define o mapa no contexto do jogo

        // Cria um renderizador para o mapa isométrico, que converte as coordenadas do mapa para o mundo
        IsometricTiledMapRenderer renderer = new IsometricTiledMapRenderer(map);
        context.getWorldContext().setMapRenderer(renderer);  /// Define o renderizador no contexto do jogo

        // Cria um manipulador de colisões que usa as camadas "Collisions" e "Jumpable" do mapa
        MapCollisionHandler handler = new MapCollisionHandler(map, "Collisions", "Jumpable");
        context.getWorldContext().setCollisionHandler(handler);  /// Define o manipulador de colisões no contexto do jogo
        context.getWorldContext().getCollisionHandler().createBox2DBodies(context.getWorldContext().getWorld());  /// Cria os corpos físicos no mundo com base no manipulador de colisões

        // Define um renderizador de formas para desenhar as colisões e outros elementos gráficos
        context.getWorldContext().setShapeRenderer(new ShapeRenderer());  /// Inicializa o ShapeRenderer para desenhar formas
    }
}
