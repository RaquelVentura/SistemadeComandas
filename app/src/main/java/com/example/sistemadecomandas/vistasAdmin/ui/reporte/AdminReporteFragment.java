package com.example.sistemadecomandas.vistasAdmin.ui.reporte;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.sistemadecomandas.Modelos.PlatilloComanda;
import com.example.sistemadecomandas.databinding.FragmentAdminReporteBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdminReporteFragment extends Fragment {

    private FragmentAdminReporteBinding binding;
    private FragmentManager manager;
    private TextView lbSinComandasHoy;
    private InformePlatosPopularesAdapter adapterPopulares;
    private InformeVentasDelDiaAdapter adapterVentas;

    private final List<PlatilloComanda> listaPopulares = new ArrayList<>();
    private final List<PlatilloComanda> listaVentas = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAdminReporteBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        manager = getParentFragmentManager();

        binding.recyclerPlatosPopulares.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapterPopulares = new InformePlatosPopularesAdapter(listaPopulares, getContext(), manager);
        binding.recyclerPlatosPopulares.setAdapter(adapterPopulares);

        binding.recyclerVentasDelDia.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapterVentas = new InformeVentasDelDiaAdapter(listaVentas, getContext(), manager);
        binding.recyclerVentasDelDia.setAdapter(adapterVentas);

        cargarComandasDesdeFirebase();

        return root;
    }

    private void cargarComandasDesdeFirebase() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Comandas");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaPopulares.clear();
                listaVentas.clear();

                HashMap<String, PlatilloComanda> mapaPlatillos = new HashMap<>();
                HashMap<String, Integer> contadorVendidos = new HashMap<>();
                HashMap<String, Double> dineroGenerado = new HashMap<>();

                for (DataSnapshot comandaSnap : snapshot.getChildren()) {
                    DataSnapshot platillosSnap = comandaSnap.child("platillos");

                    for (DataSnapshot platilloSnap : platillosSnap.getChildren()) {
                        PlatilloComanda platilloComanda = platilloSnap.getValue(PlatilloComanda.class);

                        if (platilloComanda != null && platilloComanda.getPlatillo() != null) {
                            String id = platilloComanda.getPlatillo().getIdPlatillo();

                            mapaPlatillos.putIfAbsent(id, platilloComanda);

                            int cantidad = contadorVendidos.getOrDefault(id, 0) + platilloComanda.getCantidad();
                            contadorVendidos.put(id, cantidad);

                            try {
                                double precio = Double.parseDouble(platilloComanda.getPlatillo().getPrecio());
                                double total = dineroGenerado.getOrDefault(id, 0.0) + platilloComanda.getCantidad() * precio;
                                dineroGenerado.put(id, total);
                            } catch (NumberFormatException ignored) {
                            }
                            listaVentas.add(platilloComanda);
                        }
                    }
                }

                if (listaVentas.isEmpty()) {
                    lbSinComandasHoy.setVisibility(View.VISIBLE);
                } else {
                }

                List<String> ordenIDs = new ArrayList<>(contadorVendidos.keySet());
                ordenIDs.sort((a, b) -> Integer.compare(contadorVendidos.get(b), contadorVendidos.get(a)));

                for (String id : ordenIDs) {
                    PlatilloComanda platillo = mapaPlatillos.get(id);
                    if (platillo != null) listaPopulares.add(platillo);
                }
                adapterPopulares.notifyDataSetChanged();
                adapterVentas.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
