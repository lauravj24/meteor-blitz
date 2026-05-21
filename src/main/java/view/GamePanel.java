package main.java.view;

import main.java.controller.GameController;
import main.java.model.Alien;
import main.java.model.Ball;
import main.java.model.Brick;
import main.java.model.Entity;
import main.java.model.GameTimer;
import main.java.model.IndestructibleBrick;
import main.java.model.Paddle;
import main.java.model.Player;
import main.java.model.PowerBrick;
import main.java.model.PowerUpDrop;
import main.java.model.ScoreManager;
import main.java.model.StrongBrick;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel principal de Meteor Blitz. Extiende {@link JPanel} y es la única
 * clase responsable de dibujar todos los elementos en pantalla.
 * Nunca contiene lógica de juego — solo lectura del modelo y renderizado.
 *
 * <p>Estados que dibuja:</p>
 * <ul>
 *   <li>Bienvenida — banner, nombre del juego, menú, campo de nombre</li>
 *   <li>Instrucciones — reglas del juego</li>
 *   <li>Jugando — ladrillos, pelota, paleta, aliens, HUD, power-ups</li>
 *   <li>Victoria de nivel / Victoria total</li>
 *   <li>Game Over — nombre, puntaje, tiempo, Top 3</li>
 * </ul>
 *
 * @author Laura Jaramillo, Tomas Osorio, Tomas Varona
 * @version 1.0
 */
public class GamePanel extends JPanel {

    /**
     * Ancho total del panel.
     */
    public static final int WIDTH = GameController.WIDTH;
    /**
     * Alto total del panel.
     */
    public static final int HEIGHT = GameController.HEIGHT;

    // -- Fuentes -------------------------------------------------------
    private static final Font FONT_TITLE = new Font("Arial", Font.BOLD, 36);
    private static final Font FONT_LARGE = new Font("Arial", Font.BOLD, 22);
    private static final Font FONT_NORMAL = new Font("Arial", Font.PLAIN, 16);
    private static final Font FONT_SMALL = new Font("Arial", Font.PLAIN, 13);
    private static final Font FONT_HUD = new Font("Arial", Font.BOLD, 14);

    // -- Colores -------------------------------------------------------
    private static final Color C_BG = new Color(5, 5, 20);
    private static final Color C_HUD_BG = new Color(10, 10, 35);
    private static final Color C_ACCENT = new Color(80, 180, 255);
    private static final Color C_GOLD = new Color(255, 215, 0);
    private static final Color C_GREEN = new Color(50, 220, 80);
    private static final Color C_RED = new Color(220, 60, 60);
    private static final Color C_PURPLE = new Color(160, 80, 220);
    private static final Color C_ALIEN = new Color(220, 80, 80);

    // Colores de los ladrillos (fallback si no hay sprite)
    private static final Color[] BRICK_COLORS = {
            new Color(60, 120, 220),   // azul
            new Color(50, 180, 80),    // verde
            new Color(220, 200, 50),   // amarillo
            new Color(220, 120, 40),   // fuerte
            new Color(160, 60, 200),   // power
            new Color(80, 80, 90),     // indestructible
    };

    // -- Modelos (referencias asignadas por el controlador) ------------
    private Brick[][] bricks;
    private Paddle paddle;
    private Ball ball;
    private List<Alien> aliens;
    private List<PowerUpDrop> drops;
    private Player player;
    private GameTimer timer;
    private ScoreManager scoreManager;
    private int currentLevel;
    private String gameState;

    // -- UI del menú ---------------------------------------------------
    private JTextField nameField;
    private int menuIndex = 0;
    private int menuClickIndex = -1;
    private boolean nameEnterPressed;
    /**
     * Dirección de navegación enviada desde el nameField (+-1).
     */
    private volatile int menuNavPending = 0;
    private static final String PLACEHOLDER = "Escribe tu nombre...";

    // -- Menú de pausa (0 = Continuar, 1 = Volver al inicio) ---------
    private int pauseMenuIndex = 0;

    // -- Imágenes ------------------------------------------------------
    private final BufferedImage bannerImage;
    private final BufferedImage logoUAM;

    /**
     * Crea el panel, configura tamaño y el campo de texto para el nombre.
     */
    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(C_BG);
        setLayout(null);
        setFocusable(true);

