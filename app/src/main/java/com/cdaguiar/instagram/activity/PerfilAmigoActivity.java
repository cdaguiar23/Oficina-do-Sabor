package com.cdaguiar.instagram.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.Button;

import com.cdaguiar.instagram.R;
import com.cdaguiar.instagram.model.Usuario;

public class PerfilAmigoActivity extends AppCompatActivity {

    private Usuario usuarioSelecionado;
    private Button buttonacaoPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Inicializar componentes
        buttonacaoPerfil = findViewById(R.id.buttonAcaoPerfil);
        buttonacaoPerfil.setText("Seguir");

        // Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Perfil");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        // Recuperar usuaário selecionado
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            usuarioSelecionado = (Usuario) bundle.getSerializable("usuarioSelecionado");

            // Configura nome do usuário na toolbar
            getSupportActionBar().setTitle(usuarioSelecionado.getNome());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}