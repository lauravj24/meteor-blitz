package main.java.model;

/**
 * Ladrillo indestructible de Meteor Blitz.
 * La pelota rebota en él pero nunca se rompe.
 * Se usa como obstáculo decorativo y estructural en los niveles.
 *
 * @author Laura Jaramillo, Tomas Osorio, Tomas Varona
 * @version 1.0
 */
public class IndestructibleBrick extends Brick {

    /**
     * Crea un ladrillo indestructible en la posición indicada.
     *
     * @param x posición X
     * @param y posición Y
     */
    public IndestructibleBrick(int x, int y) {
        super(x, y, "brick_indestructible.png", 0, 0, false);
    }
}
