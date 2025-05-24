package com.example.sistemadecomandas.vistasAdmin;
import android.content.Intent;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

import com.example.sistemadecomandas.Modelos.Usuario;
import com.example.sistemadecomandas.R;
import com.example.sistemadecomandas.login.LogImActivity;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;


public class RegistroActivity extends AppCompatActivity {
    private Spinner spRoles;
    private List<String> listRoles;
    private EditText txtNombre, txtEmail, txtPassword;
    private Button btnRegister;
    private ImageButton btnVolverVistaGestionUsuarios;
    private FirebaseAuth firebaseAuth;
    private CredentialManager credentialManager;
    public static final int RC_SING_INT = 2001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        spRoles = findViewById(R.id.spRoles);
        txtNombre = findViewById(R.id.txtNombreUsuario);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnVolverVistaGestionUsuarios = findViewById(R.id.btnVistaGestionUsuarios);
        firebaseAuth = FirebaseAuth.getInstance();
        credentialManager = CredentialManager.create(getBaseContext());

        LlenarSpinner();
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterUserAutentification();
            }
        });
        btnVolverVistaGestionUsuarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), VistaPrincipalAdminActivity.class);
                intent.putExtra("fragment_admin_home", "home");
                startActivity(intent);
            }
        });
    }
    private void LlenarSpinner() {
        List<String> listRoles = Arrays.asList("Administrador","Cocinero", "Mesero");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, listRoles
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRoles.setAdapter(adapter);
    }
    private String img, nombre, correo, pass, rol;
    FirebaseUser usuarioCreado;
    private void convertirAString(){
        img = "img_por_defecto_usuario";
        nombre = txtNombre.getText().toString().trim();
        correo = txtEmail.getText().toString().trim();
        pass = txtPassword.getText().toString().trim();
        rol = spRoles.getSelectedItem().toString();
    }
    private void insertarUsuario(){
        String userId = usuarioCreado.getUid();
        Usuario nuevoUsuario = new Usuario(userId, img, correo, nombre, rol);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usuarioRef = database.getReference("usuarios");
        usuarioRef.child(userId).setValue(nuevoUsuario);
    }
    private void RegisterUserAutentification() {
        convertirAString();

        if (nombre.isEmpty()||correo.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
        }
        if (pass.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
        } else {
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
                        Toast.makeText(this, "Usuario creado correctamente", Toast.LENGTH_SHORT).show();

                        insertarUsuario();

                        Intent intent = new Intent(this, VistaPrincipalAdminActivity.class);
                        startActivity(intent);
                    }
                } else {
                    String error = task.getException().getMessage();
                    if (error != null && error.contains("already in use")) {
                        Toast.makeText(this, "Ya existe cuenta con ese correo", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}