package org.example.datenbank;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.example.impl.AbsencePlanner.connection;

public class DB {
    public static void initializeDatabase() {
        String createEmployeesTableSQL = """
                CREATE TABLE IF NOT EXISTS employees (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                first_name TEXT NOT NULL,
                last_name TEXT NOT NULL,
                favorite_color TEXT NOT NULL);
                """;

        String createAbsencesTableSQL = """
                CREATE TABLE IF NOT EXISTS absences (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                employee_id INTEGER,
                type TEXT NOT NULL,
                start_date TEXT NOT NULL,
                end_date TEXT NOT NULL,
                approved INTEGER NOT NULL,
                FOREIGN KEY (employee_id) REFERENCES employees(id));
                """;

        String createTeamsTableSQL = """
                CREATE TABLE IF NOT EXISTS teams (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name STRING);
                """;

        String createTeamEmployeeTableSQL = """
                CREATE TABLE IF NOT EXISTS teamEmployee (
                employee_id INTEGER,
                team_id INTEGER);
                """;


        try (PreparedStatement preparedStatement1 = connection.prepareStatement(createEmployeesTableSQL);
             PreparedStatement preparedStatement2 = connection.prepareStatement(createAbsencesTableSQL);
             PreparedStatement preparedStatement3 = connection.prepareStatement(createTeamsTableSQL);
             PreparedStatement preparedStatement4 = connection.prepareStatement(createTeamEmployeeTableSQL)) {
            preparedStatement1.execute();
            preparedStatement2.execute();
            preparedStatement3.execute();
            preparedStatement4.execute();
            System.out.println("Tabellen 'employees', 'absences', 'teams' und'teamEmployee erstellt.");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

}
