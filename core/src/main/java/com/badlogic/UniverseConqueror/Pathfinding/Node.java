package com.badlogic.UniverseConqueror.Pathfinding;

public class Node {
    public int x, y;
    public boolean walkable;
    public float gCost, hCost;
    public Node parent;

    // Construtor que inicializa a posição e se é caminhável
    public Node(int x, int y, boolean walkable) {
        this.x = x;
        this.y = y;
        this.walkable = walkable;
    }

    // Custo total F = G + H usado para ordenação no A*
    public float getFCost() {
        return gCost + hCost;
    }

    // Comparação de igualdade baseada na posição
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Node)) return false;
        Node other = (Node) o;
        return other.x == x && other.y == y;
    }

    // HashCode baseado em x e y para uso em coleções hash
    @Override
    public int hashCode() {
        return x * 1000 + y;
    }
}
