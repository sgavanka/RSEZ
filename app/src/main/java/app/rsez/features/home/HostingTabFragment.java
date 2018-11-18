package app.rsez.features.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;

import app.rsez.R;
import app.rsez.features.events.EventDetailsActivity;
import app.rsez.models.Event;

public class HostingTabFragment extends Fragment {
    private static final String TAG = "HostingTabFragment";

    private LinearLayout eventsContainer;
    SwipeRefreshLayout pullToRefresh;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.events_tab_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        eventsContainer = view.findViewById(R.id.events_container);

        (pullToRefresh = view.findViewById(R.id.swipe_refresh_layout)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                eventsQuery();
            }
        });

        eventsQuery();
    }

    public void eventsQuery() {
        pullToRefresh.setRefreshing(true);
        eventsContainer.animate().alpha(0).setInterpolator(new DecelerateInterpolator()).start();

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("hosts").whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getEmail()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    pullToRefresh.setRefreshing(false);
                    return;
                }

                eventsContainer.removeAllViews();
                for (QueryDocumentSnapshot doc : value) {
                    System.out.println("Inside Hosting loop");
                    if (doc.get("eventId") != null) {
                        DocumentReference docRef = db.collection("events").document(doc.get("eventId").toString());
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot docSnap = task.getResult();

                                    Event event = new Event(docSnap.getId(), docSnap.getString("title"), docSnap.getString("description"), (Date) docSnap.get("date"), docSnap.getString("timezone"), docSnap.getString("hostEmail"));
                                    if (event.getEventDate() != null) {
                                        View eventView = getLayoutInflater().inflate(R.layout.list_view_event_info, null);

                                        final String id = event.getDocumentId();
                                        String name = event.getTitle();
                                        String description = event.getDescription();

                                        Date date = (Date) docSnap.get("date");
                                        String dateString = date.toString();
                                        String[] dateSplit = dateString.split(" ");
                                        String actualDate = dateSplit[1] + " " + dateSplit[2];

                                        String stringTime = dateSplit[3];
                                        String[] timeSplit = stringTime.split(":");
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

                                        hour = String.valueOf(hours);
                                        String timeString = hour + ":" + timeSplit[1] + " " + amPM;

                                        ((TextView) eventView.findViewById(R.id.title)).setText(name);
                                        ((TextView) eventView.findViewById(R.id.description)).setText(description);
                                        ((TextView) eventView.findViewById(R.id.date)).setText(actualDate);
                                        ((TextView) eventView.findViewById(R.id.time)).setText(timeString);

                                        eventView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(getActivity(), EventDetailsActivity.class);
                                                intent.putExtra("eventID", id);
                                                intent.putExtra("isHost", true);
                                                startActivity(intent);
                                            }
                                        });

                                        eventsContainer.addView(eventView);
                                    }
                                }

                                pullToRefresh.setRefreshing(false);
                                eventsContainer.animate().alpha(1).setInterpolator(new DecelerateInterpolator()).start();
                            }
                        });
                    }
                }
            }
        });
    }
}
