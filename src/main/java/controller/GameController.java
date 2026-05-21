package main.java.controller;

import main.java.model.Alien;
import main.java.model.Ball;
import main.java.model.Brick;
import main.java.model.GameTimer;
import main.java.model.LevelManager;
import main.java.model.Paddle;
import main.java.model.Player;
import main.java.model.PowerBrick;
import main.java.model.PowerUpDrop;
import main.java.model.ScoreManager;
import main.java.model.SlowBallDrop;
import main.java.view.GamePanel;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador principal de Meteor Blitz. Implementa {@link Runnable} para
 * correr el bucle de juego a 60 FPS en un hilo separado ("MeteorBlitz-GameLoop").
 *
 * <p>Responsabilidades:</p>
 * <ul>
 *   <li>Coordinar modelo (paleta, pelota, ladrillos, aliens, power-ups) con la vista</li>
 *   <li>Gestionar la máquina de estados del juego</li>
 *   <li>Detectar colisiones (pelota-pared, pelota-paleta, pelota-ladrillo, pelota-alien)</li>
 *   <li>Gestionar el audio y el puntaje</li>
 * </ul>
 *
 * @author Laura Jaramillo, Tomas Osorio, Tomas Varona
 * @version 1.0
 */
public class GameController implements Runnable {

    // -- Constantes del bucle ------------------------------------------
    private static final int FPS = 60;
    private static final long NANOS_PER_FRAME = 1_000_000_000L / FPS;

    // -- Límites del área de juego -------------------------------------
    /**
     * Ancho total de la ventana.
     */
    public static final int WIDTH = 700;
    /**
     * Alto total de la ventana.
     */
    public static final int HEIGHT = 620;
    /**
     * Límite superior del área de juego (bajo el HUD).
     */
    public static final int WALL_TOP = 50;
    /**
     * Límite inferior: si la pelota lo supera, se pierde.
     */
    public static final int PLAY_BOTTOM = HEIGHT;

    // -- Estados -------------------------------------------------------
    public static final String STATE_WELCOME = "welcome";
    public static final String STATE_INSTRUCTIONS = "instructions";
    public static final String STATE_PLAYING = "playing";
    public static final String STATE_WIN_LEVEL = "win_level";
    public static final String STATE_WIN_GAME = "win_game";
    public static final String STATE_GAMEOVER = "gameover";

    // -- Modelos -------------------------------------------------------
    private final GamePanel view;
    private final ScoreManager scoreManager;
    private final LevelManager levelManager;
    private Player player;
    private Paddle paddle;
    private Ball ball;
    private Brick[][] bricks;
    private final List<Alien> aliens;
    private final List<PowerUpDrop> drops;
    private GameTimer timer;

    // -- Control del bucle ---------------------------------------------
    private Thread gameThread;
    private volatile boolean running;
    private String state;

    // -- Entrada -------------------------------------------------------
    private main.java.controller.InputHandler inputHandler;

    // -- Estado de juego -----------------------------------------------
    /**
     * Frames desde el último alien spawneado.
     */
    private int alienSpawnCounter;
    /**
     * Timer en frames del efecto SlowBall (0 = inactivo).
     */
    private int slowBallTimer;
    /**
     * Velocidad normal de la pelota para el nivel actual.
     */
    private float normalBallSpeed;
    /**
     * Tiempo de inicio de la partida para calcular el tiempo jugado.
     */
    private long gameStartTime;

    // -- Menú principal ------------------------------------------------
    private int menuIndex = 0;
    private boolean menuKeyConsumed = false;

    // -- Menú de pausa (0=Continuar, 1=Volver al inicio) -------------
    private boolean pauseMenuKeyConsumed = false;

    // -- Audio ---------------------------------------------------------
    private Clip clipIntro;
    private Clip clipGameplay;
    private Clip clipCollision;
    private Clip clipGameOver;
    private Clip clipPowerUp;
    private Clip clipWin;

