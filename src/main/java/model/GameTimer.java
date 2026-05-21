package main.java.model;

/**
 * Cronómetro ascendente (count-up) para Meteor Blitz.
 * Cuenta los segundos jugados en cada partida y los muestra en el HUD.
 * Se actualiza desde el bucle principal a 60 FPS.
 *
 * @author Laura Jaramillo, Tomas Osorio, Tomas Varona
 * @version 2.0
 */
public class GameTimer {

    /**
     * Frames por segundo del juego.
     */
    private static final int FPS = 60;

    /**
     * Frames transcurridos desde el inicio.
     */
    private int framesElapsed;
    /**
     * Indica si el cronómetro está activo.
     */
    private boolean running;

    /**
     * Crea el cronómetro en cero, detenido.
     */
    public GameTimer() {
        this.framesElapsed = 0;
        this.running = false;
    }

    /**
     * Inicia el cronómetro.
     */
    public void start() {
        running = true;
    }

    /**
     * Detiene el cronómetro sin reiniciarlo.
     */
    public void stop() {
        running = false;
    }

    /**
     * Reinicia el cronómetro a cero y lo detiene.
     */
    public void reset() {
        framesElapsed = 0;
        running = false;
    }

    /**
     * Avanza el cronómetro un frame.
     * Debe llamarse UNA vez por frame desde el bucle principal.
     */
    public void tick() {
        if (running) framesElapsed++;
    }

    /**
     * @return true si el cronómetro está corriendo
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * @return segundos completos transcurridos
     */
    public int getSecondsElapsed() {
        return framesElapsed / FPS;
    }

    /**
     * Retorna el tiempo transcurrido en formato "MM:SS".
     *
     * @return cadena formateada, ej. "02:47"
     */
    public String getFormatted() {
        int s = getSecondsElapsed();
        return String.format("%02d:%02d", s / 60, s % 60);
    }
}
