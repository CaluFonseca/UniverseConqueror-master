package com.badlogic.UniverseConqueror.Screens;

import com.badlogic.UniverseConqueror.Audio.MusicManager;
import com.badlogic.UniverseConqueror.Audio.SoundManager;
import com.badlogic.UniverseConqueror.Utils.AssetPaths;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.UniverseConqueror.GameLauncher;

public class MainMenuScreen implements Screen {
    private SpriteBatch batch;
    private Stage stage;
    private Skin skin;
    private BitmapFont font;
    private Texture background;

    private TextButton playButton;
    private TextButton audioButton;
    private TextButton volumeUpButton;
    private TextButton volumeDownButton;
    private TextButton controlsButton;
    private TextButton creditsButton;
    private Slider volumeSlider;
    private Game game;

    private boolean isAudioOn = true;
    private float currentVolume = 1.0f;

    private final AssetManager assetManager;

    public MainMenuScreen(Game game, AssetManager assetManager) {
        this.game = game;
        this.assetManager = assetManager;

        batch = new SpriteBatch();
        stage = new Stage(new FitViewport(1920, 1080));

        // Carregar recursos
        font = new BitmapFont();
        skin =assetManager.get(AssetPaths.UI_SKIN_JSON, Skin.class);
        background = assetManager.get(AssetPaths.BACKGROUND_MAIN, Texture.class);
        MusicManager.getInstance().play("menu", true);

        // Configuração da tabela de UI
        Table table = new Table();
        table.center();
        table.setFillParent(true);

        float buttonWidth = 400f;
        float buttonHeight = 80f;

        // Configuração dos botões
        playButton = new TextButton("Play", skin);
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SoundManager.getInstance().play("keyboardClick");
                MusicManager.getInstance().stop();;
                ((GameLauncher) game).startGame(); // Chama método de GameLauncher
            }
        });

        audioButton = new TextButton("Som: On", skin);
        audioButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SoundManager.getInstance().play("keyboardClick");
                isAudioOn = !isAudioOn;
                toggleAudio();
            }
        });

        volumeUpButton = new TextButton("Volume +", skin);
        volumeUpButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SoundManager.getInstance().play("keyboardClick");
                currentVolume = Math.min(currentVolume + 0.1f, 1.0f);
                volumeSlider.setValue(currentVolume);
                setMusicVolume();
            }
        });

        volumeDownButton = new TextButton("Volume -", skin);
        volumeDownButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SoundManager.getInstance().play("keyboardClick");
                currentVolume = Math.max(currentVolume - 0.1f, 0.0f);
                volumeSlider.setValue(currentVolume);
                setMusicVolume();
            }
        });

        controlsButton = new TextButton("Controlos", skin);
        controlsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SoundManager.getInstance().play("keyboardClick");
                stopMusic();
                game.setScreen(new ControlsScreen(game,assetManager));
            }
        });

        creditsButton = new TextButton("Creditos", skin);
        creditsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SoundManager.getInstance().play("keyboardClick");
                stopMusic();
                game.setScreen(new CreditsScreen(game,assetManager)); // Redireciona para a tela de créditos
            }
        });

        // Slider de Volume
        volumeSlider = new Slider(0.0f, 1.0f, 0.01f, false, skin);
        volumeSlider.setValue(currentVolume);
        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                currentVolume = volumeSlider.getValue();
                setMusicVolume();
            }
        });

        // Adicionando os botões à tabela
        table.add(playButton).size(buttonWidth, buttonHeight).padBottom(20).row();
        table.add(audioButton).size(buttonWidth, buttonHeight).padBottom(20).row();
        table.add(volumeUpButton).size(buttonWidth, buttonHeight).padBottom(20).row();
        table.add(volumeDownButton).size(buttonWidth, buttonHeight).padBottom(20).row();
        table.add(controlsButton).size(buttonWidth, buttonHeight).padBottom(20).row();
        table.add(creditsButton).size(buttonWidth, buttonHeight).padBottom(20).row();

        // Adiciona a tabela ao stage
        stage.addActor(table);

        // Adiciona o som de hover aos botões
        addHoverSound(playButton);
        addHoverSound(audioButton);
        addHoverSound(volumeUpButton);
        addHoverSound(volumeDownButton);
        addHoverSound(controlsButton);
        addHoverSound(creditsButton);
    }

    private void addHoverSound(TextButton button) {
        button.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                SoundManager.getInstance().play("hoverButton");
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                SoundManager.getInstance().play("hoverButton");
            }
        });
    }

    private void toggleAudio() {
        if (isAudioOn) {
            MusicManager.getInstance().unmute();
            MusicManager.getInstance().play("menu", true);
            audioButton.setText("Som: On");
        } else {
            MusicManager.getInstance().mute();
            audioButton.setText("Som: Off");
        }
    }

    private void stopMusic() {
        MusicManager.getInstance().stop();
    }

    private void setMusicVolume() {
        if (isAudioOn) {
            MusicManager.getInstance().setVolume(currentVolume);
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        if (isAudioOn && !MusicManager.getInstance().isPlaying("menu")) {
            MusicManager.getInstance().play("menu", true);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(stage.getCamera().combined);
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
        stage.dispose();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        stage.dispose();
        assetManager.dispose();
    }
}
