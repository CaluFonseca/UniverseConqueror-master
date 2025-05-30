package com.badlogic.UniverseConqueror.Interfaces;

/// Interface para ecrãs que suportam sons de clique e hover
public interface SoundEnabledScreen {
    /// Método para tocar som de clique
    void playClickSound();

    /// Método para tocar som de hover
    void playHoverSound();
}
