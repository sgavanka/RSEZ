package app.rsez;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;

import app.rsez.models.Event;

public class EventDetailsActivity extends Activity implements View.OnClickListener {
    static String title;
    static String desc;
    static String date;
    static String time;
    static String email;
    String eventID;
    String eventTitle;
    Boolean updated = false;
    TextView eventName;
    TextView eventDesc;
    TextView eventDate;
    TextView eventTime;
    TextView eventEmail;
    Button inviteButton;
    Button editButton;
    Event event;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        eventID = getIntent().getStringExtra("eventID");
        final Event event = new Event(eventID);
        eventName = (TextView) findViewById(R.id.event_name_view);
        eventDesc = (TextView) findViewById(R.id.event_description_view);
        eventDate = (TextView) findViewById(R.id.event_date_view);
        eventTime = (TextView) findViewById(R.id.event_time_view);
        eventEmail = (TextView) findViewById(R.id.event_email_view);
        inviteButton = (Button) findViewById(R.id.inviteButton);
        editButton = (Button) findViewById(R.id.editButton);
        inviteButton.setOnClickListener(this);
        editButton.setOnClickListener(this);

        event.read(eventID, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                //System.out.println(doc);
                title = doc.getString("title");
                desc = doc.getString("description");
                date = doc.getString("startDate");
                time = doc.getString("startTime");
                email = doc.getString("hostEmail");
                if (currentUser.getEmail().equals(doc.getString("hostEmail"))) {
                    inviteButton.setVisibility(View.VISIBLE);
                    editButton.setVisibility(View.VISIBLE);
                }
                eventTitle = title;
                eventName.setText(title);
                eventDesc.setText("Description: " + desc);
                eventDate.setText("Date: " + date);
                eventTime.setText("Time: " + time);
                eventEmail.setText("Host email: " + email);
                updated = true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.inviteButton){
            System.out.println("INVITE BUTTON");
            Intent intent = new Intent(this, InviteActivity.class);
            intent.putExtra("eventID",eventID);
            intent.putExtra("eventName", eventTitle);
            startActivity(intent);
        } else if (i == R.id.editButton){
            Intent intent = new Intent( this, EditFragment.class);
            EventDetailsActivity.this.event = new Event(eventID, title, desc, date, time, email);

            Gson gson = new Gson();
            String obj = gson.toJson(EventDetailsActivity.this.event);
            intent.putExtra("Id", eventID);
            intent.putExtra("Title", title);
            intent.putExtra("Description", desc);
            intent.putExtra("Date", date);
            intent.putExtra("Time", time);
            intent.putExtra("Email", email);


            startActivity(intent);
        }
    }
}
