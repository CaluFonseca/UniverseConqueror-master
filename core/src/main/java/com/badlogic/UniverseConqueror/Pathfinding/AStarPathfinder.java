package com.badlogic.UniverseConqueror.Pathfinding;

import java.util.*;

public class AStarPathfinder {
    private Node[][] nodes;

    /// Construtor que recebe a matriz de nós para o mapa
    public AStarPathfinder(Node[][] nodes) {
        this.nodes = nodes;
    }

    /// Método que encontra o caminho do nó start até o nó target usando A*
    public List<Node> findPath(Node start, Node target) {
        /// Conjunto aberto: nós a serem explorados, ordenados pelo custo f
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparing(Node::getFCost));
        /// Conjunto fechado: nós já explorados
        HashSet<Node> closedSet = new HashSet<>();

        /// Adiciona o nó inicial ao conjunto aberto
        openSet.add(start);

        /// Enquanto houver nós para explorar
        while (!openSet.isEmpty()) {
            /// Pega o nó com menor custo f
            Node current = openSet.poll();

            /// Se chegou no destino, reconstrói o caminho
            if (current.equals(target)) {
                return retracePath(start, target);
            }

            /// Marca o nó atual como explorado
            closedSet.add(current);

            /// Para cada vizinho do nó atual
            for (Node neighbor : getNeighbors(current)) {
                /// Ignora vizinhos não caminháveis ou já explorados
                if (!neighbor.walkable || closedSet.contains(neighbor)) continue;

                /// Calcula o novo custo g do caminho via nó atual
                float newCost = current.gCost + getDistance(current, neighbor);

                /// Se o novo custo é melhor ou o vizinho não está no conjunto aberto
                if (newCost < neighbor.gCost || !openSet.contains(neighbor)) {
                    /// Atualiza os custos e define o pai do vizinho como o nó atual
                    neighbor.gCost = newCost;
                    neighbor.hCost = getDistance(neighbor, target);
                    neighbor.parent = current;

                    /// Adiciona o vizinho ao conjunto aberto se ainda não estiver
                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }

        /// Se não encontrar caminho, retorna lista vazia
        return Collections.emptyList();
    }

    /// Método para reconstruir o caminho do nó destino ao inicial
    private List<Node> retracePath(Node start, Node end) {
        List<Node> path = new ArrayList<>();
        Node current = end;

        /// Volta do destino até o início usando os pais
        while (!current.equals(start)) {
            path.add(current);
            current = current.parent;
        }

        /// Inverte o caminho para ficar do início ao destino
        Collections.reverse(path);
        return path;
    }

    /// Calcula a distância euclidiana entre dois nós (usada para custo e heurística)
    private float getDistance(Node a, Node b) {
        int dx = Math.abs(a.x - b.x);
        int dy = Math.abs(a.y - b.y);
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /// Obtém os nós vizinhos (8 direções: 4 cardinais + 4 diagonais)
    private List<Node> getNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<>();
        int[][] dirs = {
            {0, 1}, {1, 0}, {0, -1}, {-1, 0},  /// direções cardinais
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1} /// direções diagonais
        };
        /// Itera sobre todas as direções
        for (int[] d : dirs) {
            int nx = node.x + d[0], ny = node.y + d[1];
            /// Verifica se o vizinho está dentro dos limites da matriz
            if (nx >= 0 && ny >= 0 && nx < nodes.length && ny < nodes[0].length) {
                neighbors.add(nodes[nx][ny]);
            }
        }
        return neighbors;
    }
}
