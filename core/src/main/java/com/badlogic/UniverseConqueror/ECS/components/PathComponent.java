package com.badlogic.UniverseConqueror.ECS.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

import java.util.LinkedList;
import java.util.Queue;

public class PathComponent implements Component {
    public Queue<Vector2> waypoints = new LinkedList<>();
}
