package edu.gmu.cs321;

import java.util.ArrayList;
import java.util.List;

public class ReviewScreen {

    private int currentIndex = 0;

    // initializes review screen
    public ReviewScreen() {}

    // returns the next request currently in the REVIEW stage
    public OriginalDocumentReturnRequest loadNextRequest() {
        List<OriginalDocumentReturnRequest> pendingRequests = getReviewRequestsFromDatabase();

        if (currentIndex < pendingRequests.size()) {
            return pendingRequests.get(currentIndex++);
        }
        return null;
    }

    // updates the document type of a request during review
    public boolean editRequest(OriginalDocumentReturnRequest request,
                               String newDocumentType) {

        if (request == null) return false;

        request.setDocumentType(newDocumentType);

        DatabaseUtil.updateStatus(
                request.getRequestID(),
                request.getStatus(),
                "REVIEW"
        );
        return true;
    }

    // sends a reviewed request to the APPROVAL stage
    public void markReadyForApproval(String requestID, String reviewerComment) {
        OriginalDocumentReturnRequest request = findById(requestID);

        if (request != null) {
            WorkflowService.sendToApproval(request);

            if (reviewerComment != null && !reviewerComment.isEmpty()) {
                DatabaseUtil.insertWorkflowEntry(
                        java.util.UUID.randomUUID().toString(),
                        requestID,
                        "REVIEW",
                        "APPROVAL",
                        "REVIEW_APPROVE",
                        reviewerComment
                );
            }
        }
    }

    // returns a request back to the DATA_ENTRY stage
    public void returnToDataEntry(String requestID, String reviewerComment) {
        OriginalDocumentReturnRequest request = findById(requestID);

        if (request != null) {
            WorkflowService.returnToDataEntry(request);

            if (reviewerComment != null && !reviewerComment.isEmpty()) {
                DatabaseUtil.insertWorkflowEntry(
                        java.util.UUID.randomUUID().toString(),
                        requestID,
                        "REVIEW",
                        "DATA_ENTRY",
                        "RETURN_TO_ENTRY",
                        reviewerComment
                );
            }
        }
    }

    // retrieves all requests currently in the REVIEW stage
    private List<OriginalDocumentReturnRequest> getReviewRequestsFromDatabase() {
        List<OriginalDocumentReturnRequest> results = new ArrayList<>();

        for (OriginalDocumentReturnRequest r :
                OriginalDocumentReturnRequest.getPendingRequests()) {

            if ("REVIEW".equals(r.getStatus())) {
                results.add(r);
            }
        }
        return results;
    }

    // finds a request by its ID
    private OriginalDocumentReturnRequest findById(String id) {
        for (OriginalDocumentReturnRequest r : OriginalDocumentReturnRequest.getPendingRequests()) {
            if (r != null && r.getId() != null && r.getId().equals(id)) {
                return r;
            }
        }
        return null;
    }
}// Thanmai: updated ReviewScreen workflow integration
// Thanmai: ReviewScreen handles REVIEW to APPROVAL workflow transition
