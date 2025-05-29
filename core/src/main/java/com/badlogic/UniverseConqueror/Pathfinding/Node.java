package com.badlogic.UniverseConqueror.Pathfinding;

public class Node {
    public int x, y;             /// Coordenadas do nó na grade
    public boolean walkable;     /// Indica se o nó é acessível (sem obstáculos)
    public float gCost, hCost;   /// Custos usados no algoritmo A* (g = custo do caminho até aqui, h = heurística até destino)
    public Node parent;          /// Nó anterior no caminho encontrado (para reconstruir o caminho)

    /// Construtor que inicializa a posição e se é caminhável
    public Node(int x, int y, boolean walkable) {
        this.x = x;
        this.y = y;
        this.walkable = walkable;
    }

    /// Custo total F = G + H usado para ordenação no A*
    public float getFCost() {
        return gCost + hCost;
    }

    /// Comparação de igualdade baseada na posição (x,y)
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Node)) return false;
        Node other = (Node) o;
        return other.x == x && other.y == y;
    }

    /// HashCode baseado em x e y para uso em coleções hash (por exemplo HashSet)
    @Override
    public int hashCode() {
        return x * 1000 + y;  // número arbitrário para dispersão
    }
}
