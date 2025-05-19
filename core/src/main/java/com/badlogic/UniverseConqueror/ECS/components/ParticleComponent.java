package com.badlogic.UniverseConqueror.ECS.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;

public class ParticleComponent implements Component {
    public ParticleEffect effect;
    public Vector2 offset = new Vector2();  // deslocamento do centro da entidade
    public float rotation = 0f;

}
