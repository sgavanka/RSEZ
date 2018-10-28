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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
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

public class HostingTabFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "HostingTabFragment";
    private static FirebaseAuth mAuth;
    private static FirebaseUser user;
    private Context context;
    private int numTextViews;
    private List<String> ids = new ArrayList<>();
    protected static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private LinearLayout mLinearLayout;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.events_tab_fragment, container, false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        mLinearLayout = view.findViewById(R.id.linear);

        final SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.swipe_refresh_layout);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                query();
                pullToRefresh.setRefreshing(false);
            }
        });

        query();

       return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public void query() {
        db.collection("hosts").whereEqualTo("userId", user.getEmail()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                mLinearLayout.removeAllViews();

                for (QueryDocumentSnapshot doc : value) {
                    if (doc.get("eventId") != null) {
                        DocumentReference docRef = db.collection("events").document(doc.get("eventId").toString());
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot docSnap = task.getResult();
                                    Event event = new Event(docSnap.getId(), docSnap.getString("title"), docSnap.getString("description"), docSnap.getString("startDate"), docSnap.getString("startTime"), docSnap.getString("hostEmail"));

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
                                            intent.putExtra("isHost", true);
                                            startActivity(intent);
                                        }
                                    });

                                    ids.add(id);
                                    mLinearLayout.addView(child);
                                }
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
        for(String tags : ids){
            if(tag.equals(tags)) {
                Toast.makeText(getContext(), tags,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

}
