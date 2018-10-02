package app.rsez.models;

import com.google.firebase.firestore.FirebaseFirestore;


public class ModelBase {
    protected static FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String documentId;

    public ModelBase(String documentId) {
        this.documentId = documentId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
