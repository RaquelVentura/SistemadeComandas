package com.example.sistemadecomandas.vistasMeseros;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EditarComandaActivity extends AppCompatActivity {

    private EditText etNombreCliente, etNota;
    private RecyclerView recyclerPlatillos;
    private Button btnActualizarComanda;


    private DatabaseReference dbComandas, dbPlatillos;
    private List<Platillo> listaPlatillos = new ArrayList<>();
    private List<PlatilloComanda> platillosConCantidad = new ArrayList<>();
    private PlatilloAdapterSeleccion adapter;
    private Comanda comandaActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_comanda);

        etNombreCliente = findViewById(R.id.etNombreCliente);
        etNota= findViewById(R.id.etNota);
        recyclerPlatillos = findViewById(R.id.recyclerPlatillos);
        btnActualizarComanda = findViewById(R.id.btnActualizarComanda);

        dbComandas = FirebaseDatabase.getInstance().getReference("Comandas");
        dbPlatillos = FirebaseDatabase.getInstance().getReference("platillos");

        comandaActual = (Comanda) getIntent().getSerializableExtra("comanda");

        if (comandaActual == null){
            Toast.makeText(this, "Error: comanda no encontrada", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etNombreCliente.setText(comandaActual.getNombreCliente());
        etNota.setText(comandaActual.getNota());

        recyclerPlatillos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PlatilloAdapterSeleccion(listaPlatillos, ((platillo, seleccionado, cantidad) -> {
            PlatilloComanda pc = new PlatilloComanda( platillo, cantidad);

            if (seleccionado){
                agregarOActualizarPlatillo(pc);
            }else
            {
                eliminarPlatillo(pc.getPlatillo().getIdPlatillo());
            }
        }));

        recyclerPlatillos.setAdapter(adapter);

        cargarPlatillosDisponibles();

        btnActualizarComanda.setOnClickListener(v -> actualizarComanda());

    }

    private void cargarPlatillosDisponibles(){
        dbPlatillos.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaPlatillos.clear();
                for (DataSnapshot data : snapshot.getChildren()){
                    Platillo p =data.getValue(Platillo.class);
                    if (p !=null) listaPlatillos.add(p);
                }

                for (Platillo platillo : listaPlatillos){
                    for (PlatilloComanda pc : comandaActual.getPlatillos()){
                        if (platillo.getIdPlatillo().equals(pc.getPlatillo().getIdPlatillo())){
                            platillosConCantidad.add(pc);
                            break;
                        }
                    }
                }
                adapter.setPlatillosConCantidad(platillosConCantidad);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditarComandaActivity.this, "Error al cargar el platillo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void agregarOActualizarPlatillo(PlatilloComanda pc){
        for (int i =0; i < platillosConCantidad.size(); i++){
            if (platillosConCantidad.get(i).getPlatillo().getIdPlatillo().equals(pc.getPlatillo().getIdPlatillo())){
                platillosConCantidad.set(i, pc);
                return;
            }
        }
        platillosConCantidad.add(pc);
    }

    private void eliminarPlatillo(String idPlatillo){
        platillosConCantidad.removeIf(p -> p.getPlatillo().getIdPlatillo().equals(idPlatillo));
    }

    private void actualizarComanda() {
        String nombre = etNombreCliente.getText().toString().trim();
        String nota = etNota.getText().toString().trim();

        if (nombre.isEmpty()) {
            etNombreCliente.setError("Ingresa un nombre");
            etNombreCliente.requestFocus();
            return;
        }

        if (platillosConCantidad.isEmpty()) {
            Toast.makeText(this, "Selecciona al menos un platillo", Toast.LENGTH_SHORT).show();
            return;
        }

        double total = 0;
        for (PlatilloComanda pc : platillosConCantidad) {
            try {
                double precio = Double.parseDouble(pc.getPlatillo().getPrecio());
                total += precio * pc.getCantidad();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        comandaActual.setNombreCliente(nombre);
        comandaActual.setNota(nota);
        comandaActual.setPlatillos(platillosConCantidad);
        comandaActual.setTotalPagar(String.format(Locale.getDefault(), "%.2f", total));

        dbComandas.child(comandaActual.getCodigoComanda()).setValue(comandaActual)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Comanda actualizada", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al actualizar comanda", Toast.LENGTH_SHORT).show();
                });
    }

    private void confirmarEliminarComanda() {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Comanda")
                .setMessage("¿Estás seguro de que deseas eliminar esta comanda?")
                .setPositiveButton("Sí", (dialog, which) -> eliminarComanda())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarComanda() {
        dbComandas.child(comandaActual.getCodigoComanda()).removeValue()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Comanda eliminada correctamente", Toast.LENGTH_SHORT).show();
                    finish(); // Cierra la pantalla
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al eliminar la comanda", Toast.LENGTH_SHORT).show();
                });
    }

}