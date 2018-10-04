package app.rsez;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.util.Calendar;

import app.rsez.models.Event;

import static android.support.constraint.Constraints.TAG;

public class CreateFragment extends Fragment implements View.OnClickListener {
    public static CreateFragment newInstance() {
        return new CreateFragment();
    }

    private DatePickerDialog.OnDateSetListener mDateSetListner;
    private static TextView mDisplayDate;
    private TextView chooseTime;
    private TextView mEventName;
    private TextView mDescription;
    private FirebaseAuth mAuth;
    private Event event;

    private TimePickerDialog timePickerDialog;
    private String amPm;
    private int currentHour;
    private int currentMinute;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_create, container, false);
        mAuth = FirebaseAuth.getInstance();
        mEventName = view.findViewById(R.id.event);
        mDescription = view.findViewById(R.id.description);
        view.findViewById(R.id.createButton).setOnClickListener(this);
        mDisplayDate = (TextView) view.findViewById(R.id.date);
        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Light, mDateSetListner, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                dialog.getDatePicker().setMinDate(cal.getTimeInMillis());
                dialog.show();
                //  mDisplayDate.setText(String.format("%02d/%02d/%02d", month, day, year));
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_NEGATIVE) {
                            dialog.dismiss();
                        }
                    }
                });

            }
        });

        mDateSetListner = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                Log.d(TAG, "onDataSet: date:" + month + "/" + dayOfMonth + "/" + year);
                String date = month + "/" + dayOfMonth + "/" + year;
                mDisplayDate.setText(date);
            }
        };

        // now do time picker stuff
        chooseTime = view.findViewById(R.id.time);
        chooseTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                currentMinute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                        if (hourOfDay >= 12) {
                            amPm = "PM";

                        } else {
                            amPm = "AM";
                        }
                        if (hourOfDay > 12) {
                            hourOfDay = hourOfDay -12;
                        }
                        chooseTime.setText(String.format("%02d:%02d", hourOfDay, minutes) + amPm);

                    }
                }, currentHour, currentMinute, false);
                timePickerDialog.show();
            }
        });


        return view;

    }

    private boolean validateForm() {
        boolean valid = true;

        String event = mEventName.getText().toString();
        if (TextUtils.isEmpty(event)) {
            mEventName.setError("Required.");
            valid = false;
        } else {
            mEventName.setError(null);
        }

        String desc = mDescription.getText().toString();
        if (TextUtils.isEmpty(desc)) {
            mDescription.setError("Required.");
            valid = false;
        } else {
            mDescription.setError(null);
        }
        String date = mDisplayDate.getText().toString();
        if (TextUtils.isEmpty(date)) {
            mDisplayDate.setError("Required.");
            valid = false;
        } else {
            mDisplayDate.setError(null);
        }
        String time = chooseTime.getText().toString();
        if (TextUtils.isEmpty(time)) {
            chooseTime.setError("Required.");
            valid = false;
        } else {
            chooseTime.setError(null);
        }

        return valid;
    }

    private void eventIn(String name, String description, String date, String time) {
        Log.d(TAG, "Event:" + name);
        if (!validateForm()) {
            return;
        } else {
            FirebaseUser user = mAuth.getCurrentUser();
            String email = mAuth.getCurrentUser().getEmail();
            event = new Event(email, name, description, date, time, email);
            event.write();
            Intent myIntent = new Intent(getActivity(),
                    HomeActivity.class);
            startActivity(myIntent);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.createButton) {
            String event = mEventName.getText().toString();
            String date = mDisplayDate.getText().toString();
            String time = chooseTime.getText().toString();
            String description = mDescription.getText().toString();
            eventIn(event, description, date, time);

            //load main fragment now


        }

    }

}
