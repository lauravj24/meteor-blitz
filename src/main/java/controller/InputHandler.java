package main.java.controller;

import main.java.model.Paddle;
import main.java.model.Player;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

/**
 * Manejador de entrada del jugador para Meteor Blitz.
 * Escucha el teclado (flechas, SPACE, P, Enter) y el mouse (movimiento)
 * para controlar la paleta y navegar por los menús.
 *
 * @author Laura Jaramillo, Tomas Osorio, Tomas Varona
 * @version 2.0
 */
public class InputHandler extends KeyAdapter implements MouseMotionListener {

    /**
     * Referencia al estado del jugador (pausa, Enter, Space).
     */
    private final Player player;
    /**
     * Referencia a la paleta para control con mouse.
     */
    private Paddle paddle;

    /**
     * true mientras la tecla izquierda está presionada.
     */
    private boolean leftPressed;
    /**
     * true mientras la tecla derecha está presionada.
     */
    private boolean rightPressed;
    /**
     * true mientras la tecla arriba está presionada (navegación de menú).
     */
    private boolean upPressed;
    /**
     * true mientras la tecla abajo está presionada (navegación de menú).
     */
    private boolean downPressed;

    /**
     * Crea el manejador de entrada vinculado al jugador y la paleta.
     *
     * @param player estado del jugador
     * @param paddle paleta del jugador
     */
    public InputHandler(Player player, Paddle paddle) {
        this.player = player;
        this.paddle = paddle;
    }

    /**
     * Actualiza la referencia a la paleta (necesario al cambiar de nivel).
     *
     * @param paddle nueva paleta
     */
    public void setPaddle(Paddle paddle) {
        this.paddle = paddle;
    }

    // Teclado

    /**
     * Registra la tecla presionada.
     * Flechas/A/D → mueven la paleta; SPACE → lanza la pelota;
     * P → pausa; Enter → confirma en menús.
     *
     * @param e evento de teclado
     */
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                leftPressed = true;
                break;
            case KeyEvent.VK_RIGHT:
                rightPressed = true;
                break;
            case KeyEvent.VK_UP:
                upPressed = true;
                break;
            case KeyEvent.VK_DOWN:
                downPressed = true;
                break;
            case KeyEvent.VK_A:
                leftPressed = true;
                break;
            case KeyEvent.VK_D:
                rightPressed = true;
                break;
            case KeyEvent.VK_SPACE:
                player.setSpacePressed(true);
                break;
            case KeyEvent.VK_P:
                player.togglePause();
                break;
            case KeyEvent.VK_ENTER:
                player.setEnterPressed(true);
                break;
        }
    }

    /**
     * Registra cuando se suelta una tecla.
     *
     * @param e evento de teclado
     */
    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                leftPressed = false;
                break;
            case KeyEvent.VK_RIGHT:
                rightPressed = false;
                break;
            case KeyEvent.VK_UP:
                upPressed = false;
                break;
            case KeyEvent.VK_DOWN:
                downPressed = false;
                break;
            case KeyEvent.VK_A:
                leftPressed = false;
                break;
            case KeyEvent.VK_D:
                rightPressed = false;
                break;
            case KeyEvent.VK_SPACE:
                player.setSpacePressed(false);
                break;
            case KeyEvent.VK_ENTER:
                player.setEnterPressed(false);
                break;
        }
    }

    // Mouse

    /**
     * Mueve la paleta siguiendo el cursor del mouse.
     *
     * @param e evento de movimiento del mouse
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        if (paddle != null) paddle.moveTo(e.getX());
    }

    /**
     * Mueve la paleta cuando el mouse se arrastra con botón presionado.
     *
     * @param e evento de arrastre del mouse
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        if (paddle != null) paddle.moveTo(e.getX());
    }

    // Getters

    /**
     * @return true si la flecha izquierda (o A) está presionada
     */
    public boolean isLeftPressed() {
        return leftPressed;
    }

    /**
     * @return true si la flecha derecha (o D) está presionada
     */
    public boolean isRightPressed() {
        return rightPressed;
    }

    /**
     * @return true si la flecha arriba está presionada (menú)
     */
    public boolean isUpPressed() {
        return upPressed;
    }

    /**
     * @return true si la flecha abajo está presionada (menú)
     */
    public boolean isDownPressed() {
        return downPressed;
    }
}
