package com.example.sistemadecomandas.vistasCocineros.adapters;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;

import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sistemadecomandas.Modelos.ArchivosAdjuntosPorComanda;
import com.example.sistemadecomandas.Modelos.Comanda;
import com.example.sistemadecomandas.R;

import com.example.sistemadecomandas.vistasCocineros.AdjuntosAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ComandaAdapter extends RecyclerView.Adapter<ComandaAdapter.ComandaViewHolder> {
    private List<Comanda> listaComandas;
    private Context context;
    private FragmentManager manager;
    public interface OnAdjuntarImagenListener {
        void onAdjuntarImagen(String idComanda);
    }
    private OnAdjuntarImagenListener adjuntarImagenListener;
    public ComandaAdapter(List<Comanda> listaComandas, Context context, FragmentManager manager) {
        this.listaComandas = listaComandas;
        this.context = context;
        this.manager = manager;
    }
    public ComandaAdapter(List<Comanda> listaComandas, Context context, FragmentManager manager, OnAdjuntarImagenListener listener) {
        this.listaComandas = listaComandas;
        this.context = context;
        this.manager = manager;
        this.adjuntarImagenListener = listener;
    }
    @NonNull
    @Override
    public ComandaAdapter.ComandaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comanda_cocineros, parent, false);
        return new ComandaViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ComandaAdapter.ComandaViewHolder holder, int position) {
        Comanda comanda = listaComandas.get(position);

        holder.txtNombreCliente.setText(comanda.getNombreCliente());
        holder.txtFecha.setText(comanda.getFecha());
        holder.txtEstado.setText(comanda.getEstadoComanda());
        holder.txtNota.setText(comanda.getNota());

        PlatillosClienteAdapter platosAdapter = new PlatillosClienteAdapter(comanda.getPlatillos(), context, manager);
        holder.recyclerPlatos.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.recyclerPlatos.setAdapter(platosAdapter);

        holder.btnCambiarEstado.setText(getTextoBoton(comanda.getEstadoComanda()));
        holder.btnCambiarEstado.setBackgroundTintList(ContextCompat.getColorStateList(context, getColorBoton(comanda.getEstadoComanda())));

        holder.btnCambiarEstado.setOnClickListener(view -> {
            String estadoActual = comanda.getEstadoComanda();
            if (estadoActual.equals("pendiente")) {
                mostrarDialogoCambioEstado("En proceso", comanda.getCodigoComanda(), holder);

            } else if (estadoActual.equals("En proceso")) {
                mostrarDialogoCambioEstado("finalizado", comanda.getCodigoComanda(), holder);
            }
        });

    //-------------------------------------------------------------------------------------------------
        holder.recyclerAdjuntos.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.recyclerAdjuntos.setVisibility(View.GONE);
        holder.txtMostrar.setVisibility(View.GONE);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("AdjuntosComanda");
        ref.child(comanda.getCodigoComanda()).get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                ArchivosAdjuntosPorComanda adjuntos = snapshot.getValue(ArchivosAdjuntosPorComanda.class);
                if (adjuntos != null && adjuntos.getUrlsImagenes() != null && !adjuntos.getUrlsImagenes().isEmpty()) {
                    AdjuntosAdapter adjuntosAdapter = new AdjuntosAdapter(adjuntos.getListaUrls(), context);

                    holder.recyclerAdjuntos.setAdapter(adjuntosAdapter);

                    holder.txtMostrar.setVisibility(View.VISIBLE);
                    holder.recyclerAdjuntos.setVisibility(View.VISIBLE);
                    holder.txtMostrar.setText("Ocultar archivos adjuntos");
                }
            }
        });
        holder.txtMostrar.setOnClickListener(v -> {
            boolean visible = holder.recyclerAdjuntos.getVisibility() == View.VISIBLE;
            holder.recyclerAdjuntos.setVisibility(visible ? View.GONE : View.VISIBLE);
            holder.txtMostrar.setText(visible ? "Mostrar archivos adjuntos" : "Ocultar archivos adjuntos");
        });

        holder.btnImgAdjuntos.setOnClickListener(v -> {
            if (adjuntarImagenListener != null) {
                adjuntarImagenListener.onAdjuntarImagen(comanda.getCodigoComanda());
            }
        });
    }
    @Override
    public int getItemCount() {
        return listaComandas.size();
    }
    public class ComandaViewHolder extends RecyclerView.ViewHolder {
        private ImageButton btnImgAdjuntos;
        private TextView txtNombreCliente, txtFecha, txtEstado, txtNota, txtMostrar;
        private RecyclerView recyclerPlatos, recyclerAdjuntos;
        private Button btnCambiarEstado;
        public ComandaViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombreCliente = itemView.findViewById(R.id.txtNombreClienteComanda);
            txtFecha = itemView.findViewById(R.id.txtFechaComanda);
            txtNota = itemView.findViewById(R.id.txtNota);
            txtMostrar = itemView.findViewById(R.id.txtMostrarArchivos);
            txtEstado = itemView.findViewById(R.id.txtEstado);
            btnImgAdjuntos = itemView.findViewById(R.id.btnAdjuntarFotos);
            recyclerPlatos = itemView.findViewById(R.id.recylerPlatosPorClientes);
            recyclerAdjuntos = itemView.findViewById(R.id.recyclerAdjuntos);
            btnCambiarEstado = itemView.findViewById(R.id.btnCambiarEstado);

        }
    }
    private void mostrarDialogoCambioEstado(String nuevoEstado, String idComanda, ComandaViewHolder holder) {
        new AlertDialog.Builder(context)
                .setTitle("Cambiar estado")
                .setMessage("Quiere cambiar el estado a '" + nuevoEstado + "'?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Comandas").child(idComanda);
                    ref.child("estadoComanda").setValue(nuevoEstado);

                    holder.txtEstado.setText(nuevoEstado);
                    holder.btnCambiarEstado.setText(getTextoBoton(nuevoEstado));
                    holder.btnCambiarEstado.setBackgroundTintList(ContextCompat.getColorStateList(context, getColorBoton(nuevoEstado)));
                    notificacion();
                })
                .setNegativeButton("Cancelar", null)
                .show();
        if (nuevoEstado.equals("finalizado")) {
            holder.btnCambiarEstado.setEnabled(false);
        }

    }

    private String getTextoBoton(String estado) {
        switch (estado) {
            case "pendiente":
                return "Iniciar preparación";
            case "En proceso":
                return "Finalizar";
            case "finalizado":
                return "Finalizado";
            default:
                return "Actualizar";
        }
    }
    private int getColorBoton(String estado) {
        switch (estado) {
            case "pendiente":
                return android.R.color.holo_blue_light;
            case "En proceso":
                return android.R.color.holo_orange_light;
            case "finalizado":
                return android.R.color.holo_green_dark;
            default:
                return android.R.color.darker_gray;
        }
    }
    public void actualizarLista(List<Comanda> nuevasComandas) {
        this.listaComandas.clear();
        this.listaComandas.addAll(nuevasComandas);
        notifyDataSetChanged();
    }
    public void mostrarAdjuntosSiHay(String comandaId) {
        int index = -1;
        for (int i = 0; i < listaComandas.size(); i++) {
            if (listaComandas.get(i).getCodigoComanda().equals(comandaId)) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            notifyItemChanged(index);
        }
    }    public void notificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                Log.w("NOTIF", "Permiso de notificaciones no concedido");
                return;
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "canal_comandas")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Estado actualizado")
                .setContentText("El estado de una comanda ha cambiado.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        try {
            notificationManager.notify(1001, builder.build());
        } catch (Exception e) {
            Log.e("NOTIF_ERROR", "Error mostrando notificación", e);
        }
    }


}