    /**
     * Crea el controlador e inicializa todos los subsistemas.
     *
     * @param view panel de juego donde se renderiza todo
     */
    public GameController(GamePanel view) {
        this.view = view;
        this.scoreManager = new ScoreManager();
        this.levelManager = new LevelManager();
        this.aliens = new ArrayList<>();
        this.drops = new ArrayList<>();
        this.timer = new GameTimer();
        this.state = STATE_WELCOME;

        // Crear player y paddle/ball por defecto (se reinician al iniciar)
        this.player = new Player("Jugador");
        this.paddle = new Paddle(
                LevelManager.PADDLE_CENTER_X - Paddle.NORMAL_WIDTH / 2,
                LevelManager.PADDLE_Y, 0, WIDTH
        );
        this.ball = new Ball(LevelManager.PADDLE_CENTER_X, LevelManager.PADDLE_Y - 12);
        this.bricks = levelManager.buildLevel(1);

        inputHandler = new main.java.controller.InputHandler(player, paddle);
        view.addKeyListener(inputHandler);
        view.addMouseMotionListener(inputHandler);

        view.assignModels(bricks, paddle, ball, aliens, drops, player, timer,
                scoreManager, levelManager.getCurrentLevel());
        view.setGameState(state);

        clipIntro = loadSound("intro.wav");
        clipGameplay = loadSound("gameplay.wav");
        clipCollision = loadSound("collision.wav");
        clipGameOver = loadSound("gameover.wav");
        clipPowerUp = loadSound("powerup.wav");
        clipWin = loadSound("win.wav");
    }

    // -- Ciclo de vida -------------------------------------------------

    /**
     * Inicia el hilo del bucle de juego y reproduce la intro.
     */
    public synchronized void start() {
        if (running) return;
        running = true;
        new Thread(new Runnable() {
            public void run() {
                playSound(clipIntro);
            }
        }).start();
        gameThread = new Thread(this, "MeteorBlitz-GameLoop");
        gameThread.start();
    }

