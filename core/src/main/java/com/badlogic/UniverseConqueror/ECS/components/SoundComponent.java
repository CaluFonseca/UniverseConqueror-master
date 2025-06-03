package com.badlogic.UniverseConqueror.ECS.components;

import com.badlogic.ashley.core.Component;

public class SoundComponent implements Component {

    // Chave do som a ser reproduzido
    public String soundKey = null;

    // Indica se o som deve ser reproduzido neste frame
    public boolean play = false;

    public SoundComponent() {}
}
