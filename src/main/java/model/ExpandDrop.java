package main.java.model;

/**
 * Power-up de expansión de paleta para Meteor Blitz.
 * Al atraparse amplía visualmente la paleta durante 8 segundos,
 * facilitando devolver la pelota (transformación visual y funcional).
 *
 * @author Laura Jaramillo, Tomas Osorio, Tomas Varona
 * @version 1.0
 */
public class ExpandDrop extends PowerUpDrop {

    /**
     * Crea la cápsula de expansión en la posición indicada.
     *
     * @param x posición X
     * @param y posición Y
     */
    public ExpandDrop(int x, int y) {
        super(x, y, "powerup_expand.png", Type.EXPAND);
    }

    /**
     * Amplía la paleta activando su efecto de expansión.
     *
     * @param paddle la paleta del jugador
     * @param ball   la pelota (no se modifica)
     */
    @Override
    public void activate(Paddle paddle, Ball ball) {
        paddle.activateExpand();
    }
}
