package com.badlogic.UniverseConqueror.Screens;

import com.badlogic.UniverseConqueror.ContactListener.ContactListenerWrapper;
import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.ECS.entity.*;
import com.badlogic.UniverseConqueror.ECS.events.*;
import com.badlogic.UniverseConqueror.ECS.systems.*;
import com.badlogic.UniverseConqueror.GameLauncher;
import com.badlogic.UniverseConqueror.Initializers.*;
import com.badlogic.UniverseConqueror.Pathfinding.*;
import com.badlogic.UniverseConqueror.State.*;
import com.badlogic.UniverseConqueror.Utils.*;
import com.badlogic.ashley.core.*;
import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

public class GameScreen implements Screen {

    /// Referência principal ao launcher do jogo
    private final GameLauncher game;
    /// Engine ECS usada para gerenciar entidades e sistemas
    public PooledEngine engine;
    /// Entidade que representa o jogador
    public Entity player;
    /// Câmera do jogo
    private OrthographicCamera camera;
    /// Mundo físico Box2D
    public World world;
    /// Renderizador de debug do Box2D
    public Box2DDebugRenderer debugRenderer;
    /// Renderizador do mapa isométrico
    public IsometricTiledMapRenderer mapRenderer;
    /// Mapa Tiled carregado
    public TiledMap map;
    /// ShapeRenderer para debug gráfico
    public ShapeRenderer shapeRenderer;
    /// Fonte usada na interface
    public BitmapFont font;
    /// Stage principal da interface
    public Stage stage;
    /// Tabela de rodapé da interface
    public Table footerTable;
    /// Textura da skin da UI
    public Texture uiskinTexture;
    /// Labels da interface: vida, ataque, itens e tempo
    public Label healthLabel, attackPowerLabel, itemsLabel, timerLabel;
    /// Fundos visuais para as labels
    public TextureRegionDrawable healthBackground, attackPowerBackground, itemsBackground;
    /// Skin usada na interface gráfica
    public Skin skin;
    /// Texturas para ícones de câmera ligada/desligada
    public TextureRegion cameraOnTexture, cameraOffTexture;
    /// Imagem usada para mostrar o estado da câmera
    public Image cameraIconImage;
    /// Temporizador de tempo de jogo
    public Timer playingTimer;
    /// Mapper para acessar componente de vida
    public ComponentMapper<HealthComponent> healthMapper;
    /// Mapper para acessar componente de ataque
    public ComponentMapper<AttackComponent> attackMapper;
    /// Sistema de input da câmera
    public CameraInputSystem cameraInputSystem;
    /// Joystick virtual para dispositivos móveis
    public Joystick joystick;
    /// Handler de colisões com o mapa
    public MapCollisionHandler collisionHandler;
    /// Sistema que gerencia a coleta de itens
    private ItemCollectionSystem itemCollectionSystem;
    /// Sistema que gerencia ataques
    public AttackSystem attackSystem;
    /// Sistema que gerencia a vida dos personagens
    private HealthSystem healthSystem;
    /// Coordenadas centrais do mapa
    public float centerX, centerY;
    /// AssetManager para carregar recursos
    public final AssetManager assetManager;
    /// Sistema que remove corpos físicos
    private BodyRemovalSystem bodyRemovalSystem;
    /// Sistema que anima as entidades
    private AnimationSystem animationSystem;
    /// Sistema de input do jogador
    private PlayerInputSystem playerInputSystem;
    /// Flag indicando se o estado foi restaurado
    public boolean restoredState = false;
    /// Serviço que salva e restaura o estado do jogo
    private GameStateService gameStateService;
    /// Construtor de grafo do mapa (para pathfinding)
    private MapGraphBuilder mapGraphBuilder;
    /// Fábrica de projéteis
    private BulletFactory bulletFactory;
    /// Label que mostra o número de inimigos eliminados
    public Label enemiesKilledLabel;
    /// Contador de inimigos eliminados
    private int enemiesKilledCount = 0;
    /// Algoritmo de pathfinding A*
    private AStarPathfinder pathfinder;

    /// Construtor da GameScreen
    public GameScreen(GameLauncher game, AssetManager assetManager) {
        this.game = game;
        this.playingTimer = new Timer(Float.MAX_VALUE);
        this.shapeRenderer = new ShapeRenderer();
        this.engine = new PooledEngine();
        this.assetManager = assetManager;
    }

