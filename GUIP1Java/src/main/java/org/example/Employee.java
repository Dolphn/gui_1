package org.example;

import java.util.ArrayList;
import java.util.List;

class Employee {
    int id; // Mitarbeiter-ID in der Datenbank
    String firstName;
    String lastName;
    String favoriteColor;
    List<Absence> absences = new ArrayList<>();
    public String getName(){
        return this.lastName + ", " + firstName;
    }

    public Employee() {
    }

    public Employee(String firstName, String lastName, String favoriteColor) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.favoriteColor = favoriteColor;
    }
}
