package main.java.model;

/**
 * Interfaz que implementan todos los power-ups de Meteor Blitz.
 * Permite polimorfismo en el controlador: cualquier power-up
 * se activa llamando a {@code activate(paddle, ball)} sin conocer su tipo concreto.
 *
 * @author Laura Jaramillo, Tomas Osorio, Tomas Varona
 * @version 2.0
 */
public interface Activatable {

    /**
     * Aplica el efecto del power-up sobre la paleta y/o la pelota.
     * Cada subclase implementa su propio comportamiento.
     *
     * @param paddle la paleta del jugador
     * @param ball   la pelota activa
     */
    void activate(Paddle paddle, Ball ball);
}
