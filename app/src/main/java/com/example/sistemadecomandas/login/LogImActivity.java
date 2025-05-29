package com.example.sistemadecomandas.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

import com.example.sistemadecomandas.R;
import com.example.sistemadecomandas.vistasAdmin.VistaPrincipalAdminActivity;
import com.example.sistemadecomandas.vistasCocineros.VistaPrincipalCocineros;
import com.example.sistemadecomandas.vistasMeseros.VistaPrincipalMeserosActivity;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.Executors;

public class LogImActivity extends AppCompatActivity {
    private Button btnACeptar;
    private TextView btnRecuperarCOntrasenia;
    private EditText txtInicioEmail, txtInicioPass;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_im);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        txtInicioEmail = findViewById(R.id.txtInicioEmail);
        txtInicioPass = findViewById(R.id.txtInicioPassword);
        btnACeptar = findViewById(R.id.btnAceptar);
        firebaseAuth = FirebaseAuth.getInstance();
        btnRecuperarCOntrasenia = findViewById(R.id.btnRcuperarContrasenia);
        databaseReference = FirebaseDatabase.getInstance().getReference("usuarios");

        btnRecuperarCOntrasenia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), RecuperarContrasenaActivity.class);
                startActivity(intent);
            }
        });

        btnACeptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarSesionVerificada();
            }
        });
    }
    private void iniciarSesionVerificada(){
        String correo = txtInicioEmail.getText().toString().trim();
        String pass = txtInicioPass.getText().toString().trim();

        if (correo.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
        } else {
            firebaseAuth.signInWithEmailAndPassword(correo, pass)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser usuarioLogueado = firebaseAuth.getCurrentUser();
                            if (usuarioLogueado != null) {
                                String uid = usuarioLogueado.getUid();
                                redireccionUID(usuarioLogueado);
                            }
                        } else {
                            manejarErroresDeLogin(task.getException().getMessage());
                        }
                    });
        }
    }
    private void redireccionUID(FirebaseUser usuario) {
        String uid = usuario.getUid();
            redirigirPorRol(uid);
    }
    private void redirigirPorRol(String uid) {
        databaseReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    mostrarToast("No se encontró información del usuario");
                    return;
                }
                String rol = snapshot.child("rol").getValue(String.class);
                if (rol == null) {
                    mostrarToast("No se encontró el rol del usuario");
                    return;
                }
                switch (rol.toLowerCase()) {
                    case "administrador":
                        redirigir(VistaPrincipalAdminActivity.class);
                        break;
                    case "cocinero":
                        redirigir(VistaPrincipalCocineros.class);
                        break;
                    case "mesero":
                        redirigir(VistaPrincipalMeserosActivity.class);
                        break;
                    default:
                        mostrarToast("Rol no reconocido");
                        break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mostrarToast("Error al leer la base de datos");
            }
        });
    }

    private void redirigir(Class<?> destino) {
        Intent intent = new Intent(getBaseContext(), destino);
        startActivity(intent);
        finish();
    }
    private void mostrarToast(String mensaje) {
        Toast.makeText(LogImActivity.this, mensaje, Toast.LENGTH_SHORT).show();
    }

    private void manejarErroresDeLogin(String error) {
        if (error == null) {
            mostrarToast("Error desconocido");
            return;
        }
        if (error.contains("Contraseña no válida")) {
            mostrarToast("Contraseña incorrecta");
        } else if (error.contains("There is no user record")) {
            mostrarToast("No existe una cuenta con ese correo");
        } else {
            mostrarToast("Error al iniciar sesión: " + error);
        }
    }
}