package edu.gmu.cs321;

public class Approver extends User {

    private ApprovalScreen approvalScreen;

    public Approver(String name) {
        super(name);
        this.approvalScreen = new ApprovalScreen();
    }

    public ApprovalScreen getApprovalScreen() {
        return approvalScreen;
    }
}