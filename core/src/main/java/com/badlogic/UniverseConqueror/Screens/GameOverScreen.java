package com.badlogic.UniverseConqueror.Screens;

import com.badlogic.UniverseConqueror.Audio.MusicManager;
import com.badlogic.UniverseConqueror.Audio.SoundManager;
import com.badlogic.UniverseConqueror.GameLauncher;
import com.badlogic.UniverseConqueror.Utils.AssetPaths;
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

/// Tela de Game Over, exibe opções para reiniciar, voltar ao menu ou sair do jogo
public class GameOverScreen implements Screen, SoundEnabledScreen, NavigableScreen {
    private final GameLauncher game;
    private Stage stage;
    private Skin skin;
    private Table table;
    private SpriteBatch batch;
    private Texture background;

    private final AssetManager assetManager;

    /// Construtor recebe referência ao jogo e ao asset manager para carregar recursos
    public GameOverScreen(GameLauncher game, AssetManager assetManager) {
        this.game = game;
        this.assetManager = assetManager;
    }

    /// Inicializa elementos gráficos, sons e layout da tela
    @Override
    public void show() {
        stage = new Stage(new FitViewport(1920, 1080));
        Gdx.input.setInputProcessor(stage);
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);

        // Toca som de game over
        SoundManager.getInstance().play("gameOver");

        batch = new SpriteBatch();
        skin = assetManager.get(AssetPaths.UI_SKIN_JSON, Skin.class);
        background = assetManager.get(AssetPaths.BACKGROUND_PAUSE, Texture.class);

        // Ajusta volume e toca música do menu
        MusicManager.getInstance().setVolume(0.2f);
        MusicManager.getInstance().play("menu", true);

        table = new Table();
        table.center();
        table.setFillParent(true);

        // Cria título e botões da tela
        Label gameOverLabel = new Label("GAME OVER", skin, "title");
        gameOverLabel.setFontScale(1.8f);
        table.add(gameOverLabel).padBottom(50).row();

        float buttonWidth = 400f;
        float buttonHeight = 80f;

        TextButton restartButton = createButton("Reiniciar", this::restartGame);
        TextButton mainMenuButton = createButton("Menu Principal", this::goToMainMenu);
        TextButton exitButton = createButton("Sair", this::exitGame);

        table.add(restartButton).size(buttonWidth, buttonHeight).pad(10).row();
        table.add(mainMenuButton).size(buttonWidth, buttonHeight).pad(10).row();
        table.add(exitButton).size(buttonWidth, buttonHeight).pad(10).row();

        stage.addActor(table);
    }

    /// Cria botão com texto e ação, com sons para clique e hover
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

    /// Renderiza a tela limpando o fundo e desenhando o stage
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

    /// Ajusta viewport no redimensionamento da janela
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    /// Quando a tela é ocultada, libera recursos
    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    /// Libera recursos utilizados pela tela
    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
    }

    /// Toca som de clique em botão
    @Override
    public void playClickSound() {
        SoundManager.getInstance().play("keyboardClick");
    }

    /// Toca som ao passar o mouse sobre botão
    @Override
    public void playHoverSound() {
        SoundManager.getInstance().play("hoverButton");
    }

    /// Volta para o menu principal
    @Override
    public void goToMainMenu() {
        game.setScreen(new MainMenuScreen(game, assetManager));
    }

    /// Sai do jogo
    @Override
    public void exitGame() {
        Gdx.app.exit();
    }

    /// Reinicia o jogo, para sons e inicia nova partida
    @Override
    public void restartGame() {
        SoundManager.getInstance().stop();
        MusicManager.getInstance().stop();
        game.setNewGame(true);
        game.setScreen(new GameScreen(game, assetManager));
    }
}
