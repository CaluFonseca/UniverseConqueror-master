package com.badlogic.UniverseConqueror.Screens;

import com.badlogic.UniverseConqueror.Audio.MusicManager;
import com.badlogic.UniverseConqueror.Audio.SoundManager;
import com.badlogic.UniverseConqueror.ContactListener.ContactListenerWrapper;
import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.ECS.entity.*;
import com.badlogic.UniverseConqueror.ECS.events.*;
import com.badlogic.UniverseConqueror.ECS.observers.*;
import com.badlogic.UniverseConqueror.ECS.systems.*;
import com.badlogic.UniverseConqueror.GameLauncher;
import com.badlogic.UniverseConqueror.Pathfinding.*;
import com.badlogic.UniverseConqueror.State.*;
import com.badlogic.UniverseConqueror.Utils.*;
import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import java.util.ArrayList;

public class GameScreen implements Screen {

    /// Referência ao launcher do jogo para trocar telas
    private final GameLauncher game;

    /// Engine ECS com pooling para gerenciar entidades
    private PooledEngine engine;

    /// Referência para o jogador
    private Entity player;

    /// Câmera ortográfica usada para renderização
    private OrthographicCamera camera;

    /// Mundo físico Box2D
    private World world;

    /// Renderizador de debug do Box2D
    private Box2DDebugRenderer debugRenderer;

    /// Renderizador do mapa isométrico TMX
    private IsometricTiledMapRenderer mapRenderer;

    /// Referência ao mapa TMX carregado
    private TiledMap map;

    /// Usado para renderizar formas (ex: debug, barras de vida)
    private ShapeRenderer shapeRenderer;

    /// Fonte para textos UI
    private BitmapFont font;

    /// Cena UI para menus, botões, labels
    private Stage stage;

    /// Tabela do footer para mostrar UI na parte inferior
    private Table footerTable;

    /// Textura base da skin UI
    private Texture uiskinTexture;

    /// Labels para mostrar vida, ataque, itens coletados e tempo
    private Label healthLabel, attackPowerLabel, itemsLabel, timerLabel;

    /// Fundo para as caixas da UI
    private TextureRegionDrawable healthBackground, attackPowerBackground, itemsBackground;

    /// Skin para os widgets UI
    private Skin skin;

    /// Texturas para os ícones da câmera ligada e desligada
    private TextureRegion cameraOnTexture, cameraOffTexture;

    /// Imagem do ícone da câmera para troca de status visual
    private Image cameraIconImage;

    /// Timer para controle do tempo de jogo
    private Timer playingTimer;

    /// ComponentMappers para componentes frequentes
    private ComponentMapper<HealthComponent> healthMapper;
    private ComponentMapper<AttackComponent> attackMapper;

    /// Sistema para controlar input da câmera
    private CameraInputSystem cameraInputSystem;

    /// Joystick virtual na tela para controle móvel
    private Joystick joystick;

    /// Handler para colisões do mapa e criação de corpos físicos
    private MapCollisionHandler collisionHandler;

    /// Sistema para coleta de itens
    private ItemCollectionSystem itemCollectionSystem;

    /// Sistema de ataque do jogador
    private AttackSystem attackSystem;

    /// Sistema de vida e dano
    private HealthSystem healthSystem;

    /// Sistema para gerenciar balas/projéteis
    private BulletSystem bulletSystem;

    /// Sistema para renderizar balas
    private BulletRenderSystem bulletRenderSystem;

    /// Coordenadas centrais para spawn inicial
    private float centerX, centerY;

    /// Sistema para mover balas/projéteis
    private BulletMovementSystem bulletMovementSystem;

    /// Gerenciador de assets (texturas, sons, etc)
    private final AssetManager assetManager;

    /// Sistema para remoção segura de corpos Box2D
    private BodyRemovalSystem bodyRemovalSystem;

    /// Sistema de animações para entidades
    private AnimationSystem animationSystem;

    /// Sistema para input do jogador
    private PlayerInputSystem playerInputSystem;

    /// Flag para indicar se o estado foi restaurado de um save
    private boolean restoredState = false;

    /// Serviço que centraliza lógica de salvar/restaurar estado
    private GameStateService gameStateService;

