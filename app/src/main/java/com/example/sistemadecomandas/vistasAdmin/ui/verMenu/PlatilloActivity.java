package com.example.sistemadecomandas.vistasAdmin.ui.verMenu;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.sistemadecomandas.Modelos.Platillo;
import com.example.sistemadecomandas.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.List;

public class PlatilloActivity extends AppCompatActivity {

    private EditText txtNombrePlato, txtDescripcion, txtPrecio;
    private Spinner spCategoria;
    private Button btnAceptarPla;
    private ImageButton btnAgregarImgPlato;
    private ImageView imageViewPreview;

    private Uri imagenPlatilloUri;
    private String imgUrlEnStorage;

    private FirebaseAuth firebaseAuth;
    private StorageReference storageRef;

    private ActivityResultLauncher<Intent> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platillo);

        txtNombrePlato = findViewById(R.id.txtNombrePlato);
        txtDescripcion = findViewById(R.id.txtDescricion);
        txtPrecio = findViewById(R.id.txtPrecio);
        spCategoria = findViewById(R.id.spCategoria);
        btnAceptarPla = findViewById(R.id.btnAceptarPLa);
        btnAgregarImgPlato = findViewById(R.id.btnAgregarImagenPlato);
        imageViewPreview = findViewById(R.id.imagenPlatillo);

        firebaseAuth = FirebaseAuth.getInstance();
        storageRef   = FirebaseStorage.getInstance().getReference("imagenes");

        llenarSpinner();
        configurarPicker();

        btnAgregarImgPlato.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            pickImageLauncher.launch(intent);
        });

        btnAceptarPla.setOnClickListener(v -> {
            if (!validarCampos()) return;
            subirImagenYAñadirPlatillo();
        });
    }

    private void llenarSpinner() {
        List<String> categorias = Arrays.asList("Entrada", "Desayuno", "Plato fuerte", "Postre", "Bebida");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categorias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategoria.setAdapter(adapter);
    }
    private void configurarPicker() {
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imagenPlatilloUri = result.getData().getData();
                        Glide.with(this)
                                .load(imagenPlatilloUri)
                                .placeholder(R.drawable.img_2)
                                .into(imageViewPreview);
                    }
                }
        );
    }
    private boolean validarCampos() {
        if (txtNombrePlato.getText().toString().trim().isEmpty()
                || txtDescripcion.getText().toString().trim().isEmpty()
                || txtPrecio.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void subirImagenYAñadirPlatillo() {
        if (imagenPlatilloUri == null) {
            imgUrlEnStorage = "img_por_defecto_usuario";
            insertarPlatilloEnFirebase();
            return;
        }

        String fileName = FirebaseDatabase.getInstance()
                .getReference("platillos")
                .push()
                .getKey() + ".jpg";

        StorageReference fileRef = storageRef.child(fileName);
        fileRef.putFile(imagenPlatilloUri)
                .addOnSuccessListener(task -> fileRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            imgUrlEnStorage = uri.toString();
                            insertarPlatilloEnFirebase();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this,
                                    "Error al obtener URL de imagen: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        })
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al subir imagen: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
    private void insertarPlatilloEnFirebase() {
        String nombre = txtNombrePlato.getText().toString().trim();
        String descripcion = txtDescripcion.getText().toString().trim();
        String precio = txtPrecio.getText().toString().trim();
        String categoria = spCategoria.getSelectedItem().toString();

        DatabaseReference platoRef = FirebaseDatabase.getInstance()
                .getReference("platillos");
        String platilloId = platoRef.push().getKey();
        Platillo nuevo = new Platillo(platilloId, imgUrlEnStorage, nombre, categoria, precio, descripcion);

        platoRef.child(platilloId)
                .setValue(nuevo)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Platillo creado exitosamente", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al crear platillo: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}
