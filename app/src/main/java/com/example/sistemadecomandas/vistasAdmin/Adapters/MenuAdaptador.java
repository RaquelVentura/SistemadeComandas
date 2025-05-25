package com.example.sistemadecomandas.vistasAdmin.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sistemadecomandas.Modelos.Platillo;
import com.example.sistemadecomandas.R;
import com.example.sistemadecomandas.vistasAdmin.ui.EditarPlatillosFragment;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MenuAdaptador extends RecyclerView.Adapter<MenuAdaptador.MenuViewHolder> {
    private List<Platillo> dataPlatillo;
    private Context context;
    private FragmentManager manager;

    public MenuAdaptador(List<Platillo> dataPlatillo, Context context, FragmentManager manager) {
        this.dataPlatillo = dataPlatillo;
        this.context = context;
        this.manager = manager;
    }

    // Método para actualizar la lista de platillos
    public void actualizarLista(List<Platillo> nuevaLista) {
        dataPlatillo.clear();
        dataPlatillo.addAll(nuevaLista);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_platillo_admin, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        Platillo platillo = dataPlatillo.get(position);
        holder.lbNombrePlatillo.setText(platillo.getnombrePlatillo());
        holder.lbDescripcion.setText(platillo.getDescripcion());
        holder.lbPrecioPlatillo.setText(platillo.getPrecio());

        holder.btnElimianrPlatillo.setOnClickListener(v -> eliminarPlatillo(platillo));

        holder.btnEditarPlatillo.setOnClickListener(v -> {
            EditarPlatillosFragment editarPlatillosFragment = EditarPlatillosFragment.newInstance(platillo);
            editarPlatillosFragment.show(manager, "EditarPlatillo");
        });
    }

    @Override
    public int getItemCount() {
        return dataPlatillo.size();
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgFotoPlatillo;
        private TextView lbNombrePlatillo, lbPrecioPlatillo, lbDescripcion;
        private Button btnElimianrPlatillo, btnEditarPlatillo;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFotoPlatillo = itemView.findViewById(R.id.imgFotoPlatillo);
            lbNombrePlatillo = itemView.findViewById(R.id.lbNombrePLatillo);
            lbPrecioPlatillo = itemView.findViewById(R.id.lbPrecioPlatillo);
            lbDescripcion = itemView.findViewById(R.id.lbDescripcionPlatillo);
            btnElimianrPlatillo = itemView.findViewById(R.id.btnElimianrPlatillo);
            btnEditarPlatillo = itemView.findViewById(R.id.btnEditarPlatillo);
        }
    }

    private void eliminarPlatillo(Platillo platillo) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Mensaje de confirmación");
        alertDialog.setMessage("¿Está seguro de eliminar este platillo?");
        alertDialog.setPositiveButton("Sí", (dialog, i) -> {
            ExecutorService service = Executors.newSingleThreadExecutor();
            service.execute(() -> {
                FirebaseDatabase.getInstance()
                        .getReference("platillos")
                        .child(platillo.getIdPlatillo())
                        .removeValue();
            });
        }).setNegativeButton("No", null).show();
    }
}
