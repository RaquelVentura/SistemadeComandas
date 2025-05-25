package com.example.sistemadecomandas.vistasAdmin.ui;

import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sistemadecomandas.Modelos.Platillo;
import com.example.sistemadecomandas.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class EditarPlatillosFragment extends DialogFragment {

    private static final String ARG_ID = "idPlatillo";
    private static final String ARG_IMAGEN = "imagenPlatillo";
    private static final String ARG_NOMBRE = "nombrePlatillo";
    private static final String ARG_CATEGORIA = "categoria";
    private static final String ARG_PRECIO = "precio";
    private static final String ARG_DESCRIPCION = "descripcion";

    private EditText txtEditNombrePlato, txtEditDescripcion, txtEditPrecio;
    private Spinner spEditCategoria;
    private Button btnEditarPlatillo;

    public EditarPlatillosFragment() {}

    public static EditarPlatillosFragment newInstance(Platillo platillo) {
        EditarPlatillosFragment fragment = new EditarPlatillosFragment();
        Bundle args = new Bundle();
        args.putString("idPlatillo", platillo.getIdPlatillo());
        args.putString("nombrePlatillo", platillo.getnombrePlatillo());
        args.putString("precio", platillo.getPrecio());
        args.putString("descripcion", platillo.getDescripcion());
        args.putString("categoria", platillo.getCategoria());
        args.putString("imagenPlatillo", platillo.getImagenPlatillo());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editar_platillos, container, false);

        txtEditNombrePlato = view.findViewById(R.id.txtEditNombrePlato);
        txtEditDescripcion = view.findViewById(R.id.txtEditDescripcion);
        spEditCategoria = view.findViewById(R.id.spEditCategoria);
        txtEditPrecio = view.findViewById(R.id.txtEditPrecio);
        btnEditarPlatillo = view.findViewById(R.id.btnEditAceptarPLa);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"Entrada", "Plato fuerte", "Postre", "Bebida"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEditCategoria.setAdapter(adapter);

        if (getArguments() != null) {
            txtEditNombrePlato.setText(getArguments().getString(ARG_NOMBRE));
            txtEditDescripcion.setText(getArguments().getString(ARG_DESCRIPCION));
            txtEditPrecio.setText(getArguments().getString(ARG_PRECIO));

            String categoria = getArguments().getString(ARG_CATEGORIA);
            int pos = adapter.getPosition(categoria);
            if (pos >= 0) {
                spEditCategoria.setSelection(pos);
            }
        }

        btnEditarPlatillo.setOnClickListener(v -> {
            String id = getArguments().getString(ARG_ID);
            String nuevoNombre = txtEditNombrePlato.getText().toString().trim();
            String nuevaDescripcion = txtEditDescripcion.getText().toString().trim();
            String nuevoPrecio = txtEditPrecio.getText().toString().trim();
            String nuevaCategoria = spEditCategoria.getSelectedItem().toString();

            actualizarPlatilloEnFirebase(id, nuevoNombre, nuevaDescripcion, nuevoPrecio, nuevaCategoria);
        });
        return view;
    }

    private void actualizarPlatilloEnFirebase(String id, String nombre, String descripcion, String precio, String categoria) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("platillos")
                .child(id);

        Map<String, Object> actualizaciones = new HashMap<>();
        actualizaciones.put("nombrePlatillo", nombre);
        actualizaciones.put("descripcion", descripcion);
        actualizaciones.put("precio", precio);
        actualizaciones.put("categoria", categoria);

        ref.updateChildren(actualizaciones)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Platillo actualizado", Toast.LENGTH_SHORT).show();
                    dismiss();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
