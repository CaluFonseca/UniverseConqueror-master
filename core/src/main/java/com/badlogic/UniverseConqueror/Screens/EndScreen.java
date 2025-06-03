package com.badlogic.UniverseConqueror.Screens;

import com.badlogic.UniverseConqueror.Audio.MusicManager;
import com.badlogic.UniverseConqueror.Audio.SoundManager;
import com.badlogic.UniverseConqueror.GameLauncher;
import com.badlogic.UniverseConqueror.Interfaces.NavigableScreen;
import com.badlogic.UniverseConqueror.State.GameStateManager;
import com.badlogic.UniverseConqueror.Utils.AssetPaths;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
/**
 * Classe `EndScreen` representa o ecrã de finalização de nível do jogo.
 * Exibe estatísticas do jogo como itens coletados, vida restante, tempo de jogo e inimigos derrotados.
 * Também oferece opções para reiniciar o jogo ou sair.
 */
public class EndScreen extends BaseUIScreen implements NavigableScreen {

    private final int collectedItems;
    private final int remainingHealth;
    private final float totalTime;
    private final int enemiesKilled;

    public EndScreen(GameLauncher game, AssetManager assetManager,
                     int itemsCollected, int health, float totalTime, int enemiesKilled) {
        super(game, assetManager);
        this.collectedItems = itemsCollected;
        this.remainingHealth = health;
        this.totalTime = totalTime;
        this.enemiesKilled = enemiesKilled;
    }

    //Método que inicializa a interface UI
    @Override
    public void initializeUI() {
        background = assetManager.get(AssetPaths.BACKGROUND_PAUSE, Texture.class);

        MusicManager.getInstance().play("menu", true);
        MusicManager.getInstance().setVolume(0.3f);
        SoundManager.getInstance().play("nextLevel");

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        Label titleLabel = new Label("Fim de Nível", skin, "title");
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
    }

    @Override
    public void restartGame() {
        SoundManager.getInstance().stop();
        MusicManager.getInstance().stop();
        game.startGame();
    }

    @Override
    public void exitGame() {
        GameStateManager.delete();
        Gdx.app.exit();
    }

    @Override
    public void goToMainMenu() {
        SoundManager.getInstance().stop();
        game.goToMainMenu();
    }

    @Override public void initializeSystems() {}
    @Override public void registerObservers() {}
}
