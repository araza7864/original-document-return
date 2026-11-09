package edu.gmu.cs321;

public class WorkflowService {
    //created to centralize flow in WorkflowService
    
    // SUBMIT -> REVIEW
    public static void submit(OriginalDocumentReturnRequest req) {
        if (req == null || !req.validateRequestData()) {
            throw new IllegalArgumentException("Complete applicant, email, document type and return method are required");
        }
        String prev = "DATA_ENTRY";
        String curr = "REVIEW";

        req.updateRequestStatus(curr);

        DatabaseUtil.insertRequest(req);

        DatabaseUtil.updateStatus(req.getRequestID(), curr, "REVIEW");


        DatabaseUtil.insertWorkflowEntry(
                java.util.UUID.randomUUID().toString(),
                req.getRequestID(),
                prev,
                curr,
                "SUBMIT",
                req.getApplicantName()
        );
    }

    // REVIEW -> APPROVAL
    public static void sendToApproval(OriginalDocumentReturnRequest req) {
        String prev = "REVIEW";
        String curr = "APPROVAL";

        req.updateRequestStatus(curr);

        DatabaseUtil.updateStatus(
                req.getRequestID(),
                curr,
                "APPROVAL"
        );

        DatabaseUtil.insertWorkflowEntry(
                java.util.UUID.randomUUID().toString(),
                req.getRequestID(),
                prev,
                curr,
                "MOVE_TO_APPROVAL",
                "REVIEWER"
        );
    }

    // FINAL APPROVAL
    public static void approve(OriginalDocumentReturnRequest req) {
        String prev = "APPROVAL";
        String curr = "APPROVED";

        req.updateRequestStatus(curr);

        DatabaseUtil.updateStatus(
                req.getRequestID(),
                curr,
                "COMPLETED"
        );

        DatabaseUtil.insertWorkflowEntry(
                java.util.UUID.randomUUID().toString(),
                req.getRequestID(),
                prev,
                curr,
                "APPROVE",
                "APPROVER"
        );
    }

    public static void reject(OriginalDocumentReturnRequest req) {
        String prev = "APPROVAL";
        String curr = "REJECTED";

        req.updateRequestStatus(curr);

        DatabaseUtil.updateStatus(
                req.getRequestID(),
                curr,
                "COMPLETED"
        );

        DatabaseUtil.insertWorkflowEntry(
                java.util.UUID.randomUUID().toString(),
                req.getRequestID(),
                prev,
                curr,
                "REJECT",
                "APPROVER"
        );
    }

    public static void returnToDataEntry(OriginalDocumentReturnRequest req) {
        String prev = "REVIEW";
        String curr = "DATA_ENTRY";

        req.updateRequestStatus(curr);

        DatabaseUtil.updateStatus(
                req.getRequestID(),
                curr,
                "DATA_ENTRY"
        );

        DatabaseUtil.insertWorkflowEntry(
                java.util.UUID.randomUUID().toString(),
                req.getRequestID(),
                prev,
                curr,
                "RETURN_TO_ENTRY",
                "REVIEWER"
        );
    }
}