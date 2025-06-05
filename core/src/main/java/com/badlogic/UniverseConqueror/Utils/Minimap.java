package com.badlogic.UniverseConqueror.Utils;

import com.badlogic.UniverseConqueror.ECS.components.PositionComponent;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.g2d.Batch;

public class Minimap extends Actor {
    private Texture minimapTexture;
    private Texture characterMarker;
    private Vector2 minimapPosition;
    private float minimapWidth;
    private float minimapHeight;
    private Entity player;
    private float worldWidth;
    private float worldHeight;

    // Construtor que inicializa as texturas, posição e tamanho do minimapa
    public Minimap(Texture minimapTexturePath, Texture characterMarkerPath, float x, float y, float width, float height, Entity  player) {
        this.minimapTexture = minimapTexturePath;
        this.characterMarker = characterMarkerPath;
        this.minimapPosition = new Vector2(x, y);
        this.minimapWidth = width;
        this.minimapHeight = height;
        this.player = player;
        setPosition(x, y);
        setSize(width, height);
    }

    // Define o tamanho do mundo
    public void setWorldSize(float worldWidth, float worldHeight) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }

    // Método chamado automaticamente para desenhar o minimapa
    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(minimapTexture, getX(), getY(), getWidth(), getHeight());

        Vector2 characterPosition = getCharacterPosition();
        if (characterPosition != null && worldWidth > 0 && worldHeight > 0) {
            float scale = 0.01f;
            float scaledX = getX() + (characterPosition.x * scale) - 10;
            float scaledY = getY() + (characterPosition.y * scale) + 50;

            scaledX = MathUtils.clamp(scaledX, getX(), getX() + getWidth() - 10);
            scaledY = MathUtils.clamp(scaledY, getY(), getY() + getHeight() - 10);

            batch.draw(characterMarker, scaledX, scaledY, 20, 20);
        }
    }

    // Retorna a posição atual do jogador no mundo
    private Vector2 getCharacterPosition() {
        if (player != null) {
            PositionComponent pos = player.getComponent(PositionComponent.class);
            if (pos != null) {
                return pos.position;
            }
        }
        return null;
    }

    // Libera os recursos das texturas ao finalizar o uso
    public void dispose() {
        minimapTexture.dispose();
        characterMarker.dispose();
    }
}
