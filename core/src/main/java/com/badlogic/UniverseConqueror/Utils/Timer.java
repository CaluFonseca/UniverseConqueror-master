// Classe utilitária de temporizador (Timer) para controlar eventos no tempo.
// Pode ser usada para cooldowns, contadores regressivos ou temporizações gerais.

package com.badlogic.UniverseConqueror.Utils;

public class Timer {
    private float time;
    private float duration;
    private boolean running;

    // Construtor que define a duração inicial do temporizador.
    public Timer(float duration) {
        this.duration = duration;
        this.time = 0;
        this.running = false;
    }

    // Inicia o temporizador, reiniciando o tempo acumulado.
    public void start() {
        running = true;
//        time = 0;
    }

    // Para o temporizador, mantendo o tempo acumulado.
    public void stop() {
        running = false;
    }

    // Reinicia o tempo acumulado, mas não inicia nem para o temporizador.
    public void reset() {
        time = 0;
    }

    // Atualiza o temporizador com base no deltaTime.
    public void update(float delta) {
        if (!running) return;

        time += delta;
        if (time >= duration) {
            running = false;
        }
    }

    // Verifica se o tempo final foi alcançado.
    public boolean isFinished() {
        return !running && time >= duration;
    }

    // Verifica se o temporizador está em execução.
    public boolean isRunning() {
        return running;
    }

    // Retorna o tempo acumulado atual.
    public float getTime() {
        return time;
    }

    // Retorna a duração alvo definida.
    public float getDuration() {
        return duration;
    }

    // Define uma nova duração.
    public void setDuration(float duration) {
        this.duration = duration;
    }

    // Define manualmente o tempo acumulado.
    public void setTime(float time) {
        this.time = time;
    }
}
