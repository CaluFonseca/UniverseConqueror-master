package com.badlogic.UniverseConqueror.ECS.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class ProjectileComponent implements Component {
    public enum ProjectileType {
        BULLET,
        FIREBALL
    }

    public ProjectileType type = ProjectileType.BULLET;
    public float speed = 500f;
    public Vector2 position = new Vector2();
    public Texture texture;
    public boolean isOutOfBounds = false;
}
