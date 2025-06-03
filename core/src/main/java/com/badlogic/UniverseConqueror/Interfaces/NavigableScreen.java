package com.badlogic.UniverseConqueror.Interfaces;

// Interface para ecrãs que permitem navegação básica entre menu, sair e reiniciar jogo
public interface NavigableScreen {
    // Vai para o menu principal
    void goToMainMenu();

    // Sai do jogo
    void exitGame();

    // Reinicia o jogo
    void restartGame();
}
