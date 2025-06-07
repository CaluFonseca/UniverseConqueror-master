package com.badlogic.UniverseConqueror.Screens;

import com.badlogic.UniverseConqueror.Interfaces.BaseScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;

// ecrã de créditos do jogo, herdando da BaseInfosScreen
public class CreditsScreen extends BaseInfosScreen implements BaseScreen {

    // Construtor que passa o texto dos créditos para a classe base
    public CreditsScreen(Game game, AssetManager assetManager) {
        super(game,
            "Cláudio Fonseca - 20240628 \n" +
                "Fernando Simões - 20241477 \n" +
                "Paulo Ferreira - 20240614\n" +
                "Vítor Hugo Freitas - 20241067",
            assetManager);
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
