package com.example.sistemadecomandas.Admin;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GestionUsuarioAdaptador extends RecyclerView.Adapter<GestionUsuarioAdaptador.GestionUsuarioVIewHolder> {

    @NonNull
    @Override
    public GestionUsuarioAdaptador.GestionUsuarioVIewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull GestionUsuarioAdaptador.GestionUsuarioVIewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class GestionUsuarioVIewHolder extends RecyclerView.ViewHolder {
        public GestionUsuarioVIewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
