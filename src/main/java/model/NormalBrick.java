package main.java.model;

/**
 * Ladrillo normal de Meteor Blitz: 1 HP, se destruye con un golpe.
 * Existen tres variantes de color (azul, verde, amarillo) controladas
 * por el parámetro {@code colorIndex} al construir.
 *
 * @author Laura Jaramillo, Tomas Osorio, Tomas Varona
 * @version 1.0
 */
public class NormalBrick extends Brick {

    /**
     * Puntaje al destruir un ladrillo normal.
     */
    public static final int SCORE = 10;

    /**
     * Crea un ladrillo normal con el color indicado.
     *
     * @param x          posición X
     * @param y          posición Y
     * @param colorIndex 0 = azul, 1 = verde, 2 = amarillo
     */
    public NormalBrick(int x, int y, int colorIndex) {
        super(x, y, spriteFor(colorIndex), 1, SCORE, true);
    }

    /**
     * Retorna el nombre del sprite según el índice de color.
     *
     * @param idx índice de color (0, 1 o 2)
     * @return nombre del archivo de sprite
     */
    private static String spriteFor(int idx) {
        switch (idx % 3) {
            case 0:
                return "brick_blue.png";
            case 1:
                return "brick_green.png";
            default:
                return "brick_yellow.png";
        }
    }
}