    /// Builder do grafo do mapa para pathfinding
    private MapGraphBuilder mapGraphBuilder;

    /// Fábrica para criar balas e projéteis
    private BulletFactory bulletFactory;

    /// Label para mostrar inimigos mortos
    private Label enemiesKilledLabel;

    /// Contador de inimigos mortos
    private int enemiesKilledCount = 0;

    /// Pathfinder para encontrar caminhos A*
    private AStarPathfinder pathfinder;

    /// Construtor principal que inicializa recursos principais
    public GameScreen(GameLauncher game, AssetManager assetManager) {
        this.game = game;
        this.playingTimer = new Timer(Float.MAX_VALUE);
        this.shapeRenderer = new ShapeRenderer();
        this.engine = new PooledEngine();
        this.assetManager = assetManager;
    }

    /// Método chamado quando a tela é mostrada, inicializa tudo
    @Override
    public void show() {
        initializeAssets();
        initializeWorld();
        initializeCamera();

        this.mapGraphBuilder = new MapGraphBuilder(map);
        engine.addSystem(new PathFollowSystem());

        GameState state = GameStateManager.load();

        /// Se for um novo jogo ou estado salvo não existe, inicializa player e itens
        if (game.isNewGame() || state == null) {
            game.setNewGame(false);
            initializePlayer();
            initializeItems();
        } else {
            // Se quiser restaurar o estado, poderia chamar restoreState(state) aqui
            GameStateManager.delete();
        }

        bulletFactory = new BulletFactory(assetManager, engine);

        engine.addSystem(new RenderSpaceshipSystem(game.batch, camera));

        initializeUI();
        initializeSystems();
        initializeInputProcessor();
        createContactListener();

        /// Se existe estado salvo, atualiza contagem de itens coletados na UI
        if (state != null && !game.isNewGame()) {
            itemCollectionSystem.setCollectedCount(state.collectedItemCount);
            if (itemCollectionSystem == null) {
                throw new IllegalStateException("ItemCollectionSystem não foi inicializado ainda!");
            }
            itemsLabel.setText("Items: " + state.collectedItemCount);
        }

        mapGraphBuilder = new MapGraphBuilder(map);
        pathfinder = new AStarPathfinder(mapGraphBuilder.nodes);

        engine.addSystem(new PathRequestSystem(mapGraphBuilder, pathfinder));

        engine.addSystem(new EnemyCleanupSystem(
            engine,
            bodyRemovalSystem,
            animationSystem,
            enemy -> incrementEnemiesKilled()
        ));

        engine.addSystem(new PathDebugRenderSystem(camera));

        engine.addSystem(new UfoSpawnerSystem(engine, world, assetManager, mapGraphBuilder, player, camera));

        gameStateService = new GameStateService(engine, world, assetManager,
            bodyRemovalSystem, attackSystem, itemCollectionSystem,
            playingTimer, camera, playerInputSystem);
        gameStateService.setPlayer(player);

        engine.addSystem(new KnockbackSystem());

        initializeaddObservers();
    }

    /// Inicializa os observers para eventos do jogo
    private void initializeaddObservers(){
        EventBus.get().addObserver(new UIObserver(healthLabel, attackPowerLabel,itemsLabel));
        GameOverObserver observer = new GameOverObserver(game, assetManager, player);
        EventBus.get().addObserver(observer);
        EventBus.get().addObserver(new EndGameObserver(game, assetManager, player, itemCollectionSystem, playingTimer, () -> enemiesKilledCount));
        PauseScreen pauseScreen = new PauseScreen(game, this, assetManager);
        EventBus.get().addObserver(new PauseObserver(game, pauseScreen));
        EventBus.get().addObserver(new SoundObserver(SoundManager.getInstance()));
        EventBus.get().addObserver(new ItemCollectedObserver(attackSystem, healthSystem));
    }

