package com.badlogic.UniverseConqueror.Screens;

import com.badlogic.UniverseConqueror.Interfaces.BaseScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;

// ecrã de controles do jogo, herdando da BaseInfosScreen
public class ControlsScreen extends BaseInfosScreen implements BaseScreen {

    // Construtor que passa o texto dos controles para a classe base
    public ControlsScreen(Game game, AssetManager assetManager) {
        super(game,
            "Controlos do Jogo: \n" +
                "- W: Mover para cima\n" +
                "- S: Mover para baixo\n" +
                "- A: Mover para esquerda\n" +
                "- C: Ativar Câmera\n" +
                "- RightMouse: Mover Câmera\n" +
                "- Shift + WASD: Mover mais rápido\n" +
                "- LeftMouse: Tiro\n" +
                "- E + LeftMouse: SuperAtaque\n" +
                "- Espaço: Salto\n" +
                "- TAB: Defender\n" +
                "- ESC: Pausa \n" +
                "- H: Procura item mais próximo\n" +
                "- F: Procura Spaceship ",
            assetManager);
    }

    // Chama o método show da superclasse (inicializa input processor, etc)
    @Override
    public void show() {
        super.show();
    }

    @Override
    public void initializeUI() {
        super.initializeUI();
    }

    @Override
    public void initializeSystems() {

    }

    @Override
    public void registerObservers() {

    }

    @Override
    public void disposeResources() {

    }
}
