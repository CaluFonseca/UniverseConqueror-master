package com.badlogic.UniverseConqueror.Interfaces;

import com.badlogic.gdx.Screen;
/**
 * Interface `ScreenManager` permite gerir os diferentes screens.
 */
public interface ScreenManager {
    void show(ScreenType type);
    void show(ScreenType type, Object... args);
    Screen getCurrent();
}
