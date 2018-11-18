package app.rsez;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;

import app.rsez.features.events.EventDetailsActivity;
import app.rsez.models.Event;

import static android.support.constraint.Constraints.TAG;

public class AttendingTabFragment extends Fragment  {
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
                pullToRefresh.setRefreshing(false);
            }
        });

        eventsQuery();
    }

    public void eventsQuery() {
        pullToRefresh.setRefreshing(true);
        eventsContainer.animate().alpha(0).setInterpolator(new DecelerateInterpolator()).start();

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("tickets").whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getEmail()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        eventsContainer.removeAllViews();

                        for (QueryDocumentSnapshot doc : value) {
                            if (doc.get("eventId") != null) {
                                Event.read(doc.getString("eventId"), new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        DocumentSnapshot doc = task.getResult();
                                        Event event = new Event(doc.getId(), doc.getString("title"),
                                                doc.getString("description"),
                                                (Date) doc.get("date"),
                                                doc.getString("timezone"),
                                                doc.getString("hostEmail"));
                                        if(event.getEventDate() != null) {

                                            View child = getLayoutInflater().inflate(R.layout.list_view_event_info, null);

                                            final String id = event.getDocumentId();
                                            String name = event.getTitle();
                                            String description = event.getDescription();
                                            Date date = event.getEventDate();
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
                                                if (hours - 12 != 0) {
                                                    hours = Integer.parseInt(hour) - 12;
                                                }
                                            } else {
                                                amPM = "AM";
                                                if (hours == 24 || hours == 0) {
                                                    hours = 12;
                                                }
                                            }

                                            hour = String.valueOf(hours);
                                            String timeString = hour + ":" + timeSplit[1] + " " + amPM;

                                            ((TextView) child.findViewById(R.id.title)).setText(name);
                                            ((TextView) child.findViewById(R.id.description)).setText(description);
                                            ((TextView) child.findViewById(R.id.date)).setText(actualDate);
                                            ((TextView) child.findViewById(R.id.time)).setText(timeString);

                                            child.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(getActivity(), EventDetailsActivity.class);
                                                    intent.putExtra("eventID", id);
                                                    startActivity(intent);
                                                }
                                            });

                                            eventsContainer.addView(child);
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
