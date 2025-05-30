package com.badlogic.UniverseConqueror.Initializers;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.Screens.GameScreen;
import com.badlogic.UniverseConqueror.Utils.AssetPaths;
import com.badlogic.UniverseConqueror.Utils.Joystick;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.ashley.core.ComponentMapper;

public class UIInitializer {

    /// Referência ao ecrã principal do jogo, usada para configurar a interface
    private final GameScreen screen;

    /// Construtor recebe a instância da GameScreen para modificar seus elementos diretamente
    public UIInitializer(GameScreen screen) {
        this.screen = screen;
    }

    /// Método principal para inicializar todos os elementos de UI
    public void initialize() {
        /// Remove o cursor padrão do sistema
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.None);

        /// Carrega a skin da interface e a fonte padrão
        screen.skin = screen.assetManager.get(AssetPaths.UI_SKIN_JSON, Skin.class);
        screen.font = new BitmapFont();

        /// Cria a tabela que ficará no rodapé com status do jogador
        screen.footerTable = new Table();
        screen.footerTable.bottom().right();
        screen.footerTable.setFillParent(true);

        /// Cria o Stage e adiciona a tabela de rodapé
        screen.stage = new Stage(new ScreenViewport());
        screen.stage.addActor(screen.footerTable);

        /// Inicializa elementos da interface
        initializeLabels();
        initializeTimer();
        initializeJoystick();
        initializeCameraIcon();
        initializeEnemyCounter();
    }

    /// Inicializa os labels de status do jogador: vida, ataque e itens coletados
    private void initializeLabels() {
        screen.healthMapper = ComponentMapper.getFor(HealthComponent.class);
        HealthComponent health = screen.healthMapper.get(screen.player);

        /// Criação dos labels de status
        screen.healthLabel = new Label("Health: " + health.currentHealth, screen.skin);
        screen.attackPowerLabel = new Label("Attack: " + screen.attackSystem.getRemainingAttackPower(), screen.skin);
        screen.itemsLabel = new Label("Items: 0", screen.skin);

        /// Define fundo visual para os status
        screen.uiskinTexture = new Texture("ui/uiskin.png");
        screen.healthBackground = new TextureRegionDrawable(new TextureRegion(screen.uiskinTexture, 0, 80, 190, 75));
        screen.attackPowerBackground = new TextureRegionDrawable(new TextureRegion(screen.uiskinTexture, 0, 80, 190, 75));
        screen.itemsBackground = new TextureRegionDrawable(new TextureRegion(screen.uiskinTexture, 0, 80, 190, 75));

        /// Agrupa cada label com seu fundo em uma tabela
        Table healthBox = new Table();
        healthBox.setBackground(screen.healthBackground);
        healthBox.add(screen.healthLabel).pad(5);

        Table attackBox = new Table();
        attackBox.setBackground(screen.attackPowerBackground);
        attackBox.add(screen.attackPowerLabel).pad(5);

        Table itemsBox = new Table();
        itemsBox.setBackground(screen.itemsBackground);
        itemsBox.add(screen.itemsLabel).pad(5);

        /// Adiciona os boxes na tabela principal
        screen.footerTable.center().bottom();
        screen.footerTable.add(healthBox).pad(10).left();
        screen.footerTable.add(attackBox).pad(10).left();
        screen.footerTable.add(itemsBox).pad(10).left();
    }

    /// Inicializa o cronômetro de tempo de jogo
    private void initializeTimer() {
        screen.timerLabel = new Label("00:00:00", screen.skin);
        screen.timerLabel.setFontScale(2f);

        /// Tabela que posiciona o timer no topo
        Table timerTable = new Table();
        timerTable.top().setFillParent(true);
        timerTable.add(screen.timerLabel).expandX().center();

        /// Inicia o timer se o jogo não estiver restaurando um estado salvo
        if (!screen.restoredState) {
            screen.playingTimer.start();
        }

        screen.stage.addActor(timerTable);
    }

    /// Inicializa o joystick virtual e o adiciona ao stage
    private void initializeJoystick() {
        Texture base = screen.assetManager.get(AssetPaths.JOYSTICK_BASE, Texture.class);
        Texture knob = screen.assetManager.get(AssetPaths.JOYSTICK_KNOB, Texture.class);
        screen.joystick = new Joystick(base, knob, 100f, 100f, 60f);
        screen.stage.addActor(screen.joystick);
    }

    /// Adiciona ícone de câmera com alternância visual entre ligada e desligada
    private void initializeCameraIcon() {
        screen.cameraIconImage = new Image(screen.cameraOnTexture);

        /// Posiciona o ícone no canto superior esquerdo
        Table uiTable = new Table();
        uiTable.top().left();
        uiTable.setFillParent(true);
        uiTable.add(screen.cameraIconImage).pad(10).size(48);

        /// Clique no ícone alterna o visual com base no estado atual
        screen.cameraIconImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Texture cameraOn = screen.assetManager.get(AssetPaths.CAMERA_ON_ICON, Texture.class);
                Texture cameraOff = screen.assetManager.get(AssetPaths.CAMERA_OFF_ICON, Texture.class);
                screen.cameraIconImage.setDrawable(new TextureRegionDrawable(new TextureRegion(
                    screen.cameraInputSystem.isFollowingPlayer() ? cameraOn : cameraOff
                )));
            }
        });

        screen.stage.addActor(uiTable);
    }

    /// Mostra o contador de inimigos eliminados com imagem e número sobrepostos
    private void initializeEnemyCounter() {
        Image killedCounterImage = new Image(new Texture("Killed_alien_counter.png"));
        screen.enemiesKilledLabel = new Label("0", screen.skin);
        screen.enemiesKilledLabel.setFontScale(0.7f);
        screen.enemiesKilledLabel.setAlignment(Align.center);

        /// Empilha imagem de fundo e label
        Stack killsStack = new Stack();
        killsStack.add(killedCounterImage);
        killsStack.add(screen.enemiesKilledLabel);

        /// Posiciona o contador no canto superior direito
        Table killsTable = new Table();
        killsTable.top().right();
        killsTable.setFillParent(true);
        killsTable.add(killsStack).size(50, 60).pad(10);

        screen.stage.addActor(killsTable);
    }
}
