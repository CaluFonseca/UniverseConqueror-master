package com.badlogic.UniverseConqueror.Screens;

import com.badlogic.UniverseConqueror.Audio.MusicManager;
import com.badlogic.UniverseConqueror.Audio.SoundManager;
import com.badlogic.UniverseConqueror.GameLauncher;
import com.badlogic.UniverseConqueror.Utils.AssetPaths;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MainMenuScreen implements Screen, SoundEnabledScreen {
    /// SpriteBatch para desenhar texturas e elementos gráficos
    private SpriteBatch batch;
    /// Palco para gerenciar a UI
    private Stage stage;
    /// Skin que contém estilos para UI
    private Skin skin;
    /// Fonte para texto
    private BitmapFont font;
    /// Textura de fundo do menu
    private Texture background;

    /// Botões da UI
    private TextButton playButton;
    private TextButton audioButton;
    private TextButton volumeUpButton;
    private TextButton volumeDownButton;
    private TextButton controlsButton;
    private TextButton creditsButton;
    /// Slider para controle de volume
    private Slider volumeSlider;
    /// Referência ao jogo para trocar telas
    private Game game;

    /// Controle de som ligado/desligado
    private boolean isAudioOn = true;
    /// Volume atual da música
    private float currentVolume = 1.0f;

    /// Gerenciador de assets
    private final AssetManager assetManager;

    /// Construtor inicializa os componentes essenciais do menu principal
    public MainMenuScreen(Game game, AssetManager assetManager) {
        this.game = game;
        this.assetManager = assetManager;

        batch = new SpriteBatch();
        stage = new Stage(new FitViewport(1920, 1080));

        font = new BitmapFont();
        skin = assetManager.get(AssetPaths.UI_SKIN_JSON, Skin.class);
        background = assetManager.get(AssetPaths.BACKGROUND_MAIN, Texture.class);
        MusicManager.getInstance().play("menu", true);

        Table table = new Table();
        table.center();
        table.setFillParent(true);

        float buttonWidth = 400f;
        float buttonHeight = 80f;

        /// Botão Play que inicia o jogo e para a música do menu
        playButton = createButton("Play", () -> {
            MusicManager.getInstance().stop();
            ((GameLauncher) game).startGame();
        });

        /// Botão para alternar o som ligado/desligado
        audioButton = createButton("Som: On", this::toggleAudio);

        /// Botão para aumentar o volume
        volumeUpButton = createButton("Volume +", () -> {
            currentVolume = Math.min(currentVolume + 0.1f, 1.0f);
            volumeSlider.setValue(currentVolume);
            setMusicVolume();
        });

        /// Botão para diminuir o volume
        volumeDownButton = createButton("Volume -", () -> {
            currentVolume = Math.max(currentVolume - 0.1f, 0.0f);
            volumeSlider.setValue(currentVolume);
            setMusicVolume();
        });

        /// Botão para abrir tela de controles, parando a música
        controlsButton = createButton("Controlos", () -> {
            stopMusic();
            game.setScreen(new ControlsScreen(game, assetManager));
        });

        /// Botão para abrir tela de créditos, parando a música
        creditsButton = createButton("Creditos", () -> {
            stopMusic();
            game.setScreen(new CreditsScreen(game, assetManager));
        });

        /// Slider de volume que atualiza volume da música
        volumeSlider = new Slider(0.0f, 1.0f, 0.01f, false, skin);
        volumeSlider.setValue(currentVolume);
        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                currentVolume = volumeSlider.getValue();
                setMusicVolume();
            }
        });

        /// Adiciona botões na tabela com espaçamento
        table.add(playButton).size(buttonWidth, buttonHeight).padBottom(20).row();
        table.add(audioButton).size(buttonWidth, buttonHeight).padBottom(20).row();
        table.add(volumeUpButton).size(buttonWidth, buttonHeight).padBottom(20).row();
        table.add(volumeDownButton).size(buttonWidth, buttonHeight).padBottom(20).row();
        table.add(controlsButton).size(buttonWidth, buttonHeight).padBottom(20).row();
        table.add(creditsButton).size(buttonWidth, buttonHeight).padBottom(20).row();

        stage.addActor(table);
    }

    /// Cria botão com texto e ação executada ao clicar
    private TextButton createButton(String text, Runnable action) {
        TextButton button = new TextButton(text, skin);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playClickSound();
                action.run();
            }
        });
        button.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                playHoverSound();
            }
        });
        return button;
    }

    /// Alterna o som entre ligado e desligado
    private void toggleAudio() {
        if (isAudioOn) {
            MusicManager.getInstance().unmute();
            MusicManager.getInstance().play("menu", true);
            audioButton.setText("Som: On");
        } else {
            MusicManager.getInstance().mute();
            audioButton.setText("Som: Off");
        }
        isAudioOn = !isAudioOn;
    }

    /// Para a música atual
    private void stopMusic() {
        MusicManager.getInstance().stop();
    }

    /// Ajusta o volume da música se o som estiver ligado
    private void setMusicVolume() {
        if (isAudioOn) {
            MusicManager.getInstance().setVolume(currentVolume);
        }
    }

    /// Chamado ao mostrar a tela, configura input e inicia música se necessário
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        if (isAudioOn && !MusicManager.getInstance().isPlaying("menu")) {
            MusicManager.getInstance().play("menu", true);
        }
    }

    /// Renderiza a tela, desenhando background e UI
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

    /// Atualiza o viewport do palco ao redimensionar a janela
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    /// Oculta a tela e libera recursos do palco
    @Override
    public void hide() {
        stage.dispose();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    /// Libera os recursos gráficos usados pelo menu
    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        stage.dispose();
        assetManager.dispose();
    }

    /// Toca som de clique em botão
    @Override
    public void playClickSound() {
        SoundManager.getInstance().play("keyboardClick");
    }

    /// Toca som de hover ao passar mouse no botão
    @Override
    public void playHoverSound() {
        SoundManager.getInstance().play("hoverButton");
    }
}
