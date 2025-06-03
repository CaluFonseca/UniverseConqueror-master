package com.badlogic.UniverseConqueror.Initializers;

import com.badlogic.UniverseConqueror.ECS.entity.BulletFactory;
import com.badlogic.UniverseConqueror.ECS.systems.*;
import com.badlogic.UniverseConqueror.Utils.Joystick;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.UniverseConqueror.Context.GameContext;

/**
 * A classe `SystemInitializer` é responsável por inicializar todos os sistemas do jogo,
 * incluindo sistemas de renderização, entrada do jogador, física, animação e outros sistemas auxiliares.
 */
public class SystemInitializer extends AbstractInitializer {

    private final PooledEngine engine;  /// Engine do ECS (Sistema de Componentes Entidade) para gerenciar as entidades e sistemas.
    private final World world;  /// Mundo físico do Box2D para simulação de física.
    private final OrthographicCamera camera;  /// Câmera ortográfica para renderizar a visão do jogo.
    private final TiledMap map;  /// Mapa do jogo para renderização e movimentação.
    private final BulletFactory bulletFactory;  /// Fábrica de balas para criar e gerenciar balas.
    private final Joystick joystick;  /// Joystick virtual para controlar o jogador.
    private final AssetManager assetManager;  /// Gerenciador de recursos (texturas, sons, etc.).

    // Sistemas que serão inicializados
    public CameraInputSystem cameraInputSystem;  /// Sistema que lida com a entrada do jogador para controlar a câmera.
    public PlayerInputSystem playerInputSystem;  /// Sistema que lida com a entrada do jogador para controlar o personagem.
    public BulletSystem bulletSystem;  /// Sistema que gerencia as balas no jogo.
    public BulletRenderSystem bulletRenderSystem;  /// Sistema que renderiza as balas.
    public BulletMovementSystem bulletMovementSystem;  /// Sistema que atualiza a movimentação das balas.
    public AttackSystem attackSystem;  /// Sistema de ataque do jogador.
    public HealthSystem healthSystem;  /// Sistema de saúde do jogador e inimigos.
    public BodyRemovalSystem bodyRemovalSystem;  /// Sistema que lida com a remoção de corpos físicos (Box2D).
    public ItemCollectionSystem itemCollectionSystem;  /// Sistema de coleta de itens.
    public AnimationSystem animationSystem;  /// Sistema que gerencia animações.

    /**
     * Construtor para inicializar as dependências e preparar o contexto do jogo.
     *
     * @param context O contexto do jogo, necessário para acessar sistemas e recursos.
     * @param engine Engine do ECS.
     * @param world Mundo físico do Box2D.
     * @param camera Câmera ortográfica.
     * @param map Mapa do jogo.
     * @param bulletFactory Fábrica de balas.
     * @param joystick Joystick para entrada do jogador.
     * @param assetManager Gerenciador de recursos.
     */
    public SystemInitializer(GameContext context, PooledEngine engine, World world, OrthographicCamera camera,
                             TiledMap map, BulletFactory bulletFactory, Joystick joystick, AssetManager assetManager) {
        super(context);  /// Chama o construtor da classe pai `AbstractInitializer` para inicializar o contexto do jogo.
        this.engine = engine;
        this.world = world;
        this.camera = camera;
        this.map = map;
        this.bulletFactory = bulletFactory;
        this.joystick = joystick;
        this.assetManager = assetManager;
    }

