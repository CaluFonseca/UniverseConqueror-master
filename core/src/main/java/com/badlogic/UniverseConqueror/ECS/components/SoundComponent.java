package com.badlogic.UniverseConqueror.ECS.components;

import com.badlogic.ashley.core.Component;

public class SoundComponent implements Component {
    public String soundKey = null;  // Chave lógica (ex: "jump", "hurt")
    public boolean play = false;

    public SoundComponent() {
    }


}
