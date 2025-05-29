package com.example.sistemadecomandas.vistasCocineros.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sistemadecomandas.Modelos.Platillo;
import com.example.sistemadecomandas.Modelos.PlatilloComanda;
import com.example.sistemadecomandas.R;

import java.util.List;

public class PlatillosClienteAdapter extends RecyclerView.Adapter<PlatillosClienteAdapter.PlatilloClienteViewHolder> {

    private List<PlatilloComanda> platilloComandaList;
    private Context context;
    private FragmentManager manager;
    private String notaComanda;


    public PlatillosClienteAdapter(List<PlatilloComanda> platilloComandaList, Context context, FragmentManager manager) {
        this.platilloComandaList = platilloComandaList;
        this.context = context;
        this.manager = manager;
    }

    @NonNull
    @Override
    public PlatilloClienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recycler_platos_cocineros, parent, false);
        return new PlatilloClienteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlatilloClienteViewHolder holder, int position) {
        PlatilloComanda platilloComanda = platilloComandaList.get(position);
        Platillo platillo = platilloComanda.getPlatillo();

        if (platillo != null) {
            Log.d("ADAPTER", "Platillo: " + platillo.getnombrePlatillo());
            holder.txtNombrePlatillo.setText(platillo.getnombrePlatillo());

            if (platillo.getImagenPlatillo() != null && !platillo.getImagenPlatillo().trim().isEmpty()) {
                Glide.with(context)
                        .load(platillo.getImagenPlatillo())
                        .placeholder(R.drawable.img_2)
                        .error(R.drawable.img_2)
                        .into(holder.imagenPlatillo);
            } else {
                holder.imagenPlatillo.setImageResource(R.drawable.img_2);
            }
        }else{
            holder.txtNombrePlatillo.setText("Desconocido");
        }
    }

    @Override
    public int getItemCount() {
        return platilloComandaList != null ? platilloComandaList.size() : 0;
    }

    public static class PlatilloClienteViewHolder extends RecyclerView.ViewHolder {
        ImageView imagenPlatillo;
        TextView txtNombrePlatillo;

        public PlatilloClienteViewHolder(@NonNull View itemView) {
            super(itemView);
            imagenPlatillo = itemView.findViewById(R.id.imagenPlatoCOmandas);
            txtNombrePlatillo = itemView.findViewById(R.id.txtNombrePlatilo);
        }
    }
}
