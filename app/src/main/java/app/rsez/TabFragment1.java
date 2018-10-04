package app.rsez;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import app.rsez.models.Event;

public class TabFragment1 extends Fragment implements View.OnClickListener {
    private static final String TAG = "TabFragment1";
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static FirebaseUser user = mAuth.getCurrentUser();
    private Context context;
    private int numTextViews;
    private List<String> ids = new ArrayList<>();
    protected static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private LinearLayout mLinearLayout;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_tab1, container, false);
       System.out.println("FragmentTab");


        List<TextView> text = new ArrayList<>();
        //System.out.println("size " + list.size());
        TextView temp;
        mLinearLayout = view.findViewById(R.id.linear);
        query();

        /*for(Event event : q){
            System.out.println("one");
            String name = event.getTitle();
            String description = event.getDescription();
            String date = event.getStartDate();
            String time = event.getStartTime();
            System.out.println(name);

            String combined = "Name: " + name + " Date: " + " Time: " + time;

            temp = new TextView(view.getContext());
            temp.setText(combined);
            mLinearLayout.addView(temp);

        }*/

       return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public List<Event> query() {
        System.out.println("in query");
       final List<Event> list = new ArrayList<>();
        db.collection("events")
                .whereEqualTo("hostEmail", user.getEmail())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            System.out.println("query failed");
                            return;
                        }


                        for (QueryDocumentSnapshot doc : value) {
                            if (doc.get("title") != null) {
                                Event ev = new Event(doc.getId(), doc.getString("title")
                                        ,doc.getString("description"),
                                        doc.getString("startDate"),
                                        doc.getString("startTime"),
                                        doc.getString("hostEmail"));
                                System.out.println(doc.getString("hostEmail"));
                                //System.out.println("size " + list.size());
                                list.add(ev);
                                System.out.println(" added size " + list.size());
                            }
                        }
                        TextView temp;

                        for(Event event : list){
                            System.out.println("one");
                            final String id = event.getDocumentId();
                            String name = event.getTitle();
                            String description = event.getDescription();
                            String date = event.getStartDate();
                            String time = event.getStartTime();
                            System.out.println(name);

                            String combined = "Name: " + name + "\n" + " Date: " + date + "     Time: " + time;

                            temp = new TextView(context);
                            temp.setText(combined);
                            temp.setTextSize(20);
                            temp.setTextColor(Color.BLACK);
                            temp.setPadding(0,0,0, 10);
                            temp.setClickable(true);
                            temp.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(getContext(), id, Toast.LENGTH_SHORT).show();
//                                    Fragment fragment;
//                                    fragment = EventDetailsFragment.newInstance(id);
//                                    FragmentManager fragmentManager = getChildFragmentManager();
//                                    if(fragment!=null) {
//
//                                        fragmentManager.beginTransaction().replace(R.id.frag_frame, fragment).commit();
//                                    }
                                }
                            });
                            //temp.setTag(id);
                            ids.add(id);
                            mLinearLayout.addView(temp);

                        }

                        Log.d(TAG, "Your Events: " + list);

                    }
                });

        return list;

    }

    @Override
    public void onClick(View v) {
        System.out.print("Clicked");
        String tag = (String) v.getTag();
        for(String tags : ids){
            if(tag.equals(tags)) {
                Toast.makeText(getContext(), tags,
                        Toast.LENGTH_SHORT).show();
            }
        }

    }
}
