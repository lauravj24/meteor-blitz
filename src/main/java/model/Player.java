package main.java.model;

/**
 * Representa el estado del jugador en Meteor Blitz.
 * Almacena nombre, puntaje y vidas. No extiende Entity porque
 * el jugador no tiene posición propia en la pantalla (la tiene la paleta).
 *
 * @author Laura Jaramillo, Tomas Osorio, Tomas Varona
 * @version 1.0
 */
public class Player {

    /**
     * Número inicial de vidas.
     */
    public static final int INITIAL_LIVES = 3;
    /**
     * Máximo de vidas posibles.
     */
    public static final int MAX_LIVES = 5;

    /**
     * Nombre ingresado en la pantalla de bienvenida.
     */
    private String name;
    /**
     * Puntaje acumulado.
     */
    private int score;
    /**
     * Vidas restantes.
     */
    private int lives;
    /**
     * Indica si el juego está en pausa.
     */
    private boolean paused;
    /**
     * Indica si se presionó Enter en una pantalla de menú.
     */
    private boolean enterPressed;
    /**
     * Indica si se presionó SPACE para lanzar la pelota.
     */
    private boolean spacePressed;

    /**
     * Crea un jugador con el nombre indicado y estado inicial.
     *
     * @param name nombre del jugador
     */
    public Player(String name) {
        this.name = (name != null && !name.trim().isEmpty()) ? name.trim() : "Jugador";
        this.score = 0;
        this.lives = INITIAL_LIVES;
        this.paused = false;
        this.enterPressed = false;
        this.spacePressed = false;
    }

    /**
     * Reinicia el puntaje y las vidas para una nueva partida.
     */
    public void resetAll() {
        this.score = 0;
        this.lives = INITIAL_LIVES;
        this.paused = false;
        this.enterPressed = false;
        this.spacePressed = false;
    }

    /**
     * Descuenta una vida.
     *
     * @return true si el jugador sigue con vida, false si llegó a 0
     */
    public boolean loseLife() {
        if (lives > 0) lives--;
        return lives > 0;
    }

    /**
     * Agrega una vida si no se superó el máximo.
     */
    public void addLife() {
        if (lives < MAX_LIVES) lives++;
    }

    /**
     * Suma puntos al puntaje acumulado.
     *
     * @param points puntos a sumar (debe ser positivo)
     */
    public void addScore(int points) {
        if (points > 0) score += points;
    }

    /**
     * Alterna el estado de pausa.
     */
    public void togglePause() {
        paused = !paused;
    }

    // -- Getters y Setters --

    /**
     * @return nombre del jugador
     */
    public String getName() {
        return name;
    }

    /**
     * @param name nuevo nombre del jugador
     */
    public void setName(String name) {
        this.name = (name != null && !name.trim().isEmpty()) ? name.trim() : "Jugador";
    }

    /**
     * @return puntaje acumulado
     */
    public int getScore() {
        return score;
    }

    /**
     * @return vidas restantes
     */
    public int getLives() {
        return lives;
    }

    /**
     * @return true si el juego está en pausa
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * @return true si Enter está presionado
     */
    public boolean isEnterPressed() {
        return enterPressed;
    }

    /**
     * @param v estado de la tecla Enter
     */
    public void setEnterPressed(boolean v) {
        this.enterPressed = v;
    }

    /**
     * @return true si SPACE está presionado
     */
    public boolean isSpacePressed() {
        return spacePressed;
    }

    /**
     * @param v estado de la tecla SPACE
     */
    public void setSpacePressed(boolean v) {
        this.spacePressed = v;
    }
}
