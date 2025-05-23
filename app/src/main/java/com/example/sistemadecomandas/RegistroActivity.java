package com.example.sistemadecomandas;
import android.content.Intent;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import java.util.concurrent.Executors;


public class RegistroActivity extends AppCompatActivity {
    private VideoView videoView;
    private EditText txtEmail, txtPassword;
    private Button btnRegister, btnLoginWithGoogle;
    private ImageButton btnVolverLogin;
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
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);

        btnRegister = findViewById(R.id.btnRegister);
        btnLoginWithGoogle = findViewById(R.id.btnLoginWithGoogle);
        btnVolverLogin = findViewById(R.id.btnVolverVistaLogin);
        //videoView = view.findViewById(R.id.fondo);
        firebaseAuth = FirebaseAuth.getInstance();
        credentialManager = CredentialManager.create(getBaseContext());

        btnLoginWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterGoogle();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterUserAutentification();
            }
        });
        btnVolverLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), LogImActivity.class);
                startActivity(intent);
            }
        });
    }
    private void RegisterUserAutentification() {
        String correo = txtEmail.getText().toString().trim();
        String pass = txtPassword.getText().toString().trim();

        if (correo.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
        } else if (pass.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
        } else {
            firebaseAuth.createUserWithEmailAndPassword(correo, pass).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser usuarioCreado = firebaseAuth.getCurrentUser();
                    if (usuarioCreado != null) {
                        usuarioCreado.sendEmailVerification().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Toast.makeText(this, "Se envió un correo de verificación", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Fallo el envío de verificación", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Toast.makeText(this, "Usuario creado correctamente", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this,MenuPrincipalActivity.class);
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

    private void RegisterGoogle(){
        GetGoogleIdOption getGoogleIdOption = new GetGoogleIdOption
                .Builder()
                .setFilterByAuthorizedAccounts(false)
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
                    if(task.isSuccessful()){
                        boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();
                        if (isNewUser) {
                            Log.d("exito", "USUARIO NUEVO REGISTRADO");
                            Intent intent = new Intent(this, MenuPrincipalActivity.class);
                            startActivity(intent);
                        } else {
                            Log.d("info", "Usuario ya existía, no se avanza a Activity2");
                            Toast.makeText(this, "Ya existe una cuenta con ese correo", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Log.d("error", "fallo la autenticacion");
                        Toast.makeText(this, "Error en la autenticación con Google", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}