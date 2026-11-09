package edu.gmu.cs321;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class OriginalDocumentReturnRequest {

    private String requestID;
    private String applicantID;
    private String applicantName;
    private String email;
    private String phone;
    private String documentType;
    private Date submissionDate;
    private String preferredReturnMethod;
    private String requestReason;
    private String status;
    private String firstName;
    private String lastName;

    public OriginalDocumentReturnRequest(
            String requestID, String applicantID, String applicantName,
            String email, String phone, String documentType,
            Date submissionDate, String preferredReturnMethod, String requestReason) {

        this.requestID = requestID;
        this.applicantID = applicantID;
        this.applicantName = applicantName;
        this.email = email;
        this.phone = phone;
        this.documentType = documentType;
        this.submissionDate = submissionDate;
        this.preferredReturnMethod = preferredReturnMethod;
        this.requestReason = requestReason;
        this.status = "Draft";
    }

    public OriginalDocumentReturnRequest() {}

    public boolean validateRequestData() {
        String name = applicantName;
        if ((name == null || name.isBlank()) && firstName != null && lastName != null) {
            name = firstName.trim() + " " + lastName.trim();
        }
        return documentType != null && !documentType.isBlank()
                && name != null && !name.isBlank()
                && email != null && email.contains("@")
                && preferredReturnMethod != null && !preferredReturnMethod.isBlank();
    }

    public void updateRequestStatus(String newStatus) {
        this.status = newStatus;
    }

    public void submitForReview() {
        this.status = "REVIEW";
    }

    public boolean save() {
        if (!validateRequestData()) return false;
        DatabaseUtil.insertRequest(this);
        return true;
    }

    public OriginalDocumentReturnRequest load(String requestID) {
        return findByID(requestID);
    }

    // FIXED — now queries MySQL
    public static List<OriginalDocumentReturnRequest> getPendingRequests() {
        return DatabaseUtil.getRequestsByStage("REVIEW");
    }

    public static OriginalDocumentReturnRequest findByID(String requestID) {
        return DatabaseUtil.getRequestByID(requestID);
    }

    public String getStatus() { return status; }
    public String getId() { return requestID; }
    public String getRequestID() { return requestID; }
    public String getApplicantID() { return applicantID; }
    public String getApplicantName() { return applicantName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }
    public Date getSubmissionDate() { return submissionDate; }
    public String getPreferredReturnMethod() { return preferredReturnMethod; }
    public String getRequestReason() { return requestReason; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getUserEmail() { return email; }
    public void setRequestID(String requestID) { this.requestID = requestID; }
    public void setApplicantID(String applicantID) { this.applicantID = applicantID; }
    public void setSubmissionDate(Date submissionDate) { this.submissionDate = submissionDate; }
    public boolean validate() { return validateRequestData(); }
}