package main.java.model;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

/**
 * Clase abstracta base para todas las entidades del juego (Cobra, enemigos, objetos).
 * Define posición, sprite, dirección y hitbox.
 * Todas las subclases deben implementar el método update()
 *
 * @author Laura Jaramillo, Tomas Osorio, Tomas Varona
 * @version 1.0
 */
public abstract class Entity {

    /**
     * Dirección: ninguna (detenido).
     */
    public static final int DIR_NONE = 0;
    /**
     * Dirección: izquierda.
     */
    public static final int DIR_LEFT = 1;
    /**
     * Dirección: derecha.
     */
    public static final int DIR_RIGHT = 3;
    /**
     * Dirección: arriba.
     */
    public static final int DIR_UP = 5;
    /**
     * Dirección: abajo.
     */
    public static final int DIR_DOWN = 2;

    /**
     * Posición X en píxeles.
     */
    private int x;
    /**
     * Posición Y en píxeles.
     */
    private int y;
    /**
     * Ancho de la entidad en píxeles (tomado del sprite).
     */
    private int width;
    /**
     * Alto de la entidad en píxeles (tomado del sprite).
     */
    private int height;
    /**
     * Imagen actual del sprite.
     */
    private BufferedImage sprite;
    /**
     * Indica si la entidad está activa en el juego.
     */
    private boolean active;
    /**
     * Dirección actual de movimiento.
     */
    private int direction;

    /**
     * Constructor principal. Asigna posición y sprite, e inicializa width/height
     * correctamente a partir del sprite cargado.
     *
     * @param x      posición X inicial
     * @param y      posición Y inicial
     * @param sprite imagen del sprite (puede ser null)
     */
    public Entity(int x, int y, BufferedImage sprite) {
        this.x = x;
        this.y = y;
        this.active = true;
        this.direction = DIR_NONE;
        // setSprite asigna width y height correctamente
        setSprite(sprite);
    }

    /**
     * Constructor vacío para subclases que inicializan manualmente.
     */
    protected Entity() {
    }

    /**
     * Actualiza el estado de la entidad cada frame.
     * Debe ser implementado por cada subclase con su lógica particular.
     */
    public abstract void update();

    /**
     * Retorna la hitbox actual de la entidad en su posición real.
     *
     * @return Rectangle con posición y dimensiones actuales
     */
    public Rectangle getHitBox() {
        return new Rectangle(x, y, width, height);
    }

    /**
     * Retorna una hitbox proyectada a una posición futura.
     * Usado para detectar colisiones antes de moverse.
     *
     * @param pX posición X futura
     * @param pY posición Y futura
     * @return Rectangle proyectado
     */
    public Rectangle getHitBox(int pX, int pY) {
        return new Rectangle(pX, pY, width, height);
    }

    /**
     * Calcula el desplazamiento horizontal según la dirección actual.
     *
     * @return -1 (izquierda), +1 (derecha) o 0 (sin movimiento horizontal)
     */
    public int calculateDx() {
        if (direction == DIR_LEFT) return -1;
        if (direction == DIR_RIGHT) return 1;
        return 0;
    }

    /**
     * Calcula el desplazamiento vertical según la dirección actual.
     *
     * @return -1 (arriba), +1 (abajo) o 0 (sin movimiento vertical)
     */
    public int calculateDy() {
        if (direction == DIR_UP) return -1;
        if (direction == DIR_DOWN) return 1;
        return 0;
    }

    /**
     * Carga una imagen desde la carpeta de recursos /images/.
     *
     * @param name nombre del archivo (ej: "ball.png")
     * @return BufferedImage cargada, o null si no se encuentra
     */
    public static BufferedImage uploadImage(String name) {
        try {
            InputStream is = Entity.class.getResourceAsStream("/images/" + name);
            if (is == null) {
                System.err.println("[Entity] Imagen no encontrada: " + name);
                return null;
            }
            return ImageIO.read(is);
        } catch (Exception e) {
            System.err.println("[Entity] Error cargando imagen: " + name);
            return null;
        }
    }

    // -- Getters y Setters ---------------------------------------------

    /**
     * @return posición X actual
     */
    public int getX() {
        return x;
    }

    /**
     * @param x nueva posición X
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * @return posición Y actual
     */
    public int getY() {
        return y;
    }

    /**
     * @param y nueva posición Y
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * @return ancho del sprite
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param width nuevo ancho (usado por Paddle para expansión dinámica)
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return alto del sprite
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param height nuevo alto
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * @return sprite actual
     */
    public BufferedImage getSprite() {
        return sprite;
    }

    /**
     * Asigna un nuevo sprite y actualiza width/height automáticamente.
     *
     * @param sprite nueva imagen del sprite
     */
    public void setSprite(BufferedImage sprite) {
        this.sprite = sprite;
        if (sprite != null) {
            this.width = sprite.getWidth();
            this.height = sprite.getHeight();
        }
    }

    /**
     * @return true si la entidad está activa
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active nuevo estado de actividad
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @return dirección actual de movimiento
     */
    public int getDirection() {
        return direction;
    }

    /**
     * @param direction nueva dirección de movimiento
     */
    public void setDirection(int direction) {
        this.direction = direction;
    }
}
