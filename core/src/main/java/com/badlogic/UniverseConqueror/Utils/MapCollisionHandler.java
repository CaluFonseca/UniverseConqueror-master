// Classe responsável por processar colisões de um mapa isométrico em um jogo usando TiledMap e Box2D.
// Extrai retângulos de colisão e "saltáveis" (jumpable) e os converte em corpos físicos no mundo Box2D.

package com.badlogic.UniverseConqueror.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

public class MapCollisionHandler {
    private final Array<Rectangle> collisionRects;
    private final Array<Rectangle> jumpableRects;
    private final float tileWidth;
    private final float tileHeight;
    private TiledMapTileLayer layer;
    private TiledMapTileLayer jumpableLayer;

    // Construtor que processa o mapa e extrai retângulos das camadas especificadas.
    public MapCollisionHandler(TiledMap map, String layerName, String jumpableLayerName) {
        collisionRects = new Array<>();
        jumpableRects = new Array<>();

        this.tileWidth = map.getProperties().get("tilewidth", Integer.class);
        this.tileHeight = map.getProperties().get("tileheight", Integer.class);

        MapLayer mapLayer = map.getLayers().get(layerName);
        MapLayer jumpableMapLayer = map.getLayers().get(jumpableLayerName);

        if (!(mapLayer instanceof TiledMapTileLayer)) {
            Gdx.app.error("Collision", "Layer not found or not a valid tile layer: " + layerName);
            return;
        }

        if (!(jumpableMapLayer instanceof TiledMapTileLayer)) {
            Gdx.app.error("Jumpable", "Jumpable Layer not found or not a valid tile layer: " + jumpableLayerName);
            return;
        }

        this.layer = (TiledMapTileLayer) mapLayer;
        this.jumpableLayer = (TiledMapTileLayer) jumpableMapLayer;

        int width = layer.getWidth();
        int height = layer.getHeight();

        float tileOffsetX = tileWidth;
        float tileOffsetY = tileHeight;

        float originX = (width + height) * tileWidth / 4f + tileWidth / 2f - tileOffsetX;
        float originY = - (tileHeight / 2f) * height + tileOffsetY;

        // Itera sobre os tiles e gera retângulos para colisão e camada pulável
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                TiledMapTileLayer.Cell jumpableCell = jumpableLayer.getCell(x, y);

                if (cell != null) {
                    int rotatedX = y;
                    int rotatedY = width - 1 - x;
                    float worldX = (rotatedX - rotatedY) * tileWidth / 2f + originX;
                    float worldY = (rotatedX + rotatedY) * tileHeight / 2f + originY;

                    Rectangle isoRect = new Rectangle(worldX, worldY - tileHeight / 2f, tileWidth, tileHeight);
                    collisionRects.add(isoRect);
                }

                if (jumpableCell != null) {
                    int rotatedX = y;
                    int rotatedY = width - 1 - x;
                    float worldX = (rotatedX - rotatedY) * tileWidth / 2f + originX;
                    float worldY = (rotatedX + rotatedY) * tileHeight / 2f + originY;

                    Rectangle isoRect = new Rectangle(worldX, worldY - tileHeight / 2f, tileWidth, tileHeight);
                    jumpableRects.add(isoRect);
                }
            }
        }
    }

    // Cria corpos estáticos no mundo Box2D a partir dos retângulos coletados.
    public void createBox2DBodies(World world) {
        for (Rectangle rect : collisionRects) {
            createIsometricDiamond(world, rect, 256f, 128f, (short) 0x0001, false, 4f);
        }

        for (Rectangle rect : jumpableRects) {
            createBox2DBody(world, rect, (short) 0x0001, true);
        }
    }

    // Cria um corpo com forma de losango isométrico para simular o tile.
    public Body createIsometricDiamond(World world, Rectangle pixelPosition, float widthPx, float heightPx, short categoryBits, boolean isSensor, float ppm) {
        Vector2 position = new Vector2(pixelPosition.x, pixelPosition.y);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(position.x + 128, position.y + 64);

        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        Vector2[] vertices = new Vector2[4];

        vertices[0] = new Vector2(0, heightPx / 2f);
        vertices[1] = new Vector2(widthPx / 2f, 0);
        vertices[2] = new Vector2(0, -heightPx / 2f);
        vertices[3] = new Vector2(-widthPx / 2f, 0);

        shape.set(vertices);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = 0.8f;
        fixtureDef.isSensor = isSensor;
        fixtureDef.filter.categoryBits = categoryBits;
        fixtureDef.filter.maskBits = -1;

        Fixture fixture = body.createFixture(fixtureDef);

        body.setUserData(new Rectangle(
            pixelPosition.x - widthPx / 2f,
            pixelPosition.y - heightPx / 2f,
            widthPx,
            heightPx
        ));
        fixture.setUserData("map");

        shape.dispose();
        return body;
    }

    // Cria um corpo retangular Box2D a partir de um retângulo.
    private void createBox2DBody(World world, Rectangle rect, short categoryBits, boolean isSensor) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(rect.x + rect.width / 2, rect.y + rect.height / 2);

        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(rect.width / 2, rect.height / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = 0.8f;
        fixtureDef.isSensor = isSensor;
        fixtureDef.filter.categoryBits = categoryBits;
        fixtureDef.filter.maskBits = -1;

        body.createFixture(fixtureDef);
        shape.dispose();
    }

    // Retorna a lista de retângulos da camada de colisão.
    public Array<Rectangle> getCollisionRects() {
        return collisionRects;
    }

    // Retorna a lista de retângulos da camada pulável.
    public Array<Rectangle> getJumpableRects() {
        return jumpableRects;
    }
}
