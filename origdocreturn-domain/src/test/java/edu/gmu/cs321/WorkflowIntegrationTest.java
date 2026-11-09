package edu.gmu.cs321;

import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import static org.junit.jupiter.api.Assertions.*;

@EnabledIfEnvironmentVariable(named = "RUN_DB_TESTS", matches = "true")
class WorkflowIntegrationTest {
    @BeforeAll static void initialize() { DatabaseInitializer.initialize(); }
    private OriginalDocumentReturnRequest request() {
        return new OriginalDocumentReturnRequest("test-" + UUID.randomUUID(), "synthetic-applicant", "Demo Applicant",
                "demo@example.com", "000-000-0000", "Transcript", new Date(), "Pickup", "Synthetic verification only");
    }
    @Test void submissionReviewApprovalAndAuditPersist() throws Exception {
        var request = request();
        WorkflowService.submit(request);
        assertEquals("REVIEW", OriginalDocumentReturnRequest.findByID(request.getId()).getStatus());
        new ReviewScreen().markReadyForApproval(request.getId(), "Synthetic review comment");
        assertEquals("APPROVAL", OriginalDocumentReturnRequest.findByID(request.getId()).getStatus());
        assertEquals("Synthetic review comment", DatabaseUtil.getReviewerComment(request.getId()));
        assertTrue(new ApprovalScreen().approveById(request.getId()));
        assertEquals("APPROVED", OriginalDocumentReturnRequest.findByID(request.getId()).getStatus());
        assertFalse(new ApprovalScreen().approveById(request.getId()));
        try (var c=DatabaseInitializer.getConnection(); var stmt=c.prepareStatement("SELECT COUNT(*) FROM g14_WorkflowEntry WHERE request_id = ?")) {
            stmt.setString(1, request.getId()); try (var rs=stmt.executeQuery()) { rs.next(); assertEquals(4, rs.getInt(1)); }
        }
    }
    @Test void rejectionPersists() {
        var request=request(); WorkflowService.submit(request); WorkflowService.sendToApproval(request);
        assertTrue(new ApprovalScreen().rejectById(request.getId()));
        assertEquals("REJECTED", OriginalDocumentReturnRequest.findByID(request.getId()).getStatus());
    }
    @Test void returnToEntryPersists() {
        var request=request(); WorkflowService.submit(request);
        new ReviewScreen().returnToDataEntry(request.getId(), "Synthetic correction");
        assertEquals("DATA_ENTRY", OriginalDocumentReturnRequest.findByID(request.getId()).getStatus());
    }
    @Test void missingRequestReturnsNull() { assertNull(OriginalDocumentReturnRequest.findByID("missing-"+UUID.randomUUID())); }
}
