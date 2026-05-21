package main.java.model;

/**
 * La pelota de Meteor Blitz. Se mueve con velocidad flotante (dx, dy)
 * y rebota en paredes, paleta y ladrillos.
 * Usa coordenadas flotantes internas para mayor precisión de movimiento.
 *
 * @author Laura Jaramillo, Tomas Osorio, Tomas Varona
 * @version 1.0
 */
public class Ball extends Entity {

    /**
     * Velocidad base en px/frame para el nivel 1.
     */
    public static final float SPEED_L1 = 4.5f;
    /**
     * Velocidad base en px/frame para el nivel 2.
     */
    public static final float SPEED_L2 = 5.5f;
    /**
     * Velocidad base en px/frame para el nivel 3.
     */
    public static final float SPEED_L3 = 6.5f;
    /**
     * Velocidad reducida al recoger el power-up SlowBall.
     */
    public static final float SPEED_SLOW = 3.0f;
    /**
     * Ángulo máximo de rebote en el paddle (grados).
     */
    private static final float MAX_ANGLE = 65f;

    /**
     * Componente horizontal de velocidad (px/frame, puede ser negativo).
     */
    private float dx;
    /**
     * Componente vertical de velocidad (px/frame, negativo = hacia arriba).
     */
    private float dy;
    /**
     * Posición X con decimales para movimiento fluido.
     */
    private float preciseX;
    /**
     * Posición Y con decimales para movimiento fluido.
     */
    private float preciseY;
    /**
     * Velocidad escalar actual.
     */
    private float speed;
    /**
     * true si la pelota ha sido lanzada (no está pegada a la paleta).
     */
    private boolean launched;

    /**
     * Crea la pelota en la posición indicada, sin lanzar.
     *
     * @param x posición X inicial
     * @param y posición Y inicial
     */
    public Ball(int x, int y) {
        super(x, y, uploadImage("ball.png"));
        this.preciseX = x;
        this.preciseY = y;
        this.speed = SPEED_L1;
        this.dx = 0;
        this.dy = 0;
        this.launched = false;
        // Si el sprite no cargó, asegurar un tamaño por defecto
        if (getWidth() == 0) {
            setWidth(12);
            setHeight(12);
        }
    }

    /**
     * Actualiza la posición de la pelota en base a dx y dy.
     * Solo se mueve si ha sido lanzada.
     */
    @Override
    public void update() {
        if (!launched) return;
        preciseX += dx;
        preciseY += dy;
        setX(Math.round(preciseX));
        setY(Math.round(preciseY));
    }

    /**
     * Lanza la pelota con un ángulo relativo a la vertical.
     * 0° = recto hacia arriba; valores negativos = hacia la izquierda.
     *
     * @param angleDeg ángulo en grados (rango sugerido: -45 a 45)
     */
    public void launch(float angleDeg) {
        double rad = Math.toRadians(angleDeg);
        this.dx = (float) (speed * Math.sin(rad));
        this.dy = (float) (-speed * Math.cos(rad));
        this.launched = true;
    }

    /**
     * Invierte la componente horizontal (rebote en pared lateral).
     */
    public void bounceX() {
        dx = -dx;
    }

    /**
     * Invierte la componente vertical (rebote en techo o ladrillo).
     */
    public void bounceY() {
        dy = -dy;
    }

    /**
     * Calcula y aplica el rebote al golpear la paleta.
     * El ángulo depende del punto relativo de impacto:
     * borde izquierdo → ángulo pronunciado izquierda,
     * borde derecho → ángulo pronunciado derecha,
     * centro → casi vertical.
     *
     * @param paddleX     posición X de la paleta
     * @param paddleWidth ancho actual de la paleta
     */
    public void bounceOffPaddle(int paddleX, int paddleWidth) {
        float relHit = (preciseX + getWidth() / 2f - paddleX) / paddleWidth;
        relHit = Math.max(0.05f, Math.min(0.95f, relHit));
        float angle = (relHit - 0.5f) * 2f * MAX_ANGLE;
        double rad = Math.toRadians(angle);
        this.dx = (float) (speed * Math.sin(rad));
        this.dy = -(float) (speed * Math.cos(rad)); // siempre hacia arriba
    }

    /**
     * Pega la pelota encima de la paleta (antes del lanzamiento).
     *
     * @param paddleX     posición X de la paleta
     * @param paddleWidth ancho de la paleta
     * @param paddleY     posición Y de la paleta (arriba del rectángulo)
     */
    public void stickToPaddle(int paddleX, int paddleWidth, int paddleY) {
        this.preciseX = paddleX + paddleWidth / 2f - getWidth() / 2f;
        this.preciseY = paddleY - getHeight();
        setX(Math.round(preciseX));
        setY(Math.round(preciseY));
        this.dx = 0;
        this.dy = 0;
        this.launched = false;
    }

    /**
     * Ajusta la velocidad escalar sin cambiar la dirección de movimiento.
     *
     * @param newSpeed nueva velocidad en px/frame
     */
    public void setSpeed(float newSpeed) {
        if (!launched) {
            this.speed = newSpeed;
            return;
        }
        float mag = (float) Math.sqrt(dx * dx + dy * dy);
        if (mag > 0) {
            this.dx = dx / mag * newSpeed;
            this.dy = dy / mag * newSpeed;
        }
        this.speed = newSpeed;
    }

    /**
     * Reposiciona la pelota con precisión de float.
     *
     * @param px nueva X precisa
     * @param py nueva Y precisa
     */
    public void setPrecisePosition(float px, float py) {
        this.preciseX = px;
        this.preciseY = py;
        setX(Math.round(px));
        setY(Math.round(py));
    }

    // -- Getters -------------------------------------------------------

    /**
     * @return componente horizontal de velocidad
     */
    public float getDx() {
        return dx;
    }

    /**
     * @param dx nueva componente horizontal
     */
    public void setDx(float dx) {
        this.dx = dx;
    }

    /**
     * @return componente vertical de velocidad
     */
    public float getDy() {
        return dy;
    }

    /**
     * @param dy nueva componente vertical
     */
    public void setDy(float dy) {
        this.dy = dy;
    }

    /**
     * @return velocidad escalar actual
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * @return true si la pelota ha sido lanzada
     */
    public boolean isLaunched() {
        return launched;
    }

    /**
     * @param launched nuevo estado de lanzamiento
     */
    public void setLaunched(boolean launched) {
        this.launched = launched;
    }

    /**
     * @return posición X precisa (float)
     */
    public float getPreciseX() {
        return preciseX;
    }

    /**
     * @return posición Y precisa (float)
     */
    public float getPreciseY() {
        return preciseY;
    }
}
