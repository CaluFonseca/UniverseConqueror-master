/// Classe que representa um joystick virtual 2D para controle de personagem em ecrãs sensíveis ao toque.
/// Renderiza um botão circular que pode ser arrastado dentro de uma base circular.
/// Usa eventos de toque da Scene2D.

package com.badlogic.UniverseConqueror.Utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class Joystick extends Actor {

    /// Textura da base do joystick.
    private final Texture baseTexture;

    /// Textura do botão móvel (knob) do joystick.
    private final Texture knobTexture;

    /// Círculo representando a área da base.
    private final Circle baseCircle;

    /// Círculo representando a área do botão.
    private final Circle knobCircle;

    /// Posição atual do botão (knob).
    private float knobX, knobY;

    /// Indica se o botão está sendo arrastado.
    private boolean isDragging = false;

    /// Construtor do joystick.
    /// @param base textura da base
    /// @param knob textura do botão
    /// @param centerX posição X central da base
    /// @param centerY posição Y central da base
    /// @param radius raio da base
    public Joystick(Texture base, Texture knob, float centerX, float centerY, float radius) {
        this.baseTexture = base;
        this.knobTexture = knob;
        this.baseCircle = new Circle(centerX, centerY, radius);
        this.knobCircle = new Circle(centerX, centerY, radius / 2);
        this.knobX = centerX;
        this.knobY = centerY;

        /// Define a área clicável do joystick (bounds para eventos).
        setBounds(centerX - radius, centerY - radius, radius * 2, radius * 2);

        /// Adiciona eventos de toque.
        addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                isDragging = true;
                updateKnob(x, y); // Coordenadas relativas ao ator
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                updateKnob(x, y);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                isDragging = false;
                resetKnob();
            }
        });
    }

    /// Retorna a direção normalizada do joystick.
    /// Se o botão não estiver sendo arrastado, retorna (0,0).
    /// @return vetor de direção escalado
    public Vector2 getDirection() {
        if (!isDragging) return new Vector2(0, 0);
        float dx = knobX - baseCircle.x;
        float dy = knobY - baseCircle.y;
        return new Vector2(dx, dy).nor().scl(100f); // escala pode ser configurável
    }

    /// Verifica se o joystick está em uso (movendo).
    /// @return true se estiver arrastando o botão
    public boolean isMoving() {
        return isDragging;
    }

    /// Desenha o joystick no ecrã.
    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(baseTexture, baseCircle.x - baseCircle.radius, baseCircle.y - baseCircle.radius,
            baseCircle.radius * 2, baseCircle.radius * 2);
        batch.draw(knobTexture, knobX - knobCircle.radius, knobY - knobCircle.radius,
            knobCircle.radius * 2, knobCircle.radius * 2);
    }

    /// Atualiza a posição do botão (knob) com base no toque.
    /// Limita o movimento ao raio da base.
    private void updateKnob(float x, float y) {
        Vector2 localTouch = new Vector2(x + getX(), y + getY());
        Vector2 dir = new Vector2(localTouch.x - baseCircle.x, localTouch.y - baseCircle.y);

        if (dir.len() > baseCircle.radius) {
            dir.nor().scl(baseCircle.radius);
        }

        knobX = baseCircle.x + dir.x;
        knobY = baseCircle.y + dir.y;
    }

    /// Reseta o botão para o centro da base.
    private void resetKnob() {
        knobX = baseCircle.x;
        knobY = baseCircle.y;
    }

    /// Libera os recursos das texturas.
    public void dispose() {
        baseTexture.dispose();
        knobTexture.dispose();
    }
}
