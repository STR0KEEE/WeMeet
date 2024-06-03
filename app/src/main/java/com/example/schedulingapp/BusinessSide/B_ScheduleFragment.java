package com.example.schedulingapp.BusinessSide;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.schedulingapp.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class B_ScheduleFragment extends Fragment {

    private TextView dateTextView;
    private TextView timeTextView;
    private TextView durationTextView;
    private Button submitButton;

    private Calendar selectedDate;
    private Calendar selectedTime;
    private int selectedDuration;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.b_fragment_schedule, container, false);

        dateTextView = view.findViewById(R.id.dateTextView);
        timeTextView = view.findViewById(R.id.timeTextView);
        durationTextView = view.findViewById(R.id.durationTextView);
        submitButton = view.findViewById(R.id.submitButton);

        dateTextView.setOnClickListener(this::showDatePickerDialog);
        timeTextView.setOnClickListener(this::showTimePickerDialog);
        durationTextView.setOnClickListener(this::showDurationPickerDialog);

        submitButton.setOnClickListener(this::submitAppointment);

        return view;
    }

    private void showDatePickerDialog(View view) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (datePicker, year, month, dayOfMonth) -> {
                    selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, dayOfMonth);
                    dateTextView.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void showTimePickerDialog(View view) {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                (timePicker, hourOfDay, minute) -> {
                    selectedTime = Calendar.getInstance();
                    selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedTime.set(Calendar.MINUTE, minute);
                    timeTextView.setText(hourOfDay + ":" + String.format("%02d", minute));
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true);
        timePickerDialog.show();
    }

    private void showDurationPickerDialog(View view) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.duration_picker_dialog, null);
        NumberPicker hourPicker = dialogView.findViewById(R.id.hourPicker);
        NumberPicker minutePicker = dialogView.findViewById(R.id.minutePicker);

        // Set values programmatically for hourPicker
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(12);
        hourPicker.setFormatter(value -> value + " hr");
        hourPicker.setWrapSelectorWheel(true);

        // Set values programmatically for minutePicker
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(11);
        minutePicker.setDisplayedValues(new String[]{"0 min", "5 min", "10 min", "15 min", "20 min", "25 min", "30 min", "35 min", "40 min", "45 min", "50 min", "55 min"});
        minutePicker.setWrapSelectorWheel(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Duration");
        builder.setView(dialogView);
        builder.setPositiveButton("OK", (dialog, which) -> {
            int selectedHours = hourPicker.getValue();
            int selectedMinutes = minutePicker.getValue() * 5;
            selectedDuration = selectedHours * 60 + selectedMinutes;
            durationTextView.setText(selectedHours + " hr " + selectedMinutes + " min");
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void submitAppointment(View view) {
        if (selectedDate == null || selectedTime == null || selectedDuration == 0) {
            Toast.makeText(getContext(), "Please select date, time, and duration", Toast.LENGTH_SHORT).show();
            return;
        }

        // Combine selected date and time into a single Calendar instance
        selectedDate.set(Calendar.HOUR_OF_DAY, selectedTime.get(Calendar.HOUR_OF_DAY));
        selectedDate.set(Calendar.MINUTE, selectedTime.get(Calendar.MINUTE));

        // Retrieve business ID from SharedPreferences
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MyAppPreferences", getContext().MODE_PRIVATE);
        String businessId = sharedPreferences.getString("BUSINESS_ID", null);

        if (businessId == null) {
            Toast.makeText(getContext(), "Business ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert duration to string
        int hours = selectedDuration / 60;
        int minutes = selectedDuration % 60;
        String durationString = hours + " hr " + minutes + " min";

        // Prepare appointment data
        Map<String, Object> appointmentData = new HashMap<>();
        appointmentData.put("date", selectedDate.getTime());
        appointmentData.put("duration", durationString);  // Store duration as string
        appointmentData.put("isAvailable", true);
        appointmentData.put("businessId", businessId); // Add business ID to the data

        // Save appointment to Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("appointments").add(appointmentData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Appointment scheduled successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to schedule appointment", Toast.LENGTH_SHORT).show();
                });
    }
}
