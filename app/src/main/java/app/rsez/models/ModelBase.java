package app.rsez.models;

import com.google.firebase.firestore.FirebaseFirestore;

public class ModelBase {
    protected static FirebaseFirestore db = FirebaseFirestore.getInstance();
}
