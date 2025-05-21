package com.badlogic.UniverseConqueror.Pathfinding;

public class Node {
    public int x, y;
    public boolean walkable;
    public float gCost, hCost;
    public Node parent;

    public Node(int x, int y, boolean walkable) {
        this.x = x;
        this.y = y;
        this.walkable = walkable;
    }

    public float getFCost() {
        return gCost + hCost;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Node)) return false;
        Node other = (Node) o;
        return other.x == x && other.y == y;
    }

    @Override
    public int hashCode() {
        return x * 1000 + y;
    }
}
