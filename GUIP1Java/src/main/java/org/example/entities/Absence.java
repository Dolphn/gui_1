package org.example.entities;
import java.time.LocalDate;

public class Absence {
    public int id; // Abwesenheits-ID in der Datenbank
    public int employeeId; // Mitarbeiter-ID, zu der diese Abwesenheit gehört
    public AbsenceType type;
    public String startDate;
    public String endDate;
    public boolean approved;

    public String toString(){
        String string;
        if(approved) {
            string = type.toString() + " " + startDate + " " + endDate + " " + "approved";
        }else {
            string = type.toString() + " " + startDate + " " + endDate + " " + "not approved";
        }
        return string;
    }

    public LocalDate getStartDate() {
        return LocalDate.parse(startDate);
    }

    public LocalDate getEndDate(){
        return LocalDate.parse(endDate);
    }
}
