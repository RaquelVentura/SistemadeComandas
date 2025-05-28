package com.example.sistemadecomandas.vistasCocineros;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sistemadecomandas.R;

import java.util.List;

public class AdjuntosAdapter extends RecyclerView.Adapter<AdjuntosAdapter.AdapterViewHolder> {
    private List<String> urls;
    private Context context;
    public AdjuntosAdapter(List<String> urls, Context context) {
        this.urls = urls;
        this.context = context;
    }
    @NonNull
    @Override
    public AdjuntosAdapter.AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_imagen_adjunta_cocina, parent, false);
        return new AdapterViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull AdjuntosAdapter.AdapterViewHolder holder, int position) {
        String url = urls.get(position);
        Glide.with(context).load(url).into(holder.imageView);
    }
    @Override
    public int getItemCount() {
        return urls.size();
    }
    public class AdapterViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgAdjunta);
        }
    }
}