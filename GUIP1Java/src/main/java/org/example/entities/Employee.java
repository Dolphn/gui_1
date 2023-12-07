package org.example.entities;
import java.util.ArrayList;
import java.util.List;

public class Employee {
    public int id; // Mitarbeiter-ID in der Datenbank
    public String firstName;
    public String lastName;
    public String favoriteColor;
    public List<Absence> absences = new ArrayList<>();
    public String getName(){
        return this.lastName + ", " + firstName;
    }
}
