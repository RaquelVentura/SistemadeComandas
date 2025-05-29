package com.example.sistemadecomandas.vistasCocineros;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.sistemadecomandas.Modelos.Comanda;
import com.example.sistemadecomandas.R;
import com.example.sistemadecomandas.databinding.FragmentCocinerosHomeBinding;

import com.example.sistemadecomandas.vistasCocineros.adapters.ComandaAdapter;

import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.core.app.NotificationCompat;

import android.content.pm.PackageManager;
import java.util.ArrayList;
import java.util.List;

public class CocinerosHomeFragment extends Fragment implements ComandaAdapter.OnAdjuntarImagenListener {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISO_NOTIFICACIONES = 123;
    private static final String CANAL_ID = "canal_comandas";
    private String comandaIdParaSubirImagen;
    private Uri imagenSeleccionada;
    private TextView btnMostrarTodos;

    private Button btnFiltroPendiente, btnFiltroProceso, btnFiltroFinalizado;
    private List<Comanda> listaComandas;
    private ComandaAdapter adaptador;
    private FragmentManager manager;
    private FragmentCocinerosHomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        crearCanalNotificacion();
        solicitarPermisoNotificaciones();

        binding = FragmentCocinerosHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        manager = getParentFragmentManager();

        btnFiltroPendiente = root.findViewById(R.id.btnPendiente);
        btnFiltroProceso = root.findViewById(R.id.btnEnProceso);
        btnFiltroFinalizado = root.findViewById(R.id.btnFinalizado);
        btnMostrarTodos = root.findViewById(R.id.btnVerTodasComandas);
        binding.recycleComandas.setLayoutManager(new LinearLayoutManager(getContext()));
        listaComandas = new ArrayList<>();

        adaptador = new ComandaAdapter(new ArrayList<>(), getContext(), manager, this);

        binding.recycleComandas.setAdapter(adaptador);

        cargarComandasFirebase();
        btnFiltroPendiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filtrarComandasPorEstado("Pendiente");
                notificacion();
            }
        });
        btnFiltroProceso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filtrarComandasPorEstado("En proceso");
                notificacion();
            }
        });

        btnFiltroFinalizado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filtrarComandasPorEstado("Finalizado");
                notificacion();
            }
        });
        btnMostrarTodos.setOnClickListener(v -> {
            adaptador.actualizarLista(listaComandas);
        });
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

    public void notificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                Log.w("NOTIF", "Permiso de notificaciones no concedido");
                return;
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), "canal_comandas")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Estado actualizado")
                .setContentText("El estado de una comanda ha cambiado.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
        try {
            notificationManager.notify(1001, builder.build());
        } catch (Exception e) {
            Log.e("NOTIF_ERROR", "Error mostrando notificación", e);
        }
    }

    private void crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CANAL_ID,
                    "Canal de Comandas",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Canal para notificaciones de estado de comandas");
            NotificationManager notificationManager = requireContext().getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void solicitarPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISO_NOTIFICACIONES);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISO_NOTIFICACIONES) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("NOTIF", "Permiso concedido");
            } else {
                Log.w("NOTIF", "Permiso denegado");
            }
        }
    }
}