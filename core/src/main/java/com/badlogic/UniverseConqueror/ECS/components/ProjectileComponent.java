package com.badlogic.UniverseConqueror.ECS.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class ProjectileComponent implements Component {

    /// Tipos de projéteis disponíveis (ex: bala, bola de fogo)
    public enum ProjectileType {
        BULLET,
        FIREBALL
    }

    /// Tipo atual do projétil
    public ProjectileType type = ProjectileType.BULLET;

    /// Velocidade de deslocamento do projétil
    public float speed = 1900f;

    /// Posição atual do projétil (pode ser usada para cálculo independente da física)
    public Vector2 position = new Vector2();

    /// Textura usada para renderizar o projétil
    public Texture texture;

    /// Flag para indicar se o projétil saiu do ecrã
    public boolean isOutOfBounds = false;
}
