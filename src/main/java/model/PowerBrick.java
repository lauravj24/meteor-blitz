package main.java.model;

/**
 * Ladrillo especial de Meteor Blitz: 1 HP, al destruirse suelta
 * una cápsula de power-up que cae hacia la paleta.
 * Visualmente se distingue con un sprite morado con ícono especial.
 *
 * @author Laura Jaramillo, Tomas Osorio, Tomas Varona
 * @version 1.0
 */
public class PowerBrick extends Brick {

    /**
     * Puntaje al destruir un ladrillo de power-up.
     */
    public static final int SCORE = 30;

    /**
     * Crea un ladrillo de power-up en la posición indicada.
     *
     * @param x posición X
     * @param y posición Y
     */
    public PowerBrick(int x, int y) {
        super(x, y, "brick_power.png", 1, SCORE, true);
    }
}
