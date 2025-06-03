package com.badlogic.UniverseConqueror.Initializers;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.Screens.GameScreen;
import com.badlogic.UniverseConqueror.Utils.AssetPaths;
import com.badlogic.UniverseConqueror.Context.GameContext;
import com.badlogic.UniverseConqueror.Context.HUDContext;
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

/**
 * A classe `UIInitializer` é responsável por inicializar todos os elementos da interface do usuário (UI) do jogo,
 * incluindo a barra de saúde, o contador de itens coletados, o cronômetro, o joystick virtual, o ícone da câmera,
 * e o contador de inimigos mortos.
 */
public class UIInitializer extends AbstractInitializer {

    /// Referência ao ecrã principal do jogo, usada para configurar a interface
    GameScreen screen;

    /**
     * Construtor que recebe o contexto do jogo e inicializa a interface do usuário.
     *
     * @param context O contexto do jogo, usado para acessar recursos e sistemas.
     */
    public UIInitializer(GameContext context) {
        super(context);
    }

    /**
     * Método principal para inicializar todos os elementos da UI.
     */
    public void initialize() {
        /// Remove o cursor padrão do sistema, substituindo-o pelo customizado do jogo
        screen = context.getScreen();
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.None);

        /// Carrega a skin da interface e a fonte padrão para o HUD
        context.setSkin(context.getAssetManager().get(AssetPaths.UI_SKIN_JSON, Skin.class));
        context.setFont(new BitmapFont());  /// Define a fonte usada na interface


        Table footerTable = new Table();
        footerTable.setFillParent(true);
        footerTable.bottom().center();
        context.setStage(new Stage(new ScreenViewport()));  /// Cria a stage para adicionar os elementos da UI
        context.getStage().addActor(footerTable);  /// Adiciona a tabela do rodapé à stage

