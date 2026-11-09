package edu.gmu.cs321;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/submitRequest")
public class DataEntryServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
                          throws IOException {

            String firstName    = request.getParameter("firstName");
            String lastName     = request.getParameter("lastName");
            String email        = request.getParameter("email");
            String phone        = request.getParameter("phone");
            String docType      = request.getParameter("docType");
            String returnMethod = request.getParameter("returnMethod");
            String reason       = request.getParameter("reason");

            OriginalDocumentReturnRequest docRequest = new OriginalDocumentReturnRequest(
                    java.util.UUID.randomUUID().toString(),
                    firstName + lastName,
                    firstName + " " + lastName,
                    email,
                    phone,
                    docType,
                    new java.util.Date(),
                    returnMethod,
                    reason
            );

            if (!docRequest.validateRequestData() || firstName == null || firstName.isBlank() || lastName == null || lastName.isBlank()) {
                response.sendError(400, "Complete all required request fields");
                return;
            }
            WorkflowService.submit(docRequest);

            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<h1>Request Submitted Successfully</h1>");
            out.println("<a href='index.html'>Return Home</a>");
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws IOException {
        response.sendError(405, "Submit requests using POST");
    }
}