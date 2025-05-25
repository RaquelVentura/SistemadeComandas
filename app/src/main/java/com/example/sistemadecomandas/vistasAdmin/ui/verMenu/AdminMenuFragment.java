package com.example.sistemadecomandas.vistasAdmin.ui.verMenu;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.sistemadecomandas.Modelos.Platillo;
import com.example.sistemadecomandas.R;
import com.example.sistemadecomandas.vistasAdmin.Adapters.GestionUsuarioAdaptador;
import com.example.sistemadecomandas.vistasAdmin.Adapters.MenuAdaptador;
import com.example.sistemadecomandas.vistasAdmin.ui.verMenu.PlatilloActivity;
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
    private MenuAdaptador adaptador;
    private List<Platillo> listaPlatillos;
    public AdminMenuFragment() {
    }

    public static AdminMenuFragment newInstance(String param1, String param2) {
        AdminMenuFragment fragment = new AdminMenuFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_admin_menu, container, false);
        recyclerView = view.findViewById(R.id.recyclerMenu);
        btnNuevoPlatillo = view.findViewById(R.id.btnNuevoPlatillo);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        listaPlatillos = new ArrayList<>();
        adaptador = new MenuAdaptador(listaPlatillos, getContext(), getParentFragmentManager());
        recyclerView.setAdapter(adaptador);
        cargarPlatillosDesdeFirebase();

        btnNuevoPlatillo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PlatilloActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
    private void cargarPlatillosDesdeFirebase() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("platillos");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaPlatillos.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Platillo platillo = ds.getValue(Platillo.class);
                    listaPlatillos.add(platillo);
                }
                adaptador.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}