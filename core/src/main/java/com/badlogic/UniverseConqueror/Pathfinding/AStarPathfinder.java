package com.badlogic.UniverseConqueror.Pathfinding;

import java.util.*;

public class AStarPathfinder {
    private Node[][] nodes;

    public AStarPathfinder(Node[][] nodes) {
        this.nodes = nodes;
    }

    public List<Node> findPath(Node start, Node target) {
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparing(Node::getFCost));
        HashSet<Node> closedSet = new HashSet<>();

        openSet.add(start);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (current.equals(target)) {
                return retracePath(start, target);
            }

            closedSet.add(current);

            for (Node neighbor : getNeighbors(current)) {
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

        return Collections.emptyList();
    }

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

    private float getDistance(Node a, Node b) {
        int dx = Math.abs(a.x - b.x);
        int dy = Math.abs(a.y - b.y);
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    private List<Node> getNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<>();
      //  int[][] dirs = {{0,1},{1,0},{0,-1},{-1,0}}; // 4 direções
        int[][] dirs = {
            {0, 1}, {1, 0}, {0, -1}, {-1, 0},  // cardinal
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1} // diagonais
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

