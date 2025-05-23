package com.example.sistemadecomandas.login;

import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.sistemadecomandas.R;


public class BienvenidaFragment extends Fragment {

    private Button btnAbrirVistaRegistro;
    public BienvenidaFragment() {
    }

    public static BienvenidaFragment newInstance(String param1, String param2) {
        BienvenidaFragment fragment = new BienvenidaFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bienvenida, container, false);
        btnAbrirVistaRegistro = view.findViewById(R.id.btnAbrirVistaLogin);
        btnAbrirVistaRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LogImActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}