    /// Configura múltiplos processadores de input (UI + câmera)
    private void initializeInputProcessor() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(cameraInputSystem.getInputAdapter());
        Gdx.input.setInputProcessor(multiplexer);
    }

    /// Cria o listener para detectar colisões e disparar eventos
    private void createContactListener() {
        world.setContactListener(new ContactListenerWrapper(engine, itemCollectionSystem, bulletFactory));

        ContactListenerWrapper contactListenerWrapper = new ContactListenerWrapper(engine, itemCollectionSystem, bulletFactory);
        contactListenerWrapper.mapContactListener.setOnEndLevel(() -> {
            EventBus.get().notify(new EndGameEvent(player,enemiesKilledCount));
        });
        world.setContactListener(contactListenerWrapper);
    }

    /// Atualiza a posição da câmera para seguir o jogador se configurado
    private void updateCameraPosition() {
        PositionComponent pos = player.getComponent(PositionComponent.class);
        if (cameraInputSystem.isFollowingPlayer() && pos != null) {
            camera.position.set(pos.position.x, pos.position.y, 0);
        }
        camera.update();
    }

    /// Atualiza a UI, timers e ícones a cada frame
    private void updateUI(float delta) {
        stage.act();
        stage.draw();
        updateTimer(delta);
        updateCameraIcon();
        updateCameraPosition();
    }

    /// Alterna ícone da câmera ligado/desligado
    private void updateCameraIcon() {
        if (cameraInputSystem.isFollowingPlayer()) {
            cameraIconImage.setDrawable(new TextureRegionDrawable(cameraOnTexture));
        } else {
            cameraIconImage.setDrawable(new TextureRegionDrawable(cameraOffTexture));
        }
    }

    /// Atualiza label do timer de jogo
    private void updateTimer(float delta) {
        playingTimer.update(delta);
        float elapsed = playingTimer.getTime();
        int hours = (int) (elapsed / 3600);
        int minutes = (int) ((elapsed % 3600) / 60);
        int seconds = (int) (elapsed % 60);
        String timeFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        timerLabel.setText(timeFormatted);
    }

    /// Método principal de renderização da tela
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        world.step(1 / 60f, 6, 2);

        updateUI(delta);

        // Atualiza labels com dados atuais
        HealthComponent health = healthMapper.get(player);
        if (health != null) {
            healthLabel.setText("Health: " + health.currentHealth);
            itemsLabel.setText("Items: " + itemCollectionSystem.getCollectedCount());
            attackPowerLabel.setText("Attack: " + attackSystem.getRemainingAttackPower());
        }

        renderWorld();

        stage.draw();
    }

    /// Cria uma fábrica de itens em posições aleatórias no mapa
    private ItemFactory createItem(String tipo, String assetPath) {
        Node node = mapGraphBuilder.getRandomWalkableNode();
        Vector2 worldPos = mapGraphBuilder.toWorldPosition(node);
        return new ItemFactory(tipo, worldPos.x, worldPos.y, assetPath, assetManager);
    }

    /// Inicializa vários itens no mapa
    private void initializeItems() {
        ArrayList<ItemFactory> items = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            engine.addEntity(createItem("Vida", AssetPaths.ITEM_VIDA).createEntity(engine, world));
            engine.addEntity(createItem("Ataque", AssetPaths.ITEM_ATAQUE).createEntity(engine, world));
            engine.addEntity(createItem("SuperAtaque", AssetPaths.ITEM_SUPER_ATAQUE).createEntity(engine, world));
        }

        SpriteBatch batchItem = new SpriteBatch();
        engine.addSystem(new RenderItemSystem(batchItem, camera));
        initializeSpaceship();
    }

    /// Inicializa a UI, labels, joystick e ícone da câmera
    private void initializeUI() {
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.None);
        skin = assetManager.get(AssetPaths.UI_SKIN_JSON, Skin.class);
        font = new BitmapFont();

        footerTable = new Table();
        footerTable.bottom().right();
        footerTable.setFillParent(true);

        stage = new Stage(new ScreenViewport());
        stage.addActor(footerTable);

        initializeLabels();

        timerLabel = new Label("00:00:00", skin);
        timerLabel.setFontScale(2f);
        Table timerTable = new Table();
        timerTable.top().setFillParent(true);
        timerTable.add(timerLabel).expandX().center();
        if (!restoredState){ playingTimer.start(); }
        stage.addActor(timerTable);

        // Configuração do joystick virtual
        Texture base = assetManager.get(AssetPaths.JOYSTICK_BASE, Texture.class);
        Texture knob = assetManager.get(AssetPaths.JOYSTICK_KNOB, Texture.class);
        joystick = new Joystick(base,knob, 100f, 100f, 60f);
        stage.addActor(joystick);

        // Ícone da câmera com listener para alternar estado
        cameraIconImage = new Image(cameraOnTexture);
        Table uiTable = new Table();
        uiTable.top().left();
        uiTable.setFillParent(true);
        uiTable.add(cameraIconImage).pad(10).size(48);

        cameraIconImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Texture cameraOnTexture = assetManager.get(AssetPaths.CAMERA_ON_ICON, Texture.class);
                Texture cameraOffTexture = assetManager.get(AssetPaths.CAMERA_OFF_ICON, Texture.class);
                cameraIconImage.setDrawable(new TextureRegionDrawable(new TextureRegion(
                    cameraInputSystem.isFollowingPlayer() ? cameraOnTexture : cameraOffTexture
                )));
            }
        });
        stage.addActor(uiTable);

        // UI para contador de inimigos mortos
        Image killedCounterImage = new Image(new Texture("Killed_alien_counter.png"));
        enemiesKilledLabel = new Label("0", skin);
        enemiesKilledLabel.setFontScale(0.7f);
        enemiesKilledLabel.setAlignment(Align.center);

        Stack killsStack = new Stack();
        killsStack.add(killedCounterImage);
        killsStack.add(enemiesKilledLabel);

        Table killsTable = new Table();
        killsTable.top().right();
        killsTable.setFillParent(true);
        killsTable.add(killsStack).size(50, 60).pad(10);

        stage.addActor(killsTable);
    }

    /// Incrementa contador de inimigos mortos e atualiza label
    public void incrementEnemiesKilled() {
        enemiesKilledCount++;
        if (enemiesKilledLabel != null) {
            enemiesKilledLabel.setText(String.valueOf(enemiesKilledCount));
        }
    }

    /// Inicializa labels e caixas da UI do footer
    private void initializeLabels() {
        healthMapper = ComponentMapper.getFor(HealthComponent.class);
        HealthComponent healthComponent = healthMapper.get(player);

        healthLabel = new Label("Health: " + healthComponent.currentHealth, skin);
        attackPowerLabel = new Label("Attack: " , skin);
        itemsLabel = new Label("Items: 0", skin);

        uiskinTexture = new Texture("ui/uiskin.png");
        healthBackground = new TextureRegionDrawable(new TextureRegion(uiskinTexture, 0, 80, 190, 75));
        attackPowerBackground = new TextureRegionDrawable(new TextureRegion(uiskinTexture, 0, 80, 190, 75));
        itemsBackground = new TextureRegionDrawable(new TextureRegion(uiskinTexture, 0, 80, 190, 75));

        Table healthBox = new Table();
        healthBox.setBackground(healthBackground);
        healthBox.add(healthLabel).pad(5);

        Table attackBox = new Table();
        attackBox.setBackground(attackPowerBackground);
        attackBox.add(attackPowerLabel).pad(5);

        Table itemsBox = new Table();
        itemsBox.setBackground(itemsBackground);
        itemsBox.add(itemsLabel).pad(5);

        footerTable.center();
        footerTable.bottom();
        footerTable.add(healthBox).pad(10).left();
        footerTable.add(attackBox).pad(10).left();
        footerTable.add(itemsBox).pad(10).left();
    }

    /// Inicializa a câmera do jogo
    private void initializeCamera() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 1.0f;
    }

    /// Carrega as texturas dos ícones da câmera
    private void initializeAssets() {
        cameraOnTexture = new TextureRegion(assetManager.get(AssetPaths.CAMERA_ON_ICON, Texture.class));
        cameraOffTexture = new TextureRegion(assetManager.get(AssetPaths.CAMERA_OFF_ICON, Texture.class));
    }

    /// Inicializa o mundo físico e o mapa com colisões
    private void initializeWorld() {
        world = new World(new Vector2(0, -9.8f), true);
        debugRenderer = new Box2DDebugRenderer();
        map = new TmxMapLoader().load("mapa.tmx");
        mapRenderer = new IsometricTiledMapRenderer(map);
        this.collisionHandler = new MapCollisionHandler(map, "Collisions", "Jumpable");
        collisionHandler.createBox2DBodies(world);
        shapeRenderer = new ShapeRenderer();
    }

    /// Inicializa o jogador em uma posição walkable próxima ao centro do mapa
    private void initializePlayer() {
        int mapWidth = mapGraphBuilder.getWidth();
        int mapHeight = mapGraphBuilder.getHeight();

        Node spawnNode = mapGraphBuilder.nodes[mapWidth / 2][mapHeight / 2];
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
        Vector2 spawnPosition = mapGraphBuilder.toWorldPosition(spawnNode);
        this.centerX = spawnPosition.x + 5;
        this.centerY = spawnPosition.y + 5;
        player = PlayerFactory.createPlayer(engine, spawnPosition, world, assetManager);
        engine.addEntity(player);
    }

    /// Inicializa a nave espacial (objetivo) e os inimigos em posições ao redor
    private void initializeSpaceship() {
        Node node = mapGraphBuilder.getRandomWalkableNode();
        Vector2 worldPos = mapGraphBuilder.toWorldPosition(node);

        SpaceshipFactory spaceshipFactory = new SpaceshipFactory(assetManager);
        spaceshipFactory.createSpaceship(worldPos, engine, world);

        // Deslocamentos para formar um hexágono aproximado ao redor da nave
        int[][] offsets = new int[][] {
            {-1,  1}, // cima-esquerda
            { 1,  1}, // cima-direita
            { 2,  0}, // direita
            { 1, -1}, // baixo-direita
            {-1, -1}, // baixo-esquerda
            {-2,  0}  // esquerda
        };

        for (int[] offset : offsets) {
            Node patrolStartNode = mapGraphBuilder.findNearestWalkableOffset(node, offset[0], offset[1]);
            Node patrolEndNode = mapGraphBuilder.findNearestWalkableOffset(node, offset[0] * 2, offset[1] * 2);

            if (patrolStartNode != null && patrolEndNode != null) {
                Vector2 patrolStartWorld = mapGraphBuilder.toWorldPosition(patrolStartNode);
                Vector2 patrolEndWorld = mapGraphBuilder.toWorldPosition(patrolEndNode);

                Entity enemy = EnemyFactory.createPatrollingEnemy(
                    engine,
                    world,
                    patrolStartWorld,
                    assetManager,
                    player,
                    camera,
                    patrolStartWorld,
                    patrolEndWorld
                );

                engine.addEntity(enemy);
            }
        }
    }

    /// Inicializa os sistemas ECS e adiciona à engine
    private void initializeSystems() {
        SpriteBatch batch = new SpriteBatch();

        engine.addSystem(new CameraSystem(camera,
            map.getProperties().get("width", Integer.class) * map.getProperties().get("tilewidth", Integer.class),
            map.getProperties().get("height", Integer.class) * map.getProperties().get("tileheight", Integer.class)));

        engine.addSystem(new RenderSystem(batch, camera));
        engine.addSystem(new UfoRenderSystem(batch, camera));

        cameraInputSystem = new CameraInputSystem(camera);
        bulletSystem = new BulletSystem(camera, assetManager, engine);
        bulletRenderSystem = new BulletRenderSystem(batch);
        bulletMovementSystem = new BulletMovementSystem();
        playerInputSystem = new PlayerInputSystem(world, joystick, bulletSystem, camera, engine, bulletFactory);

        engine.addSystem(bulletMovementSystem);
        engine.addSystem(bulletSystem);
        engine.addSystem(playerInputSystem);
        engine.addSystem(new MovementSystem());
        engine.addSystem(new StateSystem());

        AnimationSystem animationSystem = new AnimationSystem();
        engine.addSystem(animationSystem);
        this.animationSystem = animationSystem;

        engine.addSystem(new JumpSystem(world));
        engine.addSystem(new PhysicsSystem(world));
        engine.addSystem(cameraInputSystem);

        attackSystem = new AttackSystem();
        attackSystem.setEngine(engine);
        healthSystem = new HealthSystem();
        healthSystem.setEngine(engine);
        bodyRemovalSystem = new BodyRemovalSystem(world);

        engine.addSystem(bodyRemovalSystem);
        engine.addSystem(attackSystem);

        itemCollectionSystem = new ItemCollectionSystem(bodyRemovalSystem);
        engine.addSystem(itemCollectionSystem);
        engine.addSystem(new HealthSystem());
        engine.addSystem(bulletRenderSystem);
        engine.addSystem(new ParticleSystem(batch, camera));
        engine.addSystem(new CrosshairRenderSystem(batch, camera, assetManager));
        engine.addSystem(new SoundSystem());
        engine.addSystem(new StateSoundSystem(camera));
        engine.addSystem(new AISystem());
        engine.addSystem(new EnemyHealthBarSystem(camera));
    }

    /// Renderiza o mapa e atualiza o motor ECS
    private void renderWorld() {
        mapRenderer.setView(camera);
        mapRenderer.render();

        // Pausa o jogo ao pressionar ESC
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            gameStateService.saveGameState();
            EventBus.get().notify(new PauseEvent());
            return;
        }

        engine.update(Gdx.graphics.getDeltaTime());

        debugRenderer.render(world, camera.combined);
    }

    /// Método para restaurar o estado do jogo salvo (posição, itens, vida, etc)
    public void restoreState(GameState state) {
        SoundManager.getInstance().stop();
        MusicManager.getInstance().stop();

        if (player != null) {
            engine.removeEntity(player);
        }

        // Remove entidades spaceship existentes
        ImmutableArray<Entity> existingSpaceships = engine.getEntitiesFor(Family.all(EndLevelComponent.class).get());
        for (Entity spaceship : existingSpaceships) {
            engine.removeEntity(spaceship);
        }

        // Cria nova spaceship na posição salva
        if (state.spaceshipPosition != null) {
            SpaceshipFactory factory = new SpaceshipFactory(assetManager);
            factory.createSpaceship(state.spaceshipPosition, engine, world);
        }

        // Cria novo player na posição salva
        player = PlayerFactory.createPlayer(engine, state.playerPosition, world, assetManager);

        PositionComponent pos = player.getComponent(PositionComponent.class);
        if (pos != null) pos.position.set(state.playerPosition);

        BodyComponent body = player.getComponent(BodyComponent.class);
        if (body != null && body.body != null) {
            body.body.setTransform(state.playerPosition.x, state.playerPosition.y, 0f);
            body.body.setLinearVelocity(0, 0);
            body.body.setAwake(true);
        }

        // Restaura vida do jogador
        HealthComponent health = player.getComponent(HealthComponent.class);
        if (health != null) health.currentHealth = state.playerHealth;

        // TODO: restaurar ataque, se for implementado

        // Garante que o player está na engine
        if (!engine.getEntities().contains(player, true)) {
            engine.addEntity(player);
        }

        // Atualiza referência do player no sistema de input
        if (playerInputSystem != null) {
            playerInputSystem.setPlayer(player);
        }

        // Remove todos os itens atuais
        ImmutableArray<Entity> currentItems = engine.getEntitiesFor(Family.all(ItemComponent.class).get());
        for (Entity e : currentItems) {
            engine.removeEntity(e);
        }

        // Restaura os itens não coletados salvos
        for (SavedItemData data : state.remainingItems) {
            Entity restored = data.createEntity(engine, world, assetManager);
            engine.addEntity(restored);
        }

        restoredState = true;

        SpriteBatch batchItem = new SpriteBatch();
        engine.addSystem(new RenderItemSystem(batchItem, camera));

        // Atualiza contagem de itens coletados e timer
        itemCollectionSystem.setCollectedCount(state.collectedItemCount);
        playingTimer.setTime(state.gameTime);
    }

    /// Atualiza viewport da câmera ao redimensionar
    @Override
    public void resize(int width, int height) {
        camera.update();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    /// Libera recursos usados pela tela
    @Override
    public void dispose() {
        map.dispose();
        mapRenderer.dispose();
        debugRenderer.dispose();
        shapeRenderer.dispose();
        assetManager.dispose();
    }
}
