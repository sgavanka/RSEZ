package app.rsez;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;

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
    Button inviteButton;
    Button editButton;
    Event event;
    FirebaseAuth mAuth;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        final String eventID = this.getArguments().getString("id");
        final Event event = new Event(eventID);
        eventName = (TextView) view.findViewById(R.id.event_name_view);
        eventDesc = (TextView) view.findViewById(R.id.event_description_view);
        eventDate = (TextView) view.findViewById(R.id.event_date_view);
        eventTime = (TextView) view.findViewById(R.id.event_time_view);
        eventEmail = (TextView) view.findViewById(R.id.event_email_view);
        inviteButton = (Button) view.findViewById(R.id.inviteButton);
        editButton = (Button) view.findViewById(R.id.editButton);
        view.findViewById(R.id.editButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( getActivity(), EditFragment.class);
                EventDetailsFragment.this.event = new Event(eventID, title, desc, date, time, email);

                Gson gson = new Gson();
                String obj = gson.toJson(EventDetailsFragment.this.event);
                intent.putExtra("Id", eventID);
                intent.putExtra("Title", title);
                intent.putExtra("Description", desc);
                intent.putExtra("Date", date);
                intent.putExtra("Time", time);
                intent.putExtra("Email", email);


                getActivity().startActivity(intent);
            }
        });

        event.read(eventID, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                //System.out.println(doc);
                title = doc.getString("title");
                desc = doc.getString("description");
                date = doc.getString("startDate");
                time = doc.getString("startTime");
                email = doc.getString("hostEmail");
                if (currentUser.getEmail().equals(doc.getString("hostEmail"))) {
                    inviteButton.setVisibility(View.VISIBLE);
                    editButton.setVisibility(View.VISIBLE);
                }
                eventName.setText(title);
                eventDesc.setText("Description: " + desc);
                eventDate.setText("Date: " + date);
                eventTime.setText("Time: " + time);
                eventEmail.setText("Host email: " + email);
                updated = true;
            }
        });
        return view;
    }

    public void setFragment(Fragment fragment) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.detach(fragment).attach(fragment).commit();
    }
}
