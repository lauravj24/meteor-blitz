package main.java.model;

/**
 * Alien invasor de Meteor Blitz. Se desplaza en diagonal hacia abajo,
 * rebotando en las paredes izquierda y derecha del área de juego.
 * Si llega a la altura de la paleta, el jugador pierde una vida.
 *
 * <p>Velocidad aumenta con el nivel:</p>
 * <ul>
 *   <li>Nivel 1: 1.5 px/frame</li>
 *   <li>Nivel 2: 2.0 px/frame</li>
 *   <li>Nivel 3: 2.5 px/frame</li>
 * </ul>
 *
 * @author Laura Jaramillo, Tomas Osorio, Tomas Varona
 * @version 1.0
 */
public class AlienDrifter extends Alien {

    /**
     * Velocidad base horizontal (puede ser negativa para ir a la izquierda).
     */
    private float dx;
    /**
     * Velocidad vertical descendente (siempre positiva).
     */
    private float dy;

    /**
     * Crea un alien que se desplaza en diagonal.
     *
     * @param x      posición X inicial
     * @param y      posición Y inicial
     * @param speed  velocidad escalar en px/frame
     * @param goLeft true si el alien comienza moviéndose hacia la izquierda
     */
    public AlienDrifter(int x, int y, float speed, boolean goLeft) {
        super(x, y, "alien_drifter.png", speed);
        // Movimiento: 70% horizontal, 30% vertical
        this.dx = goLeft ? -speed * 0.7f : speed * 0.7f;
        this.dy = speed * 0.3f;
    }

    /**
     * Mueve el alien en diagonal. Rebota horizontalmente al tocar los límites.
     * Desciende siempre a velocidad constante.
     *
     * @param leftBound  límite izquierdo del área de juego
     * @param rightBound límite derecho del área de juego
     */
    @Override
    public void move(int leftBound, int rightBound) {
        float nx = getX() + dx;
        float ny = getY() + dy;

        // Rebote en paredes laterales
        if (nx < leftBound) {
            nx = leftBound;
            dx = -dx;
        } else if (nx + getWidth() > rightBound) {
            nx = rightBound - getWidth();
            dx = -dx;
        }

        setX(Math.round(nx));
        setY(Math.round(ny));
    }

    /**
     * Los aliens se "actualizan" con move() — este método no se usa directamente.
     */
    @Override
    public void update() { /* move(int,int) es el punto de entrada */ }
}
