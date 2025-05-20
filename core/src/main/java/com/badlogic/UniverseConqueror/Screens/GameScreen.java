package com.badlogic.UniverseConqueror.Screens;

import com.badlogic.UniverseConqueror.ContactListener.ContactListenerWrapper;
import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.ECS.systems.*;
import com.badlogic.UniverseConqueror.ECS.entity.ItemFactory;
import com.badlogic.UniverseConqueror.GameLauncher;
import com.badlogic.UniverseConqueror.State.GameState;
import com.badlogic.UniverseConqueror.State.GameStateManager;
import com.badlogic.UniverseConqueror.State.GameStateService;
import com.badlogic.UniverseConqueror.State.SavedItemData;
import com.badlogic.UniverseConqueror.Utils.*;
import com.badlogic.UniverseConqueror.ECS.entity.PlayerFactory;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.*;
import java.util.ArrayList;

import com.badlogic.gdx.utils.viewport.ScreenViewport;


public class GameScreen implements Screen {
    private final GameLauncher game;
    private PooledEngine engine;
    private Entity player;
    private OrthographicCamera camera;
    private World world;
    private Box2DDebugRenderer debugRenderer;
    private IsometricTiledMapRenderer mapRenderer;
    private TiledMap map;
    private ShapeRenderer shapeRenderer;

    private BitmapFont font;
    private Stage stage;
    private Table footerTable;
    private Texture uiskinTexture;
    private Label healthLabel, attackPowerLabel, itemsLabel, timerLabel;
    private TextureRegionDrawable healthBackground, attackPowerBackground, itemsBackground;
    private Skin skin;

    private TextureRegion cameraOnTexture, cameraOffTexture;
    private Image cameraIconImage;

    private Timer playingTimer;
    private ComponentMapper<HealthComponent> healthMapper;
    private ComponentMapper<AttackComponent> attackMapper;
    private CameraInputSystem cameraInputSystem;
    private Joystick joystick;
    private MapCollisionHandler collisionHandler;
    private ItemCollectionSystem itemCollectionSystem;
    private AttackSystem attackSystem;
    private HealthSystem healthSystem;
    private BulletSystem bulletSystem; // BulletSystem para renderizar as balas
    private BulletRenderSystem bulletRenderSystem;
    private float centerX, centerY;
    private BulletMovementSystem bulletMovementSystem;

    private final AssetManager assetManager;
    private BodyRemovalSystem bodyRemovalSystem;
    private AnimationSystem animationSystem;
    private PlayerInputSystem playerInputSystem;
    private boolean restoredState = false;
    private GameStateService gameStateService;
    // Constructor
    public GameScreen(GameLauncher game, AssetManager assetManager) {
        this.game = game;
        this.playingTimer = new Timer(Float.MAX_VALUE);
        this.shapeRenderer = new ShapeRenderer();
        this.engine = new PooledEngine();
        this.assetManager = assetManager;
    }

    @Override
    public void show() {
        initializeAssets();
        initializeWorld();
        initializeCamera();
        GameState state = GameStateManager.load();
        if (state!= null) {
            restoreState(state);
            GameStateManager.delete();
        } else {
            initializePlayer();
            initializeItems();
        }

        initializeUI();
        initializeSystems();
        initializeInputProcessor();
        createContactListener();

        if (state != null) {
            itemCollectionSystem.setCollectedCount(state.collectedItemCount);
            itemsLabel.setText("Items: " + state.collectedItemCount);
        }
        gameStateService = new GameStateService(engine, world, assetManager,
            bodyRemovalSystem, attackSystem, itemCollectionSystem,
            playingTimer, camera, playerInputSystem);
        gameStateService.setPlayer(player);
    }

    private void initializeInputProcessor() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(cameraInputSystem.getInputAdapter());
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void createContactListener() {
        ContactListenerWrapper contactListenerWrapper = new ContactListenerWrapper(engine, itemCollectionSystem, healthSystem,world);
        world.setContactListener(contactListenerWrapper);
    }

    private void updateCameraPosition() {
        PositionComponent pos = player.getComponent(PositionComponent.class);
        if (cameraInputSystem.isFollowingPlayer() && pos != null) {
            camera.position.set(pos.position.x, pos.position.y, 0);
        }
        camera.update();
    }

    private void updateUI(float delta) {
        stage.act();
        stage.draw();
        updateTimer(delta);
        updateCameraIcon();
        updateCameraPosition();
    }

    private void updateCameraIcon() {
        // Alterna o ícone da câmera dependendo do estado
        if (cameraInputSystem.isFollowingPlayer()) {
            cameraIconImage.setDrawable(new TextureRegionDrawable(cameraOnTexture));
        } else {
            cameraIconImage.setDrawable(new TextureRegionDrawable(cameraOffTexture));
        }
    }

