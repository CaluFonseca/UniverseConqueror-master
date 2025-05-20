package com.badlogic.UniverseConqueror.ECS.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class AnimationComponent implements Component {
    // Mapa que associa os estados da animação aos seus respectivos tipos de animação
    public ObjectMap<StateComponent.State, Animation<TextureRegion>> animations = new ObjectMap<>();
    public float stateTime = 0f; // Tempo de execução da animação
    public TextureRegion currentFrame; // O quadro atual da animação
    public boolean facingRight = true; //

    public void setAnimations(ObjectMap<StateComponent.State, Animation<TextureRegion>> animations) {
        this.animations = animations;
    }

}
