package com.example.schedulingapp.UserSide;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedulingapp.BusinessProfile;
import com.example.schedulingapp.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.CollectionReference;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private SearchAdapter searchAdapter;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private TextView emptyView;
    private List<BusinessProfile> originalBusinessProfiles = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Initialize RecyclerView and adapter
        recyclerView = view.findViewById(R.id.recyclerViewRecentSearches);
        progressBar = view.findViewById(R.id.progressBar);
        emptyView = view.findViewById(R.id.empty_view);
        searchAdapter = new SearchAdapter(new ArrayList<>(), requireContext());
        recyclerView.setAdapter(searchAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Fetch business data from Firebase
        fetchBusinessData();

        // Initialize search functionality
        EditText searchEditText = view.findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterBusinesses(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void fetchBusinessData() {
        progressBar.setVisibility(View.VISIBLE); // Show the ProgressBar

        CollectionReference businessesRef = db.collection("Business");

        businessesRef.get().addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);

            if (task.isSuccessful()) {
                originalBusinessProfiles.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Convert each document to a BusinessProfile object and add it to the list
                    BusinessProfile businessProfile = document.toObject(BusinessProfile.class);
                    originalBusinessProfiles.add(businessProfile);
                }
                // Update the adapter with the fetched business data and loading state
                searchAdapter.updateData(originalBusinessProfiles, false); // Pass isLoading = false

                // Show or hide empty view based on the data
                if (originalBusinessProfiles.isEmpty()) {
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    emptyView.setVisibility(View.GONE);
                }
            } else {
                // Handle errors
                Log.e("SearchFragment", "Error fetching business data", task.getException());
            }
        });
    }


    private void filterBusinesses(String query) {
        List<BusinessProfile> filteredList = new ArrayList<>();
        for (BusinessProfile businessProfile : originalBusinessProfiles) {
            if (businessProfile.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(businessProfile);
            }
        }
        // Update the adapter with the filtered list of businesses
        searchAdapter.updateData(filteredList);
    }
}
