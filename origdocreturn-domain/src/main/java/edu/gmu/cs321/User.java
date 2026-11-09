package edu.gmu.cs321;

public abstract class User {
    //DataEntryClerk, Reviewer, Approver extends User

    protected String name;

    //(thanmai) constructor called by subclasses
    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
