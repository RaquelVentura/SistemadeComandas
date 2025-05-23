package com.example.sistemadecomandas.vistasAdmin.ui.slideshow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sistemadecomandas.databinding.FragmentAdminSlideshowBinding;

public class AdminSlideshowFragment extends Fragment {

    private FragmentAdminSlideshowBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AdminSlideshowViewModel slideshowViewModel =
                new ViewModelProvider(this).get(AdminSlideshowViewModel.class);

        binding = FragmentAdminSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textSlideshow;
        slideshowViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}