package com.badlogic.UniverseConqueror.ECS.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.math.Vector2;

public class PhysicsComponent implements Component {
    public Body body;  // O corpo do Box2D que controla a física
    public Vector2 velocity = new Vector2();  // Velocidade física
    public float mass = 1f;  // Massa do corpo (opcional)



}
