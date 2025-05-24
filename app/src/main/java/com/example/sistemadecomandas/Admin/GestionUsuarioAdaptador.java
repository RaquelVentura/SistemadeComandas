package com.example.sistemadecomandas.Admin;

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

import com.example.sistemadecomandas.Modelos.Usuario;
import com.example.sistemadecomandas.R;
import com.example.sistemadecomandas.vistasAdmin.ui.EditarUsuarioFragment;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GestionUsuarioAdaptador extends RecyclerView.Adapter<GestionUsuarioAdaptador.GestionUsuarioVIewHolder> {

    private List<Usuario> dataUsuarios;
    private Context context;
    private FragmentManager manager;

    public GestionUsuarioAdaptador() {
    }

    public GestionUsuarioAdaptador(List<Usuario> dataUsuarios, Context context, FragmentManager manager) {
        this.dataUsuarios = dataUsuarios;
        this.context = context;
        this.manager = manager;
    }

    public List<Usuario> getDataUsuarios() {
        return dataUsuarios;
    }

    public void setDataUsuarios(List<Usuario> dataUsuarios) {
        this.dataUsuarios = dataUsuarios;
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
    public GestionUsuarioAdaptador.GestionUsuarioVIewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_usuarios, parent, false);
        return new GestionUsuarioVIewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GestionUsuarioAdaptador.GestionUsuarioVIewHolder holder, int position) {
        Usuario usuarios = dataUsuarios.get(position);
        holder.lbNombre.setText(usuarios.getNombre());
        holder.lbRol.setText(usuarios.getRol());

        int resourceId = context.getResources().getIdentifier(
                usuarios.getImagen(), "drawable", context.getPackageName());
        holder.imageViewUsuario.setImageResource(resourceId);

        holder.btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //EditarUsuarioFragment fragment= EditarUsuarioFragment.newInstance(usuarios);
                //fragment.show(manager, "EditarUsuario");
            }
        });
        holder.btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarUsuario();
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataUsuarios.size();
    }

    public class GestionUsuarioVIewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewUsuario;
        private TextView lbNombre, lbRol;
        private Button btnEliminar, btnEditar;
        public GestionUsuarioVIewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewUsuario = itemView.findViewById(R.id.imgFotoUsuario);
            lbNombre = itemView.findViewById(R.id.lbNombreUsuario);
            lbRol = itemView.findViewById(R.id.lbRolUsuario);
            btnEliminar = itemView.findViewById(R.id.btnElimianrUsuario);
            btnEditar = itemView.findViewById(R.id.btnEditarUsuario);
        }
    }
    public void eliminarUsuario(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Mensaje de confirmacion");
        alertDialog.setMessage("Esta seguro de eliminar el registro?");
        alertDialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        ExecutorService service = Executors.newSingleThreadExecutor();
                        service.execute(()->{
                            /*FirebaseDatabase.getInstance()
                                    .getReference("usuarios")
                                    .child(usuarios.getId())
                                    .removeValue();*/
                        });
                    }
                })
                .setNegativeButton("no", null).show();
    }
}
