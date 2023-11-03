package com.androidapp.blooddonate.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.androidapp.blooddonate.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView ( @NonNull LayoutInflater inflater,
                               ViewGroup container, Bundle savedInstanceState ) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

      /*  Button button = container.findViewById(R.id.button_appointment);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SpinnerActivity spinnerActivity = (SpinnerActivity) getActivity();
                spinnerActivity.onCreate(savedInstanceState);
            }
        });*/
        return root;


    }

    @Override
    public void onDestroyView () {
        super.onDestroyView();
        binding = null;
    }
}