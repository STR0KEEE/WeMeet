package com.example.schedulingapp;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    private List<Appointment> appointmentList;
    private Context context;

    public AppointmentAdapter(List<Appointment> appointmentList, Context context) {
        this.appointmentList = appointmentList;
        this.context = context;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(position);
        holder.textViewDate.setText(appointment.getDateString());
        holder.textViewDuration.setText(appointment.getDuration().toString());

        // Ensure the item background is a GradientDrawable and update its color
        if (holder.itemView.getBackground() instanceof GradientDrawable) {
            GradientDrawable background = (GradientDrawable) holder.itemView.getBackground();
            if (appointment.isAvailable()) {
                holder.buttonBook.setVisibility(View.VISIBLE);
                background.setColor(ContextCompat.getColor(context, R.color.availableColor));
            } else {
                holder.buttonBook.setVisibility(View.GONE);
                background.setColor(ContextCompat.getColor(context, R.color.notAvailableColor));
            }
        }

        holder.buttonBook.setOnClickListener(v -> bookAppointment(appointment, holder));
    }

    public void clearAppointments() {
        appointmentList.clear();
        notifyDataSetChanged();
    }

    public void addAppointment(Appointment appointment) {
        appointmentList.add(appointment);
        notifyItemInserted(appointmentList.size() - 1);
    }

    private void bookAppointment(Appointment appointment, AppointmentViewHolder holder) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        appointment.setAvailable(false);
        appointment.setCustomerId(userId);

        Map<String, Object> appointmentData = new HashMap<>();
        appointmentData.put("isAvailable", false);
        appointmentData.put("customerId", userId);
        appointmentData.put("businessId", appointment.getBusinessId());
        appointmentData.put("date", appointment.getDate());  // Assuming dateString is used to store date
        appointmentData.put("duration", appointment.getDuration());

        FirebaseFirestore.getInstance().collection("appointments")
                .document(appointment.getId())
                .update(appointmentData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Appointment booked!", Toast.LENGTH_SHORT).show();
                    holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.notAvailableColor));
                    holder.buttonBook.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to book appointment", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDate;
        TextView textViewDuration;
        Button buttonBook;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewDuration = itemView.findViewById(R.id.textViewDuration);
            buttonBook = itemView.findViewById(R.id.buttonBook);
        }
    }
}
