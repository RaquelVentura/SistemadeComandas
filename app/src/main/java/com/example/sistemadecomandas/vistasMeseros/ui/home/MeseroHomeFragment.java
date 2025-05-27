package com.example.sistemadecomandas.vistasMeseros.ui.home;
//VISTA DE COMANDAAAAAAAAAAAAAAS
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sistemadecomandas.Modelos.Comanda;
import com.example.sistemadecomandas.vistasMeseros.CrearComandaActivity;
import com.example.sistemadecomandas.databinding.FragmentMeserosHomeBinding;
import com.example.sistemadecomandas.vistasMeseros.ui.adapter.ComandaAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MeseroHomeFragment extends Fragment {

    private RecyclerView recyclerComandas;
    private ComandaAdapter adapter;
    private Button btnCrearComanda;
    private List<Comanda> listaComandas;
    private DatabaseReference databaseReference;
    private String meseroActual= "mesero1";
    private FragmentMeserosHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMeserosHomeBinding.inflate(inflater, container,false);
        View root = binding.getRoot();

        binding.recyclerComandas.setLayoutManager(new LinearLayoutManager(getContext()));
        listaComandas = new ArrayList<>();
        adapter = new ComandaAdapter(listaComandas);
        binding.recyclerComandas.setAdapter(adapter);
        databaseReference = FirebaseDatabase.getInstance().getReference("Comandas");

      binding.btnCrearComanda.setOnClickListener(v -> {
          Intent intent = new Intent(getActivity(), CrearComandaActivity.class);
          intent.putExtra("mesero",meseroActual);
          startActivity(intent);
      });

        cargarComandasMesero();

        return root;

    }

    private void cargarComandasMesero(){
        databaseReference.orderByChild("mesero").equalTo(meseroActual)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listaComandas.clear();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            Comanda comanda = data.getValue(Comanda.class);
                            listaComandas.add(comanda);
                        }
                        adapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error al cargar comandas", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}