    /**
     * Detiene el bucle del juego y libera los recursos de audio.
     */
    public synchronized void stop() {
        running = false;
        if (gameThread != null) {
            try {
                gameThread.join(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        closeAudio();
    }

    /**
     * Bucle principal a 60 FPS con delta-time.
     */
    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double delta = 0;
        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / (double) NANOS_PER_FRAME;
            lastTime = now;
            if (delta >= 1) {
                update();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        view.repaint();
                    }
                });
                delta--;
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            }
        }
    }

    // -- Lógica central ------------------------------------------------

    /**
     * Punto central de actualización, llamado 60 veces por segundo.
     */
    private void update() {

        // -- Bienvenida ------------------------------------------------
        if (STATE_WELCOME.equals(state)) {
            int clicked = view.getMenuClickIndex();
            if (clicked >= 0) {
                view.clearMenuClick();
                executeMenu(clicked);
                return;
            }

            // Navegación por teclado: arriba = -1, abajo = +1
            // Funciona tanto si el panel tiene foco como si lo tiene el nameField
            int dir = 0;
            if (inputHandler.isUpPressed()) dir = -1;
            else if (inputHandler.isDownPressed()) dir = 1;
            int fieldDir = view.getMenuNavDirection();
            if (dir == 0 && fieldDir != 0) dir = fieldDir;

            if (dir == 0) {
                menuKeyConsumed = false;
            } else if (!menuKeyConsumed) {
                menuIndex = (menuIndex + dir + 3) % 3;
                view.setMenuIndex(menuIndex);
                menuKeyConsumed = true;
            }
            if (player.isEnterPressed() || view.isNameEnterPressed()) {
                player.setEnterPressed(false);
                view.clearNameEnterPressed();
                executeMenu(menuIndex);
            }
            return;
        }

        // -- Instrucciones ---------------------------------------------
        if (STATE_INSTRUCTIONS.equals(state)) {
            if (player.isEnterPressed() || view.isNameEnterPressed()) {
                player.setEnterPressed(false);
                view.clearNameEnterPressed();
                changeState(STATE_WELCOME);
            }
            return;
        }

        // -- Victoria de nivel → siguiente o victoria total ------------
        if (STATE_WIN_LEVEL.equals(state)) {
            if (player.isEnterPressed()) {
                player.setEnterPressed(false);
                if (levelManager.hasNextLevel()) {
                    startLevel(levelManager.getCurrentLevel() + 1);
                } else {
                    // El puntaje ya fue guardado en checkWinCondition()
                    changeState(STATE_WIN_GAME);
                }
            }
            return;
        }

        // -- Game Over / Victoria total → bienvenida -------------------
        if (STATE_GAMEOVER.equals(state) || STATE_WIN_GAME.equals(state)) {
            if (player.isEnterPressed()) {
                player.setEnterPressed(false);
                resetToWelcome();
            }
            return;
        }

        // -- Menú de pausa ---------------------------------------------
        if (STATE_PLAYING.equals(state) && player.isPaused()) {
            // Navegación por teclado dentro del menú de pausa
            int dir = 0;
            if (inputHandler.isUpPressed()) dir = -1;
            else if (inputHandler.isDownPressed()) dir = 1;

            if (dir == 0) {
                pauseMenuKeyConsumed = false;
            } else if (!pauseMenuKeyConsumed) {
                int idx = (view.getPauseMenuIndex() + dir + 2) % 2;
                view.setPauseMenuIndex(idx);
                pauseMenuKeyConsumed = true;
            }

            // Confirmar con Enter
            if (player.isEnterPressed()) {
                player.setEnterPressed(false);
                if (view.getPauseMenuIndex() == 0) {
                    // Continuar: quitar la pausa y resetear índice
                    view.setPauseMenuIndex(0);
                    player.togglePause();
                } else {
                    // Volver al inicio
                    view.setPauseMenuIndex(0);
                    player.togglePause(); // asegura que paused=false antes de resetear
                    resetToWelcome();
                }
            }
            return;
        }

        // Si llegamos aquí estando en STATE_PLAYING, el juego está activo (no pausado).
        // Reseteamos el índice del menú de pausa por si el jugador presionó P para
        // desactivar la pausa directamente sin usar el menú.
        if (STATE_PLAYING.equals(state)) {
            view.setPauseMenuIndex(0);
            pauseMenuKeyConsumed = false;
        }

        // -- Juego activo ----------------------------------------------
        if (!STATE_PLAYING.equals(state)) return;

        timer.tick();
        movePaddle();
        updateBall();
        movePowerUpDrops();
        spawnAndMoveAliens();
        checkWinCondition();
    }

    // -- Movimiento ----------------------------------------------------

    /**
     * Mueve la paleta según las teclas presionadas (el mouse lo hace en InputHandler).
     */
    private void movePaddle() {
        if (inputHandler.isLeftPressed()) paddle.moveLeft();
        if (inputHandler.isRightPressed()) paddle.moveRight();
        paddle.update();
        // Actualizar posición de la pelota si no fue lanzada aún
        if (!ball.isLaunched()) {
            ball.stickToPaddle(paddle.getX(), paddle.getWidth(), paddle.getY());
        }
    }

    /**
     * Actualiza la pelota: lanzamiento, movimiento, colisiones con paredes,
     * paleta y ladrillos, y detección de pérdida.
     */
    private void updateBall() {
        // Lanzamiento con SPACE
        if (!ball.isLaunched() && player.isSpacePressed()) {
            ball.launch(-10f + (float) (Math.random() * 20)); // pequeño ángulo aleatorio
            player.setSpacePressed(false);
        }

        ball.update();

        if (!ball.isLaunched()) return;

        // -- Colisión con paredes --------------------------------------
        if (ball.getX() < 0) {
            ball.setPrecisePosition(0, ball.getPreciseY());
            ball.bounceX();
        } else if (ball.getX() + ball.getWidth() > WIDTH) {
            ball.setPrecisePosition(WIDTH - ball.getWidth(), ball.getPreciseY());
            ball.bounceX();
        }
        if (ball.getY() < WALL_TOP) {
            ball.setPrecisePosition(ball.getPreciseX(), WALL_TOP);
            ball.bounceY();
        }

        // -- Colisión con la paleta ------------------------------------
        Rectangle ballBox = ball.getHitBox();
        Rectangle paddleBox = paddle.getHitBox();
        if (ballBox.intersects(paddleBox) && ball.getDy() > 0) {
            // Sacar la pelota del paddle para evitar que quede atascada
            ball.setPrecisePosition(ball.getPreciseX(),
                    paddle.getY() - ball.getHeight());
            ball.bounceOffPaddle(paddle.getX(), paddle.getWidth());
            playSound(clipCollision);
        }

        // -- Colisión con ladrillos ------------------------------------
        checkBallBricks();

        // -- Pelota perdida (cayó por debajo) --------------------------
        if (ball.getY() > PLAY_BOTTOM) {
            handleBallLost();
        }

        // -- Timer del SlowBall ----------------------------------------
        if (slowBallTimer > 0) {
            slowBallTimer--;
            if (slowBallTimer == 0) {
                ball.setSpeed(normalBallSpeed);
            }
        }
    }

    /**
     * Comprueba y resuelve las colisiones entre la pelota y los ladrillos.
     */
    private void checkBallBricks() {
        Rectangle ballBox = ball.getHitBox();
        for (int r = 0; r < LevelManager.ROWS; r++) {
            for (int c = 0; c < LevelManager.COLS; c++) {
                Brick brick = bricks[r][c];
                if (brick == null || !brick.isActive()) continue;
                if (!ballBox.intersects(brick.getHitBox())) continue;

                // Determinar dirección del rebote según el lado de impacto
                resolveBrickBounce(brick);

                boolean destroyed = brick.hit();
                if (destroyed) {
                    player.addScore(brick.getScoreValue());
                    playSound(clipCollision);
                    if (brick instanceof PowerBrick) {
                        // Soltar un power-up desde el centro del ladrillo
                        int dropX = brick.getX() + brick.getWidth() / 2 - 10;
                        drops.add(levelManager.spawnRandomDrop(dropX, brick.getY()));
                    }
                    bricks[r][c] = null;
                }
                return; // Un ladrillo por frame evita múltiples rebotes
            }
        }
    }

    /**
     * Calcula desde qué lado impactó la pelota al ladrillo y rebota correspondientemente.
     *
     * @param brick ladrillo golpeado
     */
    private void resolveBrickBounce(Brick brick) {
        // Centro de la pelota antes y después del movimiento
        float bCX = ball.getPreciseX() + ball.getWidth() / 2f;
        float bCY = ball.getPreciseY() + ball.getHeight() / 2f;
        float brLeft = brick.getX();
        float brRight = brick.getX() + brick.getWidth();
        float brTop = brick.getY();
        float brBottom = brick.getY() + brick.getHeight();

        // Calcular solapamiento en cada eje para determinar el lado
        float overlapLeft = brRight - (bCX - ball.getWidth() / 2f);
        float overlapRight = (bCX + ball.getWidth() / 2f) - brLeft;
        float overlapTop = brBottom - (bCY - ball.getHeight() / 2f);
        float overlapBottom = (bCY + ball.getHeight() / 2f) - brTop;

        float minH = Math.min(overlapLeft, overlapRight);
        float minV = Math.min(overlapTop, overlapBottom);

        if (minH < minV) {
            ball.bounceX();
        } else {
            ball.bounceY();
        }
    }

    /**
     * Gestiona la pérdida de la pelota (cayó por debajo de la paleta).
     */
    private void handleBallLost() {
        playSound(clipCollision);
        boolean alive = player.loseLife();
        if (!alive) {
            endGame();
        } else {
            // Reiniciar pelota sobre la paleta
            ball.stickToPaddle(paddle.getX(), paddle.getWidth(), paddle.getY());
        }
    }

    /**
     * Mueve los power-up drops activos y comprueba si la paleta los atrapa.
     * Los drops que salen de la pantalla se eliminan.
     */
    private void movePowerUpDrops() {
        List<PowerUpDrop> toRemove = new ArrayList<>();
        for (PowerUpDrop drop : drops) {
            drop.update();
            if (drop.getY() > PLAY_BOTTOM) {
                toRemove.add(drop);
                continue;
            }
            if (drop.getHitBox().intersects(paddle.getHitBox())) {
                drop.activate(paddle, ball);
                if (drop.getType() == PowerUpDrop.Type.SLOW_BALL) {
                    slowBallTimer = SlowBallDrop.getSlowFrames();
                } else if (drop.getType() == PowerUpDrop.Type.EXTRA_LIFE) {
                    player.addLife();
                }
                playSound(clipPowerUp);
                toRemove.add(drop);
            }
        }
        drops.removeAll(toRemove);
    }

    /**
     * Genera nuevos aliens según el intervalo del nivel, los mueve
     * y comprueba si alguno llega a la paleta o es golpeado por la pelota.
     */
    private void spawnAndMoveAliens() {
        int level = levelManager.getCurrentLevel();
        int interval = LevelManager.ALIEN_INTERVAL[level - 1];
        int maxAliens = LevelManager.ALIEN_MAX[level - 1];

        alienSpawnCounter++;
        if (alienSpawnCounter >= interval && aliens.size() < maxAliens) {
            aliens.add(levelManager.spawnAlien(WIDTH));
            alienSpawnCounter = 0;
        }

        List<Alien> toRemove = new ArrayList<>();
        Rectangle ballBox = ball.getHitBox();

        for (Alien alien : aliens) {
            alien.move(0, WIDTH);

            // Pelota destruye alien
            if (ball.isLaunched() && ballBox.intersects(alien.getHitBox())) {
                player.addScore(Alien.SCORE);
                playSound(clipPowerUp);
                toRemove.add(alien);
                ball.bounceY();
                continue;
            }

            // Alien llega a la paleta → pierde una vida
            if (alien.getY() + alien.getHeight() >= LevelManager.PADDLE_Y) {
                player.loseLife();
                playSound(clipCollision);
                toRemove.add(alien);
                if (player.getLives() <= 0) {
                    endGame();
                    return;
                }
            }
        }
        aliens.removeAll(toRemove);
    }

    /**
     * Verifica si quedan ladrillos destructibles.
     * Si no quedan → victoria de nivel.
     */
    private void checkWinCondition() {
        if (LevelManager.countDestructible(bricks) == 0) {
            int played = (int) ((System.currentTimeMillis() - gameStartTime) / 1000);
            scoreManager.addEntry(player.getName(), player.getScore(), played);
            playSound(clipWin);
            stopSound(clipGameplay);
            changeState(STATE_WIN_LEVEL);
        }
    }

    // -- Gestión de niveles --------------------------------------------

    /**
     * Inicia el nivel indicado: construye ladrillos, reinicia pelota/paleta/aliens.
     *
     * @param level número de nivel (1, 2 o 3)
     */
    private void startLevel(int level) {
        bricks = levelManager.buildLevel(level);
        aliens.clear();
        drops.clear();
        alienSpawnCounter = 0;
        slowBallTimer = 0;
        normalBallSpeed = LevelManager.BALL_SPEED[level - 1];

        paddle.reset(LevelManager.PADDLE_CENTER_X, LevelManager.PADDLE_Y);
        ball = new Ball(LevelManager.PADDLE_CENTER_X, LevelManager.PADDLE_Y - 12);
        ball.setSpeed(normalBallSpeed);
        inputHandler.setPaddle(paddle);

        timer.reset();
        timer.start();
        gameStartTime = System.currentTimeMillis();

        view.assignModels(bricks, paddle, ball, aliens, drops, player, timer,
                scoreManager, level);
        stopSound(clipIntro);
        playSound(clipGameplay);
        changeState(STATE_PLAYING);
    }

    /**
     * Registra el fin de la partida (sin vidas) y cambia a GAMEOVER.
     */
    private void endGame() {
        int played = (int) ((System.currentTimeMillis() - gameStartTime) / 1000);
        scoreManager.addEntry(player.getName(), player.getScore(), played);
        stopSound(clipGameplay);
        playSound(clipGameOver);
        changeState(STATE_GAMEOVER);
    }

    /**
     * Reinicia todo el estado del juego para una nueva partida desde el menú.
     */
    private void resetToWelcome() {
        player.resetAll();
        player.setName(view.getPlayerName());
        aliens.clear();
        drops.clear();
        bricks = levelManager.buildLevel(1);
        paddle.reset(LevelManager.PADDLE_CENTER_X, LevelManager.PADDLE_Y);
        ball = new Ball(LevelManager.PADDLE_CENTER_X, LevelManager.PADDLE_Y - 12);
        timer.reset();
        menuIndex = 0;
        menuKeyConsumed = false;
        view.setMenuIndex(0);
        view.assignModels(bricks, paddle, ball, aliens, drops, player, timer,
                scoreManager, 1);
        changeState(STATE_WELCOME);
    }

    /**
     * Ejecuta la opción del menú principal.
     *
     * @param option 0=Iniciar, 1=Instrucciones, 2=Salir
     */
    private void executeMenu(int option) {
        switch (option) {
            case 0:
                player.setName(view.getPlayerName());
                startLevel(1);
                break;
            case 1:
                changeState(STATE_INSTRUCTIONS);
                break;
            case 2:
                running = false;
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        System.exit(0);
                    }
                });
                break;
        }
    }

    /**
     * Cambia el estado del juego y notifica a la vista.
     *
     * @param newState nuevo estado (usar constantes STATE_*)
     */
    private void changeState(String newState) {
        state = newState;
        view.setGameState(newState);
    }

    // -- Audio ---------------------------------------------------------

    /**
     * Carga un archivo .wav desde /sounds/ en el classpath.
     *
     * @param filename nombre del archivo
     * @return Clip listo para reproducir, o null si no se encuentra
     */
    private Clip loadSound(String filename) {
        try {
            InputStream is = getClass().getResourceAsStream("/sounds/" + filename);
            if (is == null) return null;
            AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            return clip;
        } catch (Exception e) {
            System.err.println("[Audio] Error cargando: " + filename);
            return null;
        }
    }

    private void playSound(Clip clip) {
        if (clip == null) return;
        if (clip.isRunning()) clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }

    private void stopSound(Clip clip) {
        if (clip != null && clip.isRunning()) clip.stop();
    }

    private void closeAudio() {
        Clip[] clips = {clipIntro, clipGameplay, clipCollision, clipGameOver, clipPowerUp, clipWin};
        for (Clip c : clips) {
            if (c != null) {
                c.stop();
                c.close();
            }
        }
    }

    // -- Getters para la vista -------------------------------------------

    /**
     * Retorna el estado actual del juego.
     *
     * @return uno de los valores STATE_* definidos en esta clase
     */
    public String getState() {
        return state;
    }

    /**
     * Retorna el manejador de entrada del teclado/mouse.
     *
     * @return instancia de {@link main.java.controller.InputHandler}
     */
    public main.java.controller.InputHandler getInputHandler() {
        return inputHandler;
    }
}
