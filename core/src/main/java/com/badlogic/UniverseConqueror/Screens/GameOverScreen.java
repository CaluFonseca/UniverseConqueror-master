package com.badlogic.UniverseConqueror.Screens;

import com.badlogic.UniverseConqueror.Audio.MusicManager;
import com.badlogic.UniverseConqueror.Audio.SoundManager;
import com.badlogic.UniverseConqueror.GameLauncher;
import com.badlogic.UniverseConqueror.Interfaces.*;
import com.badlogic.UniverseConqueror.Utils.AssetPaths;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
/**
 * Classe `GameOverScreen` representa o ecrã de game over
 * Oferece opções para reiniciar o jogo, voltar ao menu principal ou sair do jogo.
 */
public class GameOverScreen extends BaseUIScreen implements SoundEnabledScreen, NavigableScreen {

    private final ScreenManager screenManager;

    public GameOverScreen(GameLauncher game, AssetManager assetManager, ScreenManager screenManager) {
        super(game, assetManager);
        this.screenManager = screenManager;
    }

    @Override
    public void initializeUI() {
        background = assetManager.get(AssetPaths.BACKGROUND_PAUSE, Texture.class);
        SoundManager.getInstance().play("gameOver");
        MusicManager.getInstance().setVolume(0.2f);
        MusicManager.getInstance().play("menu", true);

        Table table = new Table();
        table.center();
        table.setFillParent(true);

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

    @Override
    public void restartGame() {
        SoundManager.getInstance().stop();
        MusicManager.getInstance().stop();
        game.setNewGame(true);
        screenManager.show(ScreenType.GAME);
    }

    @Override
    public void goToMainMenu() {
        screenManager.show(ScreenType.MAIN_MENU);
    }

    @Override
    public void exitGame() {
        Gdx.app.exit();
    }

    @Override public void initializeSystems() {}
    @Override public void registerObservers() {}
}
