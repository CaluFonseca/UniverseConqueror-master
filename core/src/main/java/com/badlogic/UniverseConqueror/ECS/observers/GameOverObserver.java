package com.badlogic.UniverseConqueror.ECS.observers;

import com.badlogic.UniverseConqueror.Audio.SoundManager;
import com.badlogic.UniverseConqueror.ECS.components.AnimationComponent;
import com.badlogic.UniverseConqueror.ECS.components.StateComponent;
import com.badlogic.UniverseConqueror.ECS.events.DeathEvent;
import com.badlogic.UniverseConqueror.ECS.utils.ComponentMappers;
import com.badlogic.UniverseConqueror.GameLauncher;
import com.badlogic.UniverseConqueror.Interfaces.GameEvent;
import com.badlogic.UniverseConqueror.Interfaces.Observer;
import com.badlogic.UniverseConqueror.Interfaces.ScreenManager;
import com.badlogic.UniverseConqueror.Screens.GameOverScreen;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;


public class GameOverObserver implements Observer {

    //private final GameLauncher gameLauncher;
    private final AssetManager assetManager;
    private final Entity player;
    private final ScreenManager screenManager;

    public GameOverObserver(ScreenManager screenManager, AssetManager assetManager, Entity player) {
        this.screenManager = screenManager;
        this.assetManager = assetManager;
        this.player = player;
    }

    @Override
    public void onNotify(GameEvent event) {
        if (event instanceof DeathEvent deathEvent && deathEvent.entity == player) {

            SoundManager.getInstance().stop();

            AnimationComponent anim = ComponentMappers.animation.get(player);
            StateComponent state = ComponentMappers.state.get(player);

            if (anim != null && state != null) {
                Animation<?> deathAnim = anim.animations.get(StateComponent.State.DEATH);
                float delay = deathAnim != null ? deathAnim.getAnimationDuration() : 1f;

                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        Gdx.app.postRunnable(() ->
                            screenManager.show(com.badlogic.UniverseConqueror.Interfaces.ScreenType.GAME_OVER)
                        );
                    }
                }, delay);
            } else {
                screenManager.show(com.badlogic.UniverseConqueror.Interfaces.ScreenType.GAME_OVER);
            }
        }
    }
}
