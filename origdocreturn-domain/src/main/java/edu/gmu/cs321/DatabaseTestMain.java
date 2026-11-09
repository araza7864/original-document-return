package edu.gmu.cs321;

import java.util.Date;
import java.util.UUID;

public class DatabaseTestMain {

    public static void main(String[] args) {

        String requestId = "REQ-" + UUID.randomUUID().toString().substring(0, 8);

        // 1. CREATE TEST REQUEST
        OriginalDocumentReturnRequest req =
                new OriginalDocumentReturnRequest(
                        requestId,
                        "A001",
                        "John Doe",
                        "john@email.com",
                        "1234567890",
                        "Passport",
                        new Date(),
                        "Mail",
                        "Lost document"
                );

        // 2. TEST FULL WORKFLOW (SUBMISSION)
        WorkflowService.submit(req);

        // 3. MOVE TO REVIEW → APPROVAL
        WorkflowService.sendToApproval(req);

        // 4. FINAL APPROVAL TEST
        WorkflowService.approve(req);

        // 5. VERIFY RESULTS (DB-driven view)
        DatabaseUtil.getPendingRequests();

        System.out.println("DATABASE + WORKFLOW TEST COMPLETE");
    }
}