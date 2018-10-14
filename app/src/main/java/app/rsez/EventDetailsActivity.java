package app.rsez;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
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

public class EventDetailsActivity extends AppCompatActivity implements View.OnClickListener {
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
    DrawerLayout mDrawerLayout;
    Event event;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        /*
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        */
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
        /*
        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true;
                    }
                });
        setupDrawerContent(navigationView);
        */
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }


    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass = null;
        String args = "";
        switch (menuItem.getItemId()) {
            case R.id.nav_signOut:
                mAuth.signOut();
                finish();

                Intent myIntent = new Intent(EventDetailsActivity.this, MainActivity.class);
                startActivity(myIntent);
                break;
            case R.id.nav_event:
                fragmentClass = CreateFragment.class;
                break;

            case R.id.nav_home:
                //fragmentClass = TabsFragment.class;
                Intent homeIntent = new Intent(this, HomeActivity.class);
                startActivity(homeIntent);
                break;
            default:
                fragmentClass = TabsFragment.class;
                break;
        }
        try {
            if (args == ""){
                fragment = (Fragment) fragmentClass.newInstance();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(fragment!=null) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        }

    }
    public void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }
}
