package app.rsez.features.events;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.rsez.QRScanFragment;
import app.rsez.R;
import app.rsez.models.Event;
import app.rsez.models.QRCode;

public class EventDetailsActivity extends AppCompatActivity {
    private static final String TAG = "EventDetails";
    private boolean userIsEventOwner = false;

    private String eventID;
    private String title;
    private String description;
    //private String date;
    //private String time;
    private Date date;
    private String email;
    private boolean isHost;
    private Context context;

    protected static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String selected = null;
    private String user = null;
    private View tempView = null;
    private TextView guests;
    private ImageView qrcodeView;

    private LinearLayout mLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        mLinearLayout = this.findViewById(R.id.guests_container);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_back);

        context = this;
        eventID = getIntent().getStringExtra("eventID");
        isHost = getIntent().getBooleanExtra("isHost", false);
        guests = findViewById(R.id.guests);
        qrcodeView = findViewById(R.id.qrcodeView);

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
                //date = document.getString("startDate");
                //time = document.getString("startTime");
                date = (Date) document.get("date");
                email = document.getString("hostEmail");

                /*try {
                    DateFormat readFormat = new SimpleDateFormat("MM/dd/yy");
                    date = new SimpleDateFormat("MMMM d, YYYY", Locale.ENGLISH).format(readFormat.parse(date));
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }*/

                if (isHost) {
                    guests.setVisibility(View.VISIBLE);
                    ticketQuery(eventID, context);
                    invalidateOptionsMenu();
                } else {
                    //Show QR Code

                    try{
                        Bitmap qrcode = QRCode.generateQRCode(context, eventID + " - " + mUser.getEmail());
                        qrcodeView.setImageBitmap(qrcode);
                        qrcodeView.setVisibility(View.VISIBLE);
                        qrcodeView.setVisibility(View.VISIBLE);
                    } catch (Exception e){

                    }
                }

                titleTextView.setText(title);
                descriptionTextView.setText(description);
                dateTimeTextView.setText(date.toString());
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
                editIntent.putExtra("Date", date.toString());
                //editIntent.putExtra("Time", time);
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

    public void ticketQuery(String eventID, final Context context) {
        CollectionReference colRef = db.collection("tickets");
        Query query = colRef.whereEqualTo("eventId", eventID);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    System.out.println("task is succesfull");
                    List<DocumentSnapshot> tasks = task.getResult().getDocuments();
                    System.out.println("tasks size: "+ tasks.size());
                    for(int i = 0; i < tasks.size(); i++) {
                        final TextView temp = new TextView(context);
                        String userName = tasks.get(i).getString("userId");
                        temp.setText(userName);
                        temp.setTextSize(15);
                        temp.setTextColor(Color.BLACK);
                        temp.setPadding(10,0,0, 20);
                        temp.setClickable(true);
                        mLinearLayout.addView(temp);
                        temp.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(userIsEventOwner) {
                                    selected = temp.getText().toString();
                                    user = temp.getText().toString();
                                    tempView = v;
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setMessage("Remove User?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
                                }
                            }
                        });

                    }
                }
            }
        });
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    if(selected != null) {
                        removeGuest(eventID, user, tempView);
                    }

                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        }
    };

    public void removeGuest(String eventId, String user, final View view) {
        CollectionReference colRef = db.collection("tickets");
        Query query = colRef.whereEqualTo("eventId", eventId).whereEqualTo("userId", user);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    List<DocumentSnapshot> tasks = task.getResult().getDocuments();
                    for(int i = 0; i < tasks.size(); i++) {
                        tasks.get(i).getReference().delete();
                        mLinearLayout.removeView(view);

                    }
                }
            }
        });
    }
}
