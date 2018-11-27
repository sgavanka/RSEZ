package app.rsez.features.events;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

import app.rsez.R;
import app.rsez.models.Event;
import app.rsez.models.Host;
import app.rsez.models.QRCode;
import app.rsez.models.Ticket;

public class EventDetailsActivity extends AppCompatActivity {
    private static final String TAG = "EventDetails";

    private String eventID;
    private String title;
    private String description;
    private Date date;
    private String email;
    private boolean isHost;

    protected static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private ImageView qrcodeView;

    private LinearLayout guestsInformation;
    private TextView guestsHeader;
    private LinearLayout guestsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_back);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        guestsInformation = findViewById(R.id.guests_information);
        guestsContainer = findViewById(R.id.guests_container);

        eventID = getIntent().getStringExtra("eventID");
        isHost = getIntent().getBooleanExtra("isHost", false);

        guestsHeader = findViewById(R.id.guests_header_text_view);
        qrcodeView = findViewById(R.id.qrcodeView);

        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.swipe_refresh_layout);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
                pullToRefresh.setRefreshing(false);
            }
        });

        findViewById(R.id.guest_add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inviteIntent = new Intent(EventDetailsActivity.this, InviteActivity.class);
                inviteIntent.putExtra("eventID",eventID);
                inviteIntent.putExtra("eventName", title);

                startActivity(inviteIntent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isHost) {
            getMenuInflater().inflate(R.menu.event_details_menu_host, menu);
        } else {
            getMenuInflater().inflate(R.menu.event_details_menu_guest, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.edit_button:
                Intent editIntent = new Intent( this, EventEditActivity.class);
                String[] split = date.toString().split(" ");
                String rawTime = split[3];
                System.out.println("RAWTIME: " + rawTime);
                String[] timeSplit = rawTime.split(":");
                String hour = timeSplit[0];
                String amPM;
                int hours = Integer.parseInt(hour);
                if (hours >= 12 && hours < 24) {
                    amPM = "PM";
                    if (hours - 12 != 0)
                        hours = Integer.parseInt(hour) - 12;
                } else {
                    if (hours == 24 || hours == 0)
                        hours = 12;
                    amPM = "AM";
                }
               String strTime = hours + ":"+ timeSplit[1] + " " + amPM;
                String year = split[5];
                String newDate = split[1] + " " + split[2] + ", " + year + " ";
                editIntent.putExtra("Id", eventID);
                editIntent.putExtra("Title", title);
                editIntent.putExtra("Description", description);
                editIntent.putExtra("Date", newDate);
                editIntent.putExtra("Time",strTime);
                editIntent.putExtra("Email", email);

                startActivity(editIntent);
                break;
            case R.id.checkIn_button:
                Intent checkInIntent = new Intent(this, QRScanFragment.class);
                checkInIntent.putExtra("eventId", eventID);
                startActivity(checkInIntent);
                break;
            case R.id.remove_button:
                Snackbar.make(findViewById(R.id.root), "Delete event?", Snackbar.LENGTH_LONG)
                        .setAction("Yes", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                deleteEvent();
                            }
                        }).show();
                break;
            case R.id.leave_button:
                builder.setMessage("Are you sure you want to leave this event?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == DialogInterface.BUTTON_POSITIVE) {
                                    leaveEvent();
                                }
                            }
                        })
                        .setNegativeButton("No", null).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void refresh() {
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
                date = (Date) document.get("date");
                email = document.getString("hostEmail");

                if (isHost) {
                    guestsQuery();
                    invalidateOptionsMenu();
                } else {
                    try {
                        Bitmap qrcode = QRCode.generateQRCode(EventDetailsActivity.this, eventID + " - " + mUser.getEmail());
                        qrcodeView.setImageBitmap(qrcode);
                        qrcodeView.setVisibility(View.VISIBLE);
                        qrcodeView.setVisibility(View.VISIBLE);
                    } catch (Exception ignored){ }
                }

                titleTextView.setText(title);
                descriptionTextView.setText(description);
                dateTimeTextView.setText(date.toString());
                hostEmailTextView.setText("Hosted by " + email);
            }
        });
    }

    public void guestsQuery() {
        guestsInformation.animate().alpha(0).setInterpolator(new DecelerateInterpolator()).start();

        db.collection("tickets").whereEqualTo("eventId", eventID)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        List<DocumentSnapshot> tickets = value.getDocuments();

                        guestsHeader.setText(tickets.size() + " guest" + (tickets.size() != 1 ? "s" : ""));

                        guestsContainer.removeAllViews();
                        for (int i = 0; i < tickets.size(); i++) {
                            final Ticket ticket = new Ticket(tickets.get(i).getId(), tickets.get(i).getString("eventId"), tickets.get(i).getString("userId"), (Date) tickets.get(i).get("checkInDateTime"));

                            final View view = getLayoutInflater().inflate(R.layout.view_guest_info, null);
                            view.setPadding(0, 20, 0, 0);

                            final TextView guestInfo = view.findViewById(R.id.guest);

                            final String userName = tickets.get(i).getString("userId");
                            guestInfo.setText((i + 1) + ") " + userName);

                            guestsContainer.addView(view);
                            if (!isHost) {
                                continue;
                            }

                            final ImageView checkInButton = view.findViewById(R.id.check_in);
                            if (ticket.getCheckInDateTime() != null) {
                                checkInButton.setColorFilter(Color.rgb(39, 158, 0), PorterDuff.Mode.SRC_ATOP);
                            }

                            checkInButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(final View v) {
                                    if (ticket.getCheckInDateTime() == null) {
                                        checkInButton.setColorFilter(Color.rgb(39, 158, 0), PorterDuff.Mode.SRC_ATOP);
                                        checkIn(ticket, checkInButton);
                                    } else {
                                        checkInButton.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
                                        checkOut(ticket, checkInButton);
                                    }
                                }
                            });

                            view.findViewById(R.id.remove).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(final View v) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(EventDetailsActivity.this);
                                    builder.setMessage("Are you sure you want to remove " + userName + " from the guest list?")
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if (which == DialogInterface.BUTTON_POSITIVE) {
                                                        removeGuest(eventID, userName, v);
                                                    }
                                                }
                                            })
                                            .setNegativeButton("No", null)
                                            .show();
                                }
                            });
                        }

                        guestsInformation.animate().alpha(1).setInterpolator(new DecelerateInterpolator()).start();
                    }
                });
                    }




    private void removeGuest(String eventId, String userId, final View view) {
        CollectionReference colRef = db.collection("tickets");
        Query query = colRef.whereEqualTo("eventId", eventId).whereEqualTo("userId", userId);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    List<DocumentSnapshot> tasks = task.getResult().getDocuments();
                    for(int i = 0; i < tasks.size(); i++) {
                        tasks.get(i).getReference().delete();

                        view.animate().alpha(0).setInterpolator(new DecelerateInterpolator()).withEndAction(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        guestsContainer.removeView(view);
                                    }
                                }
                        ).start();
                    }
                }
            }
        });
    }

    private void checkIn(final Ticket ticket, final ImageView checkInButton) {
        if (ticket.getCheckInDateTime() != null) {
            return;
        }

        final Ticket newTicket = new Ticket(ticket.getDocumentId(), ticket.getEventId(), ticket.getUserId(), Calendar.getInstance().getTime());
        newTicket.write(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Check in successful", Toast.LENGTH_SHORT).show();
                ticket.setCheckInDateTime(newTicket.getCheckInDateTime());
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed to check in properly", Toast.LENGTH_SHORT).show();
                checkInButton.clearColorFilter();
            }
        });
    }

    private void checkOut(final Ticket ticket, final ImageView checkInButton) {
        if (ticket.getCheckInDateTime() == null) {
            return;
        }

        final Ticket newTicket = new Ticket(ticket.getDocumentId(), ticket.getEventId(), ticket.getUserId(), null);
        newTicket.write(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Check out successful", Toast.LENGTH_SHORT).show();
                ticket.setCheckInDateTime(null);
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed to check out properly", Toast.LENGTH_SHORT).show();
                checkInButton.setColorFilter(Color.rgb(39, 158, 0), PorterDuff.Mode.SRC_ATOP);
            }
        });
    }

    public void deleteEvent(){
        CollectionReference tickets = db.collection("tickets");
        CollectionReference hosts = db.collection("hosts");

        Query ticketQuery = tickets.whereEqualTo("eventId", eventID);
        ticketQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> tasks = task.getResult().getDocuments();
                    for(int i = 0; i < tasks.size(); i++){
                        Ticket.delete(tasks.get(i).getId(), new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            }
                        }, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
                    }
                }
            }
        });

        Query hostsQuery = hosts.whereEqualTo("eventId", eventID);
        hostsQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> tasks = task.getResult().getDocuments();
                    for(int i = 0; i < tasks.size(); i++){
                        Host.delete(tasks.get(i).getId(), new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            }
                        }, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
                    }
                }
            }
        });

        Event.delete(eventID, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                onBackPressed();
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    private void leaveEvent() {
        db.collection("tickets").whereEqualTo("eventId", eventID).whereEqualTo("userId", mUser.getEmail()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        List<DocumentSnapshot> tickets = task.getResult().getDocuments();
                        System.out.println("Found " + tickets.size() + " tickets with eventID: " + eventID + " and userId: " + mUser.getEmail());
                        if (tickets.size() != 1){
                            return;
                        }
                        Ticket ticket = new Ticket(tickets.get(0).getId(), tickets.get(0).getString("eventId"), tickets.get(0).getString("userId"), (Date) tickets.get(0).get("checkInDateTime"));
                        Ticket.delete(tickets.get(0).getId(), new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "Successfully left event", Toast.LENGTH_SHORT).show();
                                System.out.println("Successfully left event");
                                onBackPressed();
                            }
                        }, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Could not leave event", Toast.LENGTH_SHORT).show();
                                System.out.println("Failed to leave event");
                            }
                        });
                    }
                });
    }
}