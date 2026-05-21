package main.java.model;

/**
 * Power-up de vida extra para Meteor Blitz.
 * Al atraparse el controlador detecta el tipo {@code EXTRA_LIFE}
 * y llama a {@code player.addLife()}.
 * Esta clase implementa {@link Activatable} con efecto vacío
 * porque la lógica de vidas vive en {@link Player}, no en la paleta ni la pelota.
 *
 * @author Laura Jaramillo, Tomas Osorio, Tomas Varona
 * @version 1.0
 */
public class ExtraLifeDrop extends PowerUpDrop {

    /**
     * Crea la cápsula de vida extra en la posición indicada.
     *
     * @param x posición X
     * @param y posición Y
     */
    public ExtraLifeDrop(int x, int y) {
        super(x, y, "powerup_life.png", Type.EXTRA_LIFE);
    }

    /**
     * La vida se agrega en el controlador al detectar {@code Type.EXTRA_LIFE}.
     * Este método queda vacío intencionalmente.
     *
     * @param paddle la paleta (no se modifica)
     * @param ball   la pelota (no se modifica)
     */
    @Override
    public void activate(Paddle paddle, Ball ball) {
        // La vida extra la gestiona GameController al leer getType() == EXTRA_LIFE
    }
}
