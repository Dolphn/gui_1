package org.example.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.datenbank.SQLiteConnection;
import static org.example.impl.AbsencePlanner.connection;

/**
 * KalenderApp zum Planen von Abwesenheiten
 *
 * Gruppe: 13
 * Mitglieder:
 * - Luca Spera 11107412
 * - Cynthia Pola 11149967
 * - Matthis Florentin Gehlen 11161302
 * - Cornelius Johannes Engel
 *
 * Programm Funktion:
 * Diese KalenderApp ermöglicht das effiziente Planen von Abwesenheiten.
 *
 * Verwendete Technologien:
 * - Compiler Version: Java SDK 21
 * - Datenbank: SQL Lite
 * - Benutzeroberfläche: JavaFX
 *
 * Version: 1.0
 * Datum: 07.12.2023
 *
 * @author Luca Spera
 * @author Cynthia Pola
 * @author Matthis Florentin Gehlen
 * @author Cornelius Johannes Engel
 */

public class KalenderApp extends Application {
    public static void main(String[] args) {
        connection = SQLiteConnection.connect();
        //testDbErschaffen(); //Alte DB löschen, wenn die ids zu hoch werden/um die ids zu reseten
        //initializeDatabase();
        launch(args);
        // Datenbankverbindung schließen
        SQLiteConnection.disconnect(connection);
    }

    @Override
    public void start(Stage stage) throws Exception {
        //GUI erzeugen
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/main_window.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Abwesenheitsplaner");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
