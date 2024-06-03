package com.example.schedulingapp.BusinessSide;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedulingapp.Appointment;
import com.example.schedulingapp.AppointmentAdapter;
import com.example.schedulingapp.AppointmentUtils;
import com.example.schedulingapp.R;

import java.util.ArrayList;
import java.util.List;

public class B_AppointmentsFragment extends Fragment {

    private RecyclerView recyclerView;
    private AppointmentAdapter appointmentAdapter;
    private List<Appointment> appointmentList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.b_fragment_appointments, container, false);

        recyclerView = root.findViewById(R.id.recyclerViewAppointments);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        appointmentList = new ArrayList<>();
        appointmentAdapter = new AppointmentAdapter(appointmentList, getContext());
        recyclerView.setAdapter(appointmentAdapter);

        loadAppointments();

        return root;
    }

    private void loadAppointments() {
        // Get the business ID from SharedPreferences
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MyAppPreferences", getContext().MODE_PRIVATE);
        String businessId = sharedPreferences.getString("BUSINESS_ID", null);

        if (businessId == null) {
            // Handle case where business ID is not found
            return;
        }

        // Use AppointmentUtils to fetch appointments
        AppointmentUtils.fetchAppointmentsForBusiness(businessId, getContext(), appointments -> {
            appointmentList.clear();
            appointmentList.addAll(appointments);
            appointmentAdapter.notifyDataSetChanged();
        });
    }
}
