package com.badlogic.UniverseConqueror.ECS.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ObjectMap;

public class AnimationComponent implements Component {

    // Mapa que associa estados do componente State a animações específicas
    public ObjectMap<StateComponent.State, Animation<TextureRegion>> animations = new ObjectMap<>();

    // Tempo acumulado desde o início da animação atual
    public float stateTime = 0f;

    // Quadro atual da animação a ser renderizado
    public TextureRegion currentFrame;

    // Direção em que a entidade está virada (true = direita)
    public boolean facingRight = true;

    // Define o conjunto de animações para este componente
    public void setAnimations(ObjectMap<StateComponent.State, Animation<TextureRegion>> animations) {
        this.animations = animations;
    }

}
