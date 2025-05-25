package com.badlogic.UniverseConqueror.Screens;

import com.badlogic.UniverseConqueror.Audio.MusicManager;
import com.badlogic.UniverseConqueror.Audio.SoundManager;
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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.UniverseConqueror.GameLauncher;

public class PauseScreen implements Screen, SoundEnabledScreen, NavigableScreen {

    private final GameLauncher game;
    private final GameScreen gameScreen;
    private final AssetManager assetManager;

    private Stage stage;
    private Skin skin;
    private Table table;
    private SpriteBatch batch;
    private Texture background;

    private boolean isAudioOn = true;
    private Timer pauseTimer;
    private Label timerLabel;

    public PauseScreen(GameLauncher game, GameScreen gameScreen, AssetManager assetManager) {
        this.game = game;
        this.gameScreen = gameScreen;
        this.assetManager = assetManager;
    }

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

    private void addHoverSound(TextButton button) {
        button.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                playHoverSound();
            }
        });
    }

    private void updateTimer() {
        float elapsed = pauseTimer.getTime();
        int hours = (int) (elapsed / 3600);
        int minutes = (int) ((elapsed % 3600) / 60);
        int seconds = (int) (elapsed % 60);
        timerLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
    }

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

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
    }

    @Override public void playClickSound() { SoundManager.getInstance().play("keyboardClick"); }
    @Override public void playHoverSound() { SoundManager.getInstance().play("hoverButton"); }
    @Override public void goToMainMenu() { game.setScreen(new MainMenuScreen(game, assetManager)); }
    @Override public void exitGame() { GameStateManager.delete(); Gdx.app.exit(); }
    @Override
    public void restartGame() {
        SoundManager.getInstance().stop();
        MusicManager.getInstance().stop();
        game.setNewGame(true);
        game.setScreen(new GameScreen(game, assetManager));

    }
}
