package com.badlogic.UniverseConqueror.Interfaces;

import com.badlogic.gdx.Screen;

public interface ScreenManager {
    void show(ScreenType type);
    void show(ScreenType type, Object... args);
    Screen getCurrent();
}
