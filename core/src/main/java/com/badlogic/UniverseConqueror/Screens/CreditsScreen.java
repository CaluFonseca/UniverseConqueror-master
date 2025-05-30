package com.badlogic.UniverseConqueror.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;

/// ecrã de créditos do jogo, herdando da BaseInfosScreen
public class CreditsScreen extends BaseInfosScreen {

    /// Construtor que passa o texto dos créditos para a classe base
    public CreditsScreen(Game game, AssetManager assetManager) {
        super(game,
            "Cláudio Fonseca - 20240628 \n" +
                "Fernando Simões - 20241477 \n" +
                "Paulo Ferreira - \n" +
                "Vítor Hugo Freitas - 20241067",
            assetManager);
    }
}
