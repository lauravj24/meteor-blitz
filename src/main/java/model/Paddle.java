package main.java.model;

/**
 * La paleta que el jugador controla en Meteor Blitz.
 * Se mueve horizontalmente con las flechas del teclado o con el mouse.
 * Puede expandirse temporalmente al recoger el power-up correspondiente.
 *
 * @author Laura Jaramillo, Tomas Osorio, Tomas Varona
 * @version 1.0
 */
public class Paddle extends Entity {

    /**
     * Ancho normal de la paleta en píxeles.
     */
    public static final int NORMAL_WIDTH = 90;
    /**
     * Ancho expandido con el power-up Expansión.
     */
    public static final int EXPANDED_WIDTH = 140;
    /**
     * Alto de la paleta en píxeles.
     */
    public static final int HEIGHT = 14;
    /**
     * Velocidad de desplazamiento horizontal con teclado (px/frame).
     */
    public static final int SPEED = 7;
    /**
     * Duración del poder de expansión en frames (8 segundos × 60 FPS).
     */
    public static final int EXPAND_FRAMES = 480;

    /**
     * Límite izquierdo del área de juego.
     */
    private final int leftBound;
    /**
     * Límite derecho del área de juego.
     */
    private final int rightBound;
    /**
     * Frames restantes del efecto de expansión (0 = tamaño normal).
     */
    private int expandTimer;

    /**
     * Crea la paleta en la posición indicada.
     *
     * @param x          posición X inicial (esquina izquierda)
     * @param y          posición Y fija durante el juego
     * @param leftBound  límite izquierdo del área de juego
     * @param rightBound límite derecho del área de juego
     */
    public Paddle(int x, int y, int leftBound, int rightBound) {
        super(x, y, uploadImage("paddle.png"));
        this.leftBound = leftBound;
        this.rightBound = rightBound;
        this.expandTimer = 0;
        if (getWidth() == 0) {
            setWidth(NORMAL_WIDTH);
            setHeight(HEIGHT);
        }
    }

    /**
     * Actualiza el temporizador del efecto de expansión.
     * Cuando expira, restaura el tamaño normal.
     */
    @Override
    public void update() {
        if (expandTimer > 0) {
            expandTimer--;
            if (expandTimer == 0) {
                setWidth(NORMAL_WIDTH);
                setSprite(uploadImage("paddle.png"));
            }
        }
    }

    /**
     * Mueve la paleta hacia la izquierda (limitado por leftBound).
     */
    public void moveLeft() {
        setX(Math.max(leftBound, getX() - SPEED));
    }

    /**
     * Mueve la paleta hacia la derecha (limitado por rightBound).
     */
    public void moveRight() {
        setX(Math.min(rightBound - getWidth(), getX() + SPEED));
    }

    /**
     * Mueve la paleta directamente a la posición X del cursor del mouse,
     * centrándola en el puntero.
     *
     * @param mouseX posición X del cursor
     */
    public void moveTo(int mouseX) {
        int nx = mouseX - getWidth() / 2;
        setX(Math.max(leftBound, Math.min(rightBound - getWidth(), nx)));
    }

    /**
     * Activa el efecto de expansión: la paleta se vuelve más ancha
     * durante {@value #EXPAND_FRAMES} frames (transformación visual).
     */
    public void activateExpand() {
        expandTimer = EXPAND_FRAMES;
        setWidth(EXPANDED_WIDTH);
        setSprite(uploadImage("paddle_wide.png"));
    }

    /**
     * Reinicia la paleta a su estado y posición inicial para un nuevo intento.
     *
     * @param centerX posición X central donde debe reaparecer
     * @param y       posición Y de la paleta
     */
    public void reset(int centerX, int y) {
        setX(centerX - NORMAL_WIDTH / 2);
        setY(y);
        setWidth(NORMAL_WIDTH);
        setSprite(uploadImage("paddle.png"));
        expandTimer = 0;
    }

    // -- Getters -------------------------------------------------------

    /**
     * @return true si el efecto de expansión está activo
     */
    public boolean isExpanded() {
        return expandTimer > 0;
    }

    /**
     * @return frames restantes del efecto de expansión
     */
    public int getExpandTimer() {
        return expandTimer;
    }
}
