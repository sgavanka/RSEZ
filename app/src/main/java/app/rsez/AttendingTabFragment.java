package app.rsez;

import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import app.rsez.features.events.EventDetailsActivity;
import app.rsez.models.Event;

import static android.support.constraint.Constraints.TAG;

public class AttendingTabFragment extends Fragment implements View.OnClickListener  {
    private static FirebaseAuth mAuth;
    private static FirebaseUser user;

    private Context context;

    private List<String> ids = new ArrayList<>();
    protected static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private LinearLayout mLinearLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.events_tab_fragment, container, false);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        System.out.println("FragmentTab: " + user.getEmail());

        mLinearLayout = view.findViewById(R.id.guests_container);

        final SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.swipe_refresh_layout);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                eventQuery();
                pullToRefresh.setRefreshing(false);
            }
        });

        eventQuery();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public void eventQuery() {
        System.out.println("eventQUery");
        db.collection("tickets")
                .whereEqualTo("userId", user.getEmail())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        mLinearLayout.removeAllViews();

                        for (QueryDocumentSnapshot doc : value) {
                            if (doc.get("eventId") != null) {
                                Event.read(doc.getString("eventId"), new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        DocumentSnapshot doc = task.getResult();
                                        Event event = new Event(doc.getId(), doc.getString("title"),
                                                doc.getString("description"),
                                                doc.getString("startDate"),
                                                doc.getString("startTime"),
                                                doc.getString("hostEmail"));

                                        View child = getLayoutInflater().inflate(R.layout.list_view_event_info, null);

                                        final String id = event.getDocumentId();
                                        String name = event.getTitle();
                                        String description = event.getDescription();
                                        String date = event.getStartDate();
                                        String time = event.getStartTime();

                                        try {
                                            DateFormat readFormat = new SimpleDateFormat("MM/dd/yy");
                                            date = new SimpleDateFormat("MMM d", Locale.ENGLISH).format(readFormat.parse(date));
                                        } catch (ParseException e1) {
                                            e1.printStackTrace();
                                        }

                                        ((TextView) child.findViewById(R.id.title)).setText(name);
                                        ((TextView) child.findViewById(R.id.description)).setText(description);
                                        ((TextView) child.findViewById(R.id.date)).setText(date);
                                        ((TextView) child.findViewById(R.id.time)).setText(time);

                                        child.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(getActivity(), EventDetailsActivity.class);
                                                intent.putExtra("eventID", id);
                                                startActivity(intent);
                                            }
                                        });

                                        ids.add(id);
                                        mLinearLayout.addView(child);
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
