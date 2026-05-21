package main.java.model;

import java.util.Random;

/**
 * Gestiona los tres niveles de Meteor Blitz.
 * Construye la grilla de ladrillos a partir de un diseño entero (int[][])
 * y provee constantes de posición para la grilla.
 *
 * <p>Códigos de ladrillo en el diseño:</p>
 * <ul>
 *   <li>0 — celda vacía</li>
 *   <li>1 — NormalBrick azul</li>
 *   <li>2 — NormalBrick verde</li>
 *   <li>3 — NormalBrick amarillo</li>
 *   <li>4 — StrongBrick</li>
 *   <li>5 — PowerBrick</li>
 *   <li>6 — IndestructibleBrick</li>
 * </ul>
 *
 * @author Laura Jaramillo, Tomas Osorio, Tomas Varona
 * @version 1.0
 */
public class LevelManager {

    // -- Dimensiones de la grilla --------------------------------------
    /**
     * Filas de ladrillos por nivel.
     */
    public static final int ROWS = 7;
    /**
     * Columnas de ladrillos por nivel.
     */
    public static final int COLS = 12;
    /**
     * Ancho de cada ladrillo en píxeles.
     */
    public static final int BRICK_W = 50;
    /**
     * Alto de cada ladrillo en píxeles.
     */
    public static final int BRICK_H = 18;
    /**
     * Separación entre ladrillos en píxeles.
     */
    public static final int BRICK_GAP = 4;
    /**
     * Margen izquierdo de la grilla (centrada en 700px).
     */
    public static final int GRID_LEFT = 28;
    /**
     * Posición Y de la primera fila de ladrillos.
     */
    public static final int GRID_TOP = 70;

    // -- Posición inicial de la paleta y la pelota ---------------------
    /**
     * X central de aparición de la paleta.
     */
    public static final int PADDLE_CENTER_X = 350;
    /**
     * Y de la paleta.
     */
    public static final int PADDLE_Y = 580;

    // -- Intervalo de aparición de aliens (en frames) ------------------
    /**
     * Intervalo entre aliens en nivel 1 (15 s).
     */
    public static final int[] ALIEN_INTERVAL = {900, 600, 420};
    /**
     * Máximo de aliens simultáneos por nivel.
     */
    public static final int[] ALIEN_MAX = {2, 3, 4};
    /**
     * Velocidad de alien por nivel.
     */
    public static final float[] ALIEN_SPEED = {1.5f, 2.0f, 2.5f};
    /**
     * Velocidad de la pelota por nivel.
     */
    public static final float[] BALL_SPEED = {Ball.SPEED_L1, Ball.SPEED_L2, Ball.SPEED_L3};

    // -- Diseños de los tres niveles -----------------------------------
    // 0=vacío 1=azul 2=verde 3=amarillo 4=strong 5=power 6=indestructible
    private static final int[][][] LEVELS = {
            // -- Nivel 1: Invasión inicial ---------------------------------
            {
                    {6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
                    {3, 3, 5, 3, 3, 3, 3, 3, 3, 5, 3, 3},
                    {2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
                    {4, 1, 4, 1, 1, 4, 4, 1, 1, 4, 1, 4},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            },
            // -- Nivel 2: Defensa avanzada ---------------------------------
            {
                    {6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6},
                    {4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4},
                    {1, 2, 5, 2, 1, 2, 2, 1, 2, 5, 2, 1},
                    {3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3},
                    {4, 1, 4, 1, 4, 5, 4, 1, 4, 1, 4, 5},
                    {2, 5, 2, 2, 5, 2, 2, 5, 2, 2, 5, 2},
                    {6, 0, 0, 6, 0, 0, 6, 0, 0, 6, 0, 0},
            },
            // -- Nivel 3: Fortaleza galáctica ------------------------------
            {
                    {6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6},
                    {4, 4, 5, 4, 4, 4, 4, 4, 4, 5, 4, 4},
                    {6, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 6},
                    {3, 4, 3, 4, 5, 4, 4, 5, 4, 3, 4, 3},
                    {6, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 6},
                    {4, 4, 4, 5, 4, 4, 4, 4, 5, 4, 4, 4},
                    {6, 0, 6, 0, 6, 0, 6, 0, 6, 0, 6, 0},
            }
    };

    private int currentLevel;
    private final Random rng;

    /**
     * Crea el gestor de niveles iniciando en el nivel 1.
     */
    public LevelManager() {
        this.currentLevel = 1;
        this.rng = new Random();
    }

    /**
     * Construye y retorna la grilla de ladrillos para el nivel indicado.
     *
     * @param level número de nivel (1, 2 o 3)
     * @return matriz [ROWS][COLS] de Brick (null = celda vacía)
     */
    public Brick[][] buildLevel(int level) {
        this.currentLevel = Math.max(1, Math.min(3, level));
        int[][] design = LEVELS[currentLevel - 1];
        Brick[][] grid = new Brick[ROWS][COLS];

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                int px = GRID_LEFT + col * (BRICK_W + BRICK_GAP);
                int py = GRID_TOP + row * (BRICK_H + BRICK_GAP);
                grid[row][col] = createBrick(design[row][col], px, py);
            }
        }
        return grid;
    }

    /**
     * Crea la instancia de ladrillo correspondiente al código dado.
     *
     * @param code código del diseño (0-6)
     * @param x    posición X en píxeles
     * @param y    posición Y en píxeles
     * @return instancia de Brick apropiada, o null si código==0
     */
    private Brick createBrick(int code, int x, int y) {
        switch (code) {
            case 1:
                return new NormalBrick(x, y, 0); // azul
            case 2:
                return new NormalBrick(x, y, 1); // verde
            case 3:
                return new NormalBrick(x, y, 2); // amarillo
            case 4:
                return new StrongBrick(x, y);
            case 5:
                return new PowerBrick(x, y);
            case 6:
                return new IndestructibleBrick(x, y);
            default:
                return null;
        }
    }

    /**
     * Cuenta los ladrillos destructibles restantes en la grilla.
     *
     * @param grid grilla actual
     * @return número de ladrillos destructibles activos
     */
    public static int countDestructible(Brick[][] grid) {
        int count = 0;
        for (Brick[] row : grid)
            for (Brick b : row)
                if (b != null && b.isActive() && b.isDestructible()) count++;
        return count;
    }

    /**
     * Genera un alien para el nivel actual.
     *
     * @param playWidth ancho del área de juego
     * @return nuevo {@link AlienDrifter} listo para usar
     */
    public AlienDrifter spawnAlien(int playWidth) {
        int x = rng.nextInt(Math.max(1, playWidth - 28));
        boolean left = rng.nextBoolean();
        return new AlienDrifter(x, GRID_TOP + 10, ALIEN_SPEED[currentLevel - 1], left);
    }

    /**
     * Genera aleatoriamente un power-up para caer desde el punto indicado.
     *
     * @param x posición X del ladrillo destruido
     * @param y posición Y del ladrillo destruido
     * @return una instancia de {@link ExpandDrop}, {@link SlowBallDrop} o {@link ExtraLifeDrop}
     */
    public PowerUpDrop spawnRandomDrop(int x, int y) {
        int r = rng.nextInt(3);
        switch (r) {
            case 0:
                return new ExpandDrop(x, y);
            case 1:
                return new SlowBallDrop(x, y);
            default:
                return new ExtraLifeDrop(x, y);
        }
    }

    /**
     * @return nivel actual (1–3)
     */
    public int getCurrentLevel() {
        return currentLevel;
    }

    /**
     * @return true si hay un nivel siguiente
     */
    public boolean hasNextLevel() {
        return currentLevel < 3;
    }
}
