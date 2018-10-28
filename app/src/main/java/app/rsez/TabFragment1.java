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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import app.rsez.features.events.EventDetailsActivity;
import app.rsez.models.Event;

public class TabFragment1 extends Fragment implements View.OnClickListener {
    private static final String TAG = "TabFragment1";
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
       View view = inflater.inflate(R.layout.fragment_tab1, container, false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        System.out.println("FragmentTab: " + user.getEmail());



        List<TextView> text = new ArrayList<>();
        //System.out.println("size " + list.size());
        TextView temp;
        mLinearLayout = view.findViewById(R.id.linear);
        query();

       return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public void query() {
        //System.out.println("in query");
        final List<Event> list = new ArrayList<>();
        db.collection("hosts").whereEqualTo("userId", user.getEmail()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    //System.out.println("query failed");
                    return;
                }

                for (QueryDocumentSnapshot doc : value) {
                    if (doc.get("eventId") != null) {
                        //System.out.println("ID: |" + doc.get("eventId").toString() + "|");
                        //Create reference to specific event
                        DocumentReference docRef = db.collection("events").document(doc.get("eventId").toString());
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot docSnap = task.getResult();
                                    Event ev = new Event(docSnap.getId(), docSnap.getString("title"), docSnap.getString("description"), docSnap.getString("startDate"), docSnap.getString("startTime"), docSnap.getString("hostEmail"));
                                    System.out.println("Found hosted event: " + docSnap.getId() + " " + docSnap.getString("title"));
                                    list.add(ev);


                                    TextView temp;
                                    final String id = ev.getDocumentId();
                                    String name = ev.getTitle();
                                    String description = ev.getDescription();
                                    String date = ev.getStartDate();
                                    String time = ev.getStartTime();
                                    //System.out.println(name);

                                    String combined = "Name: " + name + "\n" + "Date: " + date + "     Time: " + time;

                                    temp = new TextView(context);
                                    temp.setText(combined);
                                    temp.setTextSize(20);
                                    temp.setTextColor(Color.BLACK);
                                    temp.setPadding(10, 0, 0, 20);
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
                                    //temp.setTag(id);
                                    ids.add(id);
                                    mLinearLayout.addView(temp);
                                }
                            }
                        });
                    }
                }

            }
        });
        return;

    }

    @Override
    public void onClick(View v) {
        //System.out.print("Clicked");
        String tag = (String) v.getTag();
        for(String tags : ids){
            if(tag.equals(tags)) {
                Toast.makeText(getContext(), tags,
                        Toast.LENGTH_SHORT).show();
            }
        }

    }
}
