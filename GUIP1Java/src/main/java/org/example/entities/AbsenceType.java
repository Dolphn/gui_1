package org.example.entities;

public enum AbsenceType {
    VACATION, UNPAID_LEAVE, TRAINING, REMOTE_WORK, SICKNESS, ACCIDENT;

    public static AbsenceType getAbscenceTypeByString(String text){
        if(text.equals("VACATION")) {
            return AbsenceType.VACATION;
        }else if(text.equals("UNPAID_LEAVE")){
            return AbsenceType.UNPAID_LEAVE;
        }else if(text.equals("TRAINING")){
            return AbsenceType.TRAINING;
        }else if(text.equals("REMOTE_WORK")){
            return AbsenceType.REMOTE_WORK;
        }else if(text.equals("SICKNESS")){
            return AbsenceType.SICKNESS;
        }else if(text.equals("ACCIDENT")){
            return ACCIDENT;
        }else{
            return null;
        }
    }
}
