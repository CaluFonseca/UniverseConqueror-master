package com.badlogic.UniverseConqueror.ECS.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;

public class BoundsComponent implements Component {
    public final Rectangle bounds = new Rectangle();


    public BoundsComponent() {}

    public BoundsComponent(float width, float height) {
        this.bounds.setSize(width, height);
    }
}
