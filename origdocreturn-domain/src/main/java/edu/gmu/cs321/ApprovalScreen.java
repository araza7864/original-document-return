package edu.gmu.cs321;

import java.util.List;

public class ApprovalScreen {

    private OriginalDocumentReturnRequest currentRequest;

    // FIXED — queries APPROVAL stage from DB, not in-memory list
    public void loadNextApproval() {
        List<OriginalDocumentReturnRequest> approvalRequests =
                DatabaseUtil.getRequestsByStage("APPROVAL");
        if (!approvalRequests.isEmpty()) {
            currentRequest = approvalRequests.get(0);
        } else {
            currentRequest = null;
        }
    }

    public OriginalDocumentReturnRequest getNextApproval() {
        loadNextApproval();
        return currentRequest;
    }

    public void approveRequest() {
        if (currentRequest != null) {
            WorkflowService.approve(currentRequest);
            NotificationService.sendEmail(
                    currentRequest.getUserEmail(),
                    "Your request has been approved"
            );
        }
    }

    public void rejectRequest() {
        if (currentRequest != null) {
            WorkflowService.reject(currentRequest);
            NotificationService.sendEmail(
                    currentRequest.getUserEmail(),
                    "Your request has been rejected"
            );
        }
    }

    // NEW — approve a specific request by ID (used by servlet)
    public boolean approveById(String requestId) {
        List<OriginalDocumentReturnRequest> requests =
                DatabaseUtil.getRequestsByStage("APPROVAL");
        for (OriginalDocumentReturnRequest r : requests) {
            if (r.getRequestID() != null && r.getRequestID().equals(requestId)) {
                WorkflowService.approve(r);
                NotificationService.sendEmail(
                        r.getUserEmail(), "Your request has been approved");
                return true;
            }
        }
        return false;
    }

    // NEW — reject a specific request by ID (used by servlet)
    public boolean rejectById(String requestId) {
        List<OriginalDocumentReturnRequest> requests =
                DatabaseUtil.getRequestsByStage("APPROVAL");
        for (OriginalDocumentReturnRequest r : requests) {
            if (r.getRequestID() != null && r.getRequestID().equals(requestId)) {
                WorkflowService.reject(r);
                NotificationService.sendEmail(
                        r.getUserEmail(), "Your request has been rejected");
                return true;
            }
        }
        return false;
    }
}