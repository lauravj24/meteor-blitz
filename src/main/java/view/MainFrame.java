package main.java.view;

import javax.swing.*;

/**
 * Ventana principal del juego Meteor Blitz. Hereda de {@link JFrame} y aloja
 * el {@link main.java.view.GamePanel}. Su única responsabilidad es configurar y mostrar
 * la ventana del sistema operativo.
 *
 * @author Laura Jaramillo, Tomas Osorio, Tomas Varona
 * @version 1.0
 */
public class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    /**
     * Construye la ventana principal, le agrega el GamePanel y la hace visible.
     *
     * @param panel el panel de juego que se mostrará en el interior de la ventana
     */
    public MainFrame(main.java.view.GamePanel panel) {
        setTitle("METEOR BLITZ -- POO UAM");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        add(panel);
        pack();
        setLocationRelativeTo(null); // Centrar en la pantalla
        setVisible(true);
    }
}
