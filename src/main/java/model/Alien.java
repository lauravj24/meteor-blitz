package main.java.model;

/**
 * Clase abstracta base para los aliens enemigos de Meteor Blitz.
 * Los aliens invaden el campo de juego moviéndose hacia la paleta.
 * La pelota puede destruirlos; si alcanzan la paleta el jugador pierde una vida.
 *
 * <p>Subclases:</p>
 * <ul>
 *   <li>{@link AlienDrifter} — se desplaza en diagonal, rebota en paredes</li>
 * </ul>
 *
 * @author Laura Jaramillo, Tomas Osorio, Tomas Varona
 * @version 1.0
 */
public abstract class Alien extends Entity {

    /**
     * Puntaje que otorga destruir un alien.
     */
    public static final int SCORE = 50;

    /**
     * Velocidad de movimiento en px/frame.
     */
    protected float speed;

    /**
     * Constructor base para aliens.
     *
     * @param x          posición X inicial
     * @param y          posición Y inicial
     * @param spriteName nombre del archivo de sprite
     * @param speed      velocidad base
     */
    public Alien(int x, int y, String spriteName, float speed) {
        super(x, y, uploadImage(spriteName));
        this.speed = speed;
        if (getWidth() == 0) {
            setWidth(28);
            setHeight(22);
        }
    }

    /**
     * Define el patrón de movimiento específico del alien.
     * Cada subclase implementa su propia lógica de desplazamiento.
     *
     * @param leftBound  límite izquierdo del área de juego
     * @param rightBound límite derecho del área de juego
     */
    public abstract void move(int leftBound, int rightBound);

    // -- Getters / Setters ---------------------------------------------

    /**
     * @return velocidad actual del alien
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * @param speed nueva velocidad
     */
    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
