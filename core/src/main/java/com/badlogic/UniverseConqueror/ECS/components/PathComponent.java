package com.badlogic.UniverseConqueror.ECS.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

import java.util.LinkedList;
import java.util.Queue;

public class PathComponent implements Component {

    /// Tipos de destino possíveis para o caminho (ex: buscar item ou ir à nave)
    public enum PathType { ITEM, SPACESHIP }

    /// Fila de pontos (waypoints) que a entidade deve seguir
    public Queue<Vector2> waypoints = new LinkedList<>();

    /// Tipo atual do caminho, padrão é buscar item
    public PathType type = PathType.ITEM;
}
