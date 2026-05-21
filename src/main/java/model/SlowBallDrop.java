package main.java.model;

/**
 * Power-up de reducción de velocidad de la pelota para Meteor Blitz.
 * Al atraparse ralentiza la pelota durante 10 segundos,
 * dando más tiempo de reacción al jugador.
 *
 * @author Laura Jaramillo, Tomas Osorio, Tomas Varona
 * @version 1.0
 */
public class SlowBallDrop extends PowerUpDrop {

    /**
     * Frames que dura el efecto de ralentización (10s × 60 FPS).
     */
    private static final int SLOW_FRAMES = 600;

    /**
     * Crea la cápsula de ralentización en la posición indicada.
     *
     * @param x posición X
     * @param y posición Y
     */
    public SlowBallDrop(int x, int y) {
        super(x, y, "powerup_slow.png", Type.SLOW_BALL);
    }

    /**
     * Reduce la velocidad de la pelota al mínimo.
     * El efecto expira automáticamente en timer.
     *
     * @param paddle la paleta del jugador (no se modifica)
     * @param ball   la pelota cuya velocidad se reduce
     */
    @Override
    public void activate(Paddle paddle, Ball ball) {
        ball.setSpeed(Ball.SPEED_SLOW);
    }

    /**
     * @return duración del efecto en frames
     */
    public static int getSlowFrames() {
        return SLOW_FRAMES;
    }
}
