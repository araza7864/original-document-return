package edu.gmu.cs321;

public class DataEntryClerk {
    // minimal, just so the clerk can initiate the process 
    private DataEntryScreen screen;

    public DataEntryClerk() {
        screen = new DataEntryScreen();
    }

    public void startRequestProcess() {
        screen.displayForm();
    }
}