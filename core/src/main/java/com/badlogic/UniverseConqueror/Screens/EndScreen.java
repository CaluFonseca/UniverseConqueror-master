package com.badlogic.UniverseConqueror.Screens;

import com.badlogic.UniverseConqueror.Audio.MusicManager;
import com.badlogic.UniverseConqueror.Audio.SoundManager;
import com.badlogic.UniverseConqueror.GameLauncher;
import com.badlogic.UniverseConqueror.Interfaces.NavigableScreen;
import com.badlogic.UniverseConqueror.Interfaces.SoundEnabledScreen;
import com.badlogic.UniverseConqueror.State.GameStateManager;
import com.badlogic.UniverseConqueror.Utils.AssetPaths;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/// ecrã final do nível que mostra estatísticas e opções para reiniciar ou sair do jogo
public class EndScreen extends ScreenAdapter implements SoundEnabledScreen, NavigableScreen {

    private final GameLauncher game;
    private final Stage stage;
    private final Skin skin;

    private final int collectedItems;
    private final int remainingHealth;
    private final float totalTime;
    private final AssetManager assetManager;

    private final int enemiesKilled;

    /// Construtor que inicializa o ecrã com as estatísticas do jogo e configura UI e som
    public EndScreen(GameLauncher game, AssetManager assetManager, int collectedItems, int remainingHealth, float totalTime, int enemiesKilled) {
        this.game = game;
        this.assetManager = assetManager;
        this.collectedItems = collectedItems;
        this.remainingHealth = remainingHealth;
        this.totalTime = totalTime;
        this.enemiesKilled = enemiesKilled;

        this.stage = new Stage(new ScreenViewport());
        this.skin = new Skin(Gdx.files.internal(AssetPaths.UI_SKIN_JSON));

        Gdx.input.setInputProcessor(stage);
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);

        setupUI();
    }

    /// Cria e posiciona os elementos da interface gráfica (labels, botões, etc)
    private void setupUI() {
        Table table = new Table();
        table.setFillParent(true);
        table.center();

        Label titleLabel = new Label("Fim de Nivel", skin, "title");
        Label timeLabel = new Label("Tempo de jogo: " + String.format("%.2f", totalTime) + "s", skin);
        Label itemLabel = new Label("Itens coletados: " + collectedItems, skin);
        Label healthLabel = new Label("Vida restante: " + remainingHealth, skin);
        Label enemyLabel = new Label("Inimigos derrotados: " + enemiesKilled, skin);

        TextButton retryButton = createButton("Voltar a Jogar", () -> {
            MusicManager.getInstance().stop();
            restartGame();
        });

        TextButton exitButton = createButton("Sair", this::exitGame);

        table.add(titleLabel).padBottom(20f).row();
        table.add(timeLabel).padBottom(10f).row();
        table.add(itemLabel).padBottom(10f).row();
        table.add(healthLabel).padBottom(10f).row();
        table.add(enemyLabel).padBottom(20f).row();
        table.add(retryButton).width(400).pad(10).row();
        table.add(exitButton).width(400).pad(10).row();

        stage.addActor(table);

        // Toca som indicando avanço de nível
        SoundManager.getInstance().play("nextLevel");
    }

    /// Cria um botão com texto e ação associada, incluindo sons para clique e hover
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

    /// Renderiza o ecrã limpando o fundo e desenhando o stage
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    /// Libera recursos do ecrã
    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    /// Toca som ao clicar
    @Override
    public void playClickSound() {
        SoundManager.getInstance().play("keyboardClick");
    }

    /// Toca som ao passar o mouse sobre botões
    @Override
    public void playHoverSound() {
        SoundManager.getInstance().play("hoverButton");
    }

    /// Navega para o menu principal do jogo
    @Override
    public void goToMainMenu() {
        SoundManager.getInstance().stop();

        game.setScreen(new MainMenuScreen(game, assetManager));
    }

    /// Sai do jogo limpando estado salvo
    @Override
    public void exitGame() {
        GameStateManager.delete();
        Gdx.app.exit();
    }

    /// Reinicia o jogo e para sons e músicas em execução
    @Override
    public void restartGame() {
        SoundManager.getInstance().stop();
        MusicManager.getInstance().stop();
        game.startGame();
    }
}
