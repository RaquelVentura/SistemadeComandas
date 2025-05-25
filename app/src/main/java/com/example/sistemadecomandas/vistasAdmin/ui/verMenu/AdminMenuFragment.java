package com.example.sistemadecomandas.vistasAdmin.ui.verMenu;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sistemadecomandas.R;

public class AdminMenuFragment extends Fragment {

    public AdminMenuFragment() {
        // Required empty public constructor
    }

    public static AdminMenuFragment newInstance(String param1, String param2) {
        AdminMenuFragment fragment = new AdminMenuFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_admin_menu, container, false);
    }
}