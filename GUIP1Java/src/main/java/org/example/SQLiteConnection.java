package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class SQLiteConnection {
    public static Connection connect() {
        String url = "jdbc:sqlite:absence_planner.db";
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite database established.");
        } catch (SQLException e) {
            System.err.println("Connection error: " + e.getMessage());
        }

        return connection;
    }

    public static void disconnect(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Connection to SQLite database closed.");
            }
        } catch (SQLException e) {
            System.err.println("Disconnection error: " + e.getMessage());
        }
    }
}
