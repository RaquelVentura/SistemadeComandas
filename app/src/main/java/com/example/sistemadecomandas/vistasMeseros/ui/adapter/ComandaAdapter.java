package com.example.sistemadecomandas.vistasMeseros.ui.adapter;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sistemadecomandas.Modelos.Comanda;
import com.example.sistemadecomandas.R;
import com.example.sistemadecomandas.vistasMeseros.EditarComandaActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ComandaAdapter extends RecyclerView.Adapter<ComandaAdapter.ComandaViewHolder> {
    private List<Comanda> listaComandas;
    private Context context;


    public ComandaAdapter(Context context, List<Comanda> listaComandas) {
        this.listaComandas = listaComandas;
        this.context = context;
    }

    @NonNull
    @Override
    public ComandaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comanda,parent, false);
        return new ComandaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComandaViewHolder holder, int position) {
        Comanda comanda = listaComandas.get(position);

        holder.textCliente.setText("Cliente:" + comanda.getNombreCliente());
        holder.textEstado.setText("Estado: " + comanda.getEstadoComanda());
        holder.textFecha.setText("Fecha: "+ comanda.getFecha());

        holder.btnEditarComanda.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditarComandaActivity.class);
            intent.putExtra("comanda", comanda); // Asegúrate que Comanda implemente Serializable o Parcelable
            context.startActivity(intent);
        });

        holder.btnEliminarComanda.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Eliminar Comanda")
                    .setMessage("¿Estás seguro de que deseas eliminar esta comanda?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Comandas");
                        dbRef.child(comanda.getCodigoComanda()).removeValue()
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(context, "Comanda eliminada", Toast.LENGTH_SHORT).show();
                                    listaComandas.remove(position);
                                    notifyItemRemoved(position);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Error al eliminar comanda", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

    }

    @Override
    public int getItemCount() {
        return listaComandas.size();
    }

    public static class ComandaViewHolder extends RecyclerView.ViewHolder {
        TextView textCliente, textEstado, textFecha;
        ImageButton btnEditarComanda, btnEliminarComanda;

        public ComandaViewHolder(@NonNull View itemView) {
            super(itemView);
            textCliente = itemView.findViewById(R.id.textCliente);
            textEstado = itemView.findViewById(R.id.textEstado);
            textFecha = itemView.findViewById(R.id.textFecha);
            btnEditarComanda = itemView.findViewById(R.id.btnEditarComanda);
            btnEliminarComanda = itemView.findViewById(R.id.btnEliminarComanda);


        }
    }
}
