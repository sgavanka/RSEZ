package app.rsez.models;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Ticket extends ModelBase {
    private static String COLLECTION_NAME = "tickets";

    private String eventId;
    private String userId;

    private Date checkInDateTime;

    public Ticket(String documentId, String eventId, String userId, Date checkInDateTime) {
        super(documentId);

        this.eventId = eventId;
        this.userId = userId;
        this.checkInDateTime = checkInDateTime;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getCheckInDateTime() {
        return checkInDateTime;
    }

    public void setCheckInDateTime(Date checkInDateTime) {
        this.checkInDateTime = checkInDateTime;
    }

    @Override
    public void write(OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        db.collection("tickets").document(getDocumentId()).set(this)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    @Override
    public void write() {

        Map<String, Object> ticket = new HashMap<>();
        ticket.put("eventId", eventId);
        ticket.put("userId", userId);
        ticket.put("checkInDateTime", checkInDateTime);

        db.collection("tickets").document(getDocumentId()).set(ticket);

    }

    public static void read(String documentId, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        db.collection(COLLECTION_NAME).document(documentId).get().addOnCompleteListener(onCompleteListener);
    }

    public static void delete(String documentId, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        db.collection(COLLECTION_NAME).document(documentId).delete()
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public static Ticket getTicketFromDocumentSnapshot(DocumentSnapshot documentSnapshot) {
        Ticket ticket = documentSnapshot.toObject(Ticket.class);
        ticket.setDocumentId(documentSnapshot.getId());

        return  ticket;
    }
}
