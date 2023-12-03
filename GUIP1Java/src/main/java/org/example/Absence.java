package org.example;

import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class Absence {
    int id; // Abwesenheits-ID in der Datenbank
    int employeeId; // Mitarbeiter-ID, zu der diese Abwesenheit gehört
    AbsenceType type;
    String startDate;
    String endDate;
    boolean approved;

    public LocalDate getStartDate(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate startDate = LocalDate.parse(this.startDate, dtf);
        return startDate;
    }

    public LocalDate getEndDate(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate endDate = LocalDate.parse(this.endDate, dtf);
        return endDate;
    }

    public String toString(){
        String string;
        if(approved) {
            string = type.toString() + " " + startDate + " " + endDate + " " + "approved";
        }else {
            string = type.toString() + " " + startDate + " " + endDate + " " + "not approved";
        }
        return string;
    }
}
