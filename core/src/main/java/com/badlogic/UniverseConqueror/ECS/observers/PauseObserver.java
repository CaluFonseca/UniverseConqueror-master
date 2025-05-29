package com.badlogic.UniverseConqueror.ECS.observers;

import com.badlogic.UniverseConqueror.Audio.MusicManager;
import com.badlogic.UniverseConqueror.Audio.SoundManager;
import com.badlogic.UniverseConqueror.ECS.events.*;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

/// Observador responsável por tratar eventos de pausa do jogo.
/// Ao receber o evento, interrompe os sons e muda para a tela de pausa.
public class PauseObserver implements Observer {
    private final Game game;
    private final Screen pauseScreen;

    public PauseObserver(Game game, Screen pauseScreen) {
        this.game = game;
        this.pauseScreen = pauseScreen;
    }

    @Override
    public void onNotify(GameEvent event) {
        /// Se o evento for de pausa, interrompe sons e muda para a tela de pausa
        if (event instanceof PauseEvent) {
            SoundManager.getInstance().stop();
            MusicManager.getInstance().stop();
            game.setScreen(pauseScreen);
        }
    }
}
