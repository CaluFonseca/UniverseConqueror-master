package com.badlogic.UniverseConqueror.Screens;

import com.badlogic.UniverseConqueror.Audio.MusicManager;
import com.badlogic.UniverseConqueror.Audio.SoundManager;
import com.badlogic.UniverseConqueror.GameLauncher;
import com.badlogic.UniverseConqueror.Interfaces.ScreenType;
import com.badlogic.UniverseConqueror.State.GameStateManager;
import com.badlogic.UniverseConqueror.Utils.AssetPaths;
import com.badlogic.UniverseConqueror.Utils.Timer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
/**
 * Ecrã de pausa do jogo, exibida quando o jogador pausa o jogo.
 * Permite ao jogador retomar, reiniciar, voltar ao menu principal ou sair do jogo.
 */
public class PauseScreen extends BaseUIScreen {

    private final GameScreen gameScreen;
    private Timer pauseTimer;
    private Label timerLabel;
    private boolean isAudioOn = true;
    private float currentVolume = 1.0f;
    private TextButton audioButton;

    //Constutor
    public PauseScreen(GameLauncher game, GameScreen gameScreen, AssetManager assetManager) {
        super(game, assetManager);
        this.gameScreen = gameScreen;
    }

    //Inicializa a UI.
    @Override
    public void initializeUI() {
        background = assetManager.get(AssetPaths.BACKGROUND_PAUSE, Texture.class);
        MusicManager.getInstance().play("menu", true);

        pauseTimer = new Timer(Float.MAX_VALUE);
        pauseTimer.start();

        Table table = new Table();
        table.center();
        table.setFillParent(true);

        Label pauseLabel = new Label("PAUSE", skin, "title");
        pauseLabel.setFontScale(1.5f);
        table.add(pauseLabel).padBottom(30).row();

        timerLabel = new Label("00:00:00", skin);
        timerLabel.setFontScale(2f);
        table.add(timerLabel).padBottom(40).row();

        float buttonWidth = 400f;
        float buttonHeight = 80f;

        TextButton resumeButton = createButton("Resume", this::resumeWithRestore);
        TextButton restartButton = createButton("Restart", this::restartGame);
        TextButton mainMenuButton = createButton("Main Menu", this::goToMainMenu);
        TextButton exitButton = createButton("Exit", this::exitGame);

        audioButton = createButton("Som: On", () -> {
            playClickSound();
            isAudioOn = !isAudioOn;
            if (isAudioOn) {
                MusicManager.getInstance().unmute();
                audioButton.setText("Som: On");
            } else {
                MusicManager.getInstance().mute();
                audioButton.setText("Som: Off");
            }
        });

        table.add(resumeButton).size(buttonWidth, buttonHeight).pad(10).row();
        table.add(restartButton).size(buttonWidth, buttonHeight).pad(10).row();
        table.add(mainMenuButton).size(buttonWidth, buttonHeight).pad(10).row();
        table.add(exitButton).size(buttonWidth, buttonHeight).pad(10).row();
        table.add(audioButton).size(buttonWidth, buttonHeight).pad(10).row();

        stage.addActor(table);
    }

    // Atualiza o cronômetro de pausa no ecrã
    private void updateTimer() {
        float elapsed = pauseTimer.getTime();
        int hours = (int) (elapsed / 3600);
        int minutes = (int) ((elapsed % 3600) / 60);
        int seconds = (int) (elapsed % 60);
        timerLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        pauseTimer.update(delta);
        updateTimer();
    }

    //Retoma o jogo com restauração do estado atual.
    private void resumeWithRestore() {
        playClickSound();
        MusicManager.getInstance().stop();
        GameScreen restoredGame = new GameScreen(game, assetManager);
        game.setScreen(restoredGame);
    }

    @Override
    public void restartGame() {
        SoundManager.getInstance().stop();
        MusicManager.getInstance().stop();
        game.startNewGame();
    }

    @Override
    public void exitGame() {
        GameStateManager.delete();
        Gdx.app.exit();
    }

    @Override
    public void goToMainMenu() {
        game.goToMainMenu();
    }

    @Override public void initializeSystems() {}
    @Override public void registerObservers() {}
}
