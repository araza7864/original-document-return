package edu.gmu.cs321;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class WorkflowEntry {
    private String workflowID;
    private String status;
    private String comments;
    private Date lastUpdated;

    // (thanmai) static in-memory store for workflow entries
    private static List<WorkflowEntry> workflowDatabase = new ArrayList<>();

    public WorkflowEntry() {
        this.lastUpdated = new Date();
    }

    public WorkflowEntry(String workflowID, String status) {
        this.workflowID = workflowID;
        this.status = status;
        this.lastUpdated = new Date();
        this.comments = "";
    }

    // (thanmai) update the workflow status
    public void updateStatus(String newStatus) {
        this.status = newStatus;
        this.lastUpdated = new Date();
    }

    // (thanmai) add reviewer comment
    public void addComment(String comment) {
        if (comment != null) {
            this.comments = comment;
            this.lastUpdated = new Date();
        }
    }

    public boolean save() {
        if (workflowID == null || workflowID.isEmpty()) return false;
        if (status == null || status.isEmpty()) return false;
        workflowDatabase.add(this);
        return true;
    }

    public WorkflowEntry load(String workflowID) {
        if (workflowID == null) return null;
        for (WorkflowEntry entry : workflowDatabase) {
            if (workflowID.equals(entry.getWorkflowID())) {
                return entry;
            }
        }
        return null;
    }

    public String getWorkflowID() { return workflowID; }
    public void setWorkflowID(String workflowID) { this.workflowID = workflowID; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getComments() { return comments; }
    public Date getLastUpdated() { return lastUpdated; }
}