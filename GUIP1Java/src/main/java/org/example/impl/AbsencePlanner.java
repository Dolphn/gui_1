package org.example.impl;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.datenbank.SQLiteConnection;
import org.example.entities.Absence;
import org.example.entities.AbsenceType;
import org.example.entities.Employee;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AbsencePlanner {
    public static ArrayList<Employee> employees;
    public static Connection connection;
    private static final boolean debug = true;

    //Employess
    public static int addEmployee(String firstName, String lastName, String favoriteColor) {
        int id;
        String insertEmployeeSQL = "INSERT INTO employees (first_name, last_name, favorite_color) VALUES (?, ?, ?);";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertEmployeeSQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, favoriteColor);
            preparedStatement.executeUpdate();

            System.out.println("Mitarbeiter '" + firstName + " " + lastName + "' hinzugefügt.");
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    System.out.println(generatedKeys.getInt(1));
                    return generatedKeys.getInt(1);
                }
                else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return 0;
        }
    }

    public static void fetchAllEmployees() {
        ArrayList<Employee> employeesL = new ArrayList<>();

        String getEmployeeIdSQL = "SELECT * FROM employees;";

        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(getEmployeeIdSQL);
            while (resultSet.next()) {
                Employee employee = new Employee();
                employee.id = resultSet.getInt("id");
                employee.firstName = resultSet.getString("first_name");
                employee.lastName = resultSet.getString("last_name");
                employee.favoriteColor = resultSet.getString("favorite_color");
                employee.absences = getAllAbsencesByEmployeeId(employee.id);
                employeesL.add(employee);
            }
            resultSet.close();

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        employees = employeesL;
    }

    public static ArrayList<Employee> getAllEmployees() {
        return employees;
    }

    public static boolean updateEmployee(String firstName, String lastName, String favoriteColor, int id) {
        String updateEmployeeSQL = """
                UPDATE employees
                SET first_name = ?, last_name = ?, favorite_color = ?
                WHERE id = ?;""";

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateEmployeeSQL)) {
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, favoriteColor);
            preparedStatement.setInt(4, id);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }
    public static boolean deleteEmployee(int id) {
        //Absences des Mitarbeiters aus der DB loeschen
        ArrayList<Absence> absences = getAllAbsencesByEmployeeId(id);
        for (Absence a : absences) {
            deleteAbsences(a.id);
        }
        //Loeschen aller Teammiedgliedschaften
        deleteEmployeeFromAllTeams(id);
        //Mitarbeiter aus der db loeschen
        String deleteEmployeeSQL = "DELETE FROM employee WHERE id = ?;";

        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteEmployeeSQL)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();

            System.out.println("Mitarbeiter '" + id + "' geloescht.");
            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    //Absences
    public static void requestAbsence(Employee employee, AbsenceType type, LocalDate startDate, LocalDate endDate, boolean approved) {
        if (employee != null) {
            String insertAbsenceSQL = "INSERT INTO absences (employee_id, type, start_date, end_date, approved) VALUES (?, ?, ?, ?, ?);";

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertAbsenceSQL)) {
                preparedStatement.setInt(1, employee.id);
                preparedStatement.setString(2, type.toString());
                preparedStatement.setString(3, startDate.toString());
                preparedStatement.setString(4, endDate.toString());
                preparedStatement.setBoolean(5, approved);
                preparedStatement.executeUpdate();

                //System.out.println("Abwesenheitsantrag für '" + employeeName + "' erstellt.");
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public static ArrayList<Absence> getAllAbsences() {
        ArrayList<Absence> absences = new ArrayList<>();
        String getAbsencesByIdSQL = "SELECT * FROM absences ;";
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(getAbsencesByIdSQL);

            while (resultSet.next()) {
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
    public static ArrayList<Absence> getAllAbsencesByEmployeeId(int id) {
        ArrayList<Absence> absences = new ArrayList<>();
        String getAbsencesByIdSQL = "SELECT * FROM absences WHERE employee_id= ? ;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(getAbsencesByIdSQL)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
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

    public static Map<Employee, Absence> getAbsencesPerEmployeeByDay(LocalDate date) {
        Map<Employee, Absence> toDayAbsences = new HashMap<>();
        for (Employee e : getAllEmployees()) {
            for (Absence a : e.absences) {
                if ((a.getStartDate().isBefore(date) && a.getEndDate().isAfter(date)) || a.getStartDate().isEqual(date) || a.getEndDate().isEqual(date)) {
                    toDayAbsences.put(e, a);
                }
            }
        }
        return toDayAbsences;
    }

    public static boolean updateAbsence(int id, LocalDate start, LocalDate end, boolean approved, Employee employee, AbsenceType absenceType) {
        String updateAbsenceSQL = """
                UPDATE absences
                SET employee_id = ?, type = ?, start_date = ?, end_date = ?,approved = ?
                WHERE id = ?;""";

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateAbsenceSQL)) {
            preparedStatement.setInt(1, employee.id);
            preparedStatement.setString(2, absenceType.toString());
            preparedStatement.setString(3, start.toString());
            preparedStatement.setString(4, end.toString());
            preparedStatement.setBoolean(5, approved);
            preparedStatement.setInt(6, id);
            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public static boolean deleteAbsences(int id) { //id ist für absence
        String deletAbsence = "DELETE FROM absences WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deletAbsence)) {
            preparedStatement.setInt(1, id);
            preparedStatement.execute();

            return true;
        } catch (SQLException e) {
            System.err.println("Absence nicht gefunden!");
            throw new RuntimeException(e);
        }
    }

    public static void deleteAbsence(Absence absence) {
        String deletAbsenceSQL = "DELETE FROM absences WHERE id = ?;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deletAbsenceSQL)) {
            preparedStatement.setInt(1, absence.id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void addTeam(String name) {
        String insertTeamSQL = "INSERT INTO teams (name) VALUES (?);";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertTeamSQL)) {
            preparedStatement.setString(1, name);
            preparedStatement.executeUpdate();

            System.out.println("Team " + name + " hinzugefügt");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    //Teams

    public static String getTeamNameById(int team_id) {
        String team_name = null;
        String selectTeamsSQL = "SELECT name FROM teams WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(selectTeamsSQL)) {
            preparedStatement.setInt(1, team_id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                team_name = (resultSet.getString("name"));
                return team_name;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return team_name;
    }

    public static int getTeamIdByName(String team_name) {
        int team_id = -1;
        String selectTeamsSQL = "SELECT id FROM teams WHERE name = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(selectTeamsSQL)) {
            preparedStatement.setString(1, team_name);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                team_id = (resultSet.getInt("id"));
                return team_id;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return team_id;
    }

    public static ArrayList<String> getTeams() {
        ArrayList<String> list = new ArrayList<>();
        String selectTeamsSQL = "SELECT * FROM teams";

        try (PreparedStatement preparedStatement = connection.prepareStatement(selectTeamsSQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(resultSet.getString(2));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public static void deleteTeam(String name) {
        int team_id = getTeamIdByName(name);

        String deleteTeamSQL = "DELETE FROM teams WHERE name = ?;";
        String deleteAllTeamEmployeeSQL = "DELETE FROM teamEmployee WHERE team_id = ?;";

        try (PreparedStatement preparedStatement1 = connection.prepareStatement(deleteTeamSQL);
             PreparedStatement preparedStatement2 = connection.prepareStatement(deleteAllTeamEmployeeSQL)) {
            preparedStatement1.setString(1, name);
            preparedStatement2.setInt(1, team_id);
            preparedStatement2.executeUpdate();
            preparedStatement1.executeUpdate();

            System.out.println("Team " + name + " gelöscht.");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addEmployeeToTeam(int employee_id, int team_id) {
        if (debug) {
            System.out.println("addEmployeeToTeam!");
        }
        //Elseteil ist dummy code, damit die setInts keinen Error erzeugen
        String insertIntoTeamEmployeeSQL = "INSERT INTO teamEmployee (employee_id,team_id) VALUES (?,?);";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertIntoTeamEmployeeSQL)) {
            preparedStatement.setInt(1, employee_id);
            preparedStatement.setInt(2, team_id);
            preparedStatement.executeUpdate();

            System.out.println("Employee " + employee_id + " zum Team " + team_id + " hinzugefügt");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addEmployeeToTeam(int employee_id, String team_name) {
        int team_id = getTeamIdByName(team_name);
        addEmployeeToTeam(employee_id, team_id);
    }

    //Read

    public static ArrayList<String> getTeamsOfEmployee(int employee_id) {
        ArrayList<String> teams = new ArrayList<>();
        String selectTeamsSQL = "SELECT team_id FROM teamEmployee WHERE employee_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(selectTeamsSQL)) {
            preparedStatement.setInt(1, employee_id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                teams.add(getTeamNameById(resultSet.getInt("team_id")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return teams;
    }

    //Delete
    public static void deleteEmployeeFromTeam(int employee_id, int team_id) {
        String insertIntoTeamEmployeeSQL = "DELETE FROM teamEmployee WHERE employee_id = ? AND team_id = ?;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertIntoTeamEmployeeSQL)) {
            preparedStatement.setInt(1, employee_id);
            preparedStatement.setInt(2, team_id);
            preparedStatement.executeUpdate();

            System.out.println("Employee " + employee_id + " vom Team " + team_id + " geloescht");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    //Update

    private static void deleteEmployeeFromTeam(int employee_id, String team_name) {
        int team_id = getTeamIdByName(team_name);
        deleteEmployeeFromTeam(employee_id, team_id);
    }

    private static void deleteEmployeeFromAllTeams(int id) {
        ArrayList<String> teams = getTeamsOfEmployee(id);
        for (String team : teams) {
            deleteEmployeeFromTeam(id, team);
        }
    }


    public static void updateTeam(String team_name, String team_name_new) {
        String updateAbsenceSQL = """
                UPDATE teams
                SET name = ?
                WHERE name = ?;""";

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateAbsenceSQL)) {
            preparedStatement.setString(1, team_name_new); //neuer Teamname
            preparedStatement.setString(2, team_name);     //alter Teamname

            preparedStatement.execute();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    public static LocalDate getHighestDate() {
        LocalDate highestDate = LocalDate.now();

        ArrayList<Absence> absences = getAllAbsences();
        if (!absences.isEmpty()) {
            highestDate = LocalDate.parse(absences.get(0).endDate);
            for (Absence a : absences) {
                if (a.getEndDate().isAfter(highestDate)) {
                    highestDate = a.getEndDate();
                }
            }
        }

        return highestDate;
    }
}