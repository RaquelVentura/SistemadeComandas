package com.example.sistemadecomandas.vistasMeseros;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sistemadecomandas.Modelos.Comanda;
import com.example.sistemadecomandas.Modelos.Platillo;
import com.example.sistemadecomandas.Modelos.PlatilloComanda;
import com.example.sistemadecomandas.R;
import com.example.sistemadecomandas.vistasMeseros.ui.adapter.PlatilloAdapterSeleccion;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CrearComandaActivity extends AppCompatActivity implements PlatilloAdapterSeleccion.OnPlatilloSeleccionadoListener {

    private EditText etNombreCliente, etNota;
    private RecyclerView recyclerPlatillos;
    private Button btnCrearComanda;

    private List<Platillo> listaPlatillos = new ArrayList<>();
    private List<Platillo> platillosSeleccionados = new ArrayList<>();
    private List<PlatilloComanda> platillosconCantidad = new ArrayList<>();
    private PlatilloAdapterSeleccion adapter;

    private DatabaseReference dbPlatillos;
    private DatabaseReference dbComandas;

    private String meseroActual = "mesero1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_comanda);

        etNombreCliente = findViewById(R.id.etNombreCliente);
        etNota = findViewById(R.id.etNota);
        recyclerPlatillos = findViewById(R.id.recyclerPlatillos);
        btnCrearComanda = findViewById(R.id.btnCrearComanda);

        dbPlatillos = FirebaseDatabase.getInstance().getReference("platillos");
        dbComandas = FirebaseDatabase.getInstance().getReference("Comandas");

        recyclerPlatillos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PlatilloAdapterSeleccion(listaPlatillos, this);
        recyclerPlatillos.setAdapter(adapter);

        cargarPlatillosFirebase();

        btnCrearComanda.setOnClickListener(v -> crearComanda());
    }

    private void cargarPlatillosFirebase() {
        dbPlatillos.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaPlatillos.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Platillo p = ds.getValue(Platillo.class);
                    listaPlatillos.add(p);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CrearComandaActivity.this, "Error al cargar platillos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPlatilloSeleccionado(Platillo platillo, boolean seleccionado, int cantidad) {
        if (seleccionado) {
            platillosconCantidad.add(new PlatilloComanda(platillo, cantidad));
        } else {
            platillosconCantidad.removeIf(p -> p.getPlatillo().getIdPlatillo().equals(platillo.getIdPlatillo()));
        }
    }

    private void crearComanda() {
        String nombre = etNombreCliente.getText().toString().trim();
        String nota = etNota.getText().toString().trim();

        if (nombre.isEmpty()) {
            etNombreCliente.setError("Ingresa un nombre");
            etNombreCliente.requestFocus();
            return;
        }

        if (platillosconCantidad == null || platillosconCantidad.isEmpty()) {
            Toast.makeText(this, "Selecciona al menos un platillo", Toast.LENGTH_SHORT).show();
            return;
        }

        String idComanda = dbComandas.push().getKey();
        String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        double total = 0;
        for (PlatilloComanda pc : platillosconCantidad) {
            try {
                double precio = Double.parseDouble(pc.getPlatillo().getPrecio());
                total += precio * pc.getCantidad();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Comanda comanda = new Comanda();
        comanda.setCodigoComanda(idComanda);
        comanda.setFecha(fecha);
        comanda.setNombreCliente(nombre);
        comanda.setPlatillos(platillosconCantidad);
        comanda.setEstadoComanda("pendiente");
        comanda.setMesero(meseroActual);
        comanda.setNota(nota);
        comanda.setTotalPagar(String.format(Locale.getDefault(), "%.2f", total));

        dbComandas.child(idComanda).setValue(comanda)
                .addOnSuccessListener(unused -> {
                    DatabaseReference dbClientes = FirebaseDatabase.getInstance().getReference("clientes");

                    Map<String, Object> cliente = new HashMap<>();
                    cliente.put("codigoComanda", idComanda);
                    cliente.put("nombreCliente", nombre);

                    dbClientes.child(idComanda).setValue(cliente)
                            .addOnSuccessListener(aVoid -> {
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error al guardar cliente", Toast.LENGTH_SHORT).show();
                            });
                    Toast.makeText(this, "Comanda creada", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al guardar comanda", Toast.LENGTH_SHORT).show();
                });
    }

}
