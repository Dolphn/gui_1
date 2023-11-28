package org.example;

class Absence {
    int id; // Abwesenheits-ID in der Datenbank
    int employeeId; // Mitarbeiter-ID, zu der diese Abwesenheit gehört
    AbsenceType type;
    String startDate;
    String endDate;
    boolean approved;

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
