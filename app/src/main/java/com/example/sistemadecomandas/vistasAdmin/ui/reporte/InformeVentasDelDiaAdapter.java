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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InformeVentasDelDiaAdapter extends RecyclerView.Adapter<InformeVentasDelDiaAdapter.InformeVentasDelDiaViewHolder> {

    private List<PlatilloComanda> dataPlatilloComandaV;
    private Context context;
    private FragmentManager manager;

    public InformeVentasDelDiaAdapter(List<PlatilloComanda> dataPlatilloComandaV, Context context, FragmentManager manager) {
        this.dataPlatilloComandaV = dataPlatilloComandaV;
        this.context = context;
        this.manager = manager;
    }

    @NonNull
    @Override
    public InformeVentasDelDiaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_platos_populares, parent, false);
        return new InformeVentasDelDiaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InformeVentasDelDiaViewHolder holder, int position) {
        PlatilloComanda platilloComandaV = dataPlatilloComandaV.get(position);
        Platillo platillo = platilloComandaV.getPlatillo();

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

                    String imagen = platilloCargado.getImagenPlatillo();
                    if (imagen != null && !imagen.isEmpty()) {
                        Glide.with(context)
                                .load(imagen)
                                .placeholder(R.drawable.img_2)
                                .into(holder.imageView);
                    } else {
                        holder.imageView.setImageResource(R.drawable.img_2);
                    }

                    // Calcular estadísticas
                    calcularEstadisticas(platilloCargado, holder);
                } else {
                    holder.lbNombrePlatillo.setText("Platillo no encontrado");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error al cargar platillo: " + error.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataPlatilloComandaV.size();
    }

    public static class InformeVentasDelDiaViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView lbNombrePlatillo, lbCantidadVendida, lbCantidadDinero;

        public InformeVentasDelDiaViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imagenPlatoPopular);
            lbNombrePlatillo = itemView.findViewById(R.id.lbNombrePlatiloPopular);
            lbCantidadVendida = itemView.findViewById(R.id.lbCantidadVendidos);
            lbCantidadDinero = itemView.findViewById(R.id.lbCantidadDinero);
        }
    }

    private void calcularEstadisticas(Platillo platillo, InformeVentasDelDiaViewHolder holder) {
        DatabaseReference comandasRef = FirebaseDatabase.getInstance().getReference("Comandas");

        comandasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String fechaActual = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                int cantidadTotal = 0;
                double dineroTotal = 0.0;

                for (DataSnapshot comandaSnap : snapshot.getChildren()) {
                    String fechaComanda = comandaSnap.child("fecha").getValue(String.class);

                    if (fechaComanda != null && fechaComanda.startsWith(fechaActual)) {
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
                                        Log.e("ERROR", "Precio mal formateado: " + platillo.getPrecio());
                                    }
                                }
                            }
                        }
                    }
                }
                if (cantidadTotal > 0) {
                    holder.itemView.setVisibility(View.VISIBLE);
                    ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    holder.itemView.setLayoutParams(params);

                    holder.lbCantidadVendida.setVisibility(View.VISIBLE);
                    holder.lbCantidadDinero.setVisibility(View.VISIBLE);
                    holder.imageView.setVisibility(View.VISIBLE);
                    holder.lbNombrePlatillo.setVisibility(View.VISIBLE);

                    holder.lbCantidadVendida.setText("Vendidos: " + cantidadTotal);
                    holder.lbCantidadDinero.setText("Total: $" + String.format("%.2f", dineroTotal));
                } else {
                    holder.itemView.setVisibility(View.GONE);
                    ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                    params.height = 0;
                    holder.itemView.setLayoutParams(params);
                }

            }
            private void calcularEstadisticas(Platillo platillo, PlatilloComanda platilloComanda, InformeVentasDelDiaViewHolder holder) {
                int cantidad = platilloComanda.getCantidad();
                double total = 0.0;
                try {
                    double precio = Double.parseDouble(platillo.getPrecio());
                    total = cantidad * precio;
                } catch (NumberFormatException e) {
                    Log.e("Precio", "Error en precio de platillo: " + platillo.getPrecio());
                }

                holder.lbCantidadVendida.setText("Vendidos: " + cantidad);
                holder.lbCantidadDinero.setText("Total: $" + String.format("%.2f", total));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                holder.itemView.setVisibility(View.GONE);
                ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                params.height = 0;
                holder.itemView.setLayoutParams(params);
            }
        });
    }

}
