package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.Utils.AssetPaths;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class CrosshairRenderSystem extends EntitySystem {

    /// SpriteBatch usado para desenhar a mira na tela
    private final SpriteBatch batch;

    /// Câmera usada para converter coordenadas da tela em coordenadas do mundo
    private final OrthographicCamera camera;

    /// Textura da mira (imagem do retículo)
    private final Texture crosshairTexture;

    /// Escala para diminuir o tamanho da mira
    private final float scale = 0.03f;

    /// Largura e altura da textura já escalada
    private final float width, height;

    /// Posição da mira no mundo (coordenadas convertidas)
    private final Vector2 crosshairPosition = new Vector2();

    /// AssetManager para carregar a textura (mantido por referência, mas não é usado depois do construtor)
    private AssetManager assetManager;

    /// Construtor recebe batch, câmera e gerenciador de assets
    public CrosshairRenderSystem(SpriteBatch batch, OrthographicCamera camera, AssetManager assetManager) {
        this.batch = batch;
        this.camera = camera;
        this.assetManager = assetManager;

        /// Obtém a textura da mira a partir do AssetManager
        this.crosshairTexture = assetManager.get(AssetPaths.CROSSHAIR_TEXTURE, Texture.class);

        /// Calcula o tamanho final da textura com base na escala
        this.width = crosshairTexture.getWidth() * scale;
        this.height = crosshairTexture.getHeight() * scale;
    }

    @Override
    public void update(float deltaTime) {
        /// Captura a posição atual do mouse na tela
        crosshairPosition.set(Gdx.input.getX(), Gdx.input.getY());

        /// Converte para coordenadas do mundo com base na câmera
        Vector3 worldCoords = camera.unproject(new Vector3(crosshairPosition.x, crosshairPosition.y, 0));
        crosshairPosition.set(worldCoords.x, worldCoords.y);

        /// Começa o desenho da textura da mira
        batch.begin();

        /// Desenha a textura centralizada na posição convertida do mouse
        batch.draw(
            crosshairTexture,
            crosshairPosition.x - width * 0.5f,
            crosshairPosition.y - height * 0.5f,
            width, height
        );

        /// Finaliza o desenho
        batch.end();
    }
}
