package com.example.sistemadecomandas.vistasMeseros.ui.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sistemadecomandas.Modelos.Comanda;
import com.example.sistemadecomandas.R;

import java.util.List;

public class ComandaAdapter extends RecyclerView.Adapter<ComandaAdapter.ComandaViewHolder> {
    private List<Comanda> listaComandas;

    public ComandaAdapter(List<Comanda> listaComandas, Context context, FragmentManager manager) {
        this.listaComandas = listaComandas;
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
    }

    @Override
    public int getItemCount() {
        return listaComandas.size();
    }

    public static class ComandaViewHolder extends RecyclerView.ViewHolder {
        TextView textCliente, textEstado, textFecha;

        public ComandaViewHolder(@NonNull View itemView) {
            super(itemView);
            textCliente = itemView.findViewById(R.id.textCliente);
            textEstado = itemView.findViewById(R.id.textEstado);
            textFecha = itemView.findViewById(R.id.textFecha);
        }
    }
}
