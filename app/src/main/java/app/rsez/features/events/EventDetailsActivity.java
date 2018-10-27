package app.rsez.features.events;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import app.rsez.QRScanFragment;
import app.rsez.R;
import app.rsez.models.Event;

public class EventDetailsActivity extends AppCompatActivity {
    private boolean userIsEventOwner = false;

    private String eventID;
    private String title;
    private String desc;
    private String date;
    private String time;
    private String email;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

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

                //TODO: update to query host objects
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
            case R.id.checkIn_button:
                Intent checkInIntent = new Intent(this, QRScanFragment.class);
                checkInIntent.putExtra("eventId", eventID);
                startActivity(checkInIntent);
                break;
            case R.id.checkIn_list_button:
                Intent checkInListIntent = new Intent(this, CheckInListActivity.class);
                checkInListIntent.putExtra("eventId", eventID);
                startActivity(checkInListIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
