package org.example;

import java.sql.*;
import java.util.ArrayList;

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
                "start_date TEXT NOT NULL," +
                "end_date TEXT NOT NULL," +
                "approved INTEGER NOT NULL," +
                "FOREIGN KEY (employee_id) REFERENCES employees(id));";

        try (PreparedStatement preparedStatement1 = connection.prepareStatement(createEmployeesTableSQL);
             PreparedStatement preparedStatement2 = connection.prepareStatement(createAbsencesTableSQL)) {
            preparedStatement1.execute();
            preparedStatement2.execute();
            System.out.println("Tabellen 'employees' und 'absences' erstellt.");
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

            System.out.println("Mitarbeiter '" + firstName + " " + lastName + "' hinzugefügt.");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public void deleteEmployee(int id){
        String deleteEmployeeSQL = "DELETE FROM employees WHERE id = ?;";

        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteEmployeeSQL)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();

            System.out.println("Mitarbeiter '" + id + "' geloescht.");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public void requestAbsence(String employeeName, AbsenceType type, String startDate, String endDate) {
        Employee employee = getEmployeeByName(employeeName);
        if (employee != null) {
            String insertAbsenceSQL = "INSERT INTO absences (employee_id, type, start_date, end_date, approved) VALUES (?, ?, ?, ?, 0);";

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertAbsenceSQL)) {
                preparedStatement.setInt(1, employee.id);
                preparedStatement.setString(2, type.toString());
                preparedStatement.setString(3, startDate);
                preparedStatement.setString(4, endDate);
                preparedStatement.executeUpdate();

                System.out.println("Abwesenheitsantrag für '" + employeeName + "' erstellt.");
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public void aproveAbsence(String employeeName, int absenceIndex){
        Employee employee = getEmployeeByName(employeeName);
        if (employee != null && absenceIndex >= 0 && absenceIndex < employee.absences.size()) {
            int absenceId = employee.absences.get(absenceIndex).id;
            String aproveAbsenceSQL = "UPDATE absences SET approved = 1 WHERE id = ?;";

            try (PreparedStatement preparedStatement = connection.prepareStatement(aproveAbsenceSQL)) {
                preparedStatement.setInt(1, absenceId);
                preparedStatement.executeUpdate();

                System.out.println("Abwesenheit genehmigt für '" + employeeName + "'.");
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public void deleteAbsence(String employeeName, int absenceIndex) {
        Employee employee = getEmployeeByName(employeeName);
        if (employee != null && absenceIndex >= 0 && absenceIndex < employee.absences.size()) {
            int absenceId = employee.absences.get(absenceIndex).id;
            String deleteAbsenceSQL = "DELETE FROM absences WHERE id = ?;";

            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteAbsenceSQL)) {
                preparedStatement.setInt(1, absenceId);
                preparedStatement.executeUpdate();

                System.out.println("Abwesenheit gelöscht für '" + employeeName + "'.");
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private ArrayList<Absence> getAllAbsencesByEmployeeId(int id){
        ArrayList<Absence> absences = new ArrayList<>();
        String getAbsencesByIdSQL = "SELECT * FROM absences WHERE employee_id= ? ;";
        try(PreparedStatement preparedStatement = connection.prepareStatement(getAbsencesByIdSQL)){
            preparedStatement.setInt(1,id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                Absence absence = new Absence();
                absence.id = resultSet.getInt(1);
                absence.employeeId = resultSet.getInt(2);
                absence.type = AbsenceType.getAbscenceTypeByString(resultSet.getString(3));
                absence.startDate = resultSet.getString(4);
                absence.endDate = resultSet.getString(5);
                absence.approved = resultSet.getBoolean(6);
                absences.add(absence);
            }
            resultSet.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return absences;
    }


    private Employee getEmployeeByName(String employeeName) {
        //alter Code Funktioniert nicht, weil team nicht benutzt wird.
        for (Employee employee : team.employees.values()) {
            if ((employee.firstName + " " + employee.lastName).equals(employeeName)) {
                return employee;
            }
        }

        //neuer versuch
        Employee employee = new Employee();
        String[] employeeNames =  employeeName.split(" ");
        String first_name = employeeNames[0];
        String last_name =  employeeNames[1];
        //String getEmployeeIdSQL = "SELECT id FROM employees WHERE first_name="+first_name+" AND last_name="+last_name+";";
        String getEmployeeIdSQL = "SELECT * FROM employees WHERE first_name= ? AND last_name= ? ;";

        try (PreparedStatement preparedStatement = connection.prepareStatement(getEmployeeIdSQL)) {
            preparedStatement.setString(1, first_name);
            preparedStatement.setString(2, last_name);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                employee.id = resultSet.getInt("id");
                employee.firstName = resultSet.getString("first_name");
                employee.lastName = resultSet.getString("last_name");
                employee.favoriteColor = resultSet.getString("favorite_color");
                employee.absences = getAllAbsencesByEmployeeId(employee.id);
                System.out.println("Employee "+ employeeName + " gefunden!");
                resultSet.close();
                return employee;
            }
            resultSet.close();

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public static void main(String[] args) {
        //Test
        //Testgit
        AbsencePlanner planner = new AbsencePlanner();

        Connection con = SQLiteConnection.connect();

        //neuen Mitarbeiter hinzufügen
        planner.addEmployee("Max", "Mustermann", "#87CEFA");


        // Abwesenheitsantrag stellen
        planner.requestAbsence("Max Mustermann", AbsenceType.VACATION, "2023-01-01", "2023-01-07");

        // Abwesenheit genehmigen
        planner.aproveAbsence("Max Mustermann", 0);



        //alle Mitarbeiter ausgeben
        String query = "SELECT * FROM employees;";
        try (Statement statement = con.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            // Mitarbeiterdaten aus der Abfrage auslesen und ausgeben
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String favoriteColor = resultSet.getString("favorite_color");

                System.out.println("ID: " + id + ", Name: " + firstName + " " + lastName + ", Lieblingsfarbe: " + favoriteColor);
                System.out.println("Abwesenheiten für diesen Mitarbeiter:");
                ArrayList<Absence> absences = planner.getAllAbsencesByEmployeeId(id);
                for(Absence a:absences){
                    System.out.println("    " + a.toString());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Verbindung schließen
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        //Abwesenheit entfernen
        planner.deleteAbsence("Max Mustermann",0);

        //Mitarbeiter entfernen
        planner.deleteEmployee(planner.getEmployeeByName("Max Mustermann").id);

        // Datenbankverbindung schließen
        SQLiteConnection.disconnect(con);
    }


}
