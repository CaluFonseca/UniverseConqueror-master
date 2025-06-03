package com.badlogic.UniverseConqueror.ECS.observers;

import com.badlogic.UniverseConqueror.ECS.events.EndGameEvent;
import com.badlogic.UniverseConqueror.Interfaces.GameEvent;
import com.badlogic.UniverseConqueror.Interfaces.Observer;
import com.badlogic.UniverseConqueror.GameLauncher;
import com.badlogic.UniverseConqueror.Interfaces.ScreenManager;
import com.badlogic.UniverseConqueror.Interfaces.ScreenType;
import com.badlogic.UniverseConqueror.Screens.EndScreen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.ashley.core.Entity;
import com.badlogic.UniverseConqueror.ECS.systems.ItemCollectionSystem;
import com.badlogic.UniverseConqueror.ECS.components.HealthComponent;
import com.badlogic.UniverseConqueror.Audio.MusicManager;
import com.badlogic.UniverseConqueror.Audio.SoundManager;
import com.badlogic.UniverseConqueror.Utils.Timer;

import java.util.function.Supplier;

/// Observador responsável por responder ao fim do jogo.
/// Ao receber um EndGameEvent, coleta informações relevantes
/// (itens, vida, tempo, inimigos mortos) e muda para o ecrã de fim.
public class EndGameObserver implements Observer {

    private final ScreenManager screenManager;
    private final AssetManager assetManager;
    private final Entity player;
    private final ItemCollectionSystem itemSystem;
    private final Timer timer;
    private final Supplier<Integer> enemiesKilledSupplier;

    public EndGameObserver(ScreenManager screenManager, AssetManager assetManager, Entity player,
                           ItemCollectionSystem itemSystem, Timer timer, Supplier<Integer> enemiesKilledSupplier) {
        this.screenManager = screenManager;
        this.assetManager = assetManager;
        this.player = player;
        this.itemSystem = itemSystem;
        this.timer = timer;
        this.enemiesKilledSupplier = enemiesKilledSupplier;
    }

    @Override
    public void onNotify(GameEvent event) {
        if (event instanceof EndGameEvent end && end.entity == player) {
            /// Para os sons e música
            SoundManager.getInstance().stop("patrolAlien");
            SoundManager.getInstance().stop("chaseAlien");
            SoundManager.getInstance().stop("chaseUfo");
            MusicManager.getInstance().stop();
            SoundManager.getInstance().play("nextLevel.mp3");
            /// Coleta estatísticas finais
//            int items = itemSystem.getCollectedCount();
//            int health = player.getComponent(HealthComponent.class).currentHealth;
//            float totalTime = timer.getTime();
//            int enemiesKilled =  enemiesKilledSupplier.get();

            /// Transição para ecrã final
            screenManager.show(
                ScreenType.END,
                itemSystem.getCollectedCount(),
                player.getComponent(HealthComponent.class).currentHealth,
                timer.getTime(),
                enemiesKilledSupplier.get()
            );
        }
    }
}
