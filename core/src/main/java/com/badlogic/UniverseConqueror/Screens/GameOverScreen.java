package com.badlogic.UniverseConqueror.Screens;

import com.badlogic.UniverseConqueror.GameLauncher;
import com.badlogic.UniverseConqueror.Utils.AssetPaths;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class GameOverScreen implements Screen {
    private final GameLauncher game;
    private Stage stage;
    private Skin skin;
    private Table table;
    private SpriteBatch batch;
    private Texture background;
    private Music music;
    private Sound hoverSound;
    private Sound clickSound;
    // Adicione esta nova variável
    private Sound gameOverSound;
    private final AssetManager assetManager;

    public GameOverScreen(GameLauncher game,AssetManager assetManager) {
        this.game = game;
        this.assetManager = assetManager;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(1920, 1080));
        Gdx.input.setInputProcessor(stage);
        gameOverSound = assetManager.get(AssetPaths.SOUND_GAME_OVER, Sound.class);
        gameOverSound.play(1.0f);
        batch = new SpriteBatch();
        skin =assetManager.get(AssetPaths.UI_SKIN_JSON, Skin.class);
        background = assetManager.get(AssetPaths.BACKGROUND_PAUSE, Texture.class);
        music = assetManager.get(AssetPaths.MUSIC_SPACE_INTRO, Music.class);
        hoverSound = assetManager.get(AssetPaths.SOUND_HOVER, Sound.class);
        clickSound =  assetManager.get(AssetPaths.SOUND_CLICK, Sound.class);

        music.setLooping(true);
        music.setVolume(0.2f);
        music.play();

        table = new Table();
        table.center();
        table.setFillParent(true);

        Label gameOverLabel = new Label("GAME OVER", skin, "title");
        gameOverLabel.setFontScale(1.8f);
        table.add(gameOverLabel).padBottom(50).row();

        TextButton restartButton = new TextButton("Reiniciar", skin);
        TextButton mainMenuButton = new TextButton("Menu Principal", skin);
        TextButton exitButton = new TextButton("Sair", skin);

        float buttonWidth = 400f;
        float buttonHeight = 80f;

        // Listeners
        restartButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                clickSound.play();
                music.stop();
                //game.getGameScreen().reset();
                game.setScreen(new GameScreen(game,assetManager)); // Agora reinicia o jogo!
            }
        });

        mainMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                clickSound.play();
                music.stop();
                game.setScreen(new MainMenuScreen(game,assetManager));
            }
        });

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                clickSound.play();
                Gdx.app.exit();
            }
        });

        addHoverSound(restartButton);
        addHoverSound(mainMenuButton);
        addHoverSound(exitButton);

        table.add(restartButton).size(buttonWidth, buttonHeight).pad(10).row();
        table.add(mainMenuButton).size(buttonWidth, buttonHeight).pad(10).row();
        table.add(exitButton).size(buttonWidth, buttonHeight).pad(10).row();

        stage.addActor(table);
    }

    private void addHoverSound(TextButton button) {
        button.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                hoverSound.play();
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                hoverSound.stop();
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(background, 0, 0, stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
        batch.end();

        stage.act(Math.min(delta, 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void pause() {}
    @Override
    public void resume() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        batch.dispose();
        assetManager.dispose();
    }
}
