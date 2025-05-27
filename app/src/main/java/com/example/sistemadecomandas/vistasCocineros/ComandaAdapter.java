package com.example.sistemadecomandas.vistasCocineros;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sistemadecomandas.Modelos.Comanda;
import com.example.sistemadecomandas.Modelos.Platillo;
import com.example.sistemadecomandas.R;
import com.example.sistemadecomandas.vistasAdmin.Adapters.MenuAdaptador;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ComandaAdapter extends RecyclerView.Adapter<ComandaAdapter.ComandaViewHolder> {
    private List<Comanda> listaComandas;
    private Context context;
    private FragmentManager manager;

    public ComandaAdapter(List<Comanda> listaComandas, Context context, FragmentManager manager) {
        this.listaComandas = listaComandas;
        this.context = context;
        this.manager = manager;
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
                mostrarDialogoCambioEstado("en preparación", comanda.getCodigoComanda(), holder);
            } else if (estadoActual.equals("en preparación")) {
                mostrarDialogoCambioEstado("finalizado", comanda.getCodigoComanda(), holder);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listaComandas.size();
    }

    public class ComandaViewHolder extends RecyclerView.ViewHolder {
        private TextView txtNombreCliente, txtFecha, txtEstado, txtNota;
        private RecyclerView recyclerPlatos;
        private Button btnCambiarEstado;
        public ComandaViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombreCliente = itemView.findViewById(R.id.txtNombreClienteComanda);
            txtFecha = itemView.findViewById(R.id.txtFechaComanda);
            txtNota = itemView.findViewById(R.id.txtNota);
            txtEstado = itemView.findViewById(R.id.txtEstado);
            recyclerPlatos = itemView.findViewById(R.id.recylerPlatosPorClientes);
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
            case "en preparación":
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
            case "en preparación":
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

}
