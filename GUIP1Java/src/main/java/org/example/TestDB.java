package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.example.AbsencePlanner.*;

public class TestDB {
    public static  void testDbErschaffen(){
        Connection connection = AbsencePlanner.getConnection();
        //Datenbank erschaffen
        initializeDatabase();
        //Datenbankdaten loeschen
        String clearEmployeesTableSQL = "DELETE FROM employees;";

        String clearAbsencesTableSQL = "DELETE FROM absences;";

        try (PreparedStatement preparedStatement1 = connection.prepareStatement(clearEmployeesTableSQL);
             PreparedStatement preparedStatement2 = connection.prepareStatement(clearAbsencesTableSQL)) {
            preparedStatement1.execute();
            preparedStatement2.execute();
            System.out.println("Tabellen 'employees' und 'absences' geleert.");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        //Datenbank füllen

        addEmployee("Bruno","Brenner","#00CED1");
        addEmployee("Daniel","Deiters","#A0CED5");
        addEmployee("Ruben","Reiter","#4A0D65");

        ArrayList<Employee> employees = getAllEmployees();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        requestAbsence(employees.get(0),AbsenceType.SICKNESS, LocalDate.parse("01-01-2024",dtf),LocalDate.parse("05-01-2024",dtf),false);
        requestAbsence(employees.get(1),AbsenceType.REMOTE_WORK,LocalDate.parse("02-01-2024",dtf),LocalDate.parse("04-01-2024",dtf),true);
        requestAbsence(employees.get(2),AbsenceType.SICKNESS,LocalDate.parse("01-02-2024",dtf),LocalDate.parse("02-01-2024",dtf),false);
        requestAbsence(employees.get(2),AbsenceType.TRAINING,LocalDate.parse("03-01-2024",dtf),LocalDate.parse("10-01-2024",dtf),false);

    }
}
