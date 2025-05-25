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

import com.example.sistemadecomandas.R;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MenuAdaptador extends RecyclerView.Adapter<MenuAdaptador.MenuViewHolder> {
    private List<Orden> dataOrden;
    private Context context;
    private FragmentManager manager;

    public MenuAdaptador(List<Orden> dataOrden, Context context, FragmentManager manager) {
        this.dataOrden = dataOrden;
        this.context = context;
        this.manager = manager;
    }

    public MenuAdaptador() {
    }

    public List<Orden> getDataOrden() {
        return dataOrden;
    }

    public void setDataOrden(List<Orden> dataOrden) {
        this.dataOrden = dataOrden;
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
        Orden orden = dataOrden.get(position);
        holder.lbNombrePLatillo.setText(orden.getProducto());
        holder.lbPrecioPLatillo.setText(orden.getTotalPagar());

    }

    @Override
    public int getItemCount() {return dataOrden.size();}
    public class MenuViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgFotoPlatillo;
        private TextView lbNombrePLatillo, lbPrecioPLatillo;
        private Button btnElimianrCliente, btnEditarCliente;
        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFotoPlatillo = itemView.findViewById(R.id.imgFotoPlatillo);
            lbNombrePLatillo = itemView.findViewById(R.id.lbNombrePLatillo);
            lbPrecioPLatillo = itemView.findViewById(R.id.lbPrecioPLatillo);
            btnElimianrCliente = itemView.findViewById(R.id.btnElimianrUsuario);
            btnEditarCliente = itemView.findViewById(R.id.btnEditarUsuario);
        }
    }
    public void eliminarOrden(Orden orden){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Mensaje de confirmacion");
        alertDialog.setMessage("Esta seguro de eliminar esta orden?");
        alertDialog.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                ExecutorService service = Executors.newSingleThreadExecutor();
                service.execute(() -> {
                    /*FirebaseDatabase.getInstance()
                            .getReference("orden")
                            .child(orden.getId())
                            .removeValue();*/
                });
            }
        }).setNegativeButton("no", null).show();
    }
}
