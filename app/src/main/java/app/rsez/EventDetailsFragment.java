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
import com.google.firebase.firestore.DocumentSnapshot;
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
    static String title;
    static String desc;
    static String date;
    static String time;
    static String email;
    Boolean updated = false;
    TextView eventName;
    TextView eventDesc;
    TextView eventDate;
    TextView eventTime;
    TextView eventEmail;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_event_details, container, false);
        String eventID = this.getArguments().getString("id");
        Event event = new Event(eventID);
        eventName = (TextView) view.findViewById(R.id.event_name_view);
        eventDesc = (TextView) view.findViewById(R.id.event_description_view);
        eventDate = (TextView) view.findViewById(R.id.event_date_view);
        eventTime = (TextView) view.findViewById(R.id.event_time_view);
        eventEmail = (TextView) view.findViewById(R.id.event_email_view);

        event.read(eventID, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                //System.out.println(doc);
                title = doc.getString("title");
                desc = "Description: " + doc.getString("description");
                date = "Date: " + doc.getString("startDate");
                time = "Start Time: " + doc.getString("startTime");
                email = "Host's Email: " + doc.getString("hostEmail");
                eventName.setText(title);
                eventDesc.setText(desc);
                eventDate.setText(date);
                eventTime.setText(time);
                eventEmail.setText(email);
                updated = true;
            }
        });

        return view;
    }
}
