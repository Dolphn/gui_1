<<<<<<< HEAD
package org.example;

public class Absence {
    public Absence(int id, int employeeId, AbsenceType type, String startDate, String endDate, boolean approved) {
        this.id = id;
        this.employeeId = employeeId;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.approved = approved;
    }
    int id;
    int employeeId;
    AbsenceType type;
    String startDate;
    String endDate;
    boolean approved;

    //getters und setters
    public int getId() {
        return id;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public AbsenceType getType() {
        return type;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public void setType(AbsenceType type) {
        this.type = type;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    @Override
    public String toString() {
        return "Absence[" +
                "id=" + id +
                ", employeeId=" + employeeId +
                ", type=" + type +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", approved=" + approved +
                ']';
    }

}
=======
package org.example;

class Absence {

    public Absence(int id, int employeeId, AbsenceType type, String startDate, String endDate, boolean approved) {
        this.id = id;
        this.employeeId = employeeId;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.approved = approved;

    }

        int id; // Abwesenheits-ID in der Datenbank
        int employeeId; // Mitarbeiter-ID, zu der diese Abwesenheit gehört
        AbsenceType type;
        String startDate;
        String endDate;
        boolean approved;

    public Absence() {

    }


    //getters und setters
        public int getId () {
            return id;
        }

        public int getEmployeeId () {
            return employeeId;
        }

        public AbsenceType getType () {
            return type;
        }

        public String getStartDate () {
            return startDate;
        }

        public String getEndDate () {
            return endDate;
        }

        public boolean isApproved () {
            return approved;
        }

        public void setId ( int id){
            this.id = id;
        }

        public void setEmployeeId ( int employeeId){
            this.employeeId = employeeId;
        }

        public void setType (AbsenceType type){
            this.type = type;
        }

        public void setStartDate (String startDate){
            this.startDate = startDate;
        }

        public void setEndDate (String endDate){
            this.endDate = endDate;
        }

        public void setApproved ( boolean approved){
            this.approved = approved;
        }

        public String toString () {
            return "Absence[" +
                    "id=" + id +
                    ", employeeId=" + employeeId +
                    ", type=" + type +
                    ", startDate='" + startDate + '\'' +
                    ", endDate='" + endDate + '\'' +
                    ", approved=" + approved +
                    ']';
        }
    }
>>>>>>> ab99276845e7a9dd0d30b8d2377374a56faa042f
