package com.example.sistemadecomandas.vistasAdmin.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sistemadecomandas.vistasAdmin.Adapters.GestionUsuarioAdaptador;
import com.example.sistemadecomandas.Modelos.Usuario;

import com.example.sistemadecomandas.databinding.FragmentAdminHomeBinding;
import com.example.sistemadecomandas.vistasAdmin.RegistroActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminHomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private Button btnNuevoUsuario;
    private GestionUsuarioAdaptador adaptador;
    private List<Usuario> listaUsuarios;
    private FragmentAdminHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAdminHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.recyclerUsuarios.setLayoutManager(new LinearLayoutManager(getContext()));
        listaUsuarios = new ArrayList<>();
        adaptador = new GestionUsuarioAdaptador(listaUsuarios, getContext(), getParentFragmentManager());
        binding.recyclerUsuarios.setAdapter(adaptador);

        cargarUsuariosDesdeFirebase();

        binding.btnNuevoUsuario.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), RegistroActivity.class);
            startActivity(intent);
        });
        return root;
    }
    private void cargarUsuariosDesdeFirebase() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usuarios");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaUsuarios.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Usuario usuario = ds.getValue(Usuario.class);
                    listaUsuarios.add(usuario);
                }
                adaptador.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}