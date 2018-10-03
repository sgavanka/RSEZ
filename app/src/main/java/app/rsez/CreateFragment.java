package app.rsez;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import static android.support.constraint.Constraints.TAG;

public class CreateFragment extends Fragment {
    public static CreateFragment newInstance() {
        return new CreateFragment();
    }
    private DatePickerDialog.OnDateSetListener mDateSetListner;
    private static TextView mDisplayDate;
    private TextView chooseTime;
    private TimePickerDialog timePickerDialog;
    Button btn;
    private String amPm;
    private int currentHour;
    private int currentMinute;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_create, container, false);

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
                dialog.show();

                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener()
                {
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
                month = month +1;
                 Log.d(TAG, "onDataSet: date:" + month + "/" + dayOfMonth + "/" + year );
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
                        if (hourOfDay >=12) {
                            amPm = "PM";
                        }
                        else {
                            amPm = "AM";
                        }
                        chooseTime.setText(String.format("%02d:%02d", hourOfDay, minutes)+ amPm);

                    }
                }, currentHour, currentMinute, false);
                    timePickerDialog.show();
            }
        });


        return view;

    }
}
