package com.example.sistemadecomandas.vistasAdmin.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.sistemadecomandas.databinding.FragmentAdminGalleryBinding;
import com.example.sistemadecomandas.databinding.FragmentAdminHomeBinding;
import com.example.sistemadecomandas.vistasAdmin.RegistroActivity;

public class AdminHomeFragment extends Fragment {
    private Button btnNuevoUsuario;
    private FragmentAdminHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAdminHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.btnNuevoUsuario.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getContext(), RegistroActivity.class);
            startActivity(intent);
        }
    });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}