    private void updateTimer(float delta) {
        playingTimer.update(delta);
        float elapsed = playingTimer.getTime();
        int hours = (int) (elapsed / 3600);
        int minutes = (int) ((elapsed % 3600) / 60);
        int seconds = (int) (elapsed % 60);
        String timeFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        timerLabel.setText(timeFormatted);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        world.step(1 / 60f, 6, 2);

        updateUI(delta);
        // Verifica se jogador morreu após o update dos sistemas
        HealthComponent health = healthMapper.get(player);
        triggerGameOver(health);
        if (health != null) {
            healthLabel.setText("Health: " + health.currentHealth);

        itemsLabel.setText("Items: " + itemCollectionSystem.getCollectedCount());
        attackPowerLabel.setText("Attack: " + attackSystem.getRemainingAttackPower());
        }
        // Inicia o SpriteBatch
        SpriteBatch batch = new SpriteBatch();
        batch.begin();  // Começa a renderização

        // Renderiza as balas diretamente aqui
        bulletRenderSystem.update(delta);

        batch.end();  // Finaliza a renderização

        renderWorld();
        stage.draw();

    }

    private void initializeItems() {
        ArrayList<ItemFactory> items = new ArrayList<>();
        items.add(new ItemFactory("Vida", centerX + 100, centerY, AssetPaths.ITEM_VIDA,assetManager));
        items.add(new ItemFactory("Ataque", centerX + 150, centerY + 50, AssetPaths.ITEM_ATAQUE,assetManager));
        items.add(new ItemFactory("SuperAtaque", centerX + 450, centerY + 70, AssetPaths.ITEM_SUPER_ATAQUE,assetManager));
        items.add(new ItemFactory("Vida", centerX -200, centerY-20, AssetPaths.ITEM_VIDA,assetManager));
        items.add(new ItemFactory("Ataque", centerX  -50, centerY -100, AssetPaths.ITEM_ATAQUE,assetManager));
        items.add(new ItemFactory("SuperAtaque", centerX - 600, centerY , AssetPaths.ITEM_SUPER_ATAQUE,assetManager));
        for (ItemFactory item : items)  {
            engine.addEntity(item.createEntity(engine, world));
        }
        SpriteBatch batchItem = new SpriteBatch();
        engine.addSystem(new RenderItemSystem(batchItem, camera));
    }

    private void initializeUI() {
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.None);
        skin = assetManager.get(AssetPaths.UI_SKIN_JSON, Skin.class);
        font = new BitmapFont();
        // Footer UI setup
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

        // Joystick setup
        Texture base = assetManager.get(AssetPaths.JOYSTICK_BASE, Texture.class);
        Texture knob = assetManager.get(AssetPaths.JOYSTICK_KNOB, Texture.class);
        joystick = new Joystick(base,knob, 100f, 100f, 60f);
        stage.addActor(joystick);

        // Camera icon setup
        cameraIconImage = new Image(cameraOnTexture);
        Table uiTable = new Table();
        uiTable.top().left();
        uiTable.setFillParent(true);
        uiTable.add(cameraIconImage).pad(10).size(48); // tamanho opcional

