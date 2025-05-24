package com.example.sistemadecomandas.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    private Button btnACeptar, btnGoogle;
    private EditText txtInicioEmail, txtInicioPass;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private CredentialManager credentialManager;
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
        btnGoogle = findViewById(R.id.btnLoginWithGoogle);
        credentialManager = CredentialManager.create(this);
        firebaseAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference("usuarios");

        btnACeptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarSesionVerificada();
            }
        });
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //RegisterGoogleLonIn();
            }
        });

    }
    private void iniciarSesionVerificada(){
        String correo = txtInicioEmail.getText().toString().trim();
        String pass = txtInicioPass.getText().toString().trim();

        if (correo.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
        } else {
            firebaseAuth.signInWithEmailAndPassword(correo, pass).addOnCompleteListener(task -> {

                if (task.isSuccessful()) {
                    FirebaseUser usuarioLogueado = firebaseAuth.getCurrentUser();
                    if (usuarioLogueado != null) {
                        String uid = usuarioLogueado.getUid();
                        if (uid.equals("7oRtijqE3lR87YJLLAe0iv2ZLI52")) {
                            startActivity(new Intent(getBaseContext(), VistaPrincipalAdminActivity.class));
                            finish();
                        }
                        else {
                            databaseReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        String rol = snapshot.child("rol").getValue(String.class);
                                        if (rol != null) {
                                            switch (rol.toLowerCase()) {
                                                case "cocinero":
                                                    startActivity(new Intent(getBaseContext(), VistaPrincipalCocineros.class));
                                                    break;
                                                case "mesero":
                                                    startActivity(new Intent(getBaseContext(), VistaPrincipalMeserosActivity.class));
                                                    break;
                                                default:
                                                    Toast.makeText(LogImActivity.this, "Rol no reconocido", Toast.LENGTH_SHORT).show();
                                            }
                                            finish();
                                        } else {
                                            Toast.makeText(LogImActivity.this, "No se encontró el rol del usuario", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(LogImActivity.this, "No se encontró información del usuario", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(LogImActivity.this, "Error al leer la base de datos", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    }
                }
                        else {
                    String error = task.getException().getMessage();
                    if (error != null && error.contains("Contraseña no válida")) {
                        Toast.makeText(this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                    } else if (error != null && error.contains("There is no user record")) {
                        Toast.makeText(this, "No existe una cuenta con ese correo", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error al iniciar sesión: " + error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
/*
    private void RegisterGoogleLonIn(){
        GetGoogleIdOption getGoogleIdOption = new GetGoogleIdOption
                .Builder()
                .setFilterByAuthorizedAccounts(true)
                .setServerClientId("292287364842-jd4v5cido2fo4gufv5csmtcr57bp41mk.apps.googleusercontent.com")
                .build();
        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(getGoogleIdOption)
                .build();
        credentialManager.getCredentialAsync(
                getBaseContext(), request, new CancellationSignal(),
                Executors.newSingleThreadExecutor(),
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse getCredentialResponse) {
                        handleSignIn(getCredentialResponse.getCredential());
                    }
                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        Log.e("error" , e.getMessage());
                    }
                }
        );
    }
    private void handleSignIn(Credential credential) {
        if (credential instanceof CustomCredential){
            CustomCredential customCredential = (CustomCredential) credential;
            Bundle credentialData = customCredential.getData();
            GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credentialData);
            firebaseAuthWithGoogle(googleIdTokenCredential.getIdToken());
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();
                        FirebaseUser usuario = firebaseAuth.getCurrentUser();
                        if (isNewUser) {
                            Log.d("RegistroGoogle", "Usuario nuevo creado");
                            Intent intent = new Intent(this, LogImActivity.class);
                            startActivity(intent);
                        } else {
                            Log.d("LoginGoogle", "Usuario ya registrado");
                            Intent intent = new Intent(getBaseContext(), VistaPrincipalCocineros.class);
                            startActivity(intent);
                        }
                    } else {
                        Log.e("GoogleAuth", "Error en autenticación: " + task.getException().getMessage());
                        Toast.makeText(this, "Error al autenticar con Google", Toast.LENGTH_SHORT).show();
                    }
                });
    }
*/
}