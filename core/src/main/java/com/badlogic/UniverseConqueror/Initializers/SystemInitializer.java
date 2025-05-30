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

public class SystemInitializer {

    /// Engine ECS que gerencia todas as entidades e sistemas
    private final PooledEngine engine;
    /// Mundo físico Box2D
    private final World world;
    /// Câmera ortográfica usada na renderização
    private final OrthographicCamera camera;
    /// Mapa do jogo
    private final TiledMap map;
    /// Fábrica de projéteis
    private final BulletFactory bulletFactory;
    /// Joystick virtual usado para input do jogador
    private final Joystick joystick;
    /// AssetManager para carregar recursos
    private final AssetManager assetManager;

    /// Sistemas usados no jogo, expostos para serem acessados externamente após inicialização
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

    /// Construtor da classe que recebe todas as dependências necessárias
    public SystemInitializer(PooledEngine engine, World world, OrthographicCamera camera, TiledMap map,
                             BulletFactory bulletFactory, Joystick joystick, AssetManager assetManager) {
        this.engine = engine;
        this.world = world;
        this.camera = camera;
        this.map = map;
        this.bulletFactory = bulletFactory;
        this.joystick = joystick;
        this.assetManager = assetManager;
    }

    /// Inicializa e registra todos os sistemas ECS usados no jogo
    public void initializeSystems() {
        SpriteBatch batch = new SpriteBatch();

        /// Calcula as dimensões totais do mapa em pixels
        int mapWidth = map.getProperties().get("width", Integer.class) * map.getProperties().get("tilewidth", Integer.class);
        int mapHeight = map.getProperties().get("height", Integer.class) * map.getProperties().get("tileheight", Integer.class);

        /// Sistemas de renderização e movimentação de câmera
        engine.addSystem(new CameraSystem(camera, mapWidth, mapHeight));
        engine.addSystem(new RenderSystem(batch, camera));
        engine.addSystem(new UfoRenderSystem(batch, camera));

        /// Sistemas relacionados ao input e movimentação do jogador
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

        /// Sistema de animações
        animationSystem = new AnimationSystem();
        engine.addSystem(animationSystem);

        /// Sistemas de física e pulo
        engine.addSystem(new JumpSystem(world));
        engine.addSystem(new PhysicsSystem(world));
        engine.addSystem(cameraInputSystem);

        /// Sistema de ataque
        attackSystem = new AttackSystem();
        attackSystem.setEngine(engine);
        engine.addSystem(attackSystem);

        /// Sistema de vida
        healthSystem = new HealthSystem();
        healthSystem.setEngine(engine);
        engine.addSystem(healthSystem);

        /// Sistema de remoção de corpos físicos
        bodyRemovalSystem = new BodyRemovalSystem(world);
        engine.addSystem(bodyRemovalSystem);

        /// Sistema de coleta de itens
        itemCollectionSystem = new ItemCollectionSystem(bodyRemovalSystem);
        engine.addSystem(itemCollectionSystem);

        /// Outros sistemas visuais e auxiliares
        engine.addSystem(new ParticleSystem(batch, camera));
        engine.addSystem(new CrosshairRenderSystem(batch, camera, assetManager));
        engine.addSystem(new SoundSystem());
        engine.addSystem(new StateSoundSystem(camera));
        engine.addSystem(new AISystem());
        engine.addSystem(new EnemyHealthBarSystem(camera));
    }
}
