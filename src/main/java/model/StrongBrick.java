package main.java.model;

/**
 * Ladrillo reforzado de Meteor Blitz: 2 HP.
 * Al recibir el primer golpe cambia visualmente (transformación visual),
 * mostrando un sprite dañado. Se destruye con el segundo golpe.
 *
 * @author Laura Jaramillo, Tomas Osorio, Tomas Varona
 * @version 1.0
 */
public class StrongBrick extends Brick {

    /**
     * Puntaje al destruir un ladrillo reforzado.
     */
    public static final int SCORE = 25;

    /**
     * Crea un ladrillo reforzado en la posición indicada.
     *
     * @param x posición X
     * @param y posición Y
     */
    public StrongBrick(int x, int y) {
        super(x, y, "brick_strong.png", 2, SCORE, true);
    }

    /**
     * Al recibir un golpe sin destruirse, cambia al sprite dañado
     * como transformación visual que informa al jugador del estado del ladrillo.
     */
    @Override
    protected void onDamage() {
        if (getHp() == 1) {
            setSprite(uploadImage("brick_strong_damaged.png"));
        }
    }
}
