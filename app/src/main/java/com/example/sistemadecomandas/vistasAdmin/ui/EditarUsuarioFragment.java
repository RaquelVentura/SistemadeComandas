package com.example.sistemadecomandas.vistasAdmin.ui;

import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sistemadecomandas.Modelos.Usuario;
import com.example.sistemadecomandas.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class EditarUsuarioFragment extends DialogFragment {

    private static final String ARG_ID = "id";
    private static final String ARG_NOMBRE = "nombre";
    private static final String ARG_ROL = "rol";
    private static final String ARG_IMAGEN = "imagen";

    private EditText txtEditNombreUsuario;
    private Spinner spEditRoles;
    private Button btnEdit;
    private ImageButton btnVolver;
    private ImageView imgUsuario;

    public EditarUsuarioFragment() {
    }

    public static EditarUsuarioFragment newInstance(Usuario usuario) {
        EditarUsuarioFragment fragment = new EditarUsuarioFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID, usuario.getId());
        args.putString(ARG_NOMBRE, usuario.getNombre());

        args.putString(ARG_ROL, usuario.getRol());
        args.putString(ARG_IMAGEN, usuario.getImagen());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editar_usuario, container, false);

        txtEditNombreUsuario = view.findViewById(R.id.txtEditNombreUsuario);
        spEditRoles = view.findViewById(R.id.spEditRoles);
        btnEdit = view.findViewById(R.id.btnEdit);
        btnVolver = view.findViewById(R.id.btnVolverGestionUsuarios);
        imgUsuario = view.findViewById(R.id.imageView2);


        if (getArguments() != null) {
            String id = getArguments().getString(ARG_ID);
            String nombre = getArguments().getString(ARG_NOMBRE);
            String rol = getArguments().getString(ARG_ROL);
            String imagen = getArguments().getString(ARG_IMAGEN);

            txtEditNombreUsuario.setText(nombre);
            setSpinnerSelection(rol);
        }
        btnVolver.setOnClickListener(v -> dismiss());

        btnEdit.setOnClickListener(v -> {
            String id = getArguments().getString(ARG_ID);
            String nuevoNombre = txtEditNombreUsuario.getText().toString().trim();
            String nuevoRol = spEditRoles.getSelectedItem().toString();

            actualizarUsuarioEnFirebase(id, nuevoNombre, nuevoRol);
        });

        return view;
    }

    private void setSpinnerSelection(String rol) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"Administrador", "Mesero", "Cocinero"}
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEditRoles.setAdapter(adapter);

        if (adapter != null) {
            int pos = adapter.getPosition(rol);
            if (pos >= 0) {
                spEditRoles.setSelection(pos);
            }
        }
    }

    private void actualizarUsuarioEnFirebase(String id, String nombre, String rol) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("usuarios")
                .child(id);

        Map<String, Object> actualizaciones = new HashMap<>();
        actualizaciones.put("nombre", nombre);
        actualizaciones.put("rol", rol);

        ref.updateChildren(actualizaciones)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Usuario actualizado", Toast.LENGTH_SHORT).show();
                    dismiss();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
