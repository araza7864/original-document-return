package edu.gmu.cs321;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/approveRequest")
public class ApproverServlet extends HttpServlet {

    // GET — show next request pending approval
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws IOException {

        Approver approver = new Approver("Approver");
        ApprovalScreen screen = approver.getApprovalScreen();
        OriginalDocumentReturnRequest req = screen.getNextApproval();

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html><body>");
        out.println("<h1>Approval Screen</h1>");

        if (req != null) {
            out.println("<h2>Request Details</h2>");
            out.println("<p><b>Request ID:</b> " + Html.escape(req.getRequestID()) + "</p>");
            out.println("<p><b>Applicant Name:</b> " + Html.escape(req.getApplicantName()) + "</p>");
            out.println("<p><b>Document Type:</b> " + Html.escape(req.getDocumentType()) + "</p>");
            out.println("<p><b>Email:</b> " + Html.escape(req.getEmail()) + "</p>");
            out.println("<p><b>Phone:</b> " + Html.escape(req.getPhone()) + "</p>");
            out.println("<p><b>Reason:</b> " + Html.escape(req.getRequestReason()) + "</p>");
            out.println("<p><b>Status:</b> " + Html.escape(req.getStatus()) + "</p>");

            // NEW — show reviewer comment if present
            String reviewerComment = DatabaseUtil.getReviewerComment(req.getRequestID());
            if (reviewerComment != null && !reviewerComment.isEmpty()) {
                out.println("<p><b>Reviewer Comment:</b> " + Html.escape(reviewerComment) + "</p>");
            }

            out.println("<form method='POST' action='approveRequest'>");
            out.println("<input type='hidden' name='requestId' value='"
                    + Html.escape(req.getRequestID()) + "'>");
            out.println("<input type='submit' name='action' value='Approve'> &nbsp;");
            out.println("<input type='submit' name='action' value='Reject'>");
            out.println("</form>");
        } else {
            out.println("<p>No requests are currently pending approval.</p>");
        }

        out.println("<br><a href='index.html'>Back to Home</a>");
        out.println("</body></html>");
    }

    // POST — handle approve or reject
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws IOException {

        String requestId = request.getParameter("requestId");
        String action    = request.getParameter("action");

        Approver approver = new Approver("Approver");
        ApprovalScreen screen = approver.getApprovalScreen();

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html><body>");

        if ("Approve".equals(action)) {
            boolean success = screen.approveById(requestId);
            if (success) {
                out.println("<h1>Request Approved</h1>");
                out.println("<p>Request " + Html.escape(requestId) + " has been approved "
                        + "and the applicant has been notified.</p>");
            } else {
                out.println("<h1>Approval Failed</h1>");
                out.println("<p>Could not find request " + Html.escape(requestId) + ".</p>");
            }
        } else if ("Reject".equals(action)) {
            boolean success = screen.rejectById(requestId);
            if (success) {
                out.println("<h1>Request Rejected</h1>");
                out.println("<p>Request " + Html.escape(requestId) + " has been rejected "
                        + "and the applicant has been notified.</p>");
            } else {
                out.println("<h1>Rejection Failed</h1>");
                out.println("<p>Could not find request " + Html.escape(requestId) + ".</p>");
            }
        }

        out.println("<br><a href='approveRequest'>Load Next Request</a>");
        out.println(" &nbsp;|&nbsp; <a href='index.html'>Back to Home</a>");
        out.println("</body></html>");
    }
}