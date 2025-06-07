package com.badlogic.UniverseConqueror.Screens;

import com.badlogic.UniverseConqueror.Audio.MusicManager;
import com.badlogic.UniverseConqueror.GameLauncher;
import com.badlogic.UniverseConqueror.Interfaces.ScreenManager;
import com.badlogic.UniverseConqueror.Interfaces.ScreenType;
import com.badlogic.UniverseConqueror.State.GameStateManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class MainMenuScreen extends BaseUIScreen {

    private final ScreenManager screenManager;
    private boolean isAudioOn = true;
    private float currentVolume = 1.0f;
    private TextButton audioButton;

    public MainMenuScreen(GameLauncher game, AssetManager assetManager, ScreenManager screenManager) {
        super(game, assetManager);
        this.screenManager = screenManager;
    }

    @Override
    public void initializeUI() {
        Table table = new Table();
        table.center();
        table.setFillParent(true);

        float buttonWidth = 400f;
        float buttonHeight = 80f;

        TextButton playButton = createButton("Play", () -> {
            MusicManager.getInstance().stop();
            GameStateManager.delete();
            screenManager.show(ScreenType.GAME);
        });

        audioButton = createButton("Som: On", () -> {
            if (isAudioOn) {
                MusicManager.getInstance().mute();
                audioButton.setText("Som: Off");
            } else {
                MusicManager.getInstance().unmute();
                MusicManager.getInstance().play("menu", true);
                audioButton.setText("Som: On");
            }
            isAudioOn = !isAudioOn;
        });

        TextButton volumeUp = createButton("Volume +", () -> {
            currentVolume = Math.min(currentVolume + 0.1f, 1.0f);
            if (isAudioOn) MusicManager.getInstance().setVolume(currentVolume);
        });

        TextButton volumeDown = createButton("Volume -", () -> {
            currentVolume = Math.max(currentVolume - 0.1f, 0.0f);
            if (isAudioOn) MusicManager.getInstance().setVolume(currentVolume);
        });

        TextButton controlsButton = createButton("Controlos", () -> {
            MusicManager.getInstance().stop();
            screenManager.show(ScreenType.CONTROLS);
        });

        TextButton creditsButton = createButton("Creditos", () -> {
            MusicManager.getInstance().stop();
            screenManager.show(ScreenType.CREDITS);
        });

        table.add(playButton).size(buttonWidth, buttonHeight).padBottom(20).row();
        table.add(audioButton).size(buttonWidth, buttonHeight).padBottom(20).row();
        table.add(volumeUp).size(buttonWidth, buttonHeight).padBottom(20).row();
        table.add(volumeDown).size(buttonWidth, buttonHeight).padBottom(20).row();
        table.add(controlsButton).size(buttonWidth, buttonHeight).padBottom(20).row();
        table.add(creditsButton).size(buttonWidth, buttonHeight).padBottom(20).row();

        stage.addActor(table);
    }

    @Override public void initializeSystems() {}
    @Override public void registerObservers() {}
    @Override public void restartGame() {}
    @Override public void exitGame() { Gdx.app.exit(); }
    @Override public void goToMainMenu() {}
}
