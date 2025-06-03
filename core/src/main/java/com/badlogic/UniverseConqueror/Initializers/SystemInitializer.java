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

    private final PooledEngine engine;
    private final World world;
    private final OrthographicCamera camera;
    private final TiledMap map;
    private final BulletFactory bulletFactory;
    private final Joystick joystick;
    private final AssetManager assetManager;

    // Sistemas que serão inicializados
    public CameraInputSystem cameraInputSystem;
    public PlayerInputSystem playerInputSystem;
    public BulletSystem bulletSystem;
    public BulletRenderSystem bulletRenderSystem;
    public BulletMovementSystem bulletMovementSystem;
    public AttackSystem attackSystem;
    public HealthSystem healthSystem;
    public BodyRemovalSystem bodyRemovalSystem;
    public ItemCollectionSystem itemCollectionSystem;
    public AnimationSystem animationSystem;

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
        super(context);
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
        SpriteBatch batch = new SpriteBatch();

        // Calcula as dimensões do mapa em pixels
        int mapWidth = map.getProperties().get("width", Integer.class) * map.getProperties().get("tilewidth", Integer.class);  // Largura do mapa em pixels.
        int mapHeight = map.getProperties().get("height", Integer.class) * map.getProperties().get("tileheight", Integer.class);  // Altura do mapa em pixels.

        // Inicializa sistemas de renderização e movimentação da câmera
        engine.addSystem(new CameraSystem(camera, mapWidth, mapHeight));
        engine.addSystem(new RenderSystem(batch, camera));
        engine.addSystem(new UfoRenderSystem(batch, camera));

        // Inicializa sistemas de entrada e movimentação do jogador
        cameraInputSystem = new CameraInputSystem(camera);
        bulletSystem = new BulletSystem(world, camera, assetManager, engine);
        bulletRenderSystem = new BulletRenderSystem(batch);
        bulletMovementSystem = new BulletMovementSystem();
        playerInputSystem = new PlayerInputSystem(world, joystick, bulletSystem, camera, engine, bulletFactory);

        engine.addSystem(bulletMovementSystem);
        engine.addSystem(bulletSystem);
        engine.addSystem(bulletRenderSystem);
        engine.addSystem(playerInputSystem);
        engine.addSystem(new MovementSystem());
        engine.addSystem(new StateSystem());

        // Inicializa o sistema de animações
        animationSystem = new AnimationSystem();
        engine.addSystem(animationSystem);

        // Inicializa os sistemas de física e pulo
        engine.addSystem(new JumpSystem(world));
        engine.addSystem(new PhysicsSystem(world));
        engine.addSystem(cameraInputSystem);

        // Inicializa o sistema de ataques
        attackSystem = new AttackSystem();
        attackSystem.setEngine(engine);
        engine.addSystem(attackSystem);

        // Inicializa o sistema de saúde
        healthSystem = new HealthSystem();
        engine.addSystem(healthSystem);

        // Inicializa o sistema de remoção de corpos
        bodyRemovalSystem = new BodyRemovalSystem(world);
        engine.addSystem(bodyRemovalSystem);

        // Inicializa o sistema de coleta de itens
        itemCollectionSystem = new ItemCollectionSystem(bodyRemovalSystem);
        engine.addSystem(itemCollectionSystem);

        // Inicializa outros sistemas auxiliares
        engine.addSystem(new ParticleSystem(batch, camera));
        engine.addSystem(new CrosshairRenderSystem(batch, camera,assetManager,0.04f));
        engine.addSystem(new SoundSystem());
        engine.addSystem(new StateSoundSystem(camera));
        engine.addSystem(new AISystem());
        engine.addSystem(new EnemyHealthBarSystem(camera));
        engine.addSystem(new RenderSpaceshipSystem(batch, camera));
    }
}
