package com.example.sistemadecomandas.vistasMeseros.ui.adapter;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sistemadecomandas.Modelos.Comanda;
import com.example.sistemadecomandas.R;
import com.example.sistemadecomandas.vistasMeseros.EditarComandaActivity;

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
    }

    @Override
    public int getItemCount() {
        return listaComandas.size();
    }

    public static class ComandaViewHolder extends RecyclerView.ViewHolder {
        TextView textCliente, textEstado, textFecha;
        ImageButton btnEditarComanda;

        public ComandaViewHolder(@NonNull View itemView) {
            super(itemView);
            textCliente = itemView.findViewById(R.id.textCliente);
            textEstado = itemView.findViewById(R.id.textEstado);
            textFecha = itemView.findViewById(R.id.textFecha);
            btnEditarComanda = itemView.findViewById(R.id.btnEditarComanda);

        }
    }
}
