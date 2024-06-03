package com.example.schedulingapp.UserSide;

import static com.example.schedulingapp.FirebaseUtils.logout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedulingapp.Appointment;
import com.example.schedulingapp.AppointmentAdapter;
import com.example.schedulingapp.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private TextView textViewGreeting;
    private RecyclerView recyclerViewAppointments;
    private Button buttonScheduleAppointment;
    private Button logoutBtn;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize views
        logoutBtn = view.findViewById(R.id.logoutBtn);
        textViewGreeting = view.findViewById(R.id.textView_greeting);
        recyclerViewAppointments = view.findViewById(R.id.recyclerView_appointments);
        buttonScheduleAppointment = view.findViewById(R.id.button_schedule_appointment);

        // Set greeting message
        String username = "Username";
        textViewGreeting.setText("Hello, " + username + "!");

        // Create appointment data
        List<Appointment> appointmentList = new ArrayList<>();
        // Add more appointments if needed

        // Set up RecyclerView and Adapter
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerViewAppointments.setLayoutManager(layoutManager);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout(getContext());
            }
        });

        // Set click listener for the "Schedule Appointment" button
        buttonScheduleAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToScheduleAppointmentFragment();
            }
        });
        return view;
    }

    // Method to navigate to the ScheduleAppointmentFragment
    private void navigateToScheduleAppointmentFragment() {
    }

    @Override
    public void onClick(View view) {

    }
}
