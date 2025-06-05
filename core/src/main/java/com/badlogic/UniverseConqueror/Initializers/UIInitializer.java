package com.badlogic.UniverseConqueror.Initializers;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.Screens.GameScreen;
import com.badlogic.UniverseConqueror.Utils.AssetPaths;
import com.badlogic.UniverseConqueror.Context.GameContext;
import com.badlogic.UniverseConqueror.Context.HUDContext;
import com.badlogic.UniverseConqueror.Utils.Joystick;
import com.badlogic.UniverseConqueror.Utils.Minimap;
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
 * A classe `UIInitializer` é responsável por inicializar todos os elementos da UI do jogo,
 * incluindo a barra de saúde, o contador de itens coletados, o cronômetro, o joystick virtual, o ícone da câmera,
 * e o contador de inimigos mortos.
 */
public class UIInitializer extends AbstractInitializer {

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
        // Remove o cursor padrão do sistema, substituindo-o pelo customizado do jogo
        screen = context.getScreen();
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.None);

        // Carrega a skin da interface e a fonte padrão para o HUD
        context.setSkin(context.getAssetManager().get(AssetPaths.UI_SKIN_JSON, Skin.class));
        context.setFont(new BitmapFont());


        Table footerTable = new Table();
        footerTable.setFillParent(true);
        footerTable.bottom().center();
        context.setStage(new Stage(new ScreenViewport()));
        context.getStage().addActor(footerTable);

        // Inicializa todos os componentes da interface
        initializeLabels();
        initializeTimer();
        initializeJoystick();
        initializeCameraIcon();
        initializeEnemyCounter();
        initializeMinimap();
    }

    /**
     * Inicializa os rótulos de informações na tela (saúde, ataque e itens).
     */
    private void initializeLabels() {
        HUDContext hud = context.getHUDContext();

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

        // Cria uma tabela fixa centrada no fundo
        Table bottomCenterTable = new Table();
        bottomCenterTable.setFillParent(true);
        bottomCenterTable.align(Align.bottom);
        bottomCenterTable.padBottom(20);

        bottomCenterTable.add().expandX();
        bottomCenterTable.add(healthBox).padRight(5).bottom();
        bottomCenterTable.add(attackBox).padRight(5).bottom();
        bottomCenterTable.add(itemsBox).bottom();
        bottomCenterTable.add().expandX();

        context.getStage().addActor(bottomCenterTable);
    }

    /**
     * Inicializa o cronômetro de tempo de jogo.
     */
    private void initializeTimer() {
        HUDContext hud = context.getHUDContext();
        // Cria o rótulo do cronômetro e define a escala da fonte
        Label timerLabel = new Label("00:00:00", context.getSkin());
        timerLabel.setFontScale(2f);
        hud.setTimerLabel(timerLabel);
        Table timerTable = new Table();
        timerTable.top().setFillParent(true);
        timerTable.add(hud.getTimerLabel()).expandX().center();

        // Se o estado não foi restaurado, inicia o cronômetro
        if (!context.isRestoredState()) {
            context.getPlayingTimer().start();
        }

        context.getStage().addActor(timerTable);
    }

    /**
     * Inicializa o joystick virtual e o adiciona ao stage.
     */
    private void initializeJoystick() {
        Joystick joystick = context.getJoystick();
        if (joystick != null) {
            context.getStage().addActor(joystick);
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
        uiTable.top().left();
        uiTable.setFillParent(true);
        uiTable.add(hud.getCameraIconImage()).pad(10).size(48);

        // Adiciona o listener para alternar o estado da câmera
        hud.getCameraIconImage().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                context.getSystemContext().getCameraInputSystem().toggleCameraFollow();
                boolean isFollowing = context.getSystemContext().getCameraInputSystem().isFollowingPlayer();
                hud.updateCameraIcon(isFollowing);
            }
        });

        context.getStage().addActor(uiTable);
    }

    /**
     * Inicializa o contador de inimigos eliminados e o exibe no ecrã.
     */
    private void initializeEnemyCounter() {
        HUDContext hud = context.getHUDContext();

        // Cria a imagem do contador de kills
        Image killedCounterImage = new Image(new Texture("Killed_alien_counter.png"));

        Label enemiesKilledLabel = new Label("0", context.getHUDContext().getSkin());
        enemiesKilledLabel.setFontScale(0.7f);
        enemiesKilledLabel.setAlignment(Align.center);

        hud.setEnemiesKilledLabel(enemiesKilledLabel);

        Stack killsStack = new Stack();
        killsStack.add(killedCounterImage);
        killsStack.add(hud.getEnemiesKilledLabel());

        Table killsTable = new Table();
        killsTable.top().right();
        killsTable.setFillParent(true);
        killsTable.add(killsStack).size(50, 60).pad(10);

        context.getStage().addActor(killsTable);
    }

    /**
     * Inicializa o minimapa com marcador de jogador.
     */
    private void initializeMinimap() {
        float minimapWidth = 150;
        float minimapHeight = 150;
        float margin = 10;

        float x = Gdx.graphics.getWidth() - minimapWidth - margin;
        float y = margin;

        Minimap minimap = new Minimap(
            context.getAssetManager().get(AssetPaths.MINIMAP_BACKGROUND, Texture.class),
            context.getAssetManager().get(AssetPaths.PLAYER_MARKER, Texture.class),
            x, y,
            minimapWidth, minimapHeight,
            context.getPlayer()
        );

        minimap.setWorldSize(
            context.getWorldContext().getMapWidth(),
            context.getWorldContext().getMapHeight()
        );

        context.getStage().addActor(minimap);
    }
}
