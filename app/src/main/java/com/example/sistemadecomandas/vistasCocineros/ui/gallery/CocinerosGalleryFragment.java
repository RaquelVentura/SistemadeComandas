package com.example.sistemadecomandas.vistasCocineros.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sistemadecomandas.databinding.FragmentCocinerosGalleryBinding;

public class CocinerosGalleryFragment extends Fragment {

    private FragmentCocinerosGalleryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CocinerosGalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(CocinerosGalleryViewModel.class);

        binding = FragmentCocinerosGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textGallery;
        galleryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}