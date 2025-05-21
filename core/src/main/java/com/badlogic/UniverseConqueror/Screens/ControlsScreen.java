package com.badlogic.UniverseConqueror.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;


public class ControlsScreen extends BaseInfosScreen {

 //   private final AssetManager assetManager;

    public ControlsScreen(Game game, AssetManager assetManager) {
        super(game, "Controlos do Jogo: \n- W: Mover para cima\n- S: Mover para baixo\n- A: Mover para esquerda\n"
            + "- C: Ativar Câmera\n- RightMouse: Mover Câmera\n- Shift + WASD: Mover mais rápido\n"
            + "- LeftMouse: Tiro\n- E + LeftMouse: SuperAtaque\n- Espaço: Salto\n"
            + "- TAB: Defender\n- ESC: Pausa \n- H: Procura item mais próximo\n- H: Procura Spaceship ", assetManager);
    }
}
