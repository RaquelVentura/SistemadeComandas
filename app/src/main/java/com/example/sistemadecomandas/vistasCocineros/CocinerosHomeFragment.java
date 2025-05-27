package com.example.sistemadecomandas.vistasCocineros;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.sistemadecomandas.Modelos.Comanda;
import com.example.sistemadecomandas.R;
import com.example.sistemadecomandas.databinding.FragmentCocinerosHomeBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CocinerosHomeFragment extends Fragment {

    private Button btnFiltroPendiente, btnFiltroProceso, btnFiltroFinalizado;
    private List<Comanda> listaComandas;
    private ComandaAdapter adaptador;
    private FragmentManager manager;
    private FragmentCocinerosHomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCocinerosHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        btnFiltroPendiente = root.findViewById(R.id.btnPendiente);
        btnFiltroProceso = root.findViewById(R.id.btnEnProceso);
        btnFiltroFinalizado = root.findViewById(R.id.btnFinalizado);
        binding.recycleComandas.setLayoutManager(new LinearLayoutManager(getContext()));
        listaComandas = new ArrayList<>();
        adaptador = new ComandaAdapter(new ArrayList<>(), getContext(), manager);
        binding.recycleComandas.setAdapter(adaptador);

        cargarComandasFirebase();
        btnFiltroPendiente.setOnClickListener(v -> filtrarComandasPorEstado("Pendiente"));
        btnFiltroProceso.setOnClickListener(v -> filtrarComandasPorEstado("En proceso"));
        btnFiltroFinalizado.setOnClickListener(v -> filtrarComandasPorEstado("Finalizado"));

        return root;
    }

    private void cargarComandasFirebase() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Comandas");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaComandas.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Comanda comanda = snap.getValue(Comanda.class);
                    if (comanda != null) {
                        comanda.setCodigoComanda(snap.getKey());
                        listaComandas.add(comanda);
                    }
                }
                adaptador.actualizarLista(listaComandas);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void filtrarComandasPorEstado(String estado) {
        List<Comanda> filtradas = new ArrayList<>();
        for (Comanda comanda : listaComandas) {
            if (comanda.getEstadoComanda() != null &&
                    comanda.getEstadoComanda().equalsIgnoreCase(estado)) {
                filtradas.add(comanda);
            }
        }
        adaptador.actualizarLista(filtradas);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
