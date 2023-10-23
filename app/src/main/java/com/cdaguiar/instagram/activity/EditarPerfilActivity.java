package com.cdaguiar.instagram.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cdaguiar.instagram.R;
import com.cdaguiar.instagram.helper.UsuarioFirebase;
import com.cdaguiar.instagram.model.Usuario;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditarPerfilActivity extends AppCompatActivity {

    private CircleImageView imageEditarPerfil;
    private TextView textAlterarFoto;
    private TextInputEditText editNomePerfil, editEmailPerfil;
    private Button buttonSalavarAlteracoes;
    private Usuario usuarioLoagado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        // Confogurações iniciais
        usuarioLoagado = UsuarioFirebase.getDadoUsuarioLogado();

        // Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Editar Perfil");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        // Inicializar componentes
        inicializarComponentes();

        // Recuperar os dados do usuário
        FirebaseUser usuarioPerfil = UsuarioFirebase.getUsuarioAtual();
        editNomePerfil.setText(usuarioPerfil.getDisplayName());
        editEmailPerfil.setText(usuarioPerfil.getEmail());

        // Salvar alterações do nome
        buttonSalavarAlteracoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nomeAtualizado = editNomePerfil.getText().toString();

                // Atualizar nome do perfil
                UsuarioFirebase.atualizarNomeUsuario(nomeAtualizado);

                // Atualizar nome no banco de dados
                usuarioLoagado.setNome(nomeAtualizado);
                usuarioLoagado.atualizar();
                Toast.makeText(EditarPerfilActivity.this, "Dados alterados com sucesso!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void inicializarComponentes() {
        imageEditarPerfil = findViewById(R.id.imageEditarPerfil);
        textAlterarFoto = findViewById(R.id.textAlterarFoto);
        editNomePerfil = findViewById(R.id.editNomePerfil);
        editEmailPerfil = findViewById(R.id.editEmailPerfil);
        buttonSalavarAlteracoes = findViewById(R.id.buttonSalvarAlteracoes);
        editEmailPerfil.setFocusable(false);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}