    /// Método chamado ao exibir o ecrã do jogo
    @Override
    public void show() {
        initializeAssets();
        initializeWorld();
        initializeCamera();
        initializeMapAndPath();
        initializeBulletFactoryAndJoystick();
        initializeGameStateAndPlayer();
        initializeSystems();
        initializeUIAndInput();
        initializeGameState();
        initializePathfindingSystems();
        initializeEnemySystems();
        initializeObservers();
    }

    /// Inicializa o mundo físico
    private void initializeWorld() {
        new WorldInitializer(this).initializeWorld();
    }

    /// Inicializa o grafo do mapa e sistema de path following
    private void initializeMapAndPath() {
        this.mapGraphBuilder = new MapGraphBuilder(map);
        engine.addSystem(new PathFollowSystem());
    }

    /// Inicializa a fábrica de projéteis e joystick virtual
    private void initializeBulletFactoryAndJoystick() {
        bulletFactory = new BulletFactory(assetManager, engine);
        joystick = new Joystick(
            assetManager.get(AssetPaths.JOYSTICK_BASE, Texture.class),
            assetManager.get(AssetPaths.JOYSTICK_KNOB, Texture.class),
            100f, 100f, 60f
        );
    }

    /// Inicializa o estado do jogo e o jogador
    private void initializeGameStateAndPlayer() {
        GameState state = GameStateManager.load();

        if (game.isNewGame() || state == null) {
            game.setNewGame(false);
            player = PlayerInitializer.initializePlayer(this, mapGraphBuilder);
            ItemSpawner itemSpawner = new ItemSpawner(engine, world, assetManager, camera, mapGraphBuilder);
            itemSpawner.initializeItems();
        } else {
            GameStateManager.delete();
        }
    }

    /// Inicializa os sistemas ECS principais
    private void initializeSystems() {
        SystemInitializer systemInitializer = new SystemInitializer(engine, world, camera, map, bulletFactory, joystick, assetManager);
        systemInitializer.initializeSystems();

        this.cameraInputSystem = systemInitializer.cameraInputSystem;
        this.playerInputSystem = systemInitializer.playerInputSystem;
        this.attackSystem = systemInitializer.attackSystem;
        this.healthSystem = systemInitializer.healthSystem;
        this.bodyRemovalSystem = systemInitializer.bodyRemovalSystem;
        this.itemCollectionSystem = systemInitializer.itemCollectionSystem;
        this.animationSystem = systemInitializer.animationSystem;

        engine.addSystem(new RenderSpaceshipSystem(game.batch, camera));
        engine.addSystem(new KnockbackSystem());
    }

    /// Inicializa a interface do utilizador e o sistema de input
    private void initializeUIAndInput() {
        new UIInitializer(this).initialize();
        initializeInputProcessor();
        createContactListener();
    }

    /// Inicializa os observers do padrão Observer
    private void initializeObservers() {
        PauseScreen pauseScreen = new PauseScreen(game, this, assetManager);

        ObserverRegistrar.registerAllObservers(
            game, assetManager, player, healthLabel, attackPowerLabel,
            itemsLabel, itemCollectionSystem, playingTimer,
            enemiesKilledCount, attackSystem, healthSystem, pauseScreen
        );
    }

    /// Restaura estado de save ou reinicia o estado do jogo
    private void initializeGameState() {
        GameState state = GameStateManager.load();

        if (game.isNewGame() || state == null) {
            game.setNewGame(false);
            ItemSpawner itemSpawner = new ItemSpawner(engine, world, assetManager, camera, mapGraphBuilder);
            itemSpawner.initializeItems();
        } else {
            GameStateManager.delete();
        }

        if (state != null && !game.isNewGame()) {
            if (itemCollectionSystem == null) throw new IllegalStateException("ItemCollectionSystem not initialized!");
            itemCollectionSystem.setCollectedCount(state.collectedItemCount);
            itemsLabel.setText("Items: " + state.collectedItemCount);
        }

        gameStateService = new GameStateService(engine, world, assetManager,
            bodyRemovalSystem, attackSystem, itemCollectionSystem,
            playingTimer, camera, playerInputSystem);
        gameStateService.setPlayer(player);
    }

    /// Inicializa os sistemas relacionados ao pathfinding
    private void initializePathfindingSystems() {
        pathfinder = new AStarPathfinder(mapGraphBuilder.nodes);
        engine.addSystem(new PathFollowSystem());
        engine.addSystem(new PathRequestSystem(mapGraphBuilder, pathfinder));
        engine.addSystem(new PathDebugRenderSystem(camera));
    }

