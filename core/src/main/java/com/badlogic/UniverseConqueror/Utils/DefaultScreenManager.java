package com.badlogic.UniverseConqueror.Utils;

import com.badlogic.UniverseConqueror.GameLauncher;
import com.badlogic.UniverseConqueror.Interfaces.ScreenManager;
import com.badlogic.UniverseConqueror.Interfaces.ScreenType;
import com.badlogic.UniverseConqueror.Screens.*;
import com.badlogic.UniverseConqueror.State.GameStateManager;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;

import java.util.EnumMap;

public class DefaultScreenManager implements ScreenManager {

    private final GameLauncher game;
    private final AssetManager assetManager;
    private final EnumMap<ScreenType, Screen> screenCache = new EnumMap<>(ScreenType.class);
    private Screen currentScreen;

    public DefaultScreenManager(GameLauncher game, AssetManager assetManager) {
        this.game = game;
        this.assetManager = assetManager;
    }

    @Override
    public void show(ScreenType type) {
        show(type, (Object[]) null);
    }

    @Override
    public void show(ScreenType type, Object... args) {
        Screen screen = getOrCreateScreen(type, args);
        if (screen != null) {
            currentScreen = screen;
            game.setScreen(screen);
        }
    }

    private Screen getOrCreateScreen(ScreenType type, Object... args) {
        switch (type) {
            case MAIN_MENU:
                return screenCache.computeIfAbsent(type, t -> new MainMenuScreen(game, assetManager, this));

            case GAME:
                GameStateManager.delete();
                return new GameScreen(game, assetManager); // nova instância sempre
            case PAUSE:
                return screenCache.computeIfAbsent(type, t -> new PauseScreen(game, (GameScreen) currentScreen, assetManager));
            case GAME_OVER:
                return new GameOverScreen(game, assetManager, this);
            case END:
                if (args != null && args.length == 4) {
                    int items = (int) args[0];
                    int health = (int) args[1];
                    float totalTime = (float) args[2];
                    int enemiesKilled = (int) args[3];

                    return new EndScreen(game, assetManager, items, health, totalTime, enemiesKilled);
                } else {
                    throw new IllegalArgumentException("EndScreen espera 4 argumentos: items, health, time, enemiesKilled");
                }
            case CREDITS:
                return screenCache.computeIfAbsent(type, t -> new CreditsScreen(game, assetManager));
            case CONTROLS:
                return screenCache.computeIfAbsent(type, t -> new ControlsScreen(game, assetManager));
            default:
                throw new IllegalArgumentException("Unsupported screen type: " + type);
        }
    }

    @Override
    public Screen getCurrent() {
        return currentScreen;
    }
}
