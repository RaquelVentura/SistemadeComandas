package com.example.sistemadecomandas;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentContainerView;

public class MenuPrincipalActivity extends AppCompatActivity {
    private Button btnIniciar;
    private FragmentContainerView fragmentContainerView;
    //private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_principal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //toolbar = findViewById(R.id.toolbar);
        fragmentContainerView = findViewById(R.id.fragmentContainerView2);

        /*toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.item1:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainerView2, new EstadisticasFragment())
                            .commit();
                case R.id.item2:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainerView2, new InfoFragment())
                            .commit();
                case R.id.item3:
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
            }
            return false;
        });*/

        btnIniciar = findViewById(R.id.btnIniciarJuego);
        btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getBaseContext(), ActivityJuego.class);
//                startActivity(intent);
            }
        });
    }
}