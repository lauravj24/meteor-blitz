package main.java.model;

/**
 * Clase abstracta base para todos los ladrillos de Meteor Blitz.
 * Define puntos de vida, puntaje y el comportamiento al recibir un impacto.
 *
 * <p>Jerarquía:</p>
 * <ul>
 *   <li>{@link NormalBrick} — 1 HP, rompe con un golpe</li>
 *   <li>{@link StrongBrick} — 2 HP, cambia sprite al dañarse</li>
 *   <li>{@link PowerBrick}  — 1 HP, suelta un power-up al destruirse</li>
 *   <li>{@link IndestructibleBrick} — indestructible, rebota la pelota</li>
 * </ul>
 *
 * @author Laura Jaramillo, Tomas Osorio, Tomas Varona
 * @version 1.0
 */
public abstract class Brick extends Entity {

    /**
     * Puntos de vida del ladrillo.
     */
    private int hp;
    /**
     * Puntos que se suman al score al destruir este ladrillo.
     */
    private final int scoreValue;
    /**
     * Si es false, ningún impacto lo destruye.
     */
    private final boolean destructible;

    /**
     * Constructor para subclases.
     *
     * @param x            posición X (esquina superior izquierda)
     * @param y            posición Y
     * @param spriteName   nombre del archivo de sprite
     * @param hp           puntos de vida iniciales
     * @param scoreValue   puntos al destruirse
     * @param destructible false si el ladrillo es indestructible
     */
    public Brick(int x, int y, String spriteName, int hp, int scoreValue, boolean destructible) {
        super(x, y, uploadImage(spriteName));
        this.hp = hp;
        this.scoreValue = scoreValue;
        this.destructible = destructible;
        if (getWidth() == 0) {
            setWidth(50);
            setHeight(18);
        }
    }

    /**
     * Los ladrillos son estáticos — update() no hace nada.
     */
    @Override
    public void update() { /* estático */ }

    /**
     * Registra un impacto sobre el ladrillo.
     * Si es indestructible, no hace nada.
     * Si los HP llegan a 0, el ladrillo se desactiva.
     *
     * @return true si el ladrillo fue destruido con este impacto
     */
    public boolean hit() {
        if (!destructible) return false;
        hp--;
        onDamage();
        if (hp <= 0) {
            setActive(false);
            return true;
        }
        return false;
    }

    /**
     * Lógica visual/sonora al recibir daño (sin destruirse).
     * Las subclases pueden sobreescribir para cambiar sprite, por ejemplo.
     */
    protected void onDamage() { /* las subclases pueden sobreescribir */ }

    /**
     * @return puntaje que otorga al destruirse
     */
    public int getScoreValue() {
        return scoreValue;
    }

    /**
     * @return true si puede ser destruido
     */
    public boolean isDestructible() {
        return destructible;
    }

    /**
     * @return puntos de vida restantes
     */
    public int getHp() {
        return hp;
    }
}
