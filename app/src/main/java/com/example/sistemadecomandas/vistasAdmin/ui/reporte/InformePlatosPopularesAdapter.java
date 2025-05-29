package com.example.sistemadecomandas.vistasAdmin.ui.reporte;

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
import com.google.firebase.database.*;

import java.util.List;

public class InformePlatosPopularesAdapter extends RecyclerView.Adapter<InformePlatosPopularesAdapter.InformePlatosPopularesViewHolder> {
    private List<PlatilloComanda> dataPlatilloComanda;
    private Context context;
    private FragmentManager manager;

    public InformePlatosPopularesAdapter(List<PlatilloComanda> dataPlatilloComanda, Context context, FragmentManager manager) {
        this.dataPlatilloComanda = dataPlatilloComanda;
        this.context = context;
        this.manager = manager;
    }

    @NonNull
    @Override
    public InformePlatosPopularesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_platos_populares, parent, false);
        return new InformePlatosPopularesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InformePlatosPopularesViewHolder holder, int position) {
        PlatilloComanda item = dataPlatilloComanda.get(position);
        Platillo platillo = item.getPlatillo();

        if (platillo == null || platillo.getIdPlatillo() == null) {
            holder.lbNombrePlatillo.setText("Nombre del plato");
            return;
        }

        DatabaseReference platilloRef = FirebaseDatabase.getInstance().getReference("platillos").child(platillo.getIdPlatillo());
        platilloRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Platillo platilloCargado = snapshot.getValue(Platillo.class);
                if (platilloCargado != null) {
                    holder.lbNombrePlatillo.setText(platilloCargado.getnombrePlatillo());

                    calcularEstadisticas(platilloCargado, holder.lbCantidadVendida, holder.lbCantidadDinero);

                    String imagen = platilloCargado.getImagenPlatillo();
                    if (imagen != null && !imagen.isEmpty()) {
                        Glide.with(context)
                                .load(imagen)
                                .placeholder(R.drawable.img_2)
                                .into(holder.imageView);
                    } else {
                        holder.imageView.setImageResource(R.drawable.img_2);
                    }
                } else {
                    holder.lbNombrePlatillo.setText("Platillo no encontrado");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                holder.lbNombrePlatillo.setText("Error al cargar");
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataPlatilloComanda.size();
    }

    public static class InformePlatosPopularesViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView lbNombrePlatillo, lbCantidadVendida, lbCantidadDinero;

        public InformePlatosPopularesViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imagenPlatoPopular);
            lbNombrePlatillo = itemView.findViewById(R.id.lbNombrePlatiloPopular);
            lbCantidadVendida = itemView.findViewById(R.id.lbCantidadVendidos);
            lbCantidadDinero = itemView.findViewById(R.id.lbCantidadDinero);
        }
    }

    public void calcularEstadisticas(final Platillo platillo, final TextView lbCantidadVendida, final TextView lbCantidadDinero) {
        DatabaseReference comandasRef = FirebaseDatabase.getInstance().getReference("Comandas");

        comandasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("DEBUG", "Platillo cargado: " + platillo);

                int cantidadTotal = 0;
                double dineroTotal = 0.0;

                for (DataSnapshot comandaSnap : snapshot.getChildren()) {
                    DataSnapshot platillosSnap = comandaSnap.child("platillos");

                    for (DataSnapshot platilloSnap : platillosSnap.getChildren()) {
                        PlatilloComanda platilloComanda = platilloSnap.getValue(PlatilloComanda.class);
                        if (platilloComanda != null && platilloComanda.getPlatillo() != null) {
                            String idComparar = platilloComanda.getPlatillo().getIdPlatillo();
                            if (idComparar.equals(platillo.getIdPlatillo())) {
                                cantidadTotal += platilloComanda.getCantidad();
                                try {
                                    double precio = Double.parseDouble(platillo.getPrecio());
                                    dineroTotal += platilloComanda.getCantidad() * precio;
                                } catch (NumberFormatException e) {

                                }
                            }
                        }
                    }
                }
                lbCantidadVendida.setText("Vendidos: " + cantidadTotal);
                lbCantidadDinero.setText("Total: $" + String.format("%.2f", dineroTotal));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                lbCantidadVendida.setText("Error");
                lbCantidadDinero.setText("Error");
            }
        });
    }
}