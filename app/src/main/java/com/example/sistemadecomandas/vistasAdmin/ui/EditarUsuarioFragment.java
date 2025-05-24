package com.example.sistemadecomandas.vistasAdmin.ui;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sistemadecomandas.Modelos.Usuario;
import com.example.sistemadecomandas.R;

public class EditarUsuarioFragment extends DialogFragment {
    private static final String ARG_ID = "id";
    private static final String ARG_NOMBRE = "nombre";
    private static final String ARG_ROL = "rol";
    private static final String ARG_IMAGEN = "imagen";

    public EditarUsuarioFragment() {
        // Required empty public constructor
    }
    public static EditarUsuarioFragment newInstance(Usuario usuario) {
        EditarUsuarioFragment fragment = new EditarUsuarioFragment();
        Bundle args = new Bundle();
        args.putString("id", usuario.getId());
        args.putString("nombre", usuario.getNombre());
        args.putString("rol", usuario.getRol());
        args.putString("imagen", usuario.getImagen());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String id = getArguments().getString(ARG_ID);
            String nombre = getArguments().getString(ARG_NOMBRE);
            String rol = getArguments().getString(ARG_ROL);
            String imagen = getArguments().getString(ARG_IMAGEN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editar_usuario, container, false);
        return view;
    }
}