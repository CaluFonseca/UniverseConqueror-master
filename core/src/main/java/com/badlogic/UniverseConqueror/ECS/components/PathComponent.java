package com.badlogic.UniverseConqueror.ECS.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

import java.util.LinkedList;
import java.util.Queue;

public class PathComponent implements Component {
    public enum PathType { ITEM, SPACESHIP }
    public Queue<Vector2> waypoints = new LinkedList<>();
    public PathType type = PathType.ITEM; // padrão
}
