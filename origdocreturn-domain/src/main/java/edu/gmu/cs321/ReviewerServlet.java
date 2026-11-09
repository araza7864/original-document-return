package edu.gmu.cs321;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/reviewRequest")
public class ReviewerServlet extends HttpServlet {

    // GET — show next request pending review
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws IOException {

        ReviewScreen screen = new ReviewScreen();
        Reviewer reviewer = new Reviewer("Reviewer", screen);
        OriginalDocumentReturnRequest req = screen.loadNextRequest();

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html><body>");
        out.println("<h1>Review Screen</h1>");

        if (req != null) {
            out.println("<h2>Request Details</h2>");
            out.println("<p><b>Request ID:</b> " + Html.escape(req.getRequestID()) + "</p>");
            out.println("<p><b>Applicant Name:</b> " + Html.escape(req.getApplicantName()) + "</p>");
            out.println("<p><b>Document Type:</b> " + Html.escape(req.getDocumentType()) + "</p>");
            out.println("<p><b>Email:</b> " + Html.escape(req.getEmail()) + "</p>");
            out.println("<p><b>Phone:</b> " + Html.escape(req.getPhone()) + "</p>");
            out.println("<p><b>Reason:</b> " + Html.escape(req.getRequestReason()) + "</p>");
            out.println("<p><b>Status:</b> " + Html.escape(req.getStatus()) + "</p>");

            out.println("<form method='POST' action='reviewRequest'>");
            out.println("<input type='hidden' name='requestId' value='"
                    + Html.escape(req.getRequestID()) + "'>");
            out.println("<label>Reviewer Comment: "
                    + "<input type='text' name='comment' size='40'></label><br><br>");
            out.println("<input type='submit' name='action' value='Send to Approval'> &nbsp;");
            out.println("<input type='submit' name='action' value='Return to Data Entry'>");
            out.println("</form>");
        } else {
            out.println("<p>No requests are currently pending review.</p>");
        }

        out.println("<br><a href='index.html'>Back to Home</a>");
        out.println("</body></html>");
    }

    // POST — handle reviewer decision
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws IOException {

        String requestId = request.getParameter("requestId");
        String action    = request.getParameter("action");
        String comment   = request.getParameter("comment");

        ReviewScreen screen = new ReviewScreen();

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html><body>");

        if ("Send to Approval".equals(action)) {
            screen.markReadyForApproval(requestId, comment);
            out.println("<h1>Request Sent to Approval</h1>");
            out.println("<p>Request " + Html.escape(requestId) + " has been moved to the approval stage.</p>");
        } else if ("Return to Data Entry".equals(action)) {
            screen.returnToDataEntry(requestId, comment);
            out.println("<h1>Request Returned to Data Entry</h1>");
            out.println("<p>Request " + Html.escape(requestId) + " has been returned for correction.</p>");
        }

        out.println("<br><a href='reviewRequest'>Review Next Request</a>");
        out.println(" &nbsp;|&nbsp; <a href='index.html'>Back to Home</a>");
        out.println("</body></html>");
    }
}