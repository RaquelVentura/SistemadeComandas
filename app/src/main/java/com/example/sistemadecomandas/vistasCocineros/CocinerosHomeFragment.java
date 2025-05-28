package com.example.sistemadecomandas.vistasCocineros;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.sistemadecomandas.Modelos.Comanda;
import com.example.sistemadecomandas.R;
import com.example.sistemadecomandas.databinding.FragmentCocinerosHomeBinding;

import com.example.sistemadecomandas.vistasCocineros.adapters.ComandaAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CocinerosHomeFragment extends Fragment implements ComandaAdapter.OnAdjuntarImagenListener {
    private static final int PICK_IMAGE_REQUEST = 1;
    private String comandaIdParaSubirImagen;
    private Uri imagenSeleccionada;

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
        manager = getParentFragmentManager();

        btnFiltroPendiente = root.findViewById(R.id.btnPendiente);
        btnFiltroProceso = root.findViewById(R.id.btnEnProceso);
        btnFiltroFinalizado = root.findViewById(R.id.btnFinalizado);
        binding.recycleComandas.setLayoutManager(new LinearLayoutManager(getContext()));
        listaComandas = new ArrayList<>();

        adaptador = new ComandaAdapter(new ArrayList<>(), getContext(), manager, this);

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

    @Override
    public void onAdjuntarImagen(String idComanda) {
        this.comandaIdParaSubirImagen = idComanda;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imagenSeleccionada = data.getData();

            if (comandaIdParaSubirImagen != null) {
                subirImagenAFirebase(imagenSeleccionada, comandaIdParaSubirImagen);
            }
        }
    }
    private void subirImagenAFirebase(Uri uriImagen, String idComanda) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("adjuntos_comandas/" + idComanda);
        String nombreArchivo = System.currentTimeMillis() + ".jpg";
        StorageReference archivoRef = storageRef.child(nombreArchivo);

        archivoRef.putFile(uriImagen)
                .addOnSuccessListener(taskSnapshot -> archivoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    DatabaseReference adjuntosRef = FirebaseDatabase.getInstance()
                            .getReference("AdjuntosComanda")
                            .child(idComanda)
                            .child("urlsImagenes");

                    String key = adjuntosRef.push().getKey();
                    if (key != null) {
                        adjuntosRef.child(key).setValue(uri.toString()).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                requireActivity().runOnUiThread(() -> {
                                    if (adaptador != null) {
                                        adaptador.mostrarAdjuntosSiHay(idComanda);
                                    }
                                });
                            }
                        });
                    }
                }));
    }

}
