package com.badlogic.UniverseConqueror.Context;

import com.badlogic.UniverseConqueror.ECS.utils.ComponentMappers;
import com.badlogic.UniverseConqueror.ECS.entity.BulletFactory;
import com.badlogic.UniverseConqueror.ECS.systems.AttackSystem;
import com.badlogic.UniverseConqueror.ECS.systems.HealthSystem;
import com.badlogic.UniverseConqueror.ECS.systems.ItemCollectionSystem;
import com.badlogic.UniverseConqueror.GameLauncher;
import com.badlogic.UniverseConqueror.Pathfinding.AStarPathfinder;
import com.badlogic.UniverseConqueror.Pathfinding.MapGraphBuilder;
import com.badlogic.UniverseConqueror.Screens.GameScreen;
import com.badlogic.UniverseConqueror.State.GameStateService;
import com.badlogic.UniverseConqueror.Utils.Joystick;
import com.badlogic.UniverseConqueror.Utils.Timer;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

//public class GameContext {
//    private final AssetManager assetManager;
//    private final ScreenManager screenManager;
//    private final MusicManager musicManager;
//    private final SoundManager soundManager;
//    private final SpriteBatch spriteBatch;
//    private boolean isNewGame;
//
//    public GameContext(AssetManager assetManager, ScreenManager screenManager, MusicManager musicManager, SoundManager soundManager, SpriteBatch spriteBatch) {
//        this.assetManager = assetManager;
//        this.screenManager = screenManager;
//        this.musicManager = musicManager;
//        this.soundManager = soundManager;
//        this.spriteBatch = spriteBatch;
//    }
//    public SpriteBatch getBatch() {
//        return spriteBatch;
//    }
//    // Métodos getters para acessar os serviços
//    public AssetManager getAssetManager() {
//        return assetManager;
//    }
//    public boolean isNewGame() {
//        return isNewGame;
//    }
//    public void setNewGame(boolean isNewGame) {
//        this.isNewGame = isNewGame;
//    }
//
//    public ScreenManager getScreenManager() {
//        return screenManager;
//    }
//
//    public MusicManager getMusicManager() {
//        return musicManager;
//    }
//
//    public SoundManager getSoundManager() {
//        return soundManager;
//    }
//
//    public SpriteBatch getSpriteBatch() {
//        return spriteBatch;
//    }
//
//}

public class GameContext {
    private  GameLauncher game;
    private  PooledEngine engine;
    private  AssetManager assetManager;
    private  OrthographicCamera camera;
    private Stage stage;
    private Joystick joystick;
    private Timer playingTimer;
    private GameStateService gameStateService;
    private MapGraphBuilder mapGraphBuilder;
    private AStarPathfinder pathfinder;
    private BulletFactory bulletFactory;
    private  HUDContext hudContext = new HUDContext();
    private SystemContext systemContext = new SystemContext();
    private GameScreen screen;
    private Entity player;
    private  WorldContext worldContext = new WorldContext();
    private BitmapFont font;
    private boolean restoredState = false;
    private int enemiesKilledCount = 0;

    public GameContext(GameLauncher game,
                       GameScreen screen,
                       PooledEngine engine,
                       AssetManager assetManager,
                       Timer playingTimer,
                       OrthographicCamera camera) {
        this.game = game;
        this.screen = screen;
        this.engine = engine;
        this.assetManager = assetManager;
        this.playingTimer = playingTimer;
        this.camera = camera;

    }

    // Getters
    public GameLauncher getGame() { return game; }
    public PooledEngine getEngine() { return engine; }
    public AssetManager getAssetManager() { return assetManager; }
    public OrthographicCamera getCamera() { return camera; }
     public Stage getStage() { return stage; }
    public Joystick getJoystick() { return joystick; }
    public void setJoystick(Joystick joystick) {
        this.joystick = joystick;
    }
    public Timer getPlayingTimer() { return playingTimer; }
    public GameStateService getGameStateService() { return gameStateService; }
    public MapGraphBuilder getMapGraphBuilder() { return mapGraphBuilder; }
    public AStarPathfinder getPathfinder() { return pathfinder; }
    public BulletFactory getBulletFactory() { return bulletFactory; }

