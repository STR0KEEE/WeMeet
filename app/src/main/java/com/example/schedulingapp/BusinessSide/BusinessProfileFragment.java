package com.example.schedulingapp.BusinessSide;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.example.schedulingapp.R;

public class BusinessProfileFragment extends Fragment implements View.OnClickListener {

    private TextView nameTextView;
    private TextView professionTextView;
    private AppCompatButton backButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_business_profile, container, false);

        // Initialize views
        nameTextView = view.findViewById(R.id.profile_name);
        professionTextView = view.findViewById(R.id.profile_profession);
        backButton = view.findViewById(R.id.profile_back_button);

        backButton.setOnClickListener(this);

        // Get data from arguments
        Bundle arguments = getArguments();
        if (arguments != null) {
            String name = arguments.getString("name", "");
            String profession = arguments.getString("profession", "");

            // Set data to views
            nameTextView.setText(name);
            professionTextView.setText(profession);
        }

        return view;
    }

    @Override
    public void onClick(View view) {
        if(getParentFragmentManager()!=null){
            getParentFragmentManager().popBackStack();
        }
    }
}
