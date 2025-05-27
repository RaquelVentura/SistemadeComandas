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
import com.example.sistemadecomandas.Modelos.Usuario;
import com.example.sistemadecomandas.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class EditarUsuarioFragment extends DialogFragment {

    private static final String ARG_ID = "id";
    private static final String ARG_NOMBRE = "nombre";
    private static final String ARG_ROL = "rol";
    private static final String ARG_IMAGEN = "imagen";

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText txtEditNombreUsuario;
    private Spinner spEditRoles;
    private Button btnEdit;
    private ImageButton btnVolver, btnEditarimg;
    private ImageView imgUsuario;

    private Uri imagenUri;
    private String idUsuario;
    private String imagenActual;

    private StorageReference storageRef;

    public EditarUsuarioFragment() {}

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
        storageRef = FirebaseStorage.getInstance().getReference("imagenes");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editar_usuario, container, false);

        txtEditNombreUsuario = view.findViewById(R.id.txtEditNombreUsuario);
        spEditRoles = view.findViewById(R.id.spEditRoles);
        btnEdit = view.findViewById(R.id.btnEdit);
        btnVolver = view.findViewById(R.id.btnVolverGestionUsuarios);
        imgUsuario = view.findViewById(R.id.imageView2);
        btnEditarimg = view.findViewById(R.id.btnEditarImgUsuario);

        if (getArguments() != null) {
            idUsuario = getArguments().getString(ARG_ID);
            String nombre = getArguments().getString(ARG_NOMBRE);
            String rol = getArguments().getString(ARG_ROL);
            imagenActual = getArguments().getString(ARG_IMAGEN);

            txtEditNombreUsuario.setText(nombre);
            setSpinnerSelection(rol);

            if (imagenActual == null || imagenActual.isEmpty() || imagenActual.equals("img_por_defecto_usuario")) {
                imgUsuario.setImageResource(R.drawable.img_por_defecto_usuario);
            } else {
                Glide.with(this)
                        .load(imagenActual)
                        .placeholder(R.drawable.img_por_defecto_usuario)
                        .error(R.drawable.img_por_defecto_usuario)
                        .into(imgUsuario);
            }
        }

        btnEditarimg.setOnClickListener(v -> abrirSelectorImagen());
        btnVolver.setOnClickListener(v -> dismiss());

        btnEdit.setOnClickListener(v -> {
            String nuevoNombre = txtEditNombreUsuario.getText().toString().trim();
            String nuevoRol = spEditRoles.getSelectedItem().toString();

            if (imagenUri != null) {
                subirImagenYActualizarUsuario(nuevoNombre, nuevoRol);
            } else {
                actualizarUsuarioEnFirebase(nuevoNombre, nuevoRol, imagenActual);
            }
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

        int pos = adapter.getPosition(rol);
        if (pos >= 0) {
            spEditRoles.setSelection(pos);
        }
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
            imagenUri = data.getData();
            imgUsuario.setImageURI(imagenUri);
        }
    }

    private void subirImagenYActualizarUsuario(String nombre, String rol) {
        StorageReference fileRef = storageRef.child(idUsuario + ".jpg");

        fileRef.putFile(imagenUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String nuevaImagenUrl = uri.toString();
                    actualizarUsuarioEnFirebase(nombre, rol, nuevaImagenUrl);
                }).addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error al obtener la URL de la imagen", Toast.LENGTH_SHORT).show()
                ))
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error al subir imagen", Toast.LENGTH_SHORT).show()
                );
    }

    private void actualizarUsuarioEnFirebase(String nombre, String rol, String imagenUrl) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("usuarios")
                .child(idUsuario);

        Map<String, Object> actualizaciones = new HashMap<>();
        actualizaciones.put("nombre", nombre);
        actualizaciones.put("rol", rol);
        actualizaciones.put("imagen", imagenUrl);

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
