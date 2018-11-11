package app.rsez.features.events;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import app.rsez.R;
import app.rsez.models.Ticket;
import app.rsez.models.User;

public class CheckInListActivity extends AppCompatActivity {

    protected static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Context context;
    private String eventId;
    private LinearLayout mLinearLayout;
    private List<DocumentSnapshot> attendees;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_list);
        context = this;
        mLinearLayout = findViewById(R.id.checkInListContainer);
        eventId = getIntent().getStringExtra("eventId");
        addUsers();
    }

    private void addUsers() {
        //Query tickets for userId's attending event
        CollectionReference colRef = db.collection("tickets");
        Query query = colRef.whereEqualTo("eventId", eventId);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    attendees = task.getResult().getDocuments();
                    System.out.println("Number of attendees: " + attendees.size());
                    for (int i = 0; i < attendees.size(); i++) {
                        //Loop through each attendee
                        final Ticket ticket = new Ticket(attendees.get(i).getId(), attendees.get(i).getString("eventId"), attendees.get(i).getString("userId"), (Date) attendees.get(i).get("checkInDateTime"));
                        System.out.println("Found: " + ticket.getUserId());
                        DocumentReference docRef = db.collection("users").document(ticket.getUserId());
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    final User user = new User(task.getResult().getId(), task.getResult().getString("email"), task.getResult().getString("firstName"), task.getResult().getString("lastName"));
                                    TextView temp;
                                    String name = null;
                                    String email = null;
                                    if(user.getFirstName() == null) {
                                        name = "[Unregistered Email!]";
                                        email = ticket.getUserId();
                                    }
                                    else {
                                       name = user.getFirstName() + " " + user.getLastName();
                                       email = user.getEmail();
                                    }
                                    final String checkedIn;

                                    if (ticket.getCheckInDateTime() != null) {
                                        checkedIn = "Checked In";
                                    } else {
                                        checkedIn = "Not Checked In";
                                    }
                                   String combined = name + "\n" + email + "\n" + checkedIn;

                                    temp = new TextView(context);
                                    temp.setText(combined);
                                    temp.setTextSize(20);
                                    temp.setBackground(ContextCompat.getDrawable(context, R.drawable.customborder2));
                                    if (checkedIn.equals("Checked In")) {
                                        //set text to Dark Green if already checked in
                                        temp.setTextColor(Color.rgb(39, 158, 0));
                                    } else {
                                        temp.setTextColor(Color.BLACK);
                                    }
                                    temp.setPadding(10, 0, 0, 20);
                                    temp.setClickable(true);
                                    temp.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (!checkedIn.equals("Checked In")) {
                                                checkIn(ticket);
                                            } else {
                                                //Toast.makeText(context, "User is already checked in", Toast.LENGTH_SHORT).show();
                                                //Uncheck in user
                                                uncheckIn(ticket);
                                            }
                                        }
                                    });
                                    mLinearLayout.addView(temp);
                                    Space tempSpace = new Space(context);
                                    tempSpace.setMinimumHeight(5);
                                    mLinearLayout.addView(tempSpace);
                                }

                            }
                        });
                    }
                }
            }
        });
    }

    private void checkIn(Ticket ticket) {
        Calendar cal = Calendar.getInstance();
        Date currentDate = cal.getTime();
        Ticket newTicket = new Ticket(ticket.getDocumentId(), ticket.getEventId(), ticket.getUserId(), currentDate);
        newTicket.write(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                System.out.println("Checked in Successfully");
                Toast.makeText(getApplicationContext(), "Check in Successful", Toast.LENGTH_SHORT).show();
                mLinearLayout.removeAllViews();
                addUsers();
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Checked in Failed");
                Toast.makeText(getApplicationContext(), "Failed to check in properly", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uncheckIn(Ticket ticket){

        Ticket newTicket = new Ticket(ticket.getDocumentId(), ticket.getEventId(), ticket.getUserId(), null);
        newTicket.write(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                System.out.println("Unchecked in User");
                Toast.makeText(getApplicationContext(), "User checked out", Toast.LENGTH_SHORT).show();
                mLinearLayout.removeAllViews();
                addUsers();
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Uncheck in failed");
                Toast.makeText(getApplicationContext(), "Failed to uncheck in properly", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
