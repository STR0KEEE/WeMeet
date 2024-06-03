package com.example.schedulingapp.UserSide;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedulingapp.Appointment;
import com.example.schedulingapp.AppointmentAdapter;
import com.example.schedulingapp.BusinessProfile;
import com.example.schedulingapp.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ScheduleFragment extends Fragment {

    private Spinner spinnerBusinesses;
    private RecyclerView recyclerViewAppointments;
    private List<BusinessProfile> businessList;
    private List<Appointment> appointmentList;
    private ArrayAdapter<BusinessProfile> businessAdapter;
    private AppointmentAdapter appointmentAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        spinnerBusinesses = view.findViewById(R.id.spinnerBusinesses);
        recyclerViewAppointments = view.findViewById(R.id.recyclerViewAppointments);
        businessList = new ArrayList<>();
        appointmentList = new ArrayList<>();
        businessAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, businessList);
        appointmentAdapter = new AppointmentAdapter(appointmentList, getContext());

        recyclerViewAppointments.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAppointments.setAdapter(appointmentAdapter);

        spinnerBusinesses.setAdapter(businessAdapter);
        spinnerBusinesses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fetchAppointmentsForBusiness(businessList.get(position).getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        loadBusinesses();

        return view;
    }

    private void loadBusinesses() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("User").document(userId).collection("addedBusinesses")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            businessList.clear(); // Clear existing list before adding new businesses
                            for (DocumentSnapshot document : task.getResult()) {
                                BusinessProfile business = document.toObject(BusinessProfile.class);
                                if (business != null) {
                                    businessList.add(business);
                                }
                            }
                            businessAdapter.notifyDataSetChanged();
                        } else {
                            Log.e("ScheduleFragment", "Error fetching businesses", task.getException());
                        }
                    });
        }
    }

    private void fetchAppointmentsForBusiness(String businessId) {
        appointmentList.clear(); // Clear existing list before fetching new appointments
        appointmentAdapter.notifyDataSetChanged(); // Notify adapter to update UI

        FirebaseFirestore.getInstance().collection("appointments")
                .whereEqualTo("businessId", businessId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            String appointmentId = document.getId(); // Get the appointment ID
                            Timestamp date = document.getTimestamp("date"); // Get the Timestamp object for date
                            String duration = document.getString("duration"); // Assuming duration is stored as String
                            boolean isAvailable = document.getBoolean("isAvailable");
                            String customerId = document.getString("customerId");

                            // Create an Appointment object and add it to the list
                            Appointment appointment = new Appointment(appointmentId, businessId, date, duration, isAvailable, customerId);
                            appointmentList.add(appointment);
                        }
                        appointmentAdapter.notifyDataSetChanged(); // Notify adapter to update UI
                    } else {
                        Toast.makeText(getContext(), "Failed to fetch appointments", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void resetPage() {
        // Remove the fragment
        getParentFragmentManager().beginTransaction().remove(this).commit();

        // Re-add the fragment
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new ScheduleFragment())
                .addToBackStack(null)
                .commit();

        Toast.makeText(getContext(), "Appointment Added", Toast.LENGTH_SHORT).show(); // Show toast message
    }

}