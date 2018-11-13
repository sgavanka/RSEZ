package app.rsez.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Event extends ModelBase  {
    private static String COLLECTION_NAME = "events";

    private String title;
    private String description;

    private Date startDateTime;

   // private String startDate;
   // private String startTime;
    private Date date; //this will replace start time and start date

    private String hostEmail;

    public Event(String documentId){
        super(documentId);

        this.title = null;
        this.description = null;
        //this.startDate = null;
        //this.startTime = null;
        this.date = null;
        this.hostEmail = null;
    }
    public Event(String documentId, String title, String description, Date date, String hostEmail) {
        super(documentId);

        this.title = title;
        this.description = description;
        //this.startDate = startDate;
       // this.startTime = startTime;
        this.hostEmail = hostEmail;
        this.date = date;
         //document id should always be email
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public String getHostUserId() {
        return hostEmail;
    }

    public void setHostUserId(String hostUserId) {
        this.hostEmail = hostUserId;
    }
    /*public String getStartDate() {
        return startDate;
    }

    public String getStartTime() {
        return startTime;
    } */

    public Date getEventDate() { return date;}

    @Override
    public void write(OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        Map<String, Object> event = new HashMap<>();
        event.put("title", title);
        event.put("description", description);
        //event.put("startDate", startDate);
        //event.put("startTime", startTime);
        event.put("date", date);
        event.put("hostEmail", hostEmail);

        db.collection("events").document(getDocumentId()).set(event)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    @Override
    public void write() {
        Map<String, Object> event = new HashMap<>();
        event.put("title", title);
        event.put("description", description);
        //event.put("startDate", startDate);
        //event.put("startTime", startTime);
        event.put("date", date);
        event.put("hostEmail", hostEmail);

        db.collection("events").document().set(event);

    }
    public void writeId(String documentId) {
        Map<String, Object> event = new HashMap<>();
        event.put("title", title);
        event.put("description", description);
        //event.put("startDate", startDate);
        //event.put("startTime", startTime);
        event.put("date", date);
        event.put("hostEmail", hostEmail);

        db.collection("events").document(documentId).set(event);

    }

    public static void read(String documentId, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        db.collection(COLLECTION_NAME).document(documentId).get().addOnCompleteListener(onCompleteListener);
    }

    public static void delete(String documentId, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        db.collection(COLLECTION_NAME).document(documentId).delete()
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public static Event getEventFromDocumentSnapshot(DocumentSnapshot documentSnapshot) {
        Event event = documentSnapshot.toObject(Event.class);
        event.setDocumentId(documentSnapshot.getId());

        return  event;
    }


}
