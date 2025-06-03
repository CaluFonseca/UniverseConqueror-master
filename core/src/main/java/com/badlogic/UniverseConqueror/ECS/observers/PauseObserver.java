package com.badlogic.UniverseConqueror.ECS.observers;

import com.badlogic.UniverseConqueror.Audio.MusicManager;
import com.badlogic.UniverseConqueror.Audio.SoundManager;
import com.badlogic.UniverseConqueror.ECS.events.*;
import com.badlogic.UniverseConqueror.Interfaces.GameEvent;
import com.badlogic.UniverseConqueror.Interfaces.Observer;
import com.badlogic.UniverseConqueror.Interfaces.ScreenManager;
import com.badlogic.UniverseConqueror.Interfaces.ScreenType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

/// Observador responsável por tratar eventos de pausa do jogo.
/// Ao receber o evento, interrompe os sons e muda para o ecrã de pausa.
public class PauseObserver implements Observer {
    private final ScreenManager screenManager;

    public PauseObserver(ScreenManager screenManager) {
        this.screenManager = screenManager;
    }

    @Override
    public void onNotify(GameEvent event) {
        /// Se o evento for de pausa, interrompe sons e muda para o ecrã de pausa
        if (event instanceof PauseEvent) {
            SoundManager.getInstance().stop();
            MusicManager.getInstance().stop();
            screenManager.show(ScreenType.PAUSE);
        }
    }
}
