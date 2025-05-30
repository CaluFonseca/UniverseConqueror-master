package com.badlogic.UniverseConqueror.Initializers;

import com.badlogic.UniverseConqueror.Audio.SoundManager;
import com.badlogic.UniverseConqueror.ECS.events.EventBus;
import com.badlogic.UniverseConqueror.ECS.observers.*;
import com.badlogic.UniverseConqueror.GameLauncher;
import com.badlogic.UniverseConqueror.Screens.PauseScreen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.Game;
import com.badlogic.ashley.core.Entity;
import com.badlogic.UniverseConqueror.ECS.systems.*;
import com.badlogic.UniverseConqueror.Utils.Timer;

public class ObserverRegistrar {

    /// Registra todos os observers necessários no EventBus
    public static void registerAllObservers(
        Game game,
        AssetManager assetManager,
        Entity player,
        Label healthLabel,
        Label attackPowerLabel,
        Label itemsLabel,
        ItemCollectionSystem itemCollectionSystem,
        Timer playingTimer,
        int enemiesKilledCount,
        AttackSystem attackSystem,
        HealthSystem healthSystem,
        PauseScreen pauseScreen
    ) {
        EventBus.get().addObserver(new UIObserver(healthLabel, attackPowerLabel, itemsLabel));
        EventBus.get().addObserver(new GameOverObserver((GameLauncher)game, assetManager, player));
        EventBus.get().addObserver(new EndGameObserver((GameLauncher)game, assetManager, player, itemCollectionSystem, playingTimer, () -> enemiesKilledCount));
        EventBus.get().addObserver(new PauseObserver(game, pauseScreen));
        EventBus.get().addObserver(new SoundObserver(SoundManager.getInstance()));
        EventBus.get().addObserver(new ItemCollectedObserver(attackSystem, healthSystem));
    }
}
