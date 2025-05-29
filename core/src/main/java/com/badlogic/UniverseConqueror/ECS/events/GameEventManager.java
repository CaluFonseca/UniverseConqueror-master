package com.badlogic.UniverseConqueror.ECS.events;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;

/// Gerencia o sistema de eventos e notificação dos observers
public class GameEventManager {
    /// Lista de observadores registrados
    private final Array<Observer> observers = new Array<>();

    /// Fila de eventos pendentes para notificação
    private final Queue<GameEvent> eventQueue = new Queue<>();

    /// Flag que indica se a notificação está em andamento
    private boolean notifying = false;

    /// Adiciona um observer ao sistema (evita duplicatas)
    public void addObserver(Observer observer) {
        if (!observers.contains(observer, true)) {
            observers.add(observer);
        }
    }

    /// Remove um observer da lista
    public void removeObserver(Observer observer) {
        observers.removeValue(observer, true);
    }

    /// Enfileira um evento e notifica todos os observers registrados
    public void notify(GameEvent event) {
        eventQueue.addLast(event);

        if (notifying) return;

        notifying = true;

        try {
            while (eventQueue.notEmpty()) {
                GameEvent currentEvent = eventQueue.removeFirst();

                for (int i = 0; i < observers.size; i++) {
                    observers.get(i).onNotify(currentEvent);
                }
            }
        } finally {
            notifying = false;
        }
    }
}
