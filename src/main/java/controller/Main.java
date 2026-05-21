package main.java.controller;

import main.java.view.GamePanel;
import main.java.view.MainFrame;

import javax.swing.*;

/**
 * Punto de entrada del juego Meteor Blitz.
 *
 * <p>Toda la creación de la interfaz gráfica se realiza dentro del
 * Event Dispatch Thread (EDT) usando {@link SwingUtilities#invokeLater},
 * que es la forma correcta y segura de iniciar aplicaciones Swing.</p>
 *
 * <p>Flujo de arranque:</p>
 * <ol>
 *   <li>Se crea el GamePanel (vista)</li>
 *   <li>Se crea el MainFrame (ventana) que aloja el panel</li>
 *   <li>Se crea el GameController que inicializa los modelos e inyecta
 *       los datos en la vista</li>
 *   <li>Se inicia el bucle de juego en su propio hilo</li>
 *   <li>Se registra un ShutdownHook para detener limpiamente el hilo al cerrar</li>
 * </ol>
 *
 * @author Laura Jaramillo, Tomas Osorio, Tomas Varona
 * @version 1.0
 */
public class Main {

    /**
     * Método principal. Lanza el juego en el Event Dispatch Thread de Swing.
     *
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // 1. Crear la vista
                GamePanel panel = new GamePanel();

                // 2. Crear la ventana con la vista dentro
                new MainFrame(panel);

                // 3. Crear el controlador (inyecta modelos en la vista)
                final main.java.controller.GameController controller = new main.java.controller.GameController(panel);

                // 4. Iniciar el bucle de juego
                controller.start();

                // 5. Al cerrar la ventana, detener el hilo limpiamente
                Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                    public void run() {
                        controller.stop();
                    }
                }));

                // Dar foco al panel para capturar las teclas
                panel.requestFocusInWindow();
            }
        });
    }
}
