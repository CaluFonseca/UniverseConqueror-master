package com.badlogic.UniverseConqueror.Initializers;

import com.badlogic.UniverseConqueror.Audio.SoundManager;
import com.badlogic.UniverseConqueror.Context.GameContext;
import com.badlogic.UniverseConqueror.ECS.events.EventBus;
import com.badlogic.UniverseConqueror.ECS.observers.*;
import com.badlogic.UniverseConqueror.GameLauncher;
import com.badlogic.UniverseConqueror.Interfaces.ScreenManager;
import com.badlogic.UniverseConqueror.Screens.PauseScreen;

public class ObserverRegistrar extends AbstractInitializer {

    public ObserverRegistrar(GameContext context) {
        super(context);
    }

    @Override
    public void initialize() {
        GameLauncher gameLauncher = (GameLauncher) context.getGame();
        ScreenManager screenManager = gameLauncher.getScreenManager();

        EventBus.get().addObserver(new UIObserver(
            context.getHUDContext().getHealthLabel(),
            context.getHUDContext().getAttackPowerLabel(),
            context.getHUDContext().getItemsLabel()));

        EventBus.get().addObserver(new GameOverObserver(
            screenManager,
            context.getAssetManager(),
            context.getPlayer()));

        EventBus.get().addObserver(new EndGameObserver(
            screenManager,
            context.getAssetManager(),
            context.getPlayer(),
            context.getSystemContext().getItemCollectionSystem(),
            context.getTimer(),
            () -> context.getEnemiesKilledCount()));

        EventBus.get().addObserver(new PauseObserver(screenManager));

        EventBus.get().addObserver(new SoundObserver(SoundManager.getInstance()));

        EventBus.get().addObserver(new ItemCollectedObserver(
            context.getSystemContext().getAttackSystem(),
            context.getSystemContext().getHealthSystem()));
    }
}
