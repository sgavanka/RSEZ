package app.rsez.models;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class User extends ModelBase {
    private static String COLLECTION_NAME = "users";

    private String email;

    private String firstName;
    private String lastName;
    private String documentId;

    public User(String documentId, String email, String firstName, String lastName) {
        super(documentId);

        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.documentId = documentId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public void write(OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        Map<String, Object> user = new HashMap<>();

        user.put("email", email);
        user.put("firstName", firstName);
        user.put("lastName", lastName);
        user.put("UserId", documentId);

        db.collection("users").document(email).set(user)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }
    @Override
    public void write(){
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("firstName", firstName);
        user.put("lastName", lastName);
        user.put("UserId", documentId);

        db.collection("users").document(email).set(user);
    }

    public static void read(String documentId, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        db.collection(COLLECTION_NAME).document(documentId).get().addOnCompleteListener(onCompleteListener);
    }

    public static void delete(String documentId, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        db.collection(COLLECTION_NAME).document(documentId).delete()
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public static User getUserFromDocumentSnapshot(DocumentSnapshot documentSnapshot) {
        User user = documentSnapshot.toObject(User.class);
        user.setDocumentId(documentSnapshot.getId());

        return  user;
    }
    public static User getUserFromEmail(String email){
        User user = null;
        DocumentReference docRef = db.collection("users").document(email);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
            }
        });
            return user;
    }
}
