/// Classe utilitária de temporizador (Timer) para controlar eventos no tempo.
/// Pode ser usada para cooldowns, contadores regressivos ou temporizações gerais.

package com.badlogic.UniverseConqueror.Utils;

public class Timer {

    /// Tempo atual acumulado.
    private float time;

    /// Duração alvo do temporizador.
    private float duration;

    /// Indica se o temporizador está em execução.
    private boolean running;

    /// Construtor que define a duração inicial do temporizador.
    /// @param duration tempo alvo (em segundos)
    public Timer(float duration) {
        this.duration = duration;
        this.time = 0;
        this.running = false;
    }

    /// Inicia o temporizador, reiniciando o tempo acumulado.
    public void start() {
        running = true;
        time = 0;
    }

    /// Para o temporizador, mantendo o tempo acumulado.
    public void stop() {
        running = false;
    }

    /// Reinicia o tempo acumulado, mas não inicia nem para o temporizador.
    public void reset() {
        time = 0;
    }

    /// Atualiza o temporizador com base no deltaTime.
    /// @param delta tempo decorrido desde o último frame
    public void update(float delta) {
        if (!running) return;

        time += delta;
        if (time >= duration) {
            running = false;
        }
    }

    /// Verifica se o tempo final foi alcançado.
    /// @return true se o temporizador terminou
    public boolean isFinished() {
        return !running && time >= duration;
    }

    /// Verifica se o temporizador está em execução.
    /// @return true se está rodando
    public boolean isRunning() {
        return running;
    }

    /// Retorna o tempo acumulado atual.
    public float getTime() {
        return time;
    }

    /// Retorna a duração alvo definida.
    public float getDuration() {
        return duration;
    }

    /// Define uma nova duração.
    /// @param duration nova duração (em segundos)
    public void setDuration(float duration) {
        this.duration = duration;
    }

    /// Define manualmente o tempo acumulado.
    /// Útil para restaurar estados salvos.
    /// @param time novo valor de tempo
    public void setTime(float time) {
        this.time = time;
    }
}
