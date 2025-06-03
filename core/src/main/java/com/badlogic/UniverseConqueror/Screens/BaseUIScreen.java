package com.badlogic.UniverseConqueror.Screens;

import com.badlogic.UniverseConqueror.Audio.MusicManager;
import com.badlogic.UniverseConqueror.Audio.SoundManager;
import com.badlogic.UniverseConqueror.GameLauncher;
import com.badlogic.UniverseConqueror.Interfaces.BaseScreen;
import com.badlogic.UniverseConqueror.Interfaces.NavigableScreen;
import com.badlogic.UniverseConqueror.Interfaces.SoundEnabledScreen;
import com.badlogic.UniverseConqueror.Utils.AssetPaths;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
/**
 * Classe abstrata `BaseUIScreen` que fornece a base para ecrãs do jogo,
 * implementando a interface `BaseScreen`, `NavigableScreen`, e `SoundEnabledScreen`.
 * Essa classe lida com a configuração inicial, o gerenciamento de música de fundo,
 * a criação da interface e os sistemas básicos de interação.
 */
public abstract class BaseUIScreen extends ScreenAdapter
    implements BaseScreen, NavigableScreen, SoundEnabledScreen {

    protected final GameLauncher game;
    protected final AssetManager assetManager;
    protected Stage stage;
    protected Skin skin;

    private SpriteBatch batch;
    protected Texture background;
    private boolean playBackgroundMusic = true;
    private String musicTrack = "menu";

    public BaseUIScreen(GameLauncher game, AssetManager assetManager) {
        this.game = game;
        this.assetManager = assetManager;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        stage = new Stage(new FitViewport(1920, 1080));
        skin = assetManager.get(AssetPaths.UI_SKIN_JSON, Skin.class);
        background = assetManager.get(AssetPaths.BACKGROUND_MAIN, Texture.class);

        Gdx.input.setInputProcessor(stage);
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);

        if (playBackgroundMusic && !MusicManager.getInstance().isPlaying(musicTrack)) {
            MusicManager.getInstance().play(musicTrack, true);
        }

        initializeUI();
        initializeSystems();
        registerObservers();
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
    public void dispose() {
        disposeResources();
    }

    @Override
    public void disposeResources() {
        if (batch != null) batch.dispose();
        if (stage != null) stage.dispose();
    }

    @Override
    public void playClickSound() {
        SoundManager.getInstance().play("keyboardClick");
    }

    @Override
    public void playHoverSound() {
        SoundManager.getInstance().play("hoverButton");
    }

    protected TextButton createButton(String text, Runnable action) {
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

    // Permite que subclasses desativem a música ou troquem a faixa
    protected void setPlayBackgroundMusic(boolean enabled, String track) {
        this.playBackgroundMusic = enabled;
        this.musicTrack = track;
    }
}
