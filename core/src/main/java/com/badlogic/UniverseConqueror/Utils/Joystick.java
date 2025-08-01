// Classe que representa um joystick virtual 2D para controle de personagem em ecrãs sensíveis ao toque.
// Renderiza um botão circular que pode ser arrastado dentro de uma base circular.
// Usa eventos de toque da Scene2D.

package com.badlogic.UniverseConqueror.Utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class Joystick extends Actor {
    private final Texture baseTexture;
    private final Texture knobTexture;
    private final Circle baseCircle;
    private final Circle knobCircle;
    private float knobX, knobY;
    private boolean isDragging = false;

    public Joystick(Texture base, Texture knob, float centerX, float centerY, float radius) {
        this.baseTexture = base;
        this.knobTexture = knob;
        this.baseCircle = new Circle(centerX, centerY, radius);
        this.knobCircle = new Circle(centerX, centerY, radius / 2);
        this.knobX = centerX;
        this.knobY = centerY;

        setBounds(centerX - radius, centerY - radius, radius * 2, radius * 2);

        // Adiciona eventos de toque.
        addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                isDragging = true;
                updateKnob(x, y);
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

    // Retorna a direção normalizada do joystick.
    public Vector2 getDirection() {
        if (!isDragging) return new Vector2(0, 0);
        float dx = knobX - baseCircle.x;
        float dy = knobY - baseCircle.y;
        return new Vector2(dx, dy).nor().scl(100f); // escala pode ser configurável
    }

    // Verifica se o joystick está em uso
    public boolean isMoving() {
        return isDragging;
    }

    // Desenha o joystick no ecrã.
    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(baseTexture, baseCircle.x - baseCircle.radius, baseCircle.y - baseCircle.radius,
            baseCircle.radius * 2, baseCircle.radius * 2);
        batch.draw(knobTexture, knobX - knobCircle.radius, knobY - knobCircle.radius,
            knobCircle.radius * 2, knobCircle.radius * 2);
    }

    // Atualiza a posição do botão (knob) com base no toque.
    // Limita o movimento ao raio da base.
    private void updateKnob(float x, float y) {
        Vector2 localTouch = new Vector2(x + getX(), y + getY());
        Vector2 dir = new Vector2(localTouch.x - baseCircle.x, localTouch.y - baseCircle.y);

        if (dir.len() > baseCircle.radius) {
            dir.nor().scl(baseCircle.radius);
        }

        knobX = baseCircle.x + dir.x;
        knobY = baseCircle.y + dir.y;
    }

    // Reset o botão para o centro da base.
    private void resetKnob() {
        knobX = baseCircle.x;
        knobY = baseCircle.y;
    }

    // Liberta os recursos das texturas.
    public void dispose() {
        baseTexture.dispose();
        knobTexture.dispose();
    }
}