    /// Inicializa os sistemas de inimigos e gera entidades
    private void initializeEnemySystems() {
        engine.addSystem(new EnemyCleanupSystem(
            engine, bodyRemovalSystem, animationSystem,
            enemy -> incrementEnemiesKilled()
        ));
        engine.addSystem(new UfoSpawnerSystem(engine, world, assetManager, mapGraphBuilder, player, camera));

        EnemyInitializer enemyInitializer = new EnemyInitializer(engine, world, assetManager, player, camera, mapGraphBuilder);
        Node node = enemyInitializer.initializeSpaceship();
        enemyInitializer.initializeEnemies(node);
    }

    /// Atualiza a HUD com vida, itens e ataque
    private void updateHUD() {
        if (healthMapper.has(player)) {
            HealthComponent health = healthMapper.get(player);
            healthLabel.setText("Health: " + health.currentHealth);
        }
        if (itemCollectionSystem != null) {
            itemsLabel.setText("Items: " + itemCollectionSystem.getCollectedCount());
        }
        if (attackSystem != null) {
            attackPowerLabel.setText("Attack: " + attackSystem.getRemainingAttackPower());
        }
    }

    /// Inicializa o sistema de input via multiplexer
    private void initializeInputProcessor() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(cameraInputSystem.getInputAdapter());
        Gdx.input.setInputProcessor(multiplexer);
    }

    /// Cria o listener de colisões do jogo
    private void createContactListener() {
        ContactListenerWrapper contactListenerWrapper = new ContactListenerWrapper(engine, itemCollectionSystem, bulletFactory);
        contactListenerWrapper.mapContactListener.setOnEndLevel(() -> EventBus.get().notify(new EndGameEvent(player, enemiesKilledCount)));
        world.setContactListener(contactListenerWrapper);
    }

    /// Atualiza a posição da câmera com base no jogador
    private void updateCameraPosition() {
        PositionComponent pos = player.getComponent(PositionComponent.class);
        if (cameraInputSystem.isFollowingPlayer() && pos != null) {
            camera.position.set(pos.position.x, pos.position.y, 0);
        }
        camera.update();
    }

    /// Atualiza os elementos de UI por frame
    private void updateUI(float delta) {
        stage.act();
        stage.draw();
        updateTimer(delta);
        updateCameraIcon();
        updateCameraPosition();
    }

    /// Atualiza o ícone de estado da câmera
    private void updateCameraIcon() {
        cameraIconImage.setDrawable(new TextureRegionDrawable(
            cameraInputSystem.isFollowingPlayer() ? cameraOnTexture : cameraOffTexture
        ));
    }

    /// Atualiza o temporizador da UI
    private void updateTimer(float delta) {
        playingTimer.update(delta);
        float elapsed = playingTimer.getTime();
        int hours = (int) (elapsed / 3600);
        int minutes = (int) ((elapsed % 3600) / 60);
        int seconds = (int) (elapsed % 60);
        timerLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
    }

    /// Método principal de renderização
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        world.step(1 / 60f, 6, 2);

        updateUI(delta);
        updateHUD();

        renderWorld();
        stage.draw();
    }

    /// Inicializa a câmera
    private void initializeCamera() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 1.0f;
    }

    /// Inicializa os recursos visuais da HUD
    private void initializeAssets() {
        cameraOnTexture = new TextureRegion(assetManager.get(AssetPaths.CAMERA_ON_ICON, Texture.class));
        cameraOffTexture = new TextureRegion(assetManager.get(AssetPaths.CAMERA_OFF_ICON, Texture.class));
    }

    /// Renderiza o mundo e executa os sistemas ECS
    private void renderWorld() {
        mapRenderer.setView(camera);
        mapRenderer.render();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            gameStateService.saveGameState();
            EventBus.get().notify(new PauseEvent());
            return;
        }

        engine.update(Gdx.graphics.getDeltaTime());
        debugRenderer.render(world, camera.combined);
    }

    /// Incrementa e atualiza o contador de inimigos eliminados
    public void incrementEnemiesKilled() {
        enemiesKilledCount++;
        if (enemiesKilledLabel != null) {
            enemiesKilledLabel.setText(String.valueOf(enemiesKilledCount));
        }
    }

    @Override public void resize(int width, int height) { camera.update(); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    /// Liberta os recursos usados pelo ecrã
    @Override
    public void dispose() {
        map.dispose();
        mapRenderer.dispose();
        debugRenderer.dispose();
        shapeRenderer.dispose();
        assetManager.dispose();
    }
}
