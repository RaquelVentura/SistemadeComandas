package com.example.sistemadecomandas.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sistemadecomandas.R;
import com.google.firebase.auth.FirebaseAuth;

public class RecuperarContrasenaActivity extends AppCompatActivity {

    private EditText etCorreo;
    private Button btnEnviar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_contrasena);

        etCorreo = findViewById(R.id.etCorreo);
        btnEnviar = findViewById(R.id.btnEnviar);
        auth = FirebaseAuth.getInstance();

        btnEnviar.setOnClickListener(v -> {
            String email = etCorreo.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                etCorreo.setError("Por favor ingresa un correo");
                return;
            }

            auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(RecuperarContrasenaActivity.this,
                            "Correo de recuperación enviado a " + email,
                            Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(RecuperarContrasenaActivity.this,
                            "Error: " + task.getException().getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}
