package edu.gmu.cs321;

public class DataEntryScreen {

    public void displayForm() {
        System.out.println("Displaying Original Document Return Request form.");
    }

    public boolean validateAndSave(OriginalDocumentReturnRequest request) {
        if (!request.validate()) {
            return false;
        }
        return request.save();
    }

    public boolean saveDraft(OriginalDocumentReturnRequest request) {
        // save without validation
        return request.save();
    }

    public boolean submitRequest(OriginalDocumentReturnRequest request) {
        if (!request.validate()) {
            return false;
        }
        return request.save();
    }
}