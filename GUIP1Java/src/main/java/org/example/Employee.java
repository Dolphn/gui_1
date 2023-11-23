package org.example;

import java.util.ArrayList;
import java.util.List;

class Employee {
    int id; // Mitarbeiter-ID in der Datenbank
    String firstName;
    String lastName;
    String favoriteColor;
    List<Absence> absences = new ArrayList<>();
}
