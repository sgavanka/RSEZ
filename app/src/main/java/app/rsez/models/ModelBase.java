package app.rsez.models;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;


public abstract class ModelBase {
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

    public abstract void write(OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener);
}
