package com.example.sistemadecomandas.vistasAdmin.ui.reporte;

import android.content.Context;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class InformeVentasDelDiaAdapter extends RecyclerView.Adapter<InformeVentasDelDiaAdapter.InformeVnetasDelDiaViewHolder> {
    private List<PlatilloComanda> dataPlatilloComanda;
    private Context context;
    private FragmentManager manager;

    public InformeVentasDelDiaAdapter(List<PlatilloComanda> dataPlatilloComanda, Context context, FragmentManager manager) {
        this.dataPlatilloComanda = dataPlatilloComanda;
        this.context = context;
        this.manager = manager;
    }

    @NonNull
    @Override
    public InformeVentasDelDiaAdapter.InformeVnetasDelDiaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_platos_populares, parent, false);
        return new InformeVnetasDelDiaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InformeVentasDelDiaAdapter.InformeVnetasDelDiaViewHolder holder, int position) {
        PlatilloComanda platilloComanda = dataPlatilloComanda.get(position);
        Platillo platillo = platilloComanda.getPlatillo();

        holder.lbNombrePlatillo.setText(platillo.getnombrePlatillo());

        calcularEstadisticas(platillo, holder.lbCantidadVendida, holder.lbCantidadDinero);

        String imagen = platillo.getImagenPlatillo();
        if (imagen != null && !imagen.isEmpty()) {
            Glide.with(context)
                    .load(imagen)
                    .placeholder(R.drawable.img_2)
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.img_2);
        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class InformeVnetasDelDiaViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView lbNombrePlatillo, lbCantidadVendida, lbCantidadDinero;
        public InformeVnetasDelDiaViewHolder(@NonNull View itemView) {
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
                                double precio = 0.0;
                                try {
                                    precio = Double.parseDouble(platillo.getPrecio());
                                } catch (NumberFormatException e) {
                                    precio = 0.0;
                                }
                                dineroTotal += platilloComanda.getCantidad() * precio;
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
