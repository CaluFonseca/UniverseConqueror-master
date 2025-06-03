package com.badlogic.UniverseConqueror.ECS.components;

import com.badlogic.ashley.core.Component;

public class StateComponent implements Component {

    // Enum que representa os estados possíveis de uma entidade
    public enum State {
        IDLE,
        WALK,
        CLIMB,
        FAST_MOVE,
        JUMP,
        FALL,
        DEATH,
        HURT,
        ATTACK,
        SUPER_ATTACK,
        DEFENSE,
        WALK_INJURED,
        IDLE_INJURED,
        DEFENSE_INJURED,
        PATROL,
        CHASE,
        FLY
    }

    // Estado atual da entidade
    public State currentState = State.IDLE;

    // Último estado anterior ao atual
    public State previousState = State.IDLE;

    // Tempo acumulado no estado atual
    public float timeInState = 0f;

    // Tempo restante do ataque especial
    public float superAttackTimeLeft = 0f;

    // Atualiza o tempo no estado
    public void update(float delta) {
        timeInState += delta;
    }

    // Define um novo estado para a entidade
    public void set(State newState) {
        if (currentState == State.DEATH) return;

        if (newState != currentState) {
            previousState = currentState;
            currentState = newState;
            timeInState = 0;
        }
    }

    // Retorna o estado atual da entidade
    public State get() {
        return currentState;
    }
}
