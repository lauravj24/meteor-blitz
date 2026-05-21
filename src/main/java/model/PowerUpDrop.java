package main.java.model;

/**
 * Cápsula de power-up que cae al destruirse un {@link PowerBrick}.
 * Si la paleta la atrapa, se activa el efecto del power-up.
 * Implementa {@link Activatable} para aplicar polimorfismo en el controlador.
 *
 * <p>Subclases concretas:</p>
 * <ul>
 *   <li>{@link ExpandDrop}    — amplía la paleta temporalmente</li>
 *   <li>{@link SlowBallDrop}  — reduce la velocidad de la pelota</li>
 *   <li>{@link ExtraLifeDrop} — otorga una vida extra</li>
 * </ul>
 *
 * @author Laura Jaramillo, Tomas Osorio, Tomas Varona
 * @version 1.0
 */
public abstract class PowerUpDrop extends Entity implements Activatable {

    /**
     * Velocidad de caída en px/frame.
     */
    private static final float FALL_SPEED = 3.0f;

    /**
     * Tipos de power-up disponibles en el juego.
     */
    public enum Type {EXPAND, SLOW_BALL, EXTRA_LIFE}

    /**
     * Tipo de este power-up.
     */
    private final Type type;
    /**
     * Posición Y con decimales para caída suave.
     */
    private float preciseY;

    /**
     * Crea una cápsula que cae desde la posición indicada.
     *
     * @param x          posición X central del ladrillo destruido
     * @param y          posición Y del ladrillo destruido
     * @param spriteName nombre del sprite de la cápsula
     * @param type       tipo de efecto que produce
     */
    public PowerUpDrop(int x, int y, String spriteName, Type type) {
        super(x, y, uploadImage(spriteName));
        this.type = type;
        this.preciseY = y;
        if (getWidth() == 0) {
            setWidth(20);
            setHeight(20);
        }
    }

    /**
     * Hace caer la cápsula un paso hacia abajo.
     * Se desactiva si sale de la pantalla (se pierde).
     */
    @Override
    public void update() {
        preciseY += FALL_SPEED;
        setY(Math.round(preciseY));
    }

    /**
     * Aplica el efecto del power-up al atraparse.
     * Implementado en cada subclase concreta.
     *
     * @param paddle la paleta del jugador
     * @param ball   la pelota activa
     */
    @Override
    public abstract void activate(Paddle paddle, Ball ball);

    /**
     * @return tipo de este power-up
     */
    public Type getType() {
        return type;
    }
}
