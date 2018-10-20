package app.rsez.features.events;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

import app.rsez.R;
import app.rsez.features.home.HomeActivity;
import app.rsez.models.Event;
import app.rsez.models.Host;
import app.rsez.utils.FirebaseUtils;

import static android.support.constraint.Constraints.TAG;

public class EventEditFragment extends AppCompatActivity implements View.OnClickListener {

    private DatePickerDialog.OnDateSetListener mDateSetListner;
    private static TextView mDisplayDate;
    private TextView chooseTime;
    private TextView mEventName;
    private TextView mDescription;
    private TextView mEmail;
    private FirebaseAuth mAuth;
    private Event event;

    private TimePickerDialog timePickerDialog;
    private String amPm;
    private int currentHour;
    private int currentMinute;
    private String docID;
    private Event mainEvent;
    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_edit);

        context = this;
        mAuth = FirebaseAuth.getInstance();
        mEventName = findViewById(R.id.eventEdit);
        mDescription = findViewById(R.id.descriptionEdit);
        mDisplayDate = (TextView) findViewById(R.id.dateEdit);
        chooseTime = findViewById(R.id.timeEdit);
        docID = getIntent().getStringExtra("Id");
        findViewById(R.id.editButton).setOnClickListener(this);
        mEmail = findViewById(R.id.hostEditText);
        findViewById(R.id.addHostButton).setOnClickListener(this);

        mEventName.setText(getIntent().getStringExtra("Title"));
        mDescription.setText(getIntent().getStringExtra("Description"));
        mDisplayDate.setText(getIntent().getStringExtra("Date"));
        chooseTime.setText(getIntent().getStringExtra("Time"));


        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(EventEditFragment.this, android.R.style.Theme_Holo_Light, mDateSetListner, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                dialog.getDatePicker().setMinDate(cal.getTimeInMillis());
                dialog.show();
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

        mDateSetListner = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                Log.d(TAG, "onDataSet: date:" + month + "/" + dayOfMonth + "/" + year);
                String date = month + "/" + dayOfMonth + "/" + year;
                mDisplayDate.setText(date);
            }
        };

        chooseTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                currentMinute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(EventEditFragment.this, new TimePickerDialog.OnTimeSetListener() {
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
        if (validateForm()) {
            String email = mAuth.getCurrentUser().getEmail();
            event = new Event(email, name, description, date, time, email);
            event.writeId(docID);

            Intent myIntent = new Intent(EventEditFragment.this, HomeActivity.class);
            startActivity(myIntent);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.editButton) {
            String event = mEventName.getText().toString();
            String date = mDisplayDate.getText().toString();
            String time = chooseTime.getText().toString();
            String description = mDescription.getText().toString();

            eventIn(event, description, date, time);
        } else if (v.getId() == R.id.addHostButton) {
            String host = mEmail.getText().toString();
            String id = FirebaseUtils.generateDocumentId();
            Host newHost = new Host(id, host, getIntent().getStringExtra("Id"));
            newHost.write(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    onBackPressed();
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "ERROR: Host not added", Toast.LENGTH_SHORT);
                }
            });
        }
    }
}