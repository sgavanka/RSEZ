package app.rsez;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import app.rsez.models.Event;
import app.rsez.models.User;

public class TabFragment2 extends Fragment implements View.OnClickListener  {
    private static final String TAG = "TabFragment2";
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
    //make db query to get user event list
    public void query() {
        System.out.println("in tab2 query");
        final List<Event> list = null;
        final User[] user = {null};

        final DocumentReference docRef = db.collection("users").document(this.user.getEmail());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<String> list = (List<String>) documentSnapshot.get("EventList");
                System.out.println("LIST SIZE: " + list.size());
                //System.out.println("LIST CONTENTS " + list.get(0));
                if(list.size() != 0)
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
            });
        }

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
