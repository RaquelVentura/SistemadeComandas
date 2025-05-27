package com.example.sistemadecomandas.vistasAdmin.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.sistemadecomandas.Modelos.Platillo;
import com.example.sistemadecomandas.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class EditarPlatillosFragment extends DialogFragment {

    private static final String ARG_ID = "idPlatillo";
    private static final String ARG_IMAGEN = "imagenPlatillo";
    private static final String ARG_NOMBRE = "nombrePlatillo";
    private static final String ARG_CATEGORIA = "categoria";
    private static final String ARG_PRECIO = "precio";
    private static final String ARG_DESCRIPCION = "descripcion";

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText txtEditNombrePlato, txtEditDescripcion, txtEditPrecio;
    private Spinner spEditCategoria;
    private Button btnEditarPlatillo;
    private ImageButton btnEditarimgPlatillo;
    private ImageView imgPlatillo;
    private Uri imagenPlatilloUri;
    private String idPlatillo;
    private String imagenPlatilloActual;
    private StorageReference storageRef;

    public EditarPlatillosFragment() {}

    public static EditarPlatillosFragment newInstance(Platillo platillo) {
        EditarPlatillosFragment fragment = new EditarPlatillosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID, platillo.getIdPlatillo());
        args.putString(ARG_NOMBRE, platillo.getnombrePlatillo());
        args.putString(ARG_PRECIO, platillo.getPrecio());
        args.putString(ARG_DESCRIPCION, platillo.getDescripcion());
        args.putString(ARG_CATEGORIA, platillo.getCategoria());
        args.putString(ARG_IMAGEN, platillo.getImagenPlatillo());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storageRef = FirebaseStorage.getInstance().getReference("imagenes");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editar_platillos, container, false);

        btnEditarimgPlatillo = view.findViewById(R.id.btnEditarImgPlatillo);
        txtEditNombrePlato = view.findViewById(R.id.txtEditNombrePlato);
        txtEditDescripcion = view.findViewById(R.id.txtEditDescripcion);
        spEditCategoria = view.findViewById(R.id.spEditCategoria);
        txtEditPrecio = view.findViewById(R.id.txtEditPrecio);
        imgPlatillo = view.findViewById(R.id.imagenEPlatillo);
        btnEditarPlatillo = view.findViewById(R.id.btnEditAceptarPLa);
        btnEditarimgPlatillo= view.findViewById(R.id.btnEditarImgPlatillo);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"Entrada", "Desayuno", "Plato fuerte", "Postre", "Bebida"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEditCategoria.setAdapter(adapter);

        if (getArguments() != null) {
            idPlatillo = getArguments().getString(ARG_ID);
            txtEditNombrePlato.setText(getArguments().getString(ARG_NOMBRE));
            txtEditDescripcion.setText(getArguments().getString(ARG_DESCRIPCION));
            txtEditPrecio.setText(getArguments().getString(ARG_PRECIO));

            imagenPlatilloActual = getArguments().getString(ARG_IMAGEN);

            String categoria = getArguments().getString(ARG_CATEGORIA);
            int pos = adapter.getPosition(categoria);
            if (pos >= 0) {
                spEditCategoria.setSelection(pos);
            }

            if (imagenPlatilloActual == null || imagenPlatilloActual.isEmpty() || imagenPlatilloActual.equals("img_por_defecto_platillo")) {
                imgPlatillo.setImageResource(R.drawable.icon_menu);
            } else {
                Glide.with(this)
                        .load(imagenPlatilloActual)
                        .placeholder(R.drawable.icon_menu)
                        .error(R.drawable.icon_menu)
                        .into(imgPlatillo);
            }
        }

        btnEditarimgPlatillo.setOnClickListener(v -> abrirSelectorImagen());

        btnEditarPlatillo.setOnClickListener(v -> {
            String nuevoNombre = txtEditNombrePlato.getText().toString().trim();
            String nuevaDescripcion = txtEditDescripcion.getText().toString().trim();
            String nuevoPrecio = txtEditPrecio.getText().toString().trim();
            String nuevaCategoria = spEditCategoria.getSelectedItem().toString();

            if (imagenPlatilloUri != null) {
                subirImagenYActualizarPlatillo(nuevoNombre, nuevaDescripcion, nuevoPrecio, nuevaCategoria);
            } else {
                actualizarPlatilloEnFirebase(nuevoNombre, nuevaDescripcion, nuevoPrecio, nuevaCategoria, imagenPlatilloActual);
            }
        });

        return view;
    }

    private void abrirSelectorImagen() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imagenPlatilloUri = data.getData();
            imgPlatillo.setImageURI(imagenPlatilloUri);
        }
    }

    private void subirImagenYActualizarPlatillo(String nombre, String descripcion, String precio, String categoria) {
        StorageReference fileRef = storageRef.child(idPlatillo + ".jpg");

        fileRef.putFile(imagenPlatilloUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String nuevaImagenUrl = uri.toString();
                    actualizarPlatilloEnFirebase(nombre, descripcion, precio, categoria, nuevaImagenUrl);
                }).addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error al obtener la URL de la imagen", Toast.LENGTH_SHORT).show()
                ))
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error al subir imagen", Toast.LENGTH_SHORT).show()
                );
    }

    private void actualizarPlatilloEnFirebase(String nombre, String descripcion, String precio, String categoria, String imagenUrl) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("platillos")
                .child(idPlatillo);

        Map<String, Object> actualizaciones = new HashMap<>();
        actualizaciones.put("nombrePlatillo", nombre);
        actualizaciones.put("descripcion", descripcion);
        actualizaciones.put("precio", precio);
        actualizaciones.put("categoria", categoria);
        actualizaciones.put("imagenPlatillo", imagenUrl);

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
