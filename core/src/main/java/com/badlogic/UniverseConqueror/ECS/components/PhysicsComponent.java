package com.badlogic.UniverseConqueror.ECS.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.math.Vector2;

public class PhysicsComponent implements Component {

    /// Corpo físico do Box2D associado à entidade
    public Body body;

    /// Velocidade atual aplicada ao corpo
    public Vector2 velocity = new Vector2();

    /// Massa do corpo (padrão: 1f)
    public float mass = 1f;

    public PhysicsComponent() {}

    public PhysicsComponent(Body body) {
        this.body = body;
    }
}
