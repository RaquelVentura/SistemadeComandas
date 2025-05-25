package com.example.sistemadecomandas.vistasAdmin.ui.verMenu;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sistemadecomandas.Modelos.Platillo;
import com.example.sistemadecomandas.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

public class PlatilloActivity extends AppCompatActivity {

    private EditText txtNombrePlato, txtDecripcion, txtPrecio;
    private Spinner spCategoria;
    private Button btnAceptarPla;
    private FirebaseAuth firebaseAuth;
    private ImageView imageView;
    private String img, nombre, categoria, descripcion, precio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platillo);

        txtNombrePlato = findViewById(R.id.txtNombrePlato);
        spCategoria = findViewById(R.id.spCategoria);
        txtDecripcion = findViewById(R.id.txtDescricion);
        txtPrecio = findViewById(R.id.txtPrecio);
        btnAceptarPla = findViewById(R.id.btnAceptarPLa);

        firebaseAuth = FirebaseAuth.getInstance();

        llenarSpinner();

        btnAceptarPla.setOnClickListener(v -> {
            convertirAString();
            if (validarCampos()) {
                insertarPlato();
            }
        });
    }

    private void llenarSpinner() {
        List<String> listCategoria = Arrays.asList("Entrada", "Desayuno", "Plato fuerte", "Postre");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listCategoria);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategoria.setAdapter(adapter);
    }

    private void convertirAString(){
        img = "img_por_defecto_usuario";
        nombre = txtNombrePlato.getText().toString().trim();
        categoria = spCategoria.getSelectedItem().toString();
        descripcion = txtDecripcion.getText().toString().trim();
        precio = txtPrecio.getText().toString().trim();
    }

    private boolean validarCampos() {
        if (nombre.isEmpty() || descripcion.isEmpty() || precio.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void insertarPlato() {
        DatabaseReference platoRef = FirebaseDatabase.getInstance().getReference("platillos");

        String platilloId = platoRef.push().getKey();
        Platillo nuevoPlatillo = new Platillo(platilloId, img, nombre, categoria, precio, descripcion);;

        platoRef.child(platilloId).setValue(nuevoPlatillo)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Platillo creado exitosamente", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al crear platillo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
