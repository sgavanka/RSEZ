package app.rsez;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;

import app.rsez.models.Event;

public class EventDetailsFragment extends Fragment {
    public static EventDetailsFragment newInstance() {
        return new EventDetailsFragment();
    }
    public static EventDetailsFragment newInstance(String id) {
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        EventDetailsFragment frag = new EventDetailsFragment();
        frag.setArguments(bundle);
        return frag;
    }
    private FirebaseAuth mAuth;
    static String title;
    static String desc;
    static String date;
    static String time;
    Boolean updated = false;
    TextView eventName;
    TextView eventDesc;
    TextView eventDate;
    TextView eventTime;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_event_details, container, false);
        mAuth = FirebaseAuth.getInstance();
        String eventID = this.getArguments().getString("id");
        Event event = new Event(eventID);
        eventName = (TextView) view.findViewById(R.id.event_name_view);
        eventDesc = (TextView) view.findViewById(R.id.event_description_view);
        eventDate = (TextView) view.findViewById(R.id.event_date_view);
        eventTime = (TextView) view.findViewById(R.id.event_time_view);

        event.read(eventID, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                //System.out.println(doc);
                title = doc.getString("title");
                desc = "Description: " + doc.getString("description");
                date = "Date: " + doc.getString("startDate");
                time = "Start Time: " + doc.getString("startTime");
                eventName.setText(title);
                eventDesc.setText(desc);
                eventDate.setText(date);
                eventTime.setText(time);
                updated = true;
            }
        });

        return view;
    }

    public String convertMonth(int month){
        if (month == 1){
            return "Jan";
        } else if (month == 2){
            return "Feb";
        } else if (month == 3){
            return "Mar";
        } else if (month == 4){
            return "Apr";
        } else if (month == 5){
            return "May";
        } else if (month == 6){
            return "Jun";
        } else if (month == 7){
            return "Jul";
        } else if (month == 8){
            return "Aug";
        } else if (month == 9){
            return "Sep";
        } else if (month == 10){
            return "Oct";
        } else if (month == 11){
            return "Nov";
        } else {
            return "Dec";
        }
    }
}
