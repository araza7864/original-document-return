package edu.gmu.cs321;

import java.util.Date;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OriginalDocumentReturnRequestTest {
    private OriginalDocumentReturnRequest valid() {
        return new OriginalDocumentReturnRequest("unit-id", "demo", "Demo Applicant", "demo@example.com",
                "000-000-0000", "Transcript", new Date(), "Pickup", "Synthetic test");
    }
    @Test void acceptsCompleteFormConstructorData() { assertTrue(valid().validate()); }
    @Test void rejectsEmptyDocumentType() {
        var request = valid(); request.setDocumentType("  "); assertFalse(request.validate());
    }
    @Test void rejectsEmptyRequestWithoutWritingToDatabase() {
        var request = new OriginalDocumentReturnRequest(); assertFalse(request.validate()); assertFalse(request.save());
    }
    @Test void beginsInDraftAndCanEnterReview() {
        var request = valid(); assertEquals("Draft", request.getStatus());
        request.submitForReview(); assertEquals("REVIEW", request.getStatus());
    }
    @Test void escapesUntrustedHtml() {
        assertEquals("&lt;script&gt;&amp;&quot;&#39;", Html.escape("<script>&\"'"));
    }
}
