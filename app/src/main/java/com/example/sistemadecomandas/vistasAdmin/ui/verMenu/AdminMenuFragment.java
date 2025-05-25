package com.example.sistemadecomandas.vistasAdmin.ui.verMenu;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.sistemadecomandas.Modelos.Platillo;
import com.example.sistemadecomandas.R;
import com.example.sistemadecomandas.vistasAdmin.Adapters.MenuAdaptador;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminMenuFragment extends Fragment {

    private RecyclerView recyclerView;
    private Button btnNuevoPlatillo;
    private ImageButton btnCategoriaEntrada, btnCategoriaDesayuno, btnCategoriaPlatoFuerte, btnCategoriaPostre;
    private MenuAdaptador adaptador;
    private List<Platillo> listaPlatillos;
    private List<Platillo> listaPlatillosOriginal; // <- lista completa sin filtrar

    public AdminMenuFragment() { }

    public static AdminMenuFragment newInstance(String param1, String param2) {
        return new AdminMenuFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_menu, container, false);

        recyclerView = view.findViewById(R.id.recyclerMenu);

        btnNuevoPlatillo = view.findViewById(R.id.btnNuevoPlatillo);
        btnCategoriaEntrada = view.findViewById(R.id.btnEntrada);
        btnCategoriaDesayuno = view.findViewById(R.id.btnDesayuno);
        btnCategoriaPlatoFuerte = view.findViewById(R.id.btnPlatoFuerte);
        btnCategoriaPostre = view.findViewById(R.id.btnPostre);


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        listaPlatillos = new ArrayList<>();
        listaPlatillosOriginal = new ArrayList<>();

        // Adaptador base (todos los platillos)
        adaptador = new MenuAdaptador(listaPlatillos, getContext(), getParentFragmentManager());
        recyclerView.setAdapter(adaptador);

        cargarPlatillosDesdeFirebase();

        // Botón para crear nuevo platillo
        btnNuevoPlatillo.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), PlatilloActivity.class);
            startActivity(intent);
        });

        // Botón para filtrar por categoría
        btnCategoriaEntrada.setOnClickListener(v -> {
            filtrarPorCategoria("Entrada"); // <- podés cambiar esto a la categoría que necesites
        });
        btnCategoriaDesayuno.setOnClickListener(v -> {
            filtrarPorCategoria("Desayuno"); // <- podés cambiar esto a la categoría que necesites
        });
        btnCategoriaPlatoFuerte.setOnClickListener(v -> {
            filtrarPorCategoria("Plato Fuerte"); // <- podés cambiar esto a la categoría que necesites
        });
        btnCategoriaPostre.setOnClickListener(v -> {
            filtrarPorCategoria("Postre"); // <- podés cambiar esto a la categoría que necesites
        });


        return view;
    }

    private void cargarPlatillosDesdeFirebase() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("platillos");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaPlatillos.clear();
                listaPlatillosOriginal.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Platillo platillo = ds.getValue(Platillo.class);
                    if (platillo != null) {
                        listaPlatillos.add(platillo);
                        listaPlatillosOriginal.add(platillo);
                    }
                }

                adaptador.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void filtrarPorCategoria(String categoria) {
        List<Platillo> filtrados = new ArrayList<>();
        for (Platillo p : listaPlatillosOriginal) {
            if (p.getCategoria() != null && p.getCategoria().equalsIgnoreCase(categoria)) {
                filtrados.add(p);
            }
        }

        // Nuevo adaptador con la lista filtrada
        adaptador = new MenuAdaptador(filtrados, getContext(), getParentFragmentManager());
        recyclerView.setAdapter(adaptador);
    }
}
