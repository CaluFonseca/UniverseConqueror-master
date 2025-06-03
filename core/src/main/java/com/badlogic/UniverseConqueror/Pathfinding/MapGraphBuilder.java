package com.badlogic.UniverseConqueror.Pathfinding;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;

public class MapGraphBuilder {
    private final TiledMap map;
    private final int tileWidth, tileHeight;
    private final int width, height;
    public final Node[][] nodes;

    private final float originX, originY;

    // Construtor que inicializa com base no mapa TMX, definindo dimensões e origem para isométrico
    public MapGraphBuilder(TiledMap map) {
        this.map = map;
        this.width = map.getProperties().get("width", Integer.class);
        this.height = map.getProperties().get("height", Integer.class);
        this.tileWidth = map.getProperties().get("tilewidth", Integer.class);
        this.tileHeight = map.getProperties().get("tileheight", Integer.class);

        // Ajuste para coincidir com offsets usados na colisão do mapa
        float tileOffsetX = tileWidth;
        float tileOffsetY = tileHeight;

        // Origem para conversão entre coordenadas do mapa e coordenadas mundo isométrico
        this.originX = (width + height) * tileWidth / 4f + tileWidth / 2f - tileOffsetX;
        this.originY = - (tileHeight / 2f) * height + tileOffsetY;

        nodes = new Node[width][height];
        buildNodes();
    }

    // Constrói a matriz de nós considerando o layer de colisões para marcar tiles caminháveis
    private void buildNodes() {
        TiledMapTileLayer collisionLayer = (TiledMapTileLayer) map.getLayers().get("Collisions");

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                boolean walkable = true;
                if (collisionLayer != null && collisionLayer.getCell(x, y) != null) {
                    walkable = false;  // tile ocupado bloqueia passagem
                }
                nodes[x][y] = new Node(x, y, walkable);
            }
        }
    }

    // Retorna um nó aleatório que seja caminhável
    public Node getRandomWalkableNode() {
        int width = getWidth();
        int height = getHeight();

        for (int attempts = 0; attempts < 100; attempts++) {
            int x = (int)(Math.random() * width);
            int y = (int)(Math.random() * height);
            Node node = nodes[x][y];
            if (node.walkable) return node;
        }
        return null;
    }

    // Converte um nó para posição mundo no sistema isométrico, aplicando ajuste para o centro do tile
    public Vector2 toWorldPosition(Node node) {
        int rotatedX = node.y;
        int rotatedY = width - 1 - node.x;

        float worldX = (rotatedX - rotatedY) * tileWidth / 2f + originX;
        float worldY = (rotatedX + rotatedY) * tileHeight / 2f + originY;

        return new Vector2(worldX + tileWidth / 2f, worldY);
    }

    // Retorna o nó na matriz
    public Node getNode(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return nodes[x][y];
        }
        return null;
    }

    // Encontra o nó mais próximo e caminhavel
    public Node findNearestWalkableOffset(Node origin, int dx, int dy) {
        int tx = origin.x + dx;
        int ty = origin.y + dy;

        Node target = getNode(tx, ty);
        if (target != null && target.walkable) {
            return target;
        }

        // Busca fallback em área 5x5 ao redor
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                Node n = getNode(tx + i, ty + j);
                if (n != null && n.walkable) {
                    return n;
                }
            }
        }
        return origin;
    }

    // Converte uma posição no mundo para um nó da matriz
    public Node toNode(Vector2 worldPosition) {
        float halfTileWidth = tileWidth / 2f;
        float halfTileHeight = tileHeight / 2f;

        float adjustedX = worldPosition.x - originX;
        float adjustedY = worldPosition.y - originY;

        float rotatedX = (adjustedY / halfTileHeight + adjustedX / halfTileWidth) / 2f;
        float rotatedY = (adjustedY / halfTileHeight - adjustedX / halfTileWidth) / 2f;

        int y = Math.round(rotatedX);
        int x = width - 1 - Math.round(rotatedY);

        if (x >= 0 && x < width && y >= 0 && y < height) {
            return nodes[x][y];
        }
        return null;
    }

    // Retorna o nó correspondente a uma posição mundo se for caminhavel
    public Node getNodeAtWorldPosition(float worldX, float worldY) {
        float halfTileWidth = tileWidth / 2f;
        float halfTileHeight = tileHeight / 2f;

        float adjustedX = worldX - originX;
        float adjustedY = worldY - originY;

        // Converte de volta para coords isométricas "rotacionadas"
        float rotatedX = (adjustedY / halfTileHeight + adjustedX / halfTileWidth) / 2f;
        float rotatedY = (adjustedY / halfTileHeight - adjustedX / halfTileWidth) / 2f;

        int y = Math.round(rotatedX);
        int x = width - 1 - Math.round(rotatedY);

        if (x >= 0 && x < width && y >= 0 && y < height) {
            Node node = nodes[x][y];
            if (node.walkable) return node;
        }

        return null;
    }

    // Retorna a largura em tiles
    public int getWidth() { return width; }
    // Retorna a altura em tiles
    public int getHeight() { return height; }
}