    public Entity getPlayer() { return player; }
    public void setPlayer(Entity player) { this.player = player; }
    public GameScreen getScreen() {return screen; }
    public void setScreen(GameScreen screen) { this.screen = screen; }


    public WorldContext getWorldContext() {
        return worldContext;
    }
    public HUDContext getHUDContext() { return hudContext; }
    public BitmapFont getFont() {
        return font;
    }

    public void setFont(BitmapFont font) {
        this.font = font;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }


    public boolean isRestoredState() { return restoredState; }
    public void setRestoredState(boolean value) { this.restoredState = value; }
    public static Texture getTexture(AssetManager assetManager, String path) {
        return assetManager.get(path, Texture.class);
    }
    public void setMapGraphBuilder(MapGraphBuilder mapGraphBuilder) {
        this.mapGraphBuilder = mapGraphBuilder;
    }
    public void setPathfinder(AStarPathfinder pathfinder) {
        this.pathfinder = pathfinder;
    }
    public void setBulletFactory(BulletFactory bulletFactory) {
        this.bulletFactory = bulletFactory;
    }
    public void setSystemContext(SystemContext systemContext) {
        this.systemContext = systemContext;
    }

    public SystemContext getSystemContext() {
        return systemContext;
    }
    public void setSkin(Skin skin) {
        this.hudContext.setSkin(skin);
    }

    public Skin getSkin() {
        return this.hudContext.getSkin();
    }
    public Timer getTimer() {
        return playingTimer;
    }
    public void setGameStateService(GameStateService gameStateService) {
        this.gameStateService = gameStateService;
    }


    public void updateCamera() {
        if (camera != null) {
            camera.update();
        }
    }

    public void updateHUD() {
        if (player == null || hudContext == null || systemContext == null) return;

        // Atualiza barra de vida
        HealthSystem healthSystem = systemContext.getHealthSystem();
        if (healthSystem != null && ComponentMappers.health.has(player)) {
            int currentHealth = ComponentMappers.health.get(player).currentHealth;
            hudContext.getHealthLabel().setText("Health: " + currentHealth);
        }

        // Atualiza itens coletados
        ItemCollectionSystem itemSystem = systemContext.getItemCollectionSystem();
        if (itemSystem != null) {
            hudContext.getItemsLabel().setText("Items: " + itemSystem.getCollectedCount());
        }

        // Atualiza poder de ataque restante
        AttackSystem attackSystem = systemContext.getAttackSystem();
        if (attackSystem != null) {
            hudContext.getAttackPowerLabel().setText("Attack: " + attackSystem.getRemainingAttackPower());
        }

        if (hudContext.getTimerLabel() != null) {
            float totalTime = playingTimer.getTime();
            int hours = (int) (totalTime / 3600);
            int minutes = (int) ((totalTime % 3600) / 60);
            int seconds = (int) (totalTime % 60);

            hudContext.getTimerLabel().setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
        }
    }

    public int getEnemiesKilledCount() {
        return enemiesKilledCount;
    }

    public void incrementEnemiesKilled() {
        enemiesKilledCount++;
        if (hudContext != null && hudContext.getEnemiesKilledLabel() != null) {
            hudContext.getEnemiesKilledLabel().setText(enemiesKilledCount);
        }
    }
    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }

        if (joystick != null) {
            joystick.dispose();
        }

        if (font != null) {
            font.dispose();
        }

        if (bulletFactory != null) {
        //    bulletFactory.dispose();
        }

        if (gameStateService != null) {
            //gameStateService.dispose(); // se implementar Disposable
        }

        if (assetManager != null) {
            assetManager.dispose(); // cuidado: só se for gerenciado localmente
        }


    }

}

