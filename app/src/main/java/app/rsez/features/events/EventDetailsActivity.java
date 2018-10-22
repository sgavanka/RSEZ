package app.rsez.features.events;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

import app.rsez.R;
import app.rsez.models.Event;

public class EventDetailsActivity extends AppCompatActivity {
    private static final String TAG = "EventDetails";
    private boolean userIsEventOwner = false;

    private String eventID;
    private String title;
    private String desc;
    private String date;
    private String time;
    private String email;

    protected static FirebaseFirestore db = FirebaseFirestore.getInstance();

    private LinearLayout mLinearLayout;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        mLinearLayout = this.findViewById(R.id.linear);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_back);

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();

        eventID = getIntent().getStringExtra("eventID");

        Event.read(eventID, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                TextView eventName = findViewById(R.id.event_name_view);
                TextView eventDesc = findViewById(R.id.event_description_view);
                TextView eventDate = findViewById(R.id.event_date_view);
                TextView eventTime = findViewById(R.id.event_time_view);
                TextView eventEmail = findViewById(R.id.event_email_view);

                DocumentSnapshot doc = task.getResult();
                title = doc.getString("title");
                desc = doc.getString("description");
                date = doc.getString("startDate");
                time = doc.getString("startTime");
                email = doc.getString("hostEmail");
                if (mAuth.getCurrentUser().getEmail().equals(doc.getString("hostEmail"))) {
                    userIsEventOwner = true;
                    invalidateOptionsMenu();
                }

                eventName.setText(title);
                eventDesc.setText("Description: " + desc);
                eventDate.setText("Date: " + date);
                eventTime.setText("Time: " + time);
                eventEmail.setText("Host email: " + email);
            }
        });
        ticketQuery(eventID, this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (userIsEventOwner) {
            getMenuInflater().inflate(R.menu.event_details_menu, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.edit_button:
                Intent editIntent = new Intent( this, EventEditFragment.class);
                editIntent.putExtra("Id", eventID);
                editIntent.putExtra("Title", title);
                editIntent.putExtra("Description", desc);
                editIntent.putExtra("Date", date);
                editIntent.putExtra("Time", time);
                editIntent.putExtra("Email", email);

                startActivity(editIntent);
                break;
            case R.id.invite_button:
                Intent inviteIntent = new Intent(this, InviteActivity.class);
                inviteIntent.putExtra("eventID",eventID);
                inviteIntent.putExtra("eventName", title);

                startActivity(inviteIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    public void ticketQuery(String eventID, final Context context) {
        db.collection("tickets").whereEqualTo("eventId", eventID)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            //System.out.println("query failed");
                            return;
                        }
                        for(QueryDocumentSnapshot doc : value) {
                            if(doc.getString("eventId") != null) {
                                String userName = doc.getString("userId");
                                TextView temp = temp = new TextView(context);
                                temp.setText(userName);
                                temp.setTextSize(15);
                                temp.setTextColor(Color.BLACK);
                                temp.setPadding(10,0,0, 20);
                                temp.setClickable(true);
                                mLinearLayout.addView(temp);
                                temp.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(userIsEventOwner == true) {

                                        }
                                    }
                                });
                            }
                        }

                    }
                });

    }
}
