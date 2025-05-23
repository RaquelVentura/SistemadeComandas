package com.example.sistemadecomandas.vistasCocineros.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sistemadecomandas.databinding.FragmentCocinerosHomeBinding;

public class CocinerosHomeFragment extends Fragment {

    private FragmentCocinerosHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CocinerosHomeViewModel homeViewModel =
                new ViewModelProvider(this).get(CocinerosHomeViewModel.class);

        binding = FragmentCocinerosHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}