package com.badlogic.UniverseConqueror.ECS.observers;

import com.badlogic.UniverseConqueror.Audio.SoundManager;
import com.badlogic.UniverseConqueror.ECS.events.*;
import com.badlogic.UniverseConqueror.Interfaces.GameEvent;
import com.badlogic.UniverseConqueror.Interfaces.Observer;

// Observador responsável por reproduzir sons com base nos eventos do jogo.
public class SoundObserver implements Observer {

    private final SoundManager soundManager;

    public SoundObserver(SoundManager soundManager) {
        this.soundManager = soundManager;
    }

    @Override
    public void onNotify(GameEvent event) {
        // Som de ataque
        if (event instanceof AttackStartedEvent attackEvent) {
            String soundKey = attackEvent.isSuperAttack ? "superattack" : "attack";
            soundManager.play(soundKey);

            // Som de pulo
        } else if (event instanceof JumpEvent) {
            soundManager.play("jump");

            // Som de arma sem munição
        } else if (event instanceof NoAmmoEvent) {
            soundManager.play("empty_gun");

            // Som de caminhada
        } else if (event instanceof WalkEvent) {
            soundManager.play("walk");

            // Som de movimento rápido
        } else if (event instanceof FastMoveEvent) {
            soundManager.play("fastmove");

            // Fim de ataque, para sons contínuos
        } else if (event instanceof AttackEndedEvent) {
            soundManager.stop("attack");
            soundManager.stop("superattack");

            // Som de coleta de item
        } else if (event instanceof ItemCollectedEvent) {
            soundManager.play("item");

            // Evento de morte do jogador
        } else if (event instanceof DeathEvent) {
            // soundManager.stop();
            // MusicManager.getInstance().stop();
        }
        else if (event instanceof IdleEvent) {

        }
    }
}