        // Inicializa todos os componentes da interface
        initializeLabels();  /// Inicializa os rótulos de informações (saúde, poder de ataque, itens)
        initializeTimer();  /// Inicializa o cronômetro do jogo
        initializeJoystick();  /// Inicializa o joystick virtual
        initializeCameraIcon();  /// Inicializa o ícone da câmera (com alternância)
        initializeEnemyCounter();  /// Inicializa o contador de inimigos mortos
    }

    /**
     * Inicializa os rótulos de informações na tela (saúde, ataque e itens).
     */
    private void initializeLabels() {
        HUDContext hud = context.getHUDContext();  /// Obtém o contexto do HUD

        // Obtém o componente de saúde do jogador
        ComponentMapper<HealthComponent> healthMapper = ComponentMapper.getFor(HealthComponent.class);
        HealthComponent health = healthMapper.get(context.getPlayer());

        // Cria os rótulos de saúde, ataque e itens
        Label healthLabel = new Label("Health: " + health.currentHealth, context.getSkin());
        Label attackLabel = new Label("Attack: " + context.getSystemContext().getAttackSystem().getRemainingAttackPower(), context.getSkin());
        Label itemsLabel = new Label("Items: 0", context.getSkin());

        // Define os rótulos no HUD
        hud.setHealthLabel(healthLabel);
        hud.setAttackPowerLabel(attackLabel);
        hud.setItemsLabel(itemsLabel);

        // Cria as texturas de fundo para os rótulos
        Texture uiskinTexture = new Texture("ui/uiskin.png");
        hud.setHealthBackground(new TextureRegionDrawable(new TextureRegion(uiskinTexture, 0, 80, 190, 75)));
        hud.setAttackPowerBackground(new TextureRegionDrawable(new TextureRegion(uiskinTexture, 0, 80, 190, 75)));
        hud.setItemsBackground(new TextureRegionDrawable(new TextureRegion(uiskinTexture, 0, 80, 190, 75)));

        // Organiza os rótulos em tabelas
        Table healthBox = new Table();
        healthBox.setBackground(hud.getHealthBackground());
        healthBox.add(healthLabel).pad(5);

        Table attackBox = new Table();
        attackBox.setBackground(hud.getAttackPowerBackground());
        attackBox.add(attackLabel).pad(5);

        Table itemsBox = new Table();
        itemsBox.setBackground(hud.getItemsBackground());
        itemsBox.add(itemsLabel).pad(5);

        // Cria uma tabela fixa no fundo central da tela
        Table bottomCenterTable = new Table();
        bottomCenterTable.setFillParent(true);
        bottomCenterTable.align(Align.bottom);  // Alinha a tabela no fundo
        bottomCenterTable.padBottom(20);  // Dá um pequeno afastamento da borda inferior

        bottomCenterTable.add().expandX();  // Espaço à esquerda
        bottomCenterTable.add(healthBox).padRight(5).bottom();
        bottomCenterTable.add(attackBox).padRight(5).bottom();
        bottomCenterTable.add(itemsBox).bottom();
        bottomCenterTable.add().expandX();  // Espaço à direita

        context.getStage().addActor(bottomCenterTable);  /// Adiciona a tabela de informações ao stage
    }

    /**
     * Inicializa o cronômetro de tempo de jogo.
     */
    private void initializeTimer() {
        HUDContext hud = context.getHUDContext();

        // Cria o rótulo do cronômetro e define a escala da fonte
        Label timerLabel = new Label("00:00:00", context.getSkin());
        timerLabel.setFontScale(2f);

        // Define o rótulo no HUD
        hud.setTimerLabel(timerLabel);

        // Cria a tabela para o cronômetro
        Table timerTable = new Table();
        timerTable.top().setFillParent(true);  // Alinha a tabela ao topo
        timerTable.add(hud.getTimerLabel()).expandX().center();  // Centraliza o cronômetro

        // Se o estado não foi restaurado, inicia o cronômetro
        if (!context.isRestoredState()) {
            context.getPlayingTimer().start();
        }

        context.getStage().addActor(timerTable);  /// Adiciona a tabela do cronômetro ao stage
    }

    /**
     * Inicializa o joystick virtual e o adiciona ao stage.
     */
    private void initializeJoystick() {
        Joystick joystick = context.getJoystick();  // Joystick já criado anteriormente
        if (joystick != null) {
            context.getStage().addActor(joystick);  // Adiciona o joystick ao stage
        }
    }

    /**
     * Inicializa o ícone da câmera e a funcionalidade de alternância entre ligado/desligado.
     */
    private void initializeCameraIcon() {
        HUDContext hud = context.getHUDContext();

        // Carrega as texturas de ícone de câmera ligada e desligada
        TextureRegion onTexture = new TextureRegion(GameContext.getTexture(context.getAssetManager(), AssetPaths.CAMERA_ON_ICON));
        TextureRegion offTexture = new TextureRegion(GameContext.getTexture(context.getAssetManager(), AssetPaths.CAMERA_OFF_ICON));
        hud.setCameraOnTexture(onTexture);
        hud.setCameraOffTexture(offTexture);

        // Cria o ícone de câmera e adiciona ao stage
        Image cameraIcon = new Image(hud.getCameraOnTexture());
        hud.setCameraIconImage(cameraIcon);

        Table uiTable = new Table();
        uiTable.top().left();  // Alinha o ícone no canto superior esquerdo
        uiTable.setFillParent(true);
        uiTable.add(hud.getCameraIconImage()).pad(10).size(48);  // Define o tamanho e o padding

        // Adiciona o listener para alternar o estado da câmera
        hud.getCameraIconImage().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                context.getSystemContext().getCameraInputSystem().toggleCameraFollow();  // Alterna a câmera
                boolean isFollowing = context.getSystemContext().getCameraInputSystem().isFollowingPlayer();  // Verifica o estado da câmera
                hud.updateCameraIcon(isFollowing);  // Atualiza o ícone de câmera
            }
        });

        context.getStage().addActor(uiTable);  /// Adiciona a tabela do ícone da câmera ao stage
    }

    /**
     * Inicializa o contador de inimigos eliminados e o exibe na tela.
     */
    private void initializeEnemyCounter() {
        HUDContext hud = context.getHUDContext();

        // Cria a imagem do contador de kills
        Image killedCounterImage = new Image(new Texture("Killed_alien_counter.png"));

        // Cria o rótulo do contador e aplica a configuração
        Label enemiesKilledLabel = new Label("0", context.getHUDContext().getSkin());
        enemiesKilledLabel.setFontScale(0.7f);
        enemiesKilledLabel.setAlignment(Align.center);

        // Define o rótulo no HUDContext
        hud.setEnemiesKilledLabel(enemiesKilledLabel);

        // Cria a pilha visual com a imagem e o rótulo
        Stack killsStack = new Stack();
        killsStack.add(killedCounterImage);
        killsStack.add(hud.getEnemiesKilledLabel());

        // Cria a tabela e adiciona ao stage
        Table killsTable = new Table();
        killsTable.top().right();  // Alinha no canto superior direito
        killsTable.setFillParent(true);
        killsTable.add(killsStack).size(50, 60).pad(10);  // Define o tamanho e o padding

        context.getStage().addActor(killsTable);  /// Adiciona a tabela de kills ao stage
    }
}
