package com.mealer.app;

public class Complaint {

    private String subject;
    private String cookID;
    private String description;

    public Complaint(){

    }

    public Complaint(String subject, String description, String cookID){
        this.subject = subject;
        this.description = description;
        this.cookID = cookID;
    }

    public String getCookID() {
        return cookID;
    }

    public String getDescription() {
        return description;
    }

    public String getSubject() {
        return subject;
    }
}
