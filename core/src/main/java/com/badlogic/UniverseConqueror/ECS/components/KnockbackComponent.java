package com.badlogic.UniverseConqueror.ECS.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class KnockbackComponent implements Component {

    /// Vetor que representa a direção e intensidade do impulso
    public Vector2 impulse = new Vector2();

    /// Duração total do efeito de knockback
    public float duration = 0.2f;

    /// Tempo restante até o fim do knockback
    public float timeRemaining = 0.2f;

    /// Indica se o impulso já foi aplicado (para evitar aplicar várias vezes)
    public boolean hasBeenApplied = false;
}
