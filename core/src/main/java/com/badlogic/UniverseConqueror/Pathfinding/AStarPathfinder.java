package com.badlogic.UniverseConqueror.Pathfinding;

import java.util.*;

public class AStarPathfinder {
    private Node[][] nodes;

    // Construtor que recebe a matriz de nós para o mapa
    public AStarPathfinder(Node[][] nodes) {
        this.nodes = nodes;
    }

    // Método que encontra o caminho do nó start até o nó target usando A*
    public List<Node> findPath(Node start, Node target) {
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparing(Node::getFCost));
        HashSet<Node> closedSet = new HashSet<>();

        openSet.add(start);

        // Enquanto houver nós para explorar
        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            if (current.equals(target)) {
                return retracePath(start, target);
            }
            // Marca o nó atual como explorado
            closedSet.add(current);

            for (Node neighbor : getNeighbors(current)) {
                // Ignora vizinhos não caminháveis ou já explorados
                if (!neighbor.walkable || closedSet.contains(neighbor)) continue;

                float newCost = current.gCost + getDistance(current, neighbor);

                if (newCost < neighbor.gCost || !openSet.contains(neighbor)) {
                    neighbor.gCost = newCost;
                    neighbor.hCost = getDistance(neighbor, target);
                    neighbor.parent = current;

                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }

        // Se não encontrar caminho, retorna lista vazia
        return Collections.emptyList();
    }

    // Método para reconstruir o caminho do nó destino ao inicial
    private List<Node> retracePath(Node start, Node end) {
        List<Node> path = new ArrayList<>();
        Node current = end;

        while (!current.equals(start)) {
            path.add(current);
            current = current.parent;
        }

        Collections.reverse(path);
        return path;
    }

    // Calcula a distância euclidiana entre dois nós
    private float getDistance(Node a, Node b) {
        int dx = Math.abs(a.x - b.x);
        int dy = Math.abs(a.y - b.y);
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    // Obtém os nós vizinhos
    private List<Node> getNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<>();
        int[][] dirs = {
            {0, 1}, {1, 0}, {0, -1}, {-1, 0},
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };

        for (int[] d : dirs) {
            int nx = node.x + d[0], ny = node.y + d[1];
            if (nx >= 0 && ny >= 0 && nx < nodes.length && ny < nodes[0].length) {
                neighbors.add(nodes[nx][ny]);
            }
        }
        return neighbors;
    }
}
