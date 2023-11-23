package org.example;

class Absence {
    int id; // Abwesenheits-ID in der Datenbank
    int employeeId; // Mitarbeiter-ID, zu der diese Abwesenheit gehört
    AbsenceType type;
    String startDate;
    String endDate;
    boolean approved;
}
