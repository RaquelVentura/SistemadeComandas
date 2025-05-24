package com.example.sistemadecomandas.Admin;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MenuAdaptador extends RecyclerView.Adapter<MenuAdaptador.MenuViewHolder> {
    @NonNull
    @Override
    public MenuAdaptador.MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MenuAdaptador.MenuViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
