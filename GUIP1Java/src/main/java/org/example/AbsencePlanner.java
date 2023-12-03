package org.example;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class AbsencePlanner {
    private Team team;
    private Connection connection;

    public AbsencePlanner() {
        team = new Team();
        connection = SQLiteConnection.connect();
        initializeDatabase();
    }

    private void initializeDatabase() {
        String createEmployeesTableSQL = "CREATE TABLE IF NOT EXISTS employees (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "first_name TEXT NOT NULL," +
                "last_name TEXT NOT NULL," +
                "favorite_color TEXT NOT NULL);";

        String createAbsencesTableSQL = "CREATE TABLE IF NOT EXISTS absences (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "employee_id INTEGER," +
                "type TEXT NOT NULL," +
                "start_date DATE NOT NULL," +
                "end_date DATE NOT NULL," +
                "approved INTEGER NOT NULL," +
                "FOREIGN KEY (employee_id) REFERENCES employees(id));";

        try (Statement statement = connection.createStatement()) {
            statement.execute(createEmployeesTableSQL);
            statement.execute(createAbsencesTableSQL);
            System.out.println("Tables 'employees' and 'absences' created.");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public void addEmployee(String firstName, String lastName, String favoriteColor) {
        String insertEmployeeSQL = "INSERT INTO employees (first_name, last_name, favorite_color) VALUES (?, ?, ?);";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertEmployeeSQL)) {
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, favoriteColor);
            preparedStatement.executeUpdate();

            System.out.println("Employee '" + firstName + " " + lastName + "' added.");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    // Add 3 absences for each employee
    public void addAbsencesForAllEmployees() {
        AbsenceType[] absenceTypes = AbsenceType.values();
        Random random = new Random();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String query = "SELECT id FROM employees;";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int employeeId = resultSet.getInt("id");

                // Add 5 random absences for the employee
                for (int i = 0; i < 5; i++) {
                    AbsenceType randomType = absenceTypes[random.nextInt(absenceTypes.length)];
                    LocalDate randomStartDate = getRandomStartDate();
                    // Random duration from 1 to 4 days
                    LocalDate randomEndDate = randomStartDate.plusDays(random.nextInt(4) + 1);
                    requestAbsence(employeeId, randomType, randomStartDate.format(formatter), randomEndDate.format(formatter));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding absences for all employees: " + e.getMessage());
        }
    }

    private LocalDate getRandomStartDate() {
        Random random = new Random();
        // Randomly choose a month (11 for December, 0 for January)
        int month = random.nextBoolean() ? 11 : 0;
        // Random day in December 2023 or January 2024
        int day = month == 11 ? random.nextInt(31) + 1 : random.nextInt(31) + 1;
        int year = month == 11 ? 2023 : 2024;
        return LocalDate.of(year, month + 1, day);
    }

    public void requestAbsence(int employeeId, AbsenceType type, String startDate, String endDate) {
        String insertAbsenceSQL = "INSERT INTO absences (employee_id, type, start_date, end_date, approved) VALUES (?, ?, ?, ?, 0);";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertAbsenceSQL)) {
            preparedStatement.setInt(1, employeeId);
            preparedStatement.setString(2, type.name());
            preparedStatement.setString(3, startDate);
            preparedStatement.setString(4, endDate);
            preparedStatement.executeUpdate();

            System.out.println("Absence request for employee ID '" + employeeId + "' created.");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        AbsencePlanner planner = new AbsencePlanner();

        planner.addAbsencesForAllEmployees();

        SQLiteConnection.disconnect(planner.connection);
    }
}
