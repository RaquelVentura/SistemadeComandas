package com.example.sistemadecomandas.vistasAdmin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sistemadecomandas.Modelos.Usuario;
import com.example.sistemadecomandas.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.List;

public class RegistroActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imagenUri;
    private ImageView imagenVista;
    private ImageButton btnEditarImg;
    private StorageReference storageRef;

    private Spinner spRoles;
    private EditText txtNombre, txtEmail, txtPassword;
    private Button btnRegister;
    private ImageButton btnVolverVistaGestionUsuarios;

    private FirebaseAuth firebaseAuth;

    private String img, nombre, correo, pass, rol;
    private FirebaseUser usuarioCreado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        imagenVista = findViewById(R.id.imagenEmpleado);
        btnEditarImg = findViewById(R.id.btnEditarImagenUsuario);

        storageRef = FirebaseStorage.getInstance().getReference("imagenes");

        spRoles = findViewById(R.id.spRoles);
        txtNombre = findViewById(R.id.txtNombreUsuario);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnVolverVistaGestionUsuarios = findViewById(R.id.btnVistaGestionUsuarios);
        firebaseAuth = FirebaseAuth.getInstance();

        llenarSpinner();

        btnEditarImg.setOnClickListener(v -> abrirSelectorImagen());

        btnRegister.setOnClickListener(v -> registrarUsuario());

        btnVolverVistaGestionUsuarios.setOnClickListener(v -> {
            Intent intent = new Intent(this, VistaPrincipalAdminActivity.class);
            intent.putExtra("fragment_admin_home", "home");
            startActivity(intent);
        });
    }

    private void llenarSpinner() {
        List<String> listRoles = Arrays.asList("Administrador", "Cocinero", "Mesero");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listRoles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRoles.setAdapter(adapter);
    }

    private void convertirAString() {
        nombre = txtNombre.getText().toString().trim();
        correo = txtEmail.getText().toString().trim();
        pass = txtPassword.getText().toString().trim();
        rol = spRoles.getSelectedItem().toString();
    }

    private void registrarUsuario() {
        convertirAString();

        if (nombre.isEmpty() || correo.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pass.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(correo, pass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                usuarioCreado = firebaseAuth.getCurrentUser();

                if (usuarioCreado != null) {
                    usuarioCreado.sendEmailVerification().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(this, "Se envió un correo de verificación", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Fallo el envío de verificación", Toast.LENGTH_SHORT).show();
                        }
                    });

                    if (imagenUri != null) {
                        subirImagenYGuardarUsuario(usuarioCreado.getUid());
                    } else {
                        img = "img_por_defecto_usuario"; // Imagen por defecto
                        insertarUsuario();
                        irAVistaPrincipal();
                    }
                }

            } else {
                String error = task.getException().getMessage();
                if (error != null && error.contains("already in use")) {
                    Toast.makeText(this, "Ya existe una cuenta con ese correo", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void abrirSelectorImagen() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imagenUri = data.getData();
            imagenVista.setImageURI(imagenUri);
        }
    }

    private void subirImagenYGuardarUsuario(String userId) {
        StorageReference fileRef = storageRef.child(userId + ".jpg");

        fileRef.putFile(imagenUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    img = uri.toString();
                    insertarUsuario();
                    irAVistaPrincipal();
                }).addOnFailureListener(e ->
                        Toast.makeText(this, "Error al obtener la URL de la imagen", Toast.LENGTH_SHORT).show()
                )).addOnFailureListener(e ->
                        Toast.makeText(this, "Error al subir imagen", Toast.LENGTH_SHORT).show()
                );
    }

    private void insertarUsuario() {
        String userId = usuarioCreado.getUid();
        Usuario nuevoUsuario = new Usuario(userId, img, correo, nombre, rol);

        DatabaseReference usuarioRef = FirebaseDatabase.getInstance().getReference("usuarios");
        usuarioRef.child(userId).setValue(nuevoUsuario);
    }

    private void irAVistaPrincipal() {
        Toast.makeText(this, "Usuario creado correctamente", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, VistaPrincipalAdminActivity.class);
        startActivity(intent);
        finish();
    }
}
