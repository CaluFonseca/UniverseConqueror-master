package com.badlogic.UniverseConqueror.Screens;

import com.badlogic.UniverseConqueror.Audio.SoundManager;
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

/// Classe abstrata base para ecrãs de informação, com UI e sons padrões
public abstract class BaseInfosScreen implements Screen, SoundEnabledScreen, NavigableScreen {
    protected Game game;               /// Instância do jogo para controle de ecrãs
    protected BitmapFont font;         /// Fonte para desenhar texto
    protected SpriteBatch batch;       /// Batch para renderizar texto e gráficos
    protected GlyphLayout layout;      /// Para medir e posicionar o texto

    protected Stage stage;             /// Cena para controle dos atores (botões, etc)
    protected Skin skin;               /// Skin para estilo visual dos widgets
    protected TextButton backButton;  /// Botão "Back" para voltar ao menu principal

    protected String screenText;       /// Texto exibido no ecrã
    protected final AssetManager assetManager;  /// Gerenciador de assets (skins, sons, etc)

    /// Construtor que inicializa recursos e UI, recebendo texto e assetManager
    public BaseInfosScreen(Game game, String screenText, AssetManager assetManager) {
        this.game = game;
        this.screenText = screenText;
        this.assetManager = assetManager;
        initializeResources();
        initializeUI();
    }

    /// Inicializa fontes, batch, stage e skin
    private void initializeResources() {
        font = new BitmapFont();
        batch = new SpriteBatch();
        layout = new GlyphLayout();
        stage = new Stage(new ScreenViewport());
        skin = assetManager.get("ui/uiskin.json", Skin.class);
    }

    /// Inicializa o botão Back com listeners para click e hover, adiciona ao stage
    private void initializeUI() {
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

    /// Define o input processor para receber eventos de UI
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    /// Limpa o ecrã, desenha texto e desenha a stage com atores
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        drawScreenText();

        stage.act(delta);
        stage.draw();
    }

    /// Desenha o texto centralizado no ecrã usando GlyphLayout e BitmapFont
    private void drawScreenText() {
        layout.setText(font, screenText);

        float x = (Gdx.graphics.getWidth() - layout.width) / 2;
        float y = (Gdx.graphics.getHeight() + layout.height) / 2;

        batch.begin();
        font.draw(batch, layout, x, y);
        batch.end();
    }

    /// Atualiza a viewport do stage para o novo tamanho do ecrã
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    /// Quando o ecrã é escondido, descarta recursos
    @Override
    public void hide() {
        dispose();
    }

    /// Pausa do ecrã, vazio (opcional)
    @Override
    public void pause() {}

    /// Retoma do ecrã, vazio (opcional)
    @Override
    public void resume() {}

    /// Libera os recursos gráficos do ecrã
    @Override
    public void dispose() {
        font.dispose();
        batch.dispose();
        stage.dispose();
    }

    /// Toca o som de clique de botão
    @Override
    public void playClickSound() {
        SoundManager.getInstance().play("keyboardClick");
    }

    /// Toca o som de hover do mouse em botão
    @Override
    public void playHoverSound() {
        SoundManager.getInstance().play("hoverButton");
    }

    /// Navega para o ecrã do menu principal
    @Override
    public void goToMainMenu() {
        game.setScreen(new MainMenuScreen(game, assetManager));
    }

    /// Sai do jogo
    @Override
    public void exitGame() {
        Gdx.app.exit();
    }

    /// Reinício do jogo (vazio - opcional para este ecrã)
    @Override
    public void restartGame() {
        // Optional for BaseInfosScreen
    }
}
