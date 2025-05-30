package com.badlogic.UniverseConqueror.ECS.observers;

import com.badlogic.UniverseConqueror.Audio.SoundManager;
import com.badlogic.UniverseConqueror.ECS.events.*;
import com.badlogic.UniverseConqueror.GameLauncher;
import com.badlogic.UniverseConqueror.Interfaces.GameEvent;
import com.badlogic.UniverseConqueror.Interfaces.Observer;
import com.badlogic.UniverseConqueror.Screens.GameOverScreen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.ashley.core.Entity;

/// Observador que escuta a morte do jogador.
/// Quando um DeathEvent ocorre e o alvo é o jogador,
/// para todos os sons e muda para a GameOverScreen.
public class GameOverObserver implements Observer {

    private final GameLauncher gameLauncher;
    private final AssetManager assetManager;
    private final Entity player;

    public GameOverObserver(GameLauncher gameLauncher, AssetManager assetManager, Entity player) {
        this.gameLauncher = gameLauncher;
        this.assetManager = assetManager;
        this.player = player;
    }

    @Override
    public void onNotify(GameEvent event) {
        /// Verifica se o evento é de morte e se a entidade é o jogador
        if (event instanceof DeathEvent deathEvent) {
            if (deathEvent.entity == player) {
                /// Interrompe todos os sons e troca o ecrã para Game Over
                SoundManager.getInstance().stop();
                gameLauncher.setScreen(new GameOverScreen(gameLauncher, assetManager));
            }
        }
    }
}
