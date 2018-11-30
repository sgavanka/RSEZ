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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import app.rsez.R;
import app.rsez.features.events.EventDetailsActivity;
import app.rsez.models.Event;

import static android.support.constraint.Constraints.TAG;

public class AttendingTabFragment extends Fragment {
    private LinearLayout eventsContainer;
    SwipeRefreshLayout pullToRefresh;
    private ArrayList<Event> eventList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.events_tab_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        eventsContainer = view.findViewById(R.id.events_container);
        eventList = new ArrayList<>();
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
            public void onEvent(@Nullable final QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    pullToRefresh.setRefreshing(false);
                    return;
                }
                eventsContainer.removeAllViews();
                eventList.clear();

                for (DocumentSnapshot doc : value) {
                    if (doc.get("eventId") != null) {
                        DocumentReference docRef = db.collection("events").document(doc.get("eventId").toString());
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot docSnap = task.getResult();
                                    Event temp = new Event(docSnap.getId(), docSnap.getString("title"),
                                            docSnap.getString("description"),
                                            (Date) docSnap.get("date"),
                                            docSnap.getString("timezone"),
                                            docSnap.getString("hostEmail"));
                                    System.out.println("Adding event");
                                    eventList.add(temp);


                                }
                                if (eventList.size() == value.size()) {
                                    //Sort events
                                    if (HomeActivity.sortType == 1) {
                                        Collections.sort(eventList, new Comparator<Event>() {
                                            @Override
                                            public int compare(Event o1, Event o2) {
                                                //Sort by name A-Z
                                                return -1 * o1.getTitle().compareToIgnoreCase(o2.getTitle());
                                            }
                                        });
                                    } else if (HomeActivity.sortType == 2) {
                                        Collections.sort(eventList, new Comparator<Event>() {
                                            @Override
                                            public int compare(Event o1, Event o2) {
                                                //Sort by name Z-A
                                                return 1 * o1.getTitle().compareToIgnoreCase(o2.getTitle());
                                            }
                                        });
                                    } else if (HomeActivity.sortType == 3) {
                                        Collections.sort(eventList, new Comparator<Event>() {
                                            @Override
                                            public int compare(Event o1, Event o2) {
                                                //Sort by Date closest to farthest
                                                return -1 * o1.getDate().compareTo(o2.getDate());
                                            }
                                        });
                                    } else if (HomeActivity.sortType == 4) {
                                        Collections.sort(eventList, new Comparator<Event>() {
                                            @Override
                                            public int compare(Event o1, Event o2) {
                                                //Sort by Date farthest to closest
                                                return 1 * o1.getDate().compareTo(o2.getDate());
                                            }
                                        });
                                    }
                                    writeEvents();
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private void writeEvents() {
        eventsContainer.removeAllViews();
        for (Event event : eventList) {
            if (event.getDate() != null) {
                View child = null;
                try {
                    child = getLayoutInflater().inflate(R.layout.list_view_event_info, null);
                }
                catch(IllegalStateException e) {
                    e.getMessage();
                }

                final String id = event.getDocumentId();
                String name = event.getTitle();
                String description = event.getDescription();
                Date date = event.getDate();
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
                Calendar cal = Calendar.getInstance(); // creates calendar
                cal.setTime(new Date()); // sets calendar time/date
                cal.add(Calendar.DAY_OF_MONTH, -1); // adds one day

                if (date.compareTo(cal.getTime()) >= 0 && child != null) {
                    //Event is in the past. Do not add
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
            }

            pullToRefresh.setRefreshing(false);
            eventsContainer.animate().alpha(1).setInterpolator(new DecelerateInterpolator()).start();
        }
    }
}
