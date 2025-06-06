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

import com.bumptech.glide.Glide;
import com.example.sistemadecomandas.Modelos.Usuario;
import com.example.sistemadecomandas.R;
import com.example.sistemadecomandas.vistasAdmin.ui.EditarUsuarioFragment;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GestionUsuarioAdaptador extends RecyclerView.Adapter<GestionUsuarioAdaptador.GestionUsuarioVIewHolder> {

    private List<Usuario> dataUsuarios;
    private List<Usuario> listaOriginal;
    private Context context;
    private FragmentManager manager;
    public GestionUsuarioAdaptador() {
    }
    public GestionUsuarioAdaptador(List<Usuario> dataUsuarios, Context context, FragmentManager manager) {
        this.dataUsuarios = dataUsuarios;
        this.listaOriginal = dataUsuarios;
        this.context = context;
        this.manager = manager;
    }

    public List<Usuario> getDataUsuarios() {
        return dataUsuarios;
    }

    public void setDataUsuarios(List<Usuario> dataUsuarios) {
        this.dataUsuarios = dataUsuarios;
    }

    public List<Usuario> getListaOriginal() {
        return listaOriginal;
    }

    public void setListaOriginal(List<Usuario> listaOriginal) {
        this.listaOriginal = listaOriginal;
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

        String imagen = usuarios.getImagen();
        if (imagen != null && !imagen.isEmpty()) {
            Glide.with(context)
                    .load(imagen)
                    .placeholder(R.drawable.img_por_defecto_usuario)
                    .into(holder.imageViewUsuario);
        } else {
            holder.imageViewUsuario.setImageResource(R.drawable.img_por_defecto_usuario);
        }

        holder.btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditarUsuarioFragment editarUsuarioFragment = EditarUsuarioFragment.newInstance(usuarios);
                editarUsuarioFragment.show(manager, "EditarUsuario");
            }
        });
        holder.btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarUsuario(usuarios);
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
    public void eliminarUsuario(Usuario usuario){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Mensaje de confirmacion");
        alertDialog.setMessage("Esta seguro de eliminar el registro?");
        alertDialog.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                ExecutorService service = Executors.newSingleThreadExecutor();
                service.execute(() -> {
                    FirebaseDatabase.getInstance()
                            .getReference("usuarios")
                            .child(usuario.getId())
                            .removeValue();
                });
            }
                }).setNegativeButton("no", null).show();
    }

}
