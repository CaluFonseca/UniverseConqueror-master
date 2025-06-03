package com.badlogic.UniverseConqueror.Screens;

import com.badlogic.UniverseConqueror.Audio.SoundManager;
import com.badlogic.UniverseConqueror.GameLauncher;
import com.badlogic.UniverseConqueror.Interfaces.InfoScreenUI;
import com.badlogic.UniverseConqueror.Interfaces.InitializableScreen;
import com.badlogic.UniverseConqueror.Interfaces.NavigableScreen;
import com.badlogic.UniverseConqueror.Interfaces.SoundEnabledScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

// Classe abstrata base para ecrãs de informação, com UI e sons padrões
public abstract class BaseInfosScreen implements Screen,
    InitializableScreen,
    InfoScreenUI,
    SoundEnabledScreen,
    NavigableScreen {

    protected final Game game;
    protected final String screenText;
    protected final AssetManager assetManager;

    protected BitmapFont font;
    protected SpriteBatch batch;
    protected GlyphLayout layout;

    protected Stage stage;
    protected Skin skin;
    protected TextButton backButton;

    public BaseInfosScreen(Game game, String screenText, AssetManager assetManager) {
        this.game = game;
        this.screenText = screenText;
        this.assetManager = assetManager;
        initializeResources();
        initializeUI();
    }

    @Override
    public void initializeResources() {
        font = new BitmapFont();
        batch = new SpriteBatch();
        layout = new GlyphLayout();
        stage = new Stage(new ScreenViewport());
        skin = assetManager.get("ui/uiskin.json", Skin.class);
    }

    @Override
    public void initializeUI() {
        backButton = new TextButton("Back", skin);
        backButton.setPosition(20, 20);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playClickSound();
                goToMainMenu();
            }
        });
        backButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                playHoverSound();
            }
        });
        stage.addActor(backButton);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        drawScreenText();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void drawScreenText() {
        layout.setText(font, screenText);
        float x = (Gdx.graphics.getWidth() - layout.width) / 2;
        float y = (Gdx.graphics.getHeight() + layout.height) / 2;
        batch.begin();
        font.draw(batch, layout, x, y);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override public void pause() {}
    @Override public void resume() {}

    @Override
    public void dispose() {
        font.dispose();
        batch.dispose();
        stage.dispose();
    }

    @Override
    public void playClickSound() {
        SoundManager.getInstance().play("keyboardClick");
    }

    @Override
    public void playHoverSound() {
        SoundManager.getInstance().play("hoverButton");
    }

    @Override
    public void goToMainMenu() {
        ((GameLauncher) game).goToMainMenu();
    }

    @Override
    public void exitGame() {
        Gdx.app.exit();
    }

    @Override
    public void restartGame() {

    }
}
