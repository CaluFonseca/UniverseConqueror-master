package com.badlogic.UniverseConqueror.Screens;

import com.badlogic.UniverseConqueror.Audio.MusicManager;
import com.badlogic.UniverseConqueror.Audio.SoundManager;
import com.badlogic.UniverseConqueror.Interfaces.NavigableScreen;
import com.badlogic.UniverseConqueror.Interfaces.SoundEnabledScreen;
import com.badlogic.UniverseConqueror.State.GameStateManager;
import com.badlogic.UniverseConqueror.Utils.AssetPaths;
import com.badlogic.UniverseConqueror.Utils.Timer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.UniverseConqueror.GameLauncher;

/// ecrã de pausa que implementa controle de som, navegação e exibição do tempo
public class PauseScreen implements Screen, SoundEnabledScreen, NavigableScreen {

    private final GameLauncher game;       /// Referência ao launcher para controle do ecrã
    private final GameScreen gameScreen;   /// ecrã principal para voltar do pause
    private final AssetManager assetManager; /// AssetManager para carregar recursos

    private Stage stage;                   /// Cena para UI
    private Skin skin;                     /// Skin para botões e labels
    private Table table;                   /// Layout principal
    private SpriteBatch batch;             /// Batch para renderizar background
    private Texture background;            /// Textura de fundo do ecrã de pausa

    private boolean isAudioOn = true;      /// Estado do áudio ligado/desligado
    private Timer pauseTimer;              /// Temporizador para contar tempo de pausa
    private Label timerLabel;              /// Label para mostrar o tempo de pausa

    /// Construtor, inicializa referências
    public PauseScreen(GameLauncher game, GameScreen gameScreen, AssetManager assetManager) {
        this.game = game;
        this.gameScreen = gameScreen;
        this.assetManager = assetManager;
    }

    /// Configura a cena e a interface ao mostrar o ecrã
    @Override
    public void show() {
        stage = new Stage(new FitViewport(1920, 1080));
        Gdx.input.setInputProcessor(stage);
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);

        batch = new SpriteBatch();
        skin = assetManager.get(AssetPaths.UI_SKIN_JSON, Skin.class);
        background = assetManager.get(AssetPaths.BACKGROUND_PAUSE, Texture.class);

        MusicManager.getInstance().play("menu", true);

        pauseTimer = new Timer(Float.MAX_VALUE);
        pauseTimer.start();

        table = new Table();
        table.center();
        table.setFillParent(true);

        Label pauseLabel = new Label("PAUSE", skin, "title");
        pauseLabel.setFontScale(1.5f);
        table.add(pauseLabel).padBottom(50).row();

        TextButton resumeButton = createButton("Resume", () -> game.setScreen(gameScreen));
        TextButton restartButton = createButton("Restart", this::restartGame);
        TextButton mainMenuButton = createButton("Main Menu", this::goToMainMenu);
        TextButton exitButton = createButton("Exit", this::exitGame);

        TextButton audioToggleButton = new TextButton("Sound: On", skin);
        audioToggleButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playClickSound();
                isAudioOn = !isAudioOn;
                if (isAudioOn) {
                    MusicManager.getInstance().unmute();
                    audioToggleButton.setText("Sound: On");
                } else {
                    MusicManager.getInstance().mute();
                    audioToggleButton.setText("Sound: Off");
                }
            }
        });
        addHoverSound(audioToggleButton);

        timerLabel = new Label("00:00:00", skin);
        timerLabel.setFontScale(2f);
        table.add(timerLabel).padBottom(30).row();

        float buttonWidth = 400f;
        float buttonHeight = 80f;

        table.add(resumeButton).size(buttonWidth, buttonHeight).pad(10).row();
        table.add(restartButton).size(buttonWidth, buttonHeight).pad(10).row();
        table.add(mainMenuButton).size(buttonWidth, buttonHeight).pad(10).row();
        table.add(exitButton).size(buttonWidth, buttonHeight).pad(10).row();
        table.add(audioToggleButton).size(buttonWidth, buttonHeight).pad(10).row();

        stage.addActor(table);
    }

    /// Cria botão com texto e ação associada, adicionando sons
    private TextButton createButton(String text, Runnable action) {
        TextButton button = new TextButton(text, skin);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playClickSound();
                action.run();
            }
        });
        addHoverSound(button);
        return button;
    }

    /// Adiciona som de hover ao botão
    private void addHoverSound(TextButton button) {
        button.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                playHoverSound();
            }
        });
    }

    /// Atualiza o timer e exibe o tempo formatado
    private void updateTimer() {
        float elapsed = pauseTimer.getTime();
        int hours = (int) (elapsed / 3600);
        int minutes = (int) ((elapsed % 3600) / 60);
        int seconds = (int) (elapsed % 60);
        timerLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
    }

    /// Renderiza o ecrã de pausa, fundo, UI e atualiza timer
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(background, 0, 0, stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
        batch.end();

        pauseTimer.update(delta);
        updateTimer();

        stage.act(Math.min(delta, 1 / 30f));
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void hide() { dispose(); }
    @Override public void pause() {}
    @Override public void resume() { }

    /// Libera recursos
    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
    }

    /// Toca som de clique
    @Override public void playClickSound() { SoundManager.getInstance().play("keyboardClick"); }
    /// Toca som de hover
    @Override public void playHoverSound() { SoundManager.getInstance().play("hoverButton"); }
    /// Vai para o menu principal
    @Override public void goToMainMenu() { game.setScreen(new MainMenuScreen(game, assetManager)); }
    /// Sai do jogo e apaga estado salvo
    @Override public void exitGame() { GameStateManager.delete(); Gdx.app.exit(); }

    /// Reinicia o jogo, para sons, marca novo jogo e abre ecrã principal
    @Override
    public void restartGame() {
        SoundManager.getInstance().stop();
        MusicManager.getInstance().stop();
        game.setNewGame(true);
        game.setScreen(new GameScreen(game, assetManager));
    }
}
