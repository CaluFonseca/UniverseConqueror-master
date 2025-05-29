package com.badlogic.UniverseConqueror.Screens;

/// Interface para telas que suportam sons de clique e hover
public interface SoundEnabledScreen {
    /// Método para tocar som de clique
    void playClickSound();

    /// Método para tocar som de hover
    void playHoverSound();
}
