package main.java.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Gestiona el historial de jugadores y el ranking Top 3.
 * Guarda y carga los datos desde un archivo de texto en el sistema de archivos
 * para que persistan entre sesiones de juego.
 *
 * <p>Formato del archivo (scores.txt): una línea por entrada:</p>
 * <pre>NombreJugador,puntaje,tiempoSegundos</pre>
 *
 * @author Laura Jaramillo, Tomas Osorio, Tomas Varona
 * @version 1.0
 */
public class ScoreManager {

    /**
     * Ruta del archivo donde se guardan los puntajes.
     */
    private static final String FILE_PATH = "scores.txt";
    /**
     * Máximo de entradas a guardar en el historial.
     */
    private static final int MAX_ENTRIES = 20;

    /**
     * Representa una entrada del historial de puntajes.
     */
    public static class ScoreEntry {
        /**
         * Nombre del jugador.
         */
        public final String name;
        /**
         * Puntaje obtenido.
         */
        public final int score;
        /**
         * Tiempo jugado en segundos.
         */
        public final int timeSeconds;

        /**
         * Crea una entrada del historial.
         *
         * @param name        nombre del jugador
         * @param score       puntaje obtenido
         * @param timeSeconds tiempo jugado en segundos
         */
        public ScoreEntry(String name, int score, int timeSeconds) {
            this.name = name;
            this.score = score;
            this.timeSeconds = timeSeconds;
        }

        /**
         * Retorna el tiempo en formato "MM:SS".
         *
         * @return cadena formateada
         */
        public String getFormattedTime() {
            return String.format("%02d:%02d", timeSeconds / 60, timeSeconds % 60);
        }
    }

    /**
     * Lista de todas las entradas del historial.
     */
    private final List<ScoreEntry> entries;

    /**
     * Crea el ScoreManager y carga el historial existente desde el archivo.
     */
    public ScoreManager() {
        entries = new ArrayList<>();
        loadFromFile();
    }

    /**
     * Agrega una nueva entrada al historial y la guarda en el archivo.
     *
     * @param name        nombre del jugador
     * @param score       puntaje obtenido
     * @param timeSeconds tiempo jugado en segundos
     */
    public void addEntry(String name, int score, int timeSeconds) {
        entries.add(new ScoreEntry(name, score, timeSeconds));
        // Ordenar por puntaje descendente y mantener solo las mejores MAX_ENTRIES
        entries.sort(Comparator.comparingInt((ScoreEntry e) -> e.score).reversed());
        if (entries.size() > MAX_ENTRIES) {
            entries.subList(MAX_ENTRIES, entries.size()).clear();
        }
        saveToFile();
    }

    /**
     * Retorna las 3 mejores entradas del historial (Top 3).
     * Si hay menos de 3 entradas, retorna las que haya.
     *
     * @return lista de máximo 3 entradas ordenadas por puntaje descendente
     */
    public List<ScoreEntry> getTop3() {
        int limit = Math.min(3, entries.size());
        return Collections.unmodifiableList(entries.subList(0, limit));
    }

    /**
     * Retorna todas las entradas del historial.
     *
     * @return lista completa de entradas
     */
    public List<ScoreEntry> getAllEntries() {
        return Collections.unmodifiableList(entries);
    }

    /**
     * Guarda el historial en el archivo scores.txt.
     * Cada línea tiene el formato: nombre,puntaje,tiempoSegundos
     */
    private void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (ScoreEntry e : entries) {
                // Sanitizar el nombre para que no tenga comas
                String safeName = e.name.replace(",", ";");
                writer.write(safeName + "," + e.score + "," + e.timeSeconds);
                writer.newLine();
            }
        } catch (IOException ex) {
            System.err.println("[ScoreManager] Error guardando puntajes: " + ex.getMessage());
        }
    }

    /**
     * Carga el historial desde el archivo scores.txt.
     * Si el archivo no existe, el historial queda vacío (primera ejecución).
     */
    private void loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    try {
                        String name = parts[0].trim();
                        int score = Integer.parseInt(parts[1].trim());
                        int time = Integer.parseInt(parts[2].trim());
                        entries.add(new ScoreEntry(name, score, time));
                    } catch (NumberFormatException ignored) {
                        // Línea corrupta, se ignora
                    }
                }
            }
            // Ordenar después de cargar
            entries.sort(Comparator.comparingInt((ScoreEntry e) -> e.score).reversed());
        } catch (IOException ex) {
            System.err.println("[ScoreManager] Error cargando puntajes: " + ex.getMessage());
        }
    }
}
