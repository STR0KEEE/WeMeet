package com.example.schedulingapp;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AppointmentUtils {

    public static void fetchAppointmentsForBusiness(String businessId, Context context, AppointmentFetchCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("appointments")
                .whereEqualTo("businessId", businessId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Appointment> appointments = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Appointment appointment = document.toObject(Appointment.class);
                            appointments.add(appointment);
                        }
                        callback.onFetchComplete(appointments);
                    } else {
                        Toast.makeText(context, "Failed to fetch appointments", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public interface AppointmentFetchCallback {
        void onFetchComplete(List<Appointment> appointments);
    }
}