        // Adicionando o listener ao ícone da câmera
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
    }

    private void initializeLabels() {
        healthMapper = ComponentMapper.getFor(HealthComponent.class);
        HealthComponent healthComponent = healthMapper.get(player);

        healthLabel = new Label("Health: " + healthComponent.currentHealth, skin);
        attackPowerLabel = new Label("Attack: " , skin);
        itemsLabel = new Label("Items: 0", skin);
        // UI skin setup
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
        // Footer setup
        footerTable.center();
        footerTable.bottom();
        footerTable.add(healthBox).pad(10).left();
        footerTable.add(attackBox).pad(10).left();
        footerTable.add(itemsBox).pad(10).left();
    }

    private void initializeCamera() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 1.0f;
    }

    private void initializeAssets() {
        cameraOnTexture = new TextureRegion(assetManager.get(AssetPaths.CAMERA_ON_ICON, Texture.class));
        cameraOffTexture = new TextureRegion(assetManager.get(AssetPaths.CAMERA_OFF_ICON, Texture.class));
    }

    private void initializeWorld() {
        world = new World(new Vector2(0, -9.8f), true);
        debugRenderer = new Box2DDebugRenderer();
        map = new TmxMapLoader().load("mapa.tmx");
        mapRenderer = new IsometricTiledMapRenderer(map);
        this.collisionHandler = new MapCollisionHandler(map, "Collisions", "Jumpable");
        collisionHandler.createBox2DBodies(world);
        shapeRenderer = new ShapeRenderer();
    }

    private void initializePlayer() {
        int mapWidthInTiles = map.getProperties().get("width", Integer.class);
        int mapHeightInTiles = map.getProperties().get("height", Integer.class);
        int tilePixelWidth = map.getProperties().get("tilewidth", Integer.class);
        int tilePixelHeight = map.getProperties().get("tileheight", Integer.class);

        centerX = (mapWidthInTiles + mapHeightInTiles) * tilePixelWidth / 4f;
        centerY = (mapHeightInTiles - mapWidthInTiles) * tilePixelHeight / 4f;
        ObjectMap<String, Sound> sounds = new ObjectMap<>();

        player = PlayerFactory.createPlayer(engine, new Vector2(centerX, centerY), sounds, world, assetManager);
        engine.addEntity(player);

    }

    private void initializeSystems() {
        SpriteBatch batch = new SpriteBatch();
        engine.addSystem(new CameraSystem(camera, map.getProperties().get("width", Integer.class) * map.getProperties().get("tilewidth", Integer.class),
            map.getProperties().get("height", Integer.class) * map.getProperties().get("tileheight", Integer.class)));
        engine.addSystem(new RenderSystem(batch, camera));

        playerInputSystem = new PlayerInputSystem(world, joystick, bulletSystem, camera, engine);
        engine.addSystem(playerInputSystem);
        cameraInputSystem = new CameraInputSystem(camera);
        bulletSystem = new BulletSystem(camera,assetManager);
        bulletRenderSystem = new BulletRenderSystem(batch);
        bulletMovementSystem = new BulletMovementSystem();
        engine.addSystem(bulletMovementSystem);
        engine.addSystem(bulletSystem);
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
        healthSystem = new HealthSystem(currentHealth -> healthLabel.setText("Health: " + currentHealth));
        healthSystem.setEngine(engine);
        bodyRemovalSystem = new BodyRemovalSystem(world);
        engine.addSystem(bodyRemovalSystem);
        engine.addSystem(attackSystem);
        itemCollectionSystem = new ItemCollectionSystem( itemsLabel, attackSystem, healthSystem,bodyRemovalSystem);
        engine.addSystem(itemCollectionSystem);
        engine.addSystem(new HealthSystem( currentHealth -> healthLabel.setText("Health: " + currentHealth)));
        engine.addSystem(bulletRenderSystem);
        engine.addSystem(new ParticleSystem(batch, camera));
        engine.addSystem(new CrosshairRenderSystem(batch, camera,assetManager));
        engine.addSystem(new SoundSystem());
        engine.addSystem(new StateSoundSystem());
    }

    private void renderWorld() {
        mapRenderer.setView(camera);
        mapRenderer.render();

        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
            gameStateService.saveGameState();
            game.setScreen(new PauseScreen(game, this, assetManager));
            return;
        }

        engine.update(Gdx.graphics.getDeltaTime());

        debugRenderer.render(world, camera.combined);
    }

    private void triggerGameOver(HealthComponent health) {
        if (health != null && health.isDead()) {
            if (animationSystem.isDeathAnimationFinished(player)) {
                game.setScreen(new GameOverScreen(game, assetManager));
            }
        }
    }

    public void restoreState(GameState state) {
        // Remove player atual se existir
        if (player != null) {
            engine.removeEntity(player);
        }

        // Cria novo player
        ObjectMap<String, Sound> sounds = new ObjectMap<>();
        player = PlayerFactory.createPlayer(engine, state.playerPosition, sounds, world, assetManager);

        // Aplica estado salvo
        PositionComponent pos = player.getComponent(PositionComponent.class);
        if (pos != null) pos.position.set(state.playerPosition);

        BodyComponent body = player.getComponent(BodyComponent.class);
        if (body != null && body.body != null) {
            body.body.setTransform(state.playerPosition.x, state.playerPosition.y, 0f);
            body.body.setLinearVelocity(0, 0);
            body.body.setAwake(true);
        }

        HealthComponent health = player.getComponent(HealthComponent.class);
        if (health != null) health.currentHealth = state.playerHealth;

        if (attackSystem != null) {
            // attackSystem.setRemainingAttackPower(state.playerAttack);
        }

        if (!engine.getEntities().contains(player, true)) {
            engine.addEntity(player);
        }

        if (playerInputSystem != null) {
            playerInputSystem.setPlayer(player);
        }

        // Restaurar itens
        ImmutableArray<Entity> currentItems = engine.getEntitiesFor(Family.all(ItemComponent.class).get());
        for (Entity e : currentItems) {
            engine.removeEntity(e);
        }

        for (SavedItemData data : state.remainingItems) {
            Entity restored = data.createEntity(engine, world, assetManager);
            engine.addEntity(restored);
        }

        restoredState = true;
        SpriteBatch batchItem = new SpriteBatch();
        engine.addSystem(new RenderItemSystem(batchItem, camera));
        itemCollectionSystem.setCollectedCount(state.collectedItemCount);
        playingTimer.setTime(state.gameTime);
    }

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

    @Override
    public void dispose() {
        map.dispose();
        mapRenderer.dispose();
        debugRenderer.dispose();
        shapeRenderer.dispose();
        assetManager.dispose();
    }
}
