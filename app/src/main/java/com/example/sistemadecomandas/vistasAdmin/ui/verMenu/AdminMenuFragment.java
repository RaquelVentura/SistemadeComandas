package com.example.sistemadecomandas.vistasAdmin.ui.verMenu;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.GridLayoutManager;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;

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
    private TextView btnVerLIstaCompleta;
    private Button btnNuevoPlatillo;
    private ImageButton btnCategoriaEntrada, btnCategoriaDesayuno, btnCategoriaPlatoFuerte, btnCategoriaPostre, btnCategoriaBebida;
    private MenuAdaptador adaptador;
    private List<Platillo> listaPlatillosOriginal;
    private SearchView searchView;

    public AdminMenuFragment() {}

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
        btnCategoriaBebida = view.findViewById(R.id.btnBebidas);
        btnVerLIstaCompleta = view.findViewById(R.id.btnVerTodosItems);
        searchView = view.findViewById(R.id.searchView);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        listaPlatillosOriginal = new ArrayList<>();

        adaptador = new MenuAdaptador(new ArrayList<>(), getContext(), getParentFragmentManager());
        recyclerView.setAdapter(adaptador);

        cargarPlatillosDesdeFirebase();

        btnNuevoPlatillo.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), PlatilloActivity.class);
            startActivity(intent);
        });

        // Botones para filtrar por categoría
        btnCategoriaEntrada.setOnClickListener(v -> filtrarPorCategoria("Entrada"));
        btnCategoriaDesayuno.setOnClickListener(v -> filtrarPorCategoria("Desayuno"));
        btnCategoriaPlatoFuerte.setOnClickListener(v -> filtrarPorCategoria("Plato fuerte"));
        btnCategoriaPostre.setOnClickListener(v -> filtrarPorCategoria("Postre"));
        btnCategoriaBebida.setOnClickListener(v -> filtrarPorCategoria("Bebida"));

        btnVerLIstaCompleta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adaptador.actualizarLista(new ArrayList<>(listaPlatillosOriginal));
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filtrarPlatillos(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    adaptador.actualizarLista(new ArrayList<>(listaPlatillosOriginal));
                } else {
                    List<Platillo> filtrados = new ArrayList<>();
                    String query = newText.toLowerCase();
                    for (Platillo p : listaPlatillosOriginal) {
                        if (p.getnombrePlatillo() != null &&
                                p.getnombrePlatillo().toLowerCase().contains(query) || p.getDescripcion().toLowerCase().contains(query)  || p.getPrecio().contains(query)) {
                            filtrados.add(p);
                        }
                    }
                    adaptador.actualizarLista(filtrados);
                }
                return true;
            }
        });

        return view;
    }

    private void cargarPlatillosDesdeFirebase() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("platillos");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaPlatillosOriginal.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Platillo platillo = ds.getValue(Platillo.class);
                    if (platillo != null) {
                        listaPlatillosOriginal.add(platillo);
                    }
                }

                adaptador.actualizarLista(new ArrayList<>(listaPlatillosOriginal)); // muestra todos
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void filtrarPorCategoria(String categoria) {
        List<Platillo> filtrados = new ArrayList<>();
        for (Platillo p : listaPlatillosOriginal) {
            if (p.getCategoria() != null && p.getCategoria().equalsIgnoreCase(categoria)) {
                filtrados.add(p);
            }
        }
        adaptador.actualizarLista(filtrados);
    }

    private void filtrarPlatillos(String texto) {
        List<Platillo> filtrados = new ArrayList<>();
        String query = texto.toLowerCase();
        for (Platillo p : listaPlatillosOriginal) {
            boolean coincideNombre = p.getnombrePlatillo() != null && p.getnombrePlatillo().toLowerCase().contains(query);
            boolean coincideDescripcion = p.getDescripcion() != null && p.getDescripcion().toLowerCase().contains(query);
            boolean coincidePrecio = p.getPrecio() != null && p.getPrecio().toLowerCase().contains(query);
            boolean coincideCategoria = p.getCategoria() != null && p.getCategoria().toLowerCase().contains(query);

            if (coincideNombre || coincideDescripcion || coincidePrecio || coincideCategoria) {
                filtrados.add(p);
            }
        }
        adaptador.actualizarLista(filtrados);
    }

}