        bannerImage = Entity.uploadImage("banner.png");
        logoUAM = Entity.uploadImage("logo_uam.png");

        // Campo de nombre del jugador
        nameField = new JTextField("");
        nameField.setBounds(WIDTH / 2 - 130, 313, 260, 34);
        nameField.setFont(new Font("Arial", Font.PLAIN, 16));
        nameField.setHorizontalAlignment(JTextField.CENTER);
        nameField.setBackground(new Color(20, 20, 50));
        nameField.setForeground(new Color(130, 130, 170));
        nameField.setCaretColor(Color.WHITE);
        nameField.setBorder(BorderFactory.createLineBorder(C_ACCENT, 2));
        nameField.setVisible(false);
        nameField.setText(PLACEHOLDER);
        add(nameField);

        // Placeholder: desaparece al enfocar, vuelve si queda vacío
        nameField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (PLACEHOLDER.equals(nameField.getText())) {
                    nameField.setText("");
                    nameField.setForeground(Color.WHITE);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (nameField.getText().trim().isEmpty()) {
                    nameField.setText(PLACEHOLDER);
                    nameField.setForeground(new Color(130, 130, 170));
                }
            }
        });

        nameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        nameEnterPressed = true;
                        break;
                    case KeyEvent.VK_UP:
                        menuNavPending = -1;
                        repaint();
                        break;
                    case KeyEvent.VK_DOWN:
                        menuNavPending = 1;
                        repaint();
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) nameEnterPressed = false;
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMenuClick(e.getX(), e.getY());
            }
        });

        gameState = GameController.STATE_WELCOME;
    }

    // -- API pública para el controlador ------------------------------

    /**
     * Asigna todos los modelos que la vista necesita para renderizar.
     *
     * @param bricks       grilla de ladrillos
     * @param paddle       paleta del jugador
     * @param ball         pelota activa
     * @param aliens       lista de aliens activos
     * @param drops        lista de power-ups cayendo
     * @param player       estado del jugador
     * @param timer        cronómetro del juego
     * @param scoreManager gestor de puntajes
     * @param level        nivel actual (1-3)
     */
    public void assignModels(Brick[][] bricks, Paddle paddle, Ball ball,
                             List<Alien> aliens, List<PowerUpDrop> drops,
                             Player player, GameTimer timer,
                             ScoreManager scoreManager, int level) {
        this.bricks = bricks;
        this.paddle = paddle;
        this.ball = ball;
        this.aliens = aliens;
        this.drops = drops;
        this.player = player;
        this.timer = timer;
        this.scoreManager = scoreManager;
        this.currentLevel = level;
    }

    /**
     * Cambia el estado visible del juego y ajusta la visibilidad del campo de nombre.
     *
     * @param state nuevo estado (constantes de {@link GameController})
     */
    public void setGameState(String state) {
        this.gameState = state;
        final boolean showName = GameController.STATE_WELCOME.equals(state);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                nameField.setVisible(showName);
                if (showName) nameField.requestFocus();
                else requestFocusInWindow();
            }
        });
        repaint();
    }

    /**
     * @param idx índice seleccionado en el menú principal
     */
    public void setMenuIndex(int idx) {
        this.menuIndex = idx;
    }

    /**
     * @return nombre ingresado en el campo de texto
     */
    public String getPlayerName() {
        String t = nameField.getText().trim();
        return (t.isEmpty() || PLACEHOLDER.equals(t)) ? "Jugador" : t;
    }

    /**
     * @return índice del ítem clickeado con el mouse (-1 si ninguno)
     */
    public int getMenuClickIndex() {
        return menuClickIndex;
    }

    /**
     * Limpia el índice de clic después de ser consumido.
     */
    public void clearMenuClick() {
        menuClickIndex = -1;
    }

    /**
     * @return true si Enter fue pulsado desde el campo de nombre
     */
    public boolean isNameEnterPressed() {
        return nameEnterPressed;
    }

    /**
     * Limpia el estado de Enter del campo de nombre.
     */
    public void clearNameEnterPressed() {
        nameEnterPressed = false;
    }

    /**
     * Retorna la dirección de navegación de menú enviada desde el campo de nombre
     * (-1 = arriba, +1 = abajo, 0 = ninguna) y la consume.
     *
     * @return dirección pendiente
     */
    public int getMenuNavDirection() {
        int d = menuNavPending;
        menuNavPending = 0;
        return d;
    }

    /**
     * Asigna el índice seleccionado en el menú de pausa.
     * 0 = Continuar, 1 = Volver al inicio.
     *
     * @param idx índice de la opción
     */
    public void setPauseMenuIndex(int idx) {
        this.pauseMenuIndex = idx;
    }

    /**
     * @return índice seleccionado actualmente en el menú de pausa
     */
    public int getPauseMenuIndex() {
        return pauseMenuIndex;
    }

    // -- Renderizado principal -----------------------------------------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (GameController.STATE_WELCOME.equals(gameState)) drawWelcome(g2);
        else if (GameController.STATE_INSTRUCTIONS.equals(gameState)) drawInstructions(g2);
        else if (GameController.STATE_PLAYING.equals(gameState)) drawGame(g2);
        else if (GameController.STATE_WIN_LEVEL.equals(gameState)) drawWinLevel(g2);
        else if (GameController.STATE_WIN_GAME.equals(gameState)) drawWinGame(g2);
        else if (GameController.STATE_GAMEOVER.equals(gameState)) drawGameOver(g2);
    }

    // -- Pantalla de bienvenida ----------------------------------------

    /**
     * Dibuja la pantalla de bienvenida: banner, campo de nombre y menú principal.
     *
     * @param g contexto gráfico
     */
    private void drawWelcome(Graphics2D g) {
        // Fondo
        g.setColor(C_BG);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // Banner
        int bannerH = 0;
        if (bannerImage != null) {
            g.drawImage(bannerImage, 0, 0, WIDTH, 220, null);
            bannerH = 220;
        } else {
            g.setFont(FONT_TITLE);
            g.setColor(C_GOLD);
            drawCentered(g, "METEOR BLITZ", WIDTH / 2, 130);
            bannerH = 160;
        }

        // Info del equipo
        g.setFont(FONT_SMALL);
        g.setColor(new Color(150, 150, 190));
        drawCentered(g, "Laura Jaramillo  |  Tomas Osorio  |  Tomas Varona", WIDTH / 2, bannerH + 24);
        drawCentered(g, "Programacion Orientada a Objetos — UAM 2026", WIDTH / 2, bannerH + 40);

        // Separador
        g.setColor(new Color(80, 180, 255, 80));
        g.drawLine(60, bannerH + 52, WIDTH - 60, bannerH + 52);

        // Etiqueta nombre
        g.setFont(FONT_NORMAL);
        g.setColor(Color.WHITE);
        drawCentered(g, "Nombre del jugador:", WIDTH / 2, 300);
        // nameField se dibuja como componente Swing (posición fija en y=313)

        // Opciones del menú
        String[] options = {"Iniciar juego", "Instrucciones", "Salir"};
        int[] optY = {373, 418, 463};
        int optW = 220, optH = 36;

        for (int i = 0; i < options.length; i++) {
            boolean selected = (i == menuIndex);
            int ox = WIDTH / 2 - optW / 2;

            if (selected) {
                g.setColor(new Color(255, 215, 0, 40));
                g.fillRoundRect(ox, optY[i] - 26, optW, optH, 10, 10);
                g.setColor(C_GOLD);
                g.setStroke(new BasicStroke(1.5f));
                g.drawRoundRect(ox, optY[i] - 26, optW, optH, 10, 10);
                g.setStroke(new BasicStroke(1));
                // Triángulo indicador
                int tx = ox + 14, ty = optY[i] - 8;
                int[] xs = {tx, tx + 8, tx};
                int[] ys = {ty - 6, ty, ty + 6};
                g.fillPolygon(xs, ys, 3);
            }

            g.setFont(FONT_LARGE);
            g.setColor(selected ? C_GOLD : new Color(180, 180, 220));
            drawCentered(g, options[i], WIDTH / 2, optY[i]);
        }

        // Pistas de controles
        g.setFont(FONT_SMALL);
        g.setColor(new Color(100, 100, 140));
        drawCentered(g, "Flechas arriba/abajo para seleccionar  |  Enter para confirmar", WIDTH / 2, 510);
        drawCentered(g, "Durante el juego: Flechas/AD = mover paleta  |  SPACE = lanzar  |  P = pausa", WIDTH / 2, 526);

        // Logo UAM
        if (logoUAM != null) {
            g.drawImage(logoUAM, 595, 545, 90, 60, null);
        }
    }

    // -- Pantalla de instrucciones -------------------------------------

    /**
     * Dibuja la pantalla de instrucciones del juego.
     *
     * @param g contexto gráfico
     */
    private void drawInstructions(Graphics2D g) {
        drawStarfield(g);

        g.setColor(new Color(10, 10, 40, 220));
        g.fillRoundRect(40, 40, WIDTH - 80, HEIGHT - 80, 20, 20);
        g.setColor(C_ACCENT);
        g.drawRoundRect(40, 40, WIDTH - 80, HEIGHT - 80, 20, 20);

        g.setFont(FONT_TITLE);
        g.setColor(C_GOLD);
        drawCentered(g, "INSTRUCCIONES - METEOR BLITZ", WIDTH / 2, 100);

        g.setColor(new Color(80, 180, 255, 80));
        g.drawLine(80, 115, WIDTH - 80, 115);

        int x = 90, y = 155;
        g.setFont(FONT_NORMAL);
        g.setColor(Color.WHITE);

        String[][] lines = {
                {"OBJETIVO:", "Destruye todos los ladrillos para completar el nivel."},
                {"CONTROLES:", "Flechas izq/der o A/D  ->  mover paleta"},
                {"", "Mouse (mover)           ->  mover paleta"},
                {"", "SPACE                   ->  lanzar pelota"},
                {"", "P                       ->  pausar / reanudar"},
                {"LADRILLOS:", "Normal  (azul/verde/amarillo)  ->  1 golpe  = 10 pts"},
                {"", "Reforzado (naranja)            ->  2 golpes = 25 pts"},
                {"", "Power (morado)                 ->  1 golpe  = 30 pts  + suelta capsula"},
                {"", "Indestructible (gris)          ->  rebota la pelota"},
                {"POWER-UPS:", "Expansion   ->  paleta mas ancha por 8 s"},
                {"", "Pelota lenta->  velocidad reducida por 10 s"},
                {"", "Vida extra  ->  +1 vida (max 5)"},
                {"ALIENS:", "Caen en diagonal. Destruyelos con la pelota (50 pts)."},
                {"", "Si alcanzan la paleta, pierdes una vida."},
                {"VIDAS:", "Empiezas con 3. Si llegas a 0, fin del juego."},
        };

        for (String[] pair : lines) {
            if (!pair[0].isEmpty()) {
                g.setColor(C_GOLD);
                g.setFont(new Font("Arial", Font.BOLD, 14));
                g.drawString(pair[0], x, y);
            }
            g.setColor(Color.WHITE);
            g.setFont(FONT_SMALL);
            g.drawString(pair[1], x + 110, y);
            y += 22;
        }

        g.setFont(FONT_NORMAL);
        g.setColor(C_ACCENT);
        drawCentered(g, "[ Enter para volver ]", WIDTH / 2, HEIGHT - 55);
    }

    // -- Pantalla de juego ---------------------------------------------

    /**
     * Dibuja el estado de juego activo: HUD, ladrillos, aliens, paleta, pelota y drops.
     *
     * @param g contexto gráfico
     */
    private void drawGame(Graphics2D g) {
        // Fondo
        g.setColor(C_BG);
        g.fillRect(0, GameController.WALL_TOP, WIDTH, HEIGHT - GameController.WALL_TOP);

        // Ladrillos
        if (bricks != null) {
            for (Brick[] row : bricks) {
                for (Brick b : row) {
                    if (b != null && b.isActive()) drawBrick(g, b);
                }
            }
        }

        // Power-up drops
        if (drops != null) {
            for (PowerUpDrop d : new ArrayList<>(drops)) drawDrop(g, d);
        }

        // Aliens — copia de la lista para evitar ConcurrentModificationException
        if (aliens != null) {
            for (Alien alien : new ArrayList<>(aliens)) drawAlien(g, alien);
        }

        // Paleta
        if (paddle != null) drawPaddle(g, paddle);

        // Pelota (parpadea si player == null por seguridad)
        if (ball != null) drawBall(g, ball);

        // HUD siempre encima
        drawHUD(g);

        // Menú de pausa (overlay)
        if (player != null && player.isPaused()) {
            drawPauseMenu(g);
        }

        // Pista de lanzamiento
        if (ball != null && !ball.isLaunched() && (player == null || !player.isPaused())) {
            g.setFont(FONT_NORMAL);
            g.setColor(C_GOLD);
            drawCentered(g, "Presiona SPACE para lanzar la pelota", WIDTH / 2, HEIGHT - 35);
        }
    }

    /**
     * Dibuja el menú de pausa como overlay sobre el juego.
     * Opciones: Continuar (0) y Volver al inicio (1).
     *
     * @param g contexto gráfico
     */
    private void drawPauseMenu(Graphics2D g) {
        // Fondo semitransparente
        g.setColor(new Color(0, 0, 0, 175));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // Panel central
        int panelW = 320, panelH = 200;
        int panelX = WIDTH / 2 - panelW / 2;
        int panelY = HEIGHT / 2 - panelH / 2;
        g.setColor(new Color(10, 10, 40, 230));
        g.fillRoundRect(panelX, panelY, panelW, panelH, 18, 18);
        g.setColor(C_ACCENT);
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(panelX, panelY, panelW, panelH, 18, 18);
        g.setStroke(new BasicStroke(1));

        // Título
        g.setFont(FONT_TITLE);
        g.setColor(C_GOLD);
        drawCentered(g, "PAUSA", WIDTH / 2, panelY + 52);

        // Separador
        g.setColor(new Color(80, 180, 255, 100));
        g.drawLine(panelX + 20, panelY + 65, panelX + panelW - 20, panelY + 65);

        // Opciones
        String[] options = {"Continuar", "Volver al inicio"};
        int optBaseY = panelY + 105;
        int optStep = 44;
        for (int i = 0; i < options.length; i++) {
            boolean selected = (i == pauseMenuIndex);
            int optY = optBaseY + i * optStep;

            if (selected) {
                g.setColor(new Color(255, 215, 0, 40));
                g.fillRoundRect(panelX + 18, optY - 22, panelW - 36, 32, 8, 8);
                g.setColor(C_GOLD);
                g.setStroke(new BasicStroke(1.5f));
                g.drawRoundRect(panelX + 18, optY - 22, panelW - 36, 32, 8, 8);
                g.setStroke(new BasicStroke(1));
                int tx = panelX + 30, ty = optY - 7;
                int[] xs = {tx, tx + 9, tx};
                int[] ys = {ty - 6, ty, ty + 6};
                g.fillPolygon(xs, ys, 3);
            }

            g.setFont(FONT_LARGE);
            g.setColor(selected ? C_GOLD : new Color(180, 180, 210));
            drawCentered(g, options[i], WIDTH / 2, optY);
        }

        // Pista de controles (dos lineas para que quepan en el panel)
        g.setFont(new Font("Arial", Font.PLAIN, 11));
        g.setColor(new Color(120, 120, 160));
        drawCentered(g, "Arriba/Abajo: seleccionar  |  Enter: confirmar",
                WIDTH / 2, panelY + panelH - 26);
        drawCentered(g, "P: continuar sin cambios",
                WIDTH / 2, panelY + panelH - 11);
    }

    /**
     * Dibuja el HUD (barra de estado superior) con nivel, puntaje, tiempo y vidas.
     *
     * @param g contexto gráfico
     */
    private void drawHUD(Graphics2D g) {
        g.setColor(C_HUD_BG);
        g.fillRect(0, 0, WIDTH, GameController.WALL_TOP);
        g.setColor(C_ACCENT);
        g.drawLine(0, GameController.WALL_TOP, WIDTH, GameController.WALL_TOP);

        g.setFont(FONT_HUD);
        int y = 33;

        // Nivel
        g.setColor(C_ACCENT);
        g.drawString("NIVEL: " + currentLevel, 10, y);

        // Puntaje
        g.setColor(Color.WHITE);
        g.drawString("PUNTAJE: " + (player != null ? player.getScore() : 0), 110, y);

        // Tiempo
        g.setColor(new Color(150, 200, 255));
        g.drawString("TIEMPO: " + (timer != null ? timer.getFormatted() : "00:00"), 280, y);

        // Vidas con corazones
        g.setColor(C_RED);
        int lives = player != null ? player.getLives() : 0;
        StringBuilder hearts = new StringBuilder("VIDAS: ");
        for (int i = 0; i < lives; i++) hearts.append("<3 ");
        for (int i = lives; i < Player.MAX_LIVES; i++) hearts.append("o ");
        g.drawString(hearts.toString(), 430, y);

        // Indicadores de power-up activos
        int px = 590;
        if (paddle != null && paddle.isExpanded()) {
            g.setColor(C_GREEN);
            g.drawString("[EXP]", px, y);
            px += 45;
        }
        // (indicador SLOW gestionado internamente en GameController)
    }

    // -- Dibujado de entidades -----------------------------------------

    /**
     * Dibuja un ladrillo: sprite si está disponible, rectángulo de color como fallback.
     *
     * @param g contexto gráfico
     * @param b ladrillo a dibujar
     */
    private void drawBrick(Graphics2D g, Brick b) {
        if (b.getSprite() != null) {
            g.drawImage(b.getSprite(), b.getX(), b.getY(), b.getWidth(), b.getHeight(), null);
        } else {
            // Fallback de color segun tipo de ladrillo
            Color c;
            if (b instanceof StrongBrick) c = BRICK_COLORS[3];
            else if (b instanceof PowerBrick) c = BRICK_COLORS[4];
            else if (b instanceof IndestructibleBrick) c = BRICK_COLORS[5];
            else c = BRICK_COLORS[0]; // NormalBrick azul
            g.setColor(c);
            g.fillRoundRect(b.getX(), b.getY(), b.getWidth(), b.getHeight(), 4, 4);
            g.setColor(c.brighter());
            g.drawRoundRect(b.getX(), b.getY(), b.getWidth(), b.getHeight(), 4, 4);
        }
    }

    /**
     * Dibuja la paleta: sprite si disponible, rectángulo de color como fallback.
     *
     * @param g contexto gráfico
     * @param p paleta a dibujar
     */
    private void drawPaddle(Graphics2D g, Paddle p) {
        if (p.getSprite() != null) {
            g.drawImage(p.getSprite(), p.getX(), p.getY(), p.getWidth(), p.getHeight(), null);
        } else {
            g.setColor(p.isExpanded() ? C_GREEN : C_ACCENT);
            g.fillRoundRect(p.getX(), p.getY(), p.getWidth(), p.getHeight(), 6, 6);
            g.setColor(Color.WHITE);
            g.drawRoundRect(p.getX(), p.getY(), p.getWidth(), p.getHeight(), 6, 6);
        }
    }

    /**
     * Dibuja la pelota: sprite si disponible, óvalo como fallback.
     *
     * @param g contexto gráfico
     * @param b pelota a dibujar
     */
    private void drawBall(Graphics2D g, Ball b) {
        if (b.getSprite() != null) {
            g.drawImage(b.getSprite(), b.getX(), b.getY(), b.getWidth(), b.getHeight(), null);
        } else {
            g.setColor(Color.WHITE);
            g.fillOval(b.getX(), b.getY(), b.getWidth(), b.getHeight());
            g.setColor(C_ACCENT);
            g.drawOval(b.getX(), b.getY(), b.getWidth(), b.getHeight());
        }
    }

    /**
     * Dibuja un alien: sprite si disponible, forma geométrica como fallback.
     *
     * @param g contexto gráfico
     * @param a alien a dibujar
     */
    private void drawAlien(Graphics2D g, Alien a) {
        if (a.getSprite() != null) {
            g.drawImage(a.getSprite(), a.getX(), a.getY(), a.getWidth(), a.getHeight(), null);
        } else {
            g.setColor(C_ALIEN);
            g.fillOval(a.getX(), a.getY(), a.getWidth(), a.getHeight());
            g.setColor(C_ALIEN.brighter());
            g.drawOval(a.getX(), a.getY(), a.getWidth(), a.getHeight());
        }
    }

    /**
     * Dibuja una capsula de power-up: sprite si disponible, círculo de color como fallback.
     *
     * @param g contexto gráfico
     * @param d drop a dibujar
     */
    private void drawDrop(Graphics2D g, PowerUpDrop d) {
        if (d.getSprite() != null) {
            g.drawImage(d.getSprite(), d.getX(), d.getY(), d.getWidth(), d.getHeight(), null);
        } else {
            Color c;
            switch (d.getType()) {
                case EXPAND:
                    c = C_GREEN;
                    break;
                case SLOW_BALL:
                    c = C_ACCENT;
                    break;
                default:
                    c = C_RED;
                    break;
            }
            g.setColor(c);
            g.fillOval(d.getX(), d.getY(), d.getWidth(), d.getHeight());
            g.setColor(c.brighter());
            g.drawOval(d.getX(), d.getY(), d.getWidth(), d.getHeight());
        }
    }

    // -- Pantalla victoria de nivel ------------------------------------

    /**
     * Dibuja la pantalla de victoria de nivel con puntaje actual.
     *
     * @param g contexto gráfico
     */
    private void drawWinLevel(Graphics2D g) {
        drawStarfield(g);
        g.setColor(new Color(5, 30, 5, 220));
        g.fillRoundRect(100, 150, WIDTH - 200, 300, 20, 20);
        g.setColor(C_GREEN);
        g.drawRoundRect(100, 150, WIDTH - 200, 300, 20, 20);

        g.setFont(FONT_TITLE);
        g.setColor(C_GREEN);
        drawCentered(g, "NIVEL " + currentLevel + " COMPLETADO!", WIDTH / 2, 220);

        g.setFont(FONT_LARGE);
        g.setColor(Color.WHITE);
        drawCentered(g, "Puntaje: " + (player != null ? player.getScore() : 0), WIDTH / 2, 270);

        if (currentLevel < 3) {
            g.setFont(FONT_NORMAL);
            g.setColor(new Color(180, 255, 180));
            drawCentered(g, "Siguiente: Nivel " + (currentLevel + 1) + " mas dificil!", WIDTH / 2, 320);
        }

        g.setFont(FONT_NORMAL);
        g.setColor(C_GOLD);
        drawCentered(g, "[ Enter para continuar ]", WIDTH / 2, 410);
    }

    // -- Pantalla victoria total ---------------------------------------

    /**
     * Dibuja la pantalla de victoria total al completar los 3 niveles.
     *
     * @param g contexto gráfico
     */
    private void drawWinGame(Graphics2D g) {
        drawStarfield(g);
        g.setColor(new Color(10, 5, 30, 220));
        g.fillRoundRect(60, 80, WIDTH - 120, HEIGHT - 160, 20, 20);
        g.setColor(C_GOLD);
        g.drawRoundRect(60, 80, WIDTH - 120, HEIGHT - 160, 20, 20);

        g.setFont(FONT_TITLE);
        g.setColor(C_GOLD);
        drawCentered(g, "VICTORIA TOTAL!", WIDTH / 2, 145);

        g.setFont(FONT_LARGE);
        g.setColor(Color.WHITE);
        drawCentered(g, "Jugador: " + (player != null ? player.getName() : ""), WIDTH / 2, 195);
        drawCentered(g, "Puntaje final: " + (player != null ? player.getScore() : 0), WIDTH / 2, 235);
        drawCentered(g, "Tiempo jugado: " + (timer != null ? timer.getFormatted() : "--:--"), WIDTH / 2, 270);

        drawTop3(g, 310);

        g.setFont(FONT_NORMAL);
        g.setColor(C_GOLD);
        drawCentered(g, "[ Enter para volver al inicio ]", WIDTH / 2, HEIGHT - 50);
    }

    // -- Pantalla de Game Over -----------------------------------------

    /**
     * Dibuja la pantalla de Game Over con el puntaje, tiempo y top 3.
     *
     * @param g contexto gráfico
     */
    private void drawGameOver(Graphics2D g) {
        drawStarfield(g);
        g.setColor(new Color(30, 5, 5, 220));
        g.fillRoundRect(60, 80, WIDTH - 120, HEIGHT - 160, 20, 20);
        g.setColor(C_RED);
        g.drawRoundRect(60, 80, WIDTH - 120, HEIGHT - 160, 20, 20);

        g.setFont(FONT_TITLE);
        g.setColor(C_RED);
        drawCentered(g, "GAME OVER", WIDTH / 2, 145);

        g.setFont(FONT_LARGE);
        g.setColor(Color.WHITE);
        drawCentered(g, "Jugador: " + (player != null ? player.getName() : ""), WIDTH / 2, 195);
        drawCentered(g, "Puntaje: " + (player != null ? player.getScore() : 0), WIDTH / 2, 235);
        drawCentered(g, "Tiempo: " + (timer != null ? timer.getFormatted() : "--:--"), WIDTH / 2, 270);

        drawTop3(g, 310);

        g.setFont(FONT_NORMAL);
        g.setColor(C_GOLD);
        drawCentered(g, "[ Enter para volver al inicio ]", WIDTH / 2, HEIGHT - 50);
    }

    // -- Helpers de dibujo ---------------------------------------------

    /**
     * Dibuja el top 3 del marcador centrado en la pantalla.
     *
     * @param g      contexto gráfico
     * @param startY posición Y donde comienza el bloque
     */
    private void drawTop3(Graphics2D g, int startY) {
        g.setFont(FONT_NORMAL);
        g.setColor(C_ACCENT);
        drawCentered(g, "--- TOP 3 ---", WIDTH / 2, startY);

        List<ScoreManager.ScoreEntry> top3 =
                scoreManager != null ? scoreManager.getTop3() : new ArrayList<ScoreManager.ScoreEntry>();

        if (top3.isEmpty()) {
            g.setColor(new Color(150, 150, 170));
            drawCentered(g, "Sin registros aun", WIDTH / 2, startY + 32);
        } else {
            for (int i = 0; i < top3.size(); i++) {
                ScoreManager.ScoreEntry e = top3.get(i);
                Color ec = i == 0 ? C_GOLD : (i == 1 ? new Color(200, 200, 210) : new Color(180, 140, 80));
                g.setColor(ec);
                String line = (i + 1) + ". " + e.name
                        + "   " + e.score + " pts"
                        + "   " + e.getFormattedTime();
                drawCentered(g, line, WIDTH / 2, startY + 32 + i * 30);
            }
        }
    }

    /**
     * Dibuja un fondo de estrellas estatico (semilla fija para que no parpadee).
     *
     * @param g contexto gráfico
     */
    private void drawStarfield(Graphics2D g) {
        g.setColor(C_BG);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        java.util.Random rng = new java.util.Random(42L);
        for (int i = 0; i < 90; i++) {
            int sx = rng.nextInt(WIDTH);
            int sy = rng.nextInt(HEIGHT);
            int sz = rng.nextInt(2) + 1;
            int alpha = 80 + rng.nextInt(140);
            g.setColor(new Color(255, 255, 255, alpha));
            g.fillOval(sx, sy, sz, sz);
        }
    }

    /**
     * Dibuja una cadena de texto centrada horizontalmente en la posicion dada.
     *
     * @param g    contexto gráfico
     * @param text texto a dibujar
     * @param cx   coordenada X del centro
     * @param cy   coordenada Y de la linea base
     */
    private void drawCentered(Graphics2D g, String text, int cx, int cy) {
        FontMetrics fm = g.getFontMetrics();
        int x = cx - fm.stringWidth(text) / 2;
        g.drawString(text, x, cy);
    }

    // -- Click del mouse en el menú ------------------------------------

    /**
     * Detecta si el clic del mouse coincide con una opción del menú de bienvenida.
     *
     * @param mx posición X del clic
     * @param my posición Y del clic
     */
    private void handleMenuClick(int mx, int my) {
        if (!GameController.STATE_WELCOME.equals(gameState)) return;
        int[] optY = {373, 418, 463};
        int optW = 220, optH = 36;
        int ox = WIDTH / 2 - optW / 2;
        for (int i = 0; i < optY.length; i++) {
            if (mx >= ox && mx <= ox + optW && my >= optY[i] - 26 && my <= optY[i] + optH - 26) {
                menuClickIndex = i;
                repaint();
                return;
            }
        }
    }
}
