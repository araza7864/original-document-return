package edu.gmu.cs321;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class DatabaseUtil {

    // =========================
    // CONNECTION
    // =========================
    public static Connection getConnection() {
        try {
            return DatabaseInitializer.getConnection();
        } catch (Exception e) {
            throw new IllegalStateException("Database connection failed", e);
        }
    }

    // =========================
    // INSERT REQUEST (CREATE)
    // =========================
    public static void insertRequest(OriginalDocumentReturnRequest r) {

        // check if already exists first to avoid duplicate key error
        String checkSql = "SELECT COUNT(*) FROM g14_OriginalDocumentReturnRequest WHERE request_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement check = conn.prepareStatement(checkSql)) {
            check.setString(1, r.getRequestID());
            ResultSet rs = check.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                // already exists, just update status
                updateStatus(r.getRequestID(), r.getStatus(), "REVIEW");
                return;
            }
        } catch (Exception e) {
            throw new IllegalStateException("Database operation failed", e);
        }

        String sql = """
            INSERT INTO g14_OriginalDocumentReturnRequest
            (request_id, applicant_id, applicant_name, email, phone,
             document_type, submission_date, preferred_return_method,
             request_reason, status, current_stage)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, r.getRequestID());
            ps.setString(2, r.getApplicantID() != null ? r.getApplicantID() : "");
            ps.setString(3, r.getApplicantName() != null ? r.getApplicantName() : 
                           (r.getFirstName() + " " + r.getLastName()));
            ps.setString(4, r.getEmail() != null ? r.getEmail() : "");
            ps.setString(5, r.getPhone() != null ? r.getPhone() : "");
            ps.setString(6, r.getDocumentType());
            ps.setTimestamp(7, r.getSubmissionDate() != null ?
                new Timestamp(r.getSubmissionDate().getTime()) :
                new Timestamp(System.currentTimeMillis()));
            ps.setString(8, r.getPreferredReturnMethod() != null ? r.getPreferredReturnMethod() : "");
            ps.setString(9, r.getRequestReason() != null ? r.getRequestReason() : "");
            ps.setString(10, r.getStatus() != null ? r.getStatus() : "REVIEW");
            ps.setString(11, "REVIEW");

            ps.executeUpdate();

        } catch (Exception e) {
            throw new IllegalStateException("Database operation failed", e);
        }
    }

    // =========================
    // GET REQUESTS BY STAGE — returns actual objects (REVIEW SCREEN FIX)
    // =========================
    public static List<OriginalDocumentReturnRequest> getRequestsByStage(String stage) {

        List<OriginalDocumentReturnRequest> results = new ArrayList<>();

        String sql = """
            SELECT * FROM g14_OriginalDocumentReturnRequest
            WHERE current_stage = ?
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, stage);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                OriginalDocumentReturnRequest r = new OriginalDocumentReturnRequest(
                    rs.getString("request_id"),
                    rs.getString("applicant_id"),
                    rs.getString("applicant_name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("document_type"),
                    rs.getTimestamp("submission_date"),
                    rs.getString("preferred_return_method"),
                    rs.getString("request_reason")
                );
                r.updateRequestStatus(rs.getString("status"));
                results.add(r);
            }

        } catch (Exception e) {
            throw new IllegalStateException("Database operation failed", e);
        }

        return results;
    }

    // =========================
    // GET SINGLE REQUEST BY ID
    // =========================
    public static OriginalDocumentReturnRequest getRequestByID(String requestId) {

        String sql = "SELECT * FROM g14_OriginalDocumentReturnRequest WHERE request_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, requestId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                OriginalDocumentReturnRequest r = new OriginalDocumentReturnRequest(
                    rs.getString("request_id"),
                    rs.getString("applicant_id"),
                    rs.getString("applicant_name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("document_type"),
                    rs.getTimestamp("submission_date"),
                    rs.getString("preferred_return_method"),
                    rs.getString("request_reason")
                );
                r.updateRequestStatus(rs.getString("status"));
                return r;
            }

        } catch (Exception e) {
            throw new IllegalStateException("Database operation failed", e);
        }

        return null;
    }

    // =========================
    // GET PENDING REQUESTS (print only — kept for logging)
    // =========================
    public static void getPendingRequests() {

        String sql = """
            SELECT * FROM g14_OriginalDocumentReturnRequest
            WHERE current_stage = 'REVIEW'
        """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println(
                    "RequestID: " + rs.getString("request_id") +
                    " | Name: " + rs.getString("applicant_name") +
                    " | Status: " + rs.getString("status") +
                    " | Stage: " + rs.getString("current_stage")
                );
            }

        } catch (Exception e) {
            throw new IllegalStateException("Database operation failed", e);
        }
    }

    // =========================
    // SHOW REVIEWER COMMENT
    // =========================
    public static String getReviewerComment(String requestId) {
    String sql = """
        SELECT updated_by FROM g14_WorkflowEntry
        WHERE request_id = ? AND action = 'REVIEW_APPROVE'
        ORDER BY updated_at DESC LIMIT 1
    """;
    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, requestId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getString("updated_by");
        }
    } catch (Exception e) {
        throw new IllegalStateException("Database operation failed", e);
    }
    return null;
}

    // =========================
    // UPDATE STATUS + STAGE
    // =========================
    public static void updateStatus(String requestId, String status, String stage) {

        String sql = """
            UPDATE g14_OriginalDocumentReturnRequest
            SET status = ?, current_stage = ?
            WHERE request_id = ?
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setString(2, stage);
            ps.setString(3, requestId);

            ps.executeUpdate();

        } catch (Exception e) {
            throw new IllegalStateException("Database operation failed", e);
        }
    }

    // =========================
    // INSERT WORKFLOW ENTRY (AUDIT TRAIL)
    // =========================
    public static void insertWorkflowEntry(
            String workflowId,
            String requestId,
            String previousStage,
            String currentStage,
            String action,
            String updatedBy
    ) {

        String sql = """
            INSERT INTO g14_WorkflowEntry
            (workflow_id, request_id, previous_stage, current_stage, action, updated_by, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, NOW())
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, workflowId);
            ps.setString(2, requestId);
            ps.setString(3, previousStage);
            ps.setString(4, currentStage);
            ps.setString(5, action);
            ps.setString(6, updatedBy);

            ps.executeUpdate();

        } catch (Exception e) {
            throw new IllegalStateException("Database operation failed", e);
        }
    }
}