package app.rsez.features.events;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import app.rsez.QRScanFragment;
import app.rsez.R;
import app.rsez.models.Event;

public class EventDetailsActivity extends AppCompatActivity {
    private boolean userIsEventOwner = false;
    protected static FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String eventID;
    private String title;
    private String description;
    private String date;
    private String time;
    private String email;
    private boolean isHost;

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
        isHost = getIntent().getBooleanExtra("isHost", false);

        Event.read(eventID, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                TextView titleTextView = findViewById(R.id.title);
                TextView descriptionTextView = findViewById(R.id.description);
                TextView dateTimeTextView = findViewById(R.id.date_time);
                TextView hostEmailTextView = findViewById(R.id.host_email);

                DocumentSnapshot document = task.getResult();

                title = document.getString("title");
                description = document.getString("description");
                date = document.getString("startDate");
                time = document.getString("startTime");
                email = document.getString("hostEmail");

                try {
                    DateFormat readFormat = new SimpleDateFormat("MM/dd/yy");
                    date = new SimpleDateFormat("MMMM d, YYYY", Locale.ENGLISH).format(readFormat.parse(date));
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }

                if (isHost) {
                    invalidateOptionsMenu();
                }

                titleTextView.setText(title);
                descriptionTextView.setText(description);
                dateTimeTextView.setText(date + " at " + time);
                hostEmailTextView.setText("Hosted by " + email);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isHost) {
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
                editIntent.putExtra("Description", description);
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
