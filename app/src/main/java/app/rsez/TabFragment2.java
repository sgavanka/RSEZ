package app.rsez;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import app.rsez.features.events.EventDetailsActivity;
import app.rsez.models.Event;
import app.rsez.models.Ticket;
import app.rsez.models.User;

import static android.support.constraint.Constraints.TAG;

public class TabFragment2 extends Fragment implements View.OnClickListener  {
    private static FirebaseAuth mAuth;
    private static FirebaseUser user;

    private Context context;

    private List<String> ids = new ArrayList<>();
    protected static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private LinearLayout mLinearLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab1, container, false);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        System.out.println("FragmentTab: " + user.getEmail());

        mLinearLayout = view.findViewById(R.id.linear);
        //query();
        eventQuery();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    // Make db query to get user event list
    public void query() {
        final DocumentReference docRef = db.collection("users").document(this.user.getEmail());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<String> list = (List<String>) documentSnapshot.get("EventList");
                queryForEvents(list);
            }
        });
    }

    public void queryForEvents (List<String> events) {
        for(int i = 0; i < events.size(); i++) {
            final DocumentReference docRef = db.collection("events").document(events.get(i));
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                   Event event = new Event(documentSnapshot.getId(), documentSnapshot.getString("title"),
                           documentSnapshot.getString("description"), documentSnapshot.getString("startDate"),
                           documentSnapshot.getString("startTime"), documentSnapshot.getString("hostEmail"));

                    final String id = event.getDocumentId();
                    String name = event.getTitle();
                    String description = event.getDescription();
                    String date = event.getStartDate();
                    String time = event.getStartTime();
                    System.out.println(name);

                    String combined = "Name: " + name + "\n" + "Date: " + date + "     Time: " + time;
                    TextView temp;
                    temp = new TextView(context);
                    temp.setText(combined);
                    temp.setTextSize(20);
                    temp.setTextColor(Color.BLACK);
                    temp.setPadding(10,0,0, 20);
                    temp.setClickable(true);
                    temp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), EventDetailsActivity.class);
                            intent.putExtra("eventID", id);
                            intent.putExtra("isHost", false);
                            startActivity(intent);
                        }
                    });

                    ids.add(id);
                    mLinearLayout.addView(temp);
                }
            });
        }
    }
    public void eventQuery() {
        System.out.println("eventQUery");
        final List<Event> list = new ArrayList<>();
        db.collection("tickets")
                .whereEqualTo("userId", user.getEmail())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        System.out.println("Inside onEvent");
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            System.out.println("query failed");
                            return;
                        }

                        for (QueryDocumentSnapshot doc : value) {
                            System.out.println("looping snapshots");
                            if (doc.get("eventId") != null) {
                                System.out.println("READING FOR EVENTS1");
                                Event.read(doc.getString("eventId"), new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        System.out.println("READING FOR EVENTS");
                                        DocumentSnapshot doc = task.getResult();
                                        Event event = new Event(doc.getId(), doc.getString("title")
                                                ,doc.getString("description"),
                                                doc.getString("startDate"),
                                                doc.getString("startTime"),
                                                doc.getString("hostEmail"));

                                        TextView temp;
                                        final String id = event.getDocumentId();
                                        System.out.println("INSIDE MAKING TExt view for ticket");
                                        System.out.println("Title: " + event.getTitle());
                                        String name = event.getTitle();
                                        String description = event.getDescription();
                                        String date = event.getStartDate();
                                        String time = event.getStartTime();

                                        String combined = "Name: " + name + "\n" + "Date: " + date + "     Time: " + time;

                                        temp = new TextView(context);
                                        temp.setText(combined);
                                        temp.setTextSize(20);
                                        temp.setTextColor(Color.BLACK);
                                        temp.setPadding(10,0,0, 20);
                                        temp.setClickable(true);
                                        temp.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                //Toast.makeText(getContext(), id, Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getActivity(), EventDetailsActivity.class);
                                                intent.putExtra("eventID", id);
                                                startActivity(intent);

                                            }

                                        });

                                        ids.add(id);
                                        mLinearLayout.addView(temp);
                                    }
                                });

                            }
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        String tag = (String) v.getTag();
        for (String tags : ids){
            if (tag.equals(tags)) {
                Toast.makeText(getContext(), tags, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
