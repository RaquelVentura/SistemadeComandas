package com.example.sistemadecomandas.vistasMeseros.ui.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sistemadecomandas.Modelos.Platillo;
import com.example.sistemadecomandas.Modelos.PlatilloComanda;
import com.example.sistemadecomandas.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PlatilloAdapterSeleccion extends RecyclerView.Adapter<PlatilloAdapterSeleccion.ViewHolder> {

    private List<Platillo> listaPlatillos;
    private Set<String> idsSeleccionados = new HashSet<>();
    private Map<String, Integer> cantidadesSeleccionadas = new HashMap<>();
    private OnPlatilloSeleccionadoListener listener;
    private List<PlatilloComanda> platillosConCantidad = new ArrayList<>();

    private Context context;

    public PlatilloAdapterSeleccion(List<Platillo> listaPlatillos, OnPlatilloSeleccionadoListener listener) {
        this.listaPlatillos = listaPlatillos;
        this.listener = listener;
    }

    public interface OnPlatilloSeleccionadoListener {
        void onPlatilloSeleccionado(Platillo platillo, boolean seleccionado, int cantidad);
    }

    public void setPlatillosConCantidad(List<PlatilloComanda> platillosConCantidad) {
        this.platillosConCantidad = platillosConCantidad;
    }



    @NonNull
    @Override
    public PlatilloAdapterSeleccion.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View vista = LayoutInflater.from(context)
                .inflate(R.layout.item_platillo_seleccionado, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull PlatilloAdapterSeleccion.ViewHolder holder, int position) {
        Platillo platillo = listaPlatillos.get(position);
        holder.bind(platillo);
    }

    @Override
    public int getItemCount() {
        return listaPlatillos.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtCategoria, txtDescripcion, txtPrecio;
        EditText edtCantidad;
        ImageView imgPlatillo;
        CheckBox checkBox;

        ViewHolder(View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtCategoria = itemView.findViewById(R.id.txtCategoria);
            txtDescripcion = itemView.findViewById(R.id.txtDescripcion);
            txtPrecio = itemView.findViewById(R.id.txtPrecio);
            edtCantidad = itemView.findViewById(R.id.edtCantidad);
            imgPlatillo = itemView.findViewById(R.id.imgPlatillo);
            checkBox = itemView.findViewById(R.id.checkboxSeleccion);
        }

        void bind(Platillo platillo) {
            txtNombre.setText(platillo.getnombrePlatillo());
            txtCategoria.setText("Categoría: " + platillo.getCategoria());
            txtDescripcion.setText("Descripción: " + platillo.getDescripcion());
            txtPrecio.setText("$" + platillo.getPrecio());

            // Manejo de imagen por defecto (puedes cambiar esto según tu lógica)
            if (platillo.getImagenPlatillo().equals("img_por_defecto_usuario")) {
                imgPlatillo.setImageResource(R.drawable.ic_launcher_foreground); // imagen por defecto
            } else {
                // Si usas Glide/Picasso para URLs, puedes cargarlo así:
                // Glide.with(context).load(platillo.getImagenPlatillo()).into(imgPlatillo);
            }

            int cantidadGuardada = cantidadesSeleccionadas.getOrDefault(platillo.getIdPlatillo(),1);
            edtCantidad.setText(String.valueOf(cantidadGuardada));

            checkBox.setOnCheckedChangeListener(null); // evitar efecto de reciclado
            checkBox.setChecked(idsSeleccionados.contains(platillo.getIdPlatillo()));
            edtCantidad.setEnabled(checkBox.isChecked());

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    idsSeleccionados.add(platillo.getIdPlatillo());
                }
                else
                {
                    idsSeleccionados.remove(platillo.getIdPlatillo());
                    cantidadesSeleccionadas.remove(platillo.getIdPlatillo());
                }

                int cantidad = getCantidadDesdeEditText(edtCantidad);
                cantidadesSeleccionadas.remove(platillo.getIdPlatillo(),cantidad);

                if (listener !=null){
                    listener.onPlatilloSeleccionado(platillo, isChecked, cantidad);
                }

                edtCantidad.setEnabled(isChecked);
            });

            edtCantidad.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus && checkBox.isChecked()){
                    int cantidad = getCantidadDesdeEditText(edtCantidad);
                    cantidadesSeleccionadas.put(platillo.getIdPlatillo(), cantidad);
                    if (listener !=null){
                        listener.onPlatilloSeleccionado(platillo,true,cantidad);
                    }
                }
            });
        }

        private int getCantidadDesdeEditText(EditText edt) {
            try {
                String texto = edt.getText().toString().trim();
                return Math.max(0, Integer.parseInt(texto));
            } catch (NumberFormatException e) {
                return 0;
            }
        }
    }
}

