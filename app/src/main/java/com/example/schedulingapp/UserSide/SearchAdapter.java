package com.example.schedulingapp.UserSide;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedulingapp.BusinessProfile;
import com.example.schedulingapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private List<BusinessProfile> items;
    private Context context;
    private boolean isLoading;

    public SearchAdapter(List<BusinessProfile> items, Context context) {
        this.items = items;
        this.context = context;
        this.isLoading = true;
    }

    public void updateData(List<BusinessProfile> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BusinessProfile businessProfile = items.get(position);
        holder.nameTextView.setText(businessProfile.getName());
        holder.professionTextView.setText(businessProfile.getType());

        // Check if the businessProfile is added and set the button accordingly
        isBusinessAdded(businessProfile, isAdded -> {
            if (isAdded) {
                holder.addRemoveButton.setBackgroundResource(R.drawable.remove_circle);
            } else {
                holder.addRemoveButton.setBackgroundResource(R.drawable.add_button);
            }
            // Make the button visible when loading is complete
            if (!isLoading) {
                holder.addRemoveButton.setVisibility(View.VISIBLE);
            }
        });
    }




    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nameTextView;
        TextView professionTextView;
        Button addRemoveButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            professionTextView = itemView.findViewById(R.id.professionTextView);
            addRemoveButton = itemView.findViewById(R.id.addRemoveButton);
            addRemoveButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                BusinessProfile businessProfile = items.get(position);
                isBusinessAdded(businessProfile, isAdded -> {
                    if (isAdded) {
                        // Remove the businessProfile
                        removeBusinessFromCollection(businessProfile);
                        Toast.makeText(context, "BusinessProfile removed", Toast.LENGTH_SHORT).show();
                    } else {
                        // Add the businessProfile
                        addBusinessToCollection(businessProfile);
                        Toast.makeText(context, "BusinessProfile added", Toast.LENGTH_SHORT).show();
                    }
                    notifyDataSetChanged();
                });
            }
        }
    }

    private void isBusinessAdded(BusinessProfile businessProfile, OnCheckBusinessAddedListener listener) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Check if the businessProfile exists in the user's addedBusinesses collection
        DocumentReference addedBusinessRef = db.collection("User").document(userId)
                .collection("addedBusinesses").document(businessProfile.getId());

        addedBusinessRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // BusinessProfile is already added
                    listener.onCheckBusinessAdded(true);
                } else {
                    // BusinessProfile is not added
                    listener.onCheckBusinessAdded(false);
                }
            } else {
                // Error occurred while checking if businessProfile is added
                Log.e("TAG", "Error checking if businessProfile is added", task.getException());
                listener.onCheckBusinessAdded(false); // Assume businessProfile is not added in case of error
            }
        });
    }

    // Interface for callback
    interface OnCheckBusinessAddedListener {
        void onCheckBusinessAdded(boolean isAdded);
    }

    private void addBusinessToCollection(BusinessProfile businessProfile) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference addedBusinessesRef = db.collection("User").document(userId).collection("addedBusinesses");
        addedBusinessesRef.document(businessProfile.getId()).set(businessProfile);
    }

    private void removeBusinessFromCollection(BusinessProfile businessProfile) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference addedBusinessesRef = db.collection("User").document(userId).collection("addedBusinesses");
        addedBusinessesRef.document(businessProfile.getId()).delete();
    }

    public void updateData(List<BusinessProfile> newItems, boolean isLoading) {
        this.items = newItems;
        this.isLoading = isLoading;
        notifyDataSetChanged(); // Refresh the adapter
    }
}
