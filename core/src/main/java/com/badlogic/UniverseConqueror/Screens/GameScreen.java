package com.badlogic.UniverseConqueror.Screens;

import com.badlogic.UniverseConqueror.ContactListener.ContactListenerWrapper;
import com.badlogic.UniverseConqueror.Context.GameContext;
import com.badlogic.UniverseConqueror.Context.SystemContext;
import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.ECS.entity.*;
import com.badlogic.UniverseConqueror.ECS.events.EndGameEvent;
import com.badlogic.UniverseConqueror.ECS.events.EventBus;
import com.badlogic.UniverseConqueror.ECS.events.PauseEvent;
import com.badlogic.UniverseConqueror.ECS.systems.*;
import com.badlogic.UniverseConqueror.GameLauncher;
import com.badlogic.UniverseConqueror.Initializers.*;
import com.badlogic.UniverseConqueror.Interfaces.BaseScreen;
import com.badlogic.UniverseConqueror.Interfaces.Spawner;
import com.badlogic.UniverseConqueror.Pathfinding.*;
import com.badlogic.UniverseConqueror.Spawner.*;
import com.badlogic.UniverseConqueror.State.*;
import com.badlogic.UniverseConqueror.Utils.*;
import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.ashley.core.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class GameScreen implements Screen, BaseScreen {

    private final GameLauncher game;
    private final AssetManager assetManager;
    private GameContext gameContext;
    private PooledEngine engine;
    private Entity player;
    private AStarPathfinder pathfinder;

    private OrthographicCamera camera;
    private Timer playingTimer;
    private GameStateService gameStateService;
    public void disposeResources() {
        dispose();
    }

    public GameScreen(GameLauncher game, AssetManager assetManager) {
        this.game = game;
        this.assetManager = assetManager;
    }

    @Override
    public void show() {
        initializeEssentials();
        initializeContext();
        initializeWorld();
        initializeBulletFactory();
        initializeGraph();
        createJoystick();
        initializeSystems();

        initializeGameState(); // create gameStateService

        // Ensure the gameStateService is loaded properly
        if (!gameStateService.loadGameStateFromJson()) {
            initializePlayerAndItems(); // Create player
            gameStateService.setPlayer(player); // Update player in GameStateService
            initializePathfinding();
            initializeEnemies();
            initializeCameraEntity();
        } else {
            // Restoring game state
            Entity restoredPlayer = gameStateService.getPlayer();
            gameContext.setPlayer(restoredPlayer);
            player = restoredPlayer;
            gameStateService.setPlayer(player); // Ensure the player is updated
            pathfinder = gameContext.getPathfinder();
        }

        initializeUI();
        initializeInput();
        registerObservers();
        createContactListener();
    }
    @Override
    public void registerObservers() {
        new ObserverRegistrar(gameContext).initialize();
    }

    private void createJoystick() {
        Texture base = assetManager.get(AssetPaths.JOYSTICK_BASE, Texture.class);
        Texture knob = assetManager.get(AssetPaths.JOYSTICK_KNOB, Texture.class);
        Joystick joystick = new Joystick(base, knob, 100f, 100f, 60f);
        gameContext.setJoystick(joystick); // para o sistema usar
    }
    /// Cria o listener de colisões do jogo
    private void createContactListener() {
        ContactListenerWrapper contactListenerWrapper = new ContactListenerWrapper(
            engine,
            gameContext.getSystemContext().getItemCollectionSystem(),
            gameContext.getBulletFactory()
        );
        contactListenerWrapper.setOnEndLevel(() -> EventBus.get().notify(new EndGameEvent(player, gameContext.getEnemiesKilledCount())));
        gameContext.getWorldContext().getWorld().setContactListener(contactListenerWrapper);
    }

    private void initializeEssentials() {
        engine = new PooledEngine();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        playingTimer = new Timer(Float.MAX_VALUE);
        gameContext = new GameContext(game, this, engine, assetManager, playingTimer, camera);
    }

    private void initializeContext() {
        new WorldInitializer(gameContext, this).initialize();
    }

    private void initializeWorld() {
        gameContext.getWorldContext().setWorld(gameContext.getWorldContext().getWorld());
    }

    private void initializeBulletFactory() {
        BulletFactory bulletFactory = new BulletFactory(assetManager, engine);
        gameContext.setBulletFactory(bulletFactory);
    }

    private void initializeGraph() {
        MapGraphBuilder builder = new MapGraphBuilder(gameContext.getWorldContext().getMap());
        gameContext.setMapGraphBuilder(builder);
    }

    @Override
    public void initializeSystems() {
//        SystemInitializer sysInit = new SystemInitializer(
//            engine,
//           ,
//            gameContext.getCamera(),
//         ,
//
//        ,
//            assetManager);
//        sysInit.initializeSystems();
        SystemInitializer sysInit = new SystemInitializer(gameContext, engine,  gameContext.getWorldContext().getWorld(), camera,
            gameContext.getWorldContext().getMap(),
            gameContext.getBulletFactory(),    gameContext.getJoystick(), assetManager);
        sysInit.initialize();  // Initialize the systems
        SystemContext sysContext = SystemContext.createFrom(sysInit);
        gameContext.setSystemContext(sysContext);
    }

    private void initializePlayerAndItems() {
        // Create an instance of PlayerInitializer
        PlayerInitializer playerInitializer = new PlayerInitializer(gameContext);

// Call the initialize method to create and add the player to the engine
        playerInitializer.initialize();
        player = gameContext.getPlayer();


        Spawner<Void> itemSpawner = new ItemSpawner(
            engine, gameContext.getWorldContext().getWorld(),
            assetManager, gameContext.getCamera(), gameContext.getMapGraphBuilder());
        itemSpawner.spawn();
    }

    @Override
    public void initializeUI() {
        new UIInitializer(gameContext).initialize();
    }

    private void initializeInput() {
        InputAdapter globalInput = createGlobalInputAdapter();
        InputAdapter cameraInput = gameContext.getSystemContext().getCameraInputSystem().getInputAdapter();
        Stage stage = gameContext.getStage();

        // globalInput comes first for priority
        InputMultiplexer multiplexer = new InputMultiplexer(globalInput, cameraInput, stage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    private InputAdapter createGlobalInputAdapter() {
        return new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    gameStateService.saveGameState();
                    EventBus.get().notify(new PauseEvent());
                    return true;
                }

                if (keycode == Input.Keys.C) {
                    CameraInputSystem cameraSystem = gameContext.getSystemContext().getCameraInputSystem();
                    cameraSystem.toggleCameraFollow();
                    boolean isFollowing = cameraSystem.isFollowingPlayer();
                    updateCameraIcon(isFollowing);
                    return true;
                }

                return false;
            }
        };
    }

    private void updateCameraIcon(boolean isFollowing) {
        Image cameraIcon = gameContext.getHUDContext().getCameraIconImage();
        TextureRegion icon = isFollowing
            ? gameContext.getHUDContext().getCameraOnTexture()
            : gameContext.getHUDContext().getCameraOffTexture();

        cameraIcon.setDrawable(new TextureRegionDrawable(icon));
    }

    private void initializeCameraEntity() {
        Entity cameraEntity = new Entity();
        CameraComponent cameraComponent = new CameraComponent();
        cameraComponent.followPlayer = true;
        cameraEntity.add(cameraComponent);
        engine.addEntity(cameraEntity);
    }

    private void initializeGameState() {
        gameStateService = new GameStateService(
            engine,
            gameContext.getWorldContext().getWorld(),
            assetManager,
            gameContext.getSystemContext().getBodyRemovalSystem(),
            gameContext.getSystemContext().getAttackSystem(),
            gameContext.getSystemContext().getItemCollectionSystem(),
            gameContext.getPlayingTimer(),
            camera,
            gameContext.getSystemContext().getPlayerInputSystem()
        );
        gameStateService.setPlayer(player);
        gameContext.setGameStateService(gameStateService);
    }

    private void initializePathfinding() {
        pathfinder = new AStarPathfinder(gameContext.getMapGraphBuilder().nodes);
        gameContext.setPathfinder(pathfinder);
        engine.addSystem(new PathFollowSystem());
        engine.addSystem(new PathRequestSystem(gameContext.getMapGraphBuilder(), pathfinder));
        engine.addSystem(new PathDebugRenderSystem(gameContext.getCamera()));
    }

    private void initializeEnemies() {
        EnemyInitializer enemyInit = new EnemyInitializer(gameContext);
        Node spaceshipNode = enemyInit.initializeSpaceship();
        enemyInit.initializeEnemies(spaceshipNode);

        Spawner<Void> ufoSpawner = new UfoSpawner(
            engine,
            gameContext.getWorldContext().getWorld(),
            assetManager,
            player,
            gameContext.getCamera(),
            gameContext.getMapGraphBuilder()
        );

        UfoSpawnerSystem ufoSpawnerSystem = new UfoSpawnerSystem(ufoSpawner);
        engine.addSystem(ufoSpawnerSystem);

        gameStateService.setUfoSpawnerSystem(ufoSpawnerSystem);

        engine.addSystem(new EnemyCleanupSystem(
            engine,
            gameContext.getSystemContext().getBodyRemovalSystem(),
            gameContext.getSystemContext().getAnimationSystem(),
            enemy -> gameContext.incrementEnemiesKilled()
        ));
    }

    private void updateCameraPosition() {
        PositionComponent pos = player.getComponent(PositionComponent.class);
        CameraInputSystem cameraInputSystem = gameContext.getSystemContext().getCameraInputSystem();
        if (cameraInputSystem.isFollowingPlayer() && pos != null) {
            camera.position.set(pos.position.x, pos.position.y, 0);
        }
        camera.update();
    }


    @Override
    public void render(float delta) {
        gameContext.getTimer().update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Atualiza física
        gameContext.getWorldContext().getWorld().step(1 / 60f, 6, 2);

        // Atualiza câmera
        gameContext.updateCamera();
        updateCameraPosition();
        // Renderiza o mapa
        IsometricTiledMapRenderer renderer = gameContext.getWorldContext().getMapRenderer();
        renderer.setView(gameContext.getCamera());
        renderer.render();

        // Atualiza ECS (renderiza o player e inimigos, por exemplo)
        engine.update(delta);

        // Atualiza HUD
        gameContext.updateHUD();

        // Renderiza debug físico (opcional)
        gameContext.getWorldContext().getDebugRenderer().render(
            gameContext.getWorldContext().getWorld(),
            gameContext.getCamera().combined
        );

        // Renderiza Stage (HUD por cima de tudo)
        gameContext.getStage().act(delta);
        gameContext.getStage().draw();
    }

    @Override public void resize(int width, int height) { gameContext.getCamera().update(); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { gameContext.dispose(); }
}
