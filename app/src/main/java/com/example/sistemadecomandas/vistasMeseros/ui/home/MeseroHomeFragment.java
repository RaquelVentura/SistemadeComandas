package com.example.sistemadecomandas.vistasMeseros.ui.home;
//VISTA DE COMANDAAAAAAAAAAAAAAS
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.sistemadecomandas.databinding.FragmentMeserosHomeBinding;

public class MeseroHomeFragment extends Fragment {
    private FragmentMeserosHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}