    @Override
    public void initialize() {
        SpriteBatch batch = new SpriteBatch();  /// Inicializa o SpriteBatch para renderizar as texturas.

        // Calcula as dimensões do mapa em pixels
        int mapWidth = map.getProperties().get("width", Integer.class) * map.getProperties().get("tilewidth", Integer.class);  /// Largura do mapa em pixels.
        int mapHeight = map.getProperties().get("height", Integer.class) * map.getProperties().get("tileheight", Integer.class);  /// Altura do mapa em pixels.

        // Inicializa sistemas de renderização e movimentação da câmera
        engine.addSystem(new CameraSystem(camera, mapWidth, mapHeight));  /// Sistema de controle da câmera.
        engine.addSystem(new RenderSystem(batch, camera));  /// Sistema de renderização do jogo.
        engine.addSystem(new UfoRenderSystem(batch, camera));  /// Sistema de renderização para os UFOs.

        // Inicializa sistemas de entrada e movimentação do jogador
        cameraInputSystem = new CameraInputSystem(camera);  /// Sistema de entrada para controlar a câmera.
        bulletSystem = new BulletSystem(world, camera, assetManager, engine);  /// Sistema para gerenciar as balas.
        bulletRenderSystem = new BulletRenderSystem(batch);  /// Sistema para renderizar as balas.
        bulletMovementSystem = new BulletMovementSystem();  /// Sistema que controla a movimentação das balas.
        playerInputSystem = new PlayerInputSystem(world, joystick, bulletSystem, camera, engine, bulletFactory);  /// Sistema de entrada para controlar o jogador.

        engine.addSystem(bulletMovementSystem);  /// Adiciona o sistema de movimentação das balas.
        engine.addSystem(bulletSystem);  /// Adiciona o sistema de balas.
        engine.addSystem(bulletRenderSystem);  /// Adiciona o sistema de renderização de balas.
        engine.addSystem(playerInputSystem);  /// Adiciona o sistema de entrada do jogador.
        engine.addSystem(new MovementSystem());  /// Sistema de movimentação de entidades.
        engine.addSystem(new StateSystem());  /// Sistema que gerencia os estados das entidades.

        // Inicializa o sistema de animações
        animationSystem = new AnimationSystem();  /// Sistema que gerencia as animações das entidades.
        engine.addSystem(animationSystem);

        // Inicializa os sistemas de física e pulo
        engine.addSystem(new JumpSystem(world));  /// Sistema que gerencia o pulo das entidades.
        engine.addSystem(new PhysicsSystem(world));  /// Sistema que gerencia a física das entidades.
        engine.addSystem(cameraInputSystem);  /// Adiciona o sistema de entrada da câmera.

        // Inicializa o sistema de ataques
        attackSystem = new AttackSystem();  /// Sistema de ataques do jogador e inimigos.
        attackSystem.setEngine(engine);  /// Define a engine para o sistema de ataque.
        engine.addSystem(attackSystem);

        // Inicializa o sistema de saúde
        healthSystem = new HealthSystem();  /// Sistema de saúde para o jogador e inimigos.
      //  healthSystem.setEngine(engine);  /// Define a engine para o sistema de saúde.
        engine.addSystem(healthSystem);

        // Inicializa o sistema de remoção de corpos
        bodyRemovalSystem = new BodyRemovalSystem(world);  /// Sistema que lida com a remoção de corpos no mundo físico.
        engine.addSystem(bodyRemovalSystem);

        // Inicializa o sistema de coleta de itens
        itemCollectionSystem = new ItemCollectionSystem(bodyRemovalSystem);  /// Sistema de coleta de itens.
        engine.addSystem(itemCollectionSystem);

        // Inicializa outros sistemas auxiliares
        engine.addSystem(new ParticleSystem(batch, camera));  /// Sistema de partículas para efeitos visuais.
        engine.addSystem(new CrosshairRenderSystem(batch, camera,assetManager,0.04f));  /// Sistema de renderização da mira do jogador.
        engine.addSystem(new SoundSystem());  /// Sistema que lida com sons e músicas.
        engine.addSystem(new StateSoundSystem(camera));  /// Sistema de sons baseados no estado da câmera.
        engine.addSystem(new AISystem());  /// Sistema de inteligência artificial.
        engine.addSystem(new EnemyHealthBarSystem(camera));  /// Sistema de renderização das barras de vida dos inimigos.
        engine.addSystem(new RenderSpaceshipSystem(batch, camera));  /// Sistema de renderização da nave espacial.
    }
}
