package com.badlogic.UniverseConqueror.ECS.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;

public class ParticleComponent implements Component {

    // Efeito de partícula que será renderizado
    public ParticleEffect effect;

    // Deslocamento em relação ao centro da entidade para posicionar o efeito
    public Vector2 offset = new Vector2();

    // Rotação do efeito
    public float rotation = 0f;
}
