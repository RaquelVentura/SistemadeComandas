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

    public MenuAdaptador() {
    }

    public List<Platillo> getDataPlatillo() {
        return dataPlatillo;
    }

    public void setDataPlatillo(List<Platillo> dataPlatillo) {
        this.dataPlatillo = dataPlatillo;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public FragmentManager getManager() {
        return manager;
    }

    public void setManager(FragmentManager manager) {
        this.manager = manager;
    }

    @NonNull
    @Override
    public MenuAdaptador.MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_platillo_admin, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuAdaptador.MenuViewHolder holder, int position) {
        Platillo platillo = dataPlatillo.get(position);
        holder.lbNombrePlatillo.setText(platillo.getnombrePlatillo());
        holder.lbDescripcion.setText(platillo.getDescripcion());
        holder.lbPrecioPlatillo.setText(platillo.getPrecio());
        holder.btnElimianrPlatillo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //eliminarPlatillo(platillo);
            }
        });
        holder.btnEditarPlatillo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //EditarPlatilloFragment editarPlatilloFragment = EditarPlatilloFragment.newInstance(platillo);
                //editarPlatilloFragment.show(manager, "EditarPlatillo");
            }
        });
    }

    @Override
    public int getItemCount() {return dataPlatillo.size();}
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
    public void eliminarPlatillo(Platillo platillo){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Mensaje de confirmacion");
        alertDialog.setMessage("Esta seguro de eliminar esta platillo?");
        alertDialog.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                ExecutorService service = Executors.newSingleThreadExecutor();
                service.execute(() -> {
                    FirebaseDatabase.getInstance()
                            .getReference("platillos")
                            .child(platillo.getIdPlatillo())
                            .removeValue();
                });
            }
        }).setNegativeButton("no", null).show();
    }
}
