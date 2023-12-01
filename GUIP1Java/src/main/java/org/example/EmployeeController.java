package org.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeController {
    private final Connection connection;

    public EmployeeController() {
        this.connection = SQLiteConnection.connect();
    }

    public ObservableList<String> getEmployeeNames() {
        ObservableList<String> employeeNames = FXCollections.observableArrayList();
        String query = "SELECT first_name, last_name FROM employees;";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                employeeNames.add(firstName + " " + lastName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employeeNames;
    }

    //getUserColor
    public String getUserColor(String name) {
        String[] names = name.split(" ", 2); // Split into first name and the rest
        String firstName = names[0];
        String lastName = names.length > 1 ? names[1] : "";

        String userColor = ""; // Default to 0 (not found)
        String query = "SELECT favorite_color FROM employees WHERE first_name = ? AND last_name = ?;";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    userColor = resultSet.getString("favorite_color");
                }
            }
        } catch (SQLException e) {
            System.err.println("An error occurred while fetching the user ID: " + e.getMessage());
        }

        return userColor;
    }

    public int getUserId(String name) {
        String[] names = name.split(" ", 2); // Split into first name and the rest
        String firstName = names[0];
        String lastName = names.length > 1 ? names[1] : "";

        int userId = 0; // Default to 0 (not found)
        String query = "SELECT id FROM employees WHERE first_name = ? AND last_name = ?;";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    userId = resultSet.getInt("id");
                }
            }
        } catch (SQLException e) {
            System.err.println("An error occurred while fetching the user ID: " + e.getMessage());
        }

        return userId;
    }


    public List<Absence> getEmployeeAbsences(int employeeId) {
        List<Absence> absences = new ArrayList<>();
        String query = "SELECT * FROM absences WHERE employee_id = ?;";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, employeeId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Absence absence = new Absence(
                            resultSet.getInt("id"),
                            resultSet.getInt("employee_id"),
                            AbsenceType.valueOf(resultSet.getString("type")),
                            resultSet.getString("start_date"),
                            resultSet.getString("end_date"),
                            resultSet.getInt("approved") != 0
                    );

                    absences.add(absence);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return absences;